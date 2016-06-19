package com.db.iss.core.cluster.mina;

import java.net.InetSocketAddress;

import com.db.iss.core.cluster.MinaCluster;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;


public class ClusterConnector {

    private NioSocketConnector connector = null;

    private int CONNECT_TIME_OUT = 5000;

    private MinaCluster owner = null;

    public ClusterConnector(MinaCluster plugin) {
        this.owner = plugin;
        connector = new NioSocketConnector();
        connector.getSessionConfig().setReuseAddress(true);
//		connector.getSessionConfig().setReceiveBufferSize(4096);
//		connector.getSessionConfig().setReadBufferSize(4096);
//        connector.getSessionConfig().setTcpNoDelay(true);
        connector.getSessionConfig().setSoLinger(-1);
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
        chain.addLast("codec", new ProtocolCodecFilter(owner.getCodecFactory()));
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
