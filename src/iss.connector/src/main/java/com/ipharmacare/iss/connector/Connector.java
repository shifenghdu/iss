package com.ipharmacare.iss.connector;

import com.ipharmacare.iss.common.dispatch.IBizContext;
import com.ipharmacare.iss.common.esb.EsbMsg;
import com.ipharmacare.iss.connector.msgpack.CommonCodeFactory;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class Connector {

    private NioSocketConnector connector = null;

    private int CONNECT_TIME_OUT = 50000;

    private String address;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private List<IoSession> sessions = new Vector<IoSession>();

    private IConnectorCallBack callBack;

    private Map<Long, EsbMsg> map = new ConcurrentHashMap<Long, EsbMsg>();

    public AtomicLong id = new AtomicLong(0);

    public static ThreadLocal<EsbMsg> local = new ThreadLocal<EsbMsg>();

    private String clientname;

    private int current = 0;

    public String getClientname() {
        return clientname;
    }

    private BizContext bizContext = null;

    private int timeout = 10000;

    private final int SIZE_128K = 131072;

    private final int RETRY_TIMES = 3;

    private final long WRITE_WAIT_TIME = 10000L;

    public AtomicLong receiveCount = new AtomicLong(0);

    public Connector(String address) {
        this("anonymous", address);
    }

    public Connector(String name, String address) {
        this.clientname = name;
        this.address = address;
        connector = new NioSocketConnector();
        connector.getSessionConfig().setReuseAddress(true);
        connector.getSessionConfig().setReceiveBufferSize(SIZE_128K);
        connector.getSessionConfig().setReadBufferSize(SIZE_128K);
//        connector.getSessionConfig().setTcpNoDelay(true);
        connector.getSessionConfig().setSoLinger(-1);
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
        chain.addLast("codec", new ProtocolCodecFilter(new CommonCodeFactory()));
        chain.addLast("threadpool", new ExecutorFilter(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1)));
        connector.setHandler(new ClientMsgHandler(this));
        connector.setConnectTimeoutMillis(CONNECT_TIME_OUT);
        this.bizContext = new BizContext(this);
//		IoBuffer.setUseDirectBuffer(true);
    }

    public boolean connect(IConnectorCallBack callBack) {
        this.callBack = callBack;
        return connect();
    }

    public boolean connect() {
        try {
            String[] ads = address.split("\\,");
            for (String ad : ads) {
                String[] strings = ad.split(":");
                ConnectFuture cf = connector.connect(new InetSocketAddress(strings[0],
                        Integer.valueOf(strings[1])));
                cf.awaitUninterruptibly();
                IoSession session = cf.getSession();
                if (session != null) {
                    session.setAttribute("address", ad);
                    sessions.add(session);
                }
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("connect failed:", e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        connector.dispose();
        super.finalize();
    }

    public void reconnect(final String addr) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                IoSession session = null;
                while (session == null) {
                    try {
                        Thread.sleep(10000);
                        String[] strings = addr.split(":");
                        ConnectFuture cf = connector.connect(new InetSocketAddress(
                                strings[0], Integer.valueOf(strings[1])));
                        cf.awaitUninterruptibly();
                        session = cf.getSession();
                    } catch (Throwable e) {
                        logger.error(String.format("reconnect faild [%s]", addr), e);
                    }
                }
                session.setAttribute("address", addr);
                sessions.add(session);
            }
        }).start();
    }

    private void next() {
        current++;
        if (current >= sessions.size()) {
            current -= (sessions.size());
        }
    }

    public IoSession getNext() {
        IoSession session = null;
        synchronized (sessions) {
            try {
                session = sessions.get(current);
                while (session.isClosing()) {
                    sessions.remove(current);
                    session = sessions.get(current);
                }
                next();
            } catch (Exception e) {
                current = 0;
                if (sessions.size() != 0) {
                    return sessions.get(current);
                } else {
                    return null;
                }
            }
        }
        return session;
    }

    public void send(EsbMsg pack) throws Exception {
        pack.setPackageid(Thread.currentThread().getId());
        pack.addTimetick(clientname, "connector", System.nanoTime());
        map.put(pack.getPackageid(),pack);
        local.set(pack);
        if (callBack != null) {
            IoSession session = getNext();
            if (writeSession(session,pack,RETRY_TIMES)){
                throw new RuntimeException(String.format("send msg system [%d] function [%d] failed session [%s]",
                        pack.getSystemid(),pack.getFunctionid(), session.toString()));
            }
        }
    }

    public EsbMsg recv(long timeout) throws Exception {
        IoSession session = getNext();
        if (session != null) {
            EsbMsg orgin = local.get();
            int total = 0;
            synchronized (orgin) { //同步发送接收线程
                if (writeSession(session, orgin, RETRY_TIMES)) { //发送成功
                    while (orgin.getResponse().size() == 0) {
                        try {
                            long begin = System.currentTimeMillis();
                            orgin.wait(timeout);
                            long end = System.currentTimeMillis();
                            total += (end - begin);
                        } catch (InterruptedException e) {
                            if (logger.isWarnEnabled())
                                logger.warn("中断异常");
                            continue;
                        }
                        if (total >= timeout) {
                            throw new Exception(String.format("recv time out, session [%s]", session.toString()));
                        }
                    }
                } else {
                    throw new Exception(String.format("send msg system [%d] function [%d] failed session [%s]",
                            orgin.getSystemid(), orgin.getFunctionid(), session.toString()));
                }
            }
            return orgin.getResponse().get(0);
        }
        return null;
    }

    private boolean writeSession(IoSession session,EsbMsg msg,int times){
        int t = 0;
        if(session == null || session.isClosing() || msg == null) return false;
        while (t < times) {
//            if(session.write(msg).awaitUninterruptibly().isWritten()){ // 存在返回延迟 需要与接收线程同步
//                return true;
//            }
            WriteFuture future = session.write(msg);
            if(future.awaitUninterruptibly(WRITE_WAIT_TIME)){
                if(future.isWritten()) {
                    return true;
                }
            }
            t ++;
        }
        session.close(true);
        return false;
    }
    
    public List<EsbMsg> recvMulti(long timeout) throws Exception {
        IoSession session = getNext();
        if (session != null) {
            EsbMsg orgin = local.get();
            int total = 0;
            synchronized (orgin) { //同步发送接收线程
                if (writeSession(session, orgin, RETRY_TIMES)) { //发送成功
                    while (orgin.getResponse().size() == 0) {
                        try {
                            long begin = System.currentTimeMillis();
                            orgin.wait(timeout);
                            long end = System.currentTimeMillis();
                            total += (end - begin);
                        } catch (InterruptedException e) {
                            if (logger.isWarnEnabled())
                                logger.warn("中断异常");
                            continue;
                        }
                        if (total >= timeout) {
                            throw new Exception(String.format("recv time out, session [%s]", session.toString()));
                        }
                    }
                } else {
                    throw new Exception(String.format("send msg system [%d] function [%d] failed session [%s]",
                            orgin.getSystemid(), orgin.getFunctionid(), session.toString()));
                }
            }
            return orgin.getResponse();
        }
        return null;
    }

    public IConnectorCallBack getCallBack() {
        return callBack;
    }

    public void onReceived(EsbMsg pack) {
        if (callBack != null) {
            callBack.onReceived(pack);
        } else {
        	if(pack.getRetcode() == 0){
	        	if(pack.getCopyCount() <= 1 && !pack.isCopySend()){
		            EsbMsg orgin = map.get(pack.getPackageid());
		            if(orgin != null){
                        synchronized (orgin) { //同步发送接收线程
                            orgin.getResponse().add(pack);
			                orgin.notifyAll();
                            receiveCount.incrementAndGet();
			            }
		            }else {
                        logger.warn("返回包 [{}] 未找到匹配线程",pack.toString());
                    }
	        	}else{
		            EsbMsg orgin = map.get(pack.getPackageid());
		            if(orgin != null){
			            synchronized (orgin) { //同步发送接收线程
                            orgin.getResponse().add(pack);
			            	if(pack.getCopyCount() == orgin.getResponse().size()){
			            		orgin.notifyAll();
                                receiveCount.incrementAndGet();
			            	}
			            }
		            }else {
                        logger.warn("返回包 [{}] 未找到匹配线程",pack.toString());
                    }
	        	}
        	}else {
                logger.warn("返回异常包 [{}] ",pack.toString());
            }
        }
    }

    public IBizContext getContext() {
        return this.bizContext;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
