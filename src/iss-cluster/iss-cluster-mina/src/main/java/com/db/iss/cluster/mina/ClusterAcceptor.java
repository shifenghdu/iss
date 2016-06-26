package com.db.iss.cluster.mina;

import com.db.iss.cluster.mina.codec.ClusterCodecFactory;
import com.db.iss.cluster.mina.handler.ServerMsgHandler;
import com.db.iss.core.plugin.AbstractTransportPlugin;
import com.db.iss.core.plugin.EsbMsg;
import com.db.iss.core.compressor.CompressorType;
import com.db.iss.core.serializer.SerializerType;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ClusterAcceptor {

    private NioSocketAcceptor acceptor;

    private int TIME_OUT = 25;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final int SIZE_128K = 131072;

    private final int RETRY_TIMES = 3;

    private Map<Long,IoSession> sessionMap = new ConcurrentHashMap<>();

    public ClusterAcceptor(SerializerType type, CompressorType compressorType, AbstractTransportPlugin plugin) {
        acceptor = new NioSocketAcceptor();
        acceptor.setReuseAddress(false);
        acceptor.getSessionConfig().setReuseAddress(false);
		acceptor.getSessionConfig().setReceiveBufferSize(SIZE_128K);
		acceptor.getSessionConfig().setReadBufferSize(SIZE_128K);
        acceptor.getSessionConfig().setSoLinger(-1);
        acceptor.setBacklog(10240);
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ClusterCodecFactory(type,compressorType)));
        acceptor.getFilterChain().addLast("pool", new ExecutorFilter(Runtime.getRuntime().availableProcessors() + 1));
        acceptor.setHandler(new ServerMsgHandler(plugin,this));
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, TIME_OUT);
    }

    public void bind(URL url) throws IOException {
        acceptor.bind(new InetSocketAddress(url.getHost(),url.getPort()));
    }

    public boolean write(URL url,EsbMsg msg){
        IoSession session = sessionMap.get(msg.getSessionId());
        int times = 0;
        while(times < RETRY_TIMES) {
            times ++;
            WriteFuture future = session.write(msg, new InetSocketAddress(url.getHost(), url.getPort()));
            future.awaitUninterruptibly();
            if(future.isWritten()){
                return true;
            }
        }
        session.close(true);
        return false;
    }

    @Override
    protected void finalize() throws Throwable {
        acceptor.unbind();
        acceptor.dispose();
        super.finalize();
    }

}
