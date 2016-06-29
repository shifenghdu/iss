package com.db.iss.cluster.mina;

import com.db.iss.cluster.mina.codec.ClusterCodecFactory;
import com.db.iss.cluster.mina.handler.ClientMsgHandler;
import com.db.iss.core.compressor.CompressorType;
import com.db.iss.core.plugin.AbstractTransportPlugin;
import com.db.iss.core.plugin.EsbMsg;
import com.db.iss.core.serializer.SerializerType;
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
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ClusterConnector {

    private NioSocketConnector connector = null;

    private int CONNECT_TIME_OUT = 5000;

    private final int RETRY_TIMES = 3;

    private final int SIZE_128K = 131072;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<String,IoSession> sessionMap = new ConcurrentHashMap<>();

    public ClusterConnector(SerializerType type, CompressorType compressorType, AbstractTransportPlugin plugin) {
        connector = new NioSocketConnector();
        connector.getSessionConfig().setReuseAddress(true);
		connector.getSessionConfig().setReceiveBufferSize(SIZE_128K);
		connector.getSessionConfig().setReadBufferSize(SIZE_128K);
        connector.getSessionConfig().setSoLinger(-1);
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
        chain.addLast("codec", new ProtocolCodecFilter(new ClusterCodecFactory(type,compressorType)));
        //chain.addLast("pool", new ExecutorFilter(Runtime.getRuntime().availableProcessors() + 1));
        connector.setHandler(new ClientMsgHandler(plugin,this));
        connector.setConnectTimeoutMillis(CONNECT_TIME_OUT);
    }

    public IoSession connect(String node,URL url) {
        ConnectFuture cf = connector.connect(new InetSocketAddress(url.getHost(), url.getPort()));
        cf.awaitUninterruptibly();
        IoSession session = cf.getSession();
        session.setAttribute("node",node);
        session.setAttribute("url",url);
        sessionMap.put(node,session);
        return session;
    }

    /**
     * 获取session
     * @param node
     * @param url
     * @return
     */
    public IoSession getSession(String node,URL url){
        IoSession session = sessionMap.get(node);
        if(session == null || session.isClosing()){
            synchronized (this) {
                session = sessionMap.get(node); //再次检查 避免并发情况下重复链接
                if(session == null || session.isClosing()) {
                    session = connect(node, url);
                }
            }
        }
        return session;
    }

    /**
     * 写入数据
     * @param node
     * @param url
     * @param msg
     * @return
     */
    public boolean write(String node,URL url,EsbMsg msg){
        IoSession session = getSession(node,url);
        int times = 0;
        while(times < RETRY_TIMES) {
            times ++;
            WriteFuture future = session.write(msg);
            future.awaitUninterruptibly();
            if (future.isWritten()) {
                return true;
            }
        }
        logger.error("url [{}] write request message[{}] error",url,msg);
        session.close(true);
        return false;
    }

    @Override
    protected void finalize() throws Throwable {
        connector.dispose();
        super.finalize();
    }

}
