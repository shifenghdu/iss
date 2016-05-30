package com.ipharmacare.iss.connector;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ipharmacare.iss.common.dispatch.IBizContext;
import com.ipharmacare.iss.common.esb.EsbMsg;
import com.ipharmacare.iss.connector.msgpack.CommonCodeFactory;

public class Connector {

    private NioSocketConnector connector = null;

    private int CONNECT_TIME_OUT = 50000;

    private String address;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private List<IoSession> sessions = new Vector<IoSession>();

    private IConnectorCallBack callBack;

    private Map<Long, EsbMsg> map = new ConcurrentHashMap<Long, EsbMsg>();

    public static AtomicLong id = new AtomicLong(0);

    public static ThreadLocal<EsbMsg> local = new ThreadLocal<EsbMsg>();

    private String clientname;

    private int current = 0;

    public String getClientname() {
        return clientname;
    }

    private BizContext bizContext = null;

    private int timeout = 10000;

    public Connector(String address) {
        this("anonymous", address);
    }

    public Connector(String name, String address) {
        this.clientname = name;
        this.address = address;
        connector = new NioSocketConnector();
        connector.getSessionConfig().setReuseAddress(true);
        // connector.getSessionConfig().setReceiveBufferSize(4096);
        // connector.getSessionConfig().setReadBufferSize(4096);
//        connector.getSessionConfig().setTcpNoDelay(true);
        connector.getSessionConfig().setSoLinger(-1);
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
        chain.addLast("codec", new ProtocolCodecFilter(new CommonCodeFactory()));
        chain.addLast("threadpool", new ExecutorFilter(Executors.newCachedThreadPool()));
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

    public void reconnect(String addr) {
        IoSession session = null;
        while (session == null) {
            try {
                Thread.sleep(10000);
                String[] strings = addr.split(":");
                ConnectFuture cf = connector.connect(new InetSocketAddress(
                        strings[0], Integer.valueOf(strings[1])));
                cf.awaitUninterruptibly();
                session = cf.getSession();
            } catch (Exception e) {
                logger.error(String.format("reconnect faild [%s]", addr), e);
            }
        }
        session.setAttribute("address", addr);
        sessions.add(session);
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

    public void send(EsbMsg pack) {
        pack.setPackageid(Thread.currentThread().getId());
        pack.addTimetick(clientname, "connector", System.nanoTime());
        local.set(pack);
        if (callBack != null) {
            IoSession session = getNext();
            if (session != null)
                session.write(pack);
        }
    }

    public EsbMsg recv(long timeout) throws Exception {
        IoSession session = getNext();
        if (session != null) {
            EsbMsg orgin = local.get();
            long begin = System.currentTimeMillis();
            synchronized (orgin) {
                map.put(Thread.currentThread().getId(), orgin);
                session.write(orgin);
                orgin.wait(timeout);
            }
            long end = System.currentTimeMillis();
            if ((end - begin) > timeout) {
                throw new Exception(String.format("recv time out, session [%s]", session.toString()));
            }
            EsbMsg dest = map.get(orgin.getPackageid());
            if(dest.hashCode() == orgin.hashCode()){
                logger.error("系统被中断异常唤醒");
                return null;
            }
            return dest;
        }
        return null;
    }
    
    public List<EsbMsg> recvMulti(long timeout) throws Exception {
        IoSession session = getNext();
        if (session != null) {
            EsbMsg orgin = local.get();
            long begin = System.currentTimeMillis();
            synchronized (orgin) {
                map.put(Thread.currentThread().getId(), orgin);
                session.write(orgin);
                orgin.wait(timeout);
            }
            long end = System.currentTimeMillis();
            if ((end - begin) > timeout) {
                throw new Exception(String.format("recv time out, session [%s]", session.toString()));
            }
            if(orgin.getResponse().size()==0){
                logger.error("系统被中断异常唤醒");
                return null;
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
			            synchronized (orgin) {
			                map.put(pack.getPackageid(), pack);
			                orgin.notify();
			            }
		            }
	        	}else{
		            EsbMsg orgin = map.get(pack.getPackageid());
		            if(orgin != null){
			            synchronized (orgin) {
			            	orgin.getResponse().add(pack);
			            	if(pack.getCopyCount() == orgin.getResponse().size()){
			            		orgin.notify();
			            	}
			            }
		            }
	        	}
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
