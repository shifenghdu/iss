package com.ipharmacare.iss.core.cluster.mina;

import com.ipharmacare.iss.core.cluster.MinaCluster;
import com.ipharmacare.iss.core.cluster.keepalive.KeepAliveMessageFactoryImpl;
import com.ipharmacare.iss.core.cluster.keepalive.KeepAliveRequestTimeoutHandlerImpl;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;


public class ClusterConnector {

    private NioSocketConnector connector = null;

    private int CONNECT_TIME_OUT = 5000;

    private MinaCluster owner = null;

    private final int SIZE_128K = 131072;

    public ClusterConnector(MinaCluster plugin) {
        this.owner = plugin;
        connector = new NioSocketConnector();
        connector.getSessionConfig().setReuseAddress(true);
		connector.getSessionConfig().setReceiveBufferSize(SIZE_128K);
		connector.getSessionConfig().setReadBufferSize(SIZE_128K);
//        connector.getSessionConfig().setTcpNoDelay(true);
        connector.getSessionConfig().setSoLinger(-1);
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
        chain.addLast("codec", new ProtocolCodecFilter(owner.getCodecFactory()));
        KeepAliveMessageFactory keepAliveMessageFactory = new KeepAliveMessageFactoryImpl();
        KeepAliveFilter keepAliveFilter = new KeepAliveFilter(keepAliveMessageFactory);
        chain.addLast("keepalive",keepAliveFilter);
        chain.addLast("threadpool", new ExecutorFilter(Runtime.getRuntime().availableProcessors() + 1));
        connector.setHandler(new ClientMsgHandler(owner));
        connector.setConnectTimeoutMillis(CONNECT_TIME_OUT);
    }

    public IoSession connect(String addr) {
        String[] strings = addr.split(":");
        ConnectFuture cf = connector.connect(new InetSocketAddress(strings[0],
                Integer.valueOf(strings[1])));
        cf.awaitUninterruptibly();
        return cf.getSession();
    }

    @Override
    protected void finalize() throws Throwable {
        connector.dispose();
        super.finalize();
    }

}
