package com.ipharmacare.iss.core.acceptor.mina;

import com.ipharmacare.iss.core.acceptor.MinaAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class AccessAcceptor {

    private NioSocketAcceptor acceptor = null;

    private int TIME_OUT = 10;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private MinaAcceptor owner = null;

    private final int SIZE_128K = 131072;

    public AccessAcceptor(MinaAcceptor plugin) {
        this.owner = plugin;
        acceptor = new NioSocketAcceptor();
        acceptor.setReuseAddress(true);
        acceptor.getSessionConfig().setReuseAddress(true);
        acceptor.getSessionConfig().setReceiveBufferSize(SIZE_128K);
        acceptor.getSessionConfig().setReadBufferSize(SIZE_128K);
//        acceptor.getSessionConfig().setTcpNoDelay(true);
        acceptor.getSessionConfig().setSoLinger(-1);
        acceptor.setBacklog(10240);
        acceptor.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(owner.getCodecFactory()));
        acceptor.getFilterChain().addLast("threadpool", new ExecutorFilter(Runtime.getRuntime().availableProcessors() + 1));
        acceptor.setHandler(new AccessMsgHandler(owner));
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, TIME_OUT);
    }

    public void bind(int port) {
        try {
            acceptor.bind(new InetSocketAddress(port));
        } catch (Exception e) {
            logger.error("Error bind cluster port", e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        acceptor.unbind();
        acceptor.dispose();
        super.finalize();
    }

}
