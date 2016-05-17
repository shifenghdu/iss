package com.ipharmacare.iss.core.tcpshort.mina;

import java.net.InetSocketAddress;

import com.ipharmacare.iss.core.tcpshort.MinaAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessAcceptor {

	private NioSocketAcceptor acceptor = null;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private MinaAcceptor owner = null;

	public AccessAcceptor(MinaAcceptor plugin) {
		this.owner = plugin;
		acceptor = new NioSocketAcceptor();
		acceptor.setReuseAddress(true);
		acceptor.getSessionConfig().setReuseAddress(true);
		acceptor.getSessionConfig().setTcpNoDelay(true);
		acceptor.getSessionConfig().setSoLinger(-1);
		acceptor.setBacklog(10240);
		acceptor.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new CommonCodeFactory(owner)));
		acceptor.setHandler(new AccessMsgHandler(owner));
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
