package com.db.iss.core.tcpshort.mina;

import java.util.concurrent.atomic.AtomicLong;

import com.db.iss.core.tcpshort.MinaAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.db.iss.common.esb.EsbMsg;
import com.db.iss.common.plugin.IPlugin;

public class AccessMsgHandler extends IoHandlerAdapter {

//	private final int IDLE = 60;

	private Logger logger = LoggerFactory.getLogger(getClass());

	protected MinaAcceptor owner = null;

	public static AtomicLong count = new AtomicLong(0);

	public AccessMsgHandler(IPlugin plugin) {
		this.owner = (MinaAcceptor) plugin;
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		logger.error(String.format("session[%s] cause an exception", session),
				cause);
	}

	@Override
	public void sessionCreated(IoSession session) {

	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		// System.err.println("messageReceived");
		long current = count.incrementAndGet();
		EsbMsg pack = new EsbMsg();
		pack.setSystemid(1000);
		pack.setFunctionid(1001);
		pack.setSendname(owner.getPluginName());
		pack.setSendarg(String.valueOf(session.getId()));
		pack.addTimetick(owner.getNodeName(), owner.getPluginName(),
				System.nanoTime());
		pack.setContent((byte[]) message);
		owner.addSession(session);
		if (current % 1000 == 0) {
			logger.info(String.format("TcpShort收到消息数[%d]", current));
		}
		owner.sendMsg(pack);

	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {

	}

	@SuppressWarnings("deprecation")
	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		session.close();
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {

	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE,
				owner.getTimeout());
	}

}
