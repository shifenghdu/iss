package com.db.iss.cluster.mina.handler;

import com.db.iss.cluster.mina.ClusterAcceptor;
import com.db.iss.core.plugin.AbstractTransportPlugin;
import com.db.iss.core.plugin.EsbMsg;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMsgHandler extends IoHandlerAdapter {

    private final int IDLE = 60 * 10; // 10min

    private Logger logger = LoggerFactory.getLogger(getClass());

    private AbstractTransportPlugin plugin;

    private ClusterAcceptor acceptor;

    public ServerMsgHandler(AbstractTransportPlugin plugin, ClusterAcceptor acceptor) {
        this.plugin = plugin;
        this.acceptor = acceptor;
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        if (session != null) {
            session.close(true);
            logger.error(String.format("session [%s] cause an exception", session), cause);
        }
    }

    @Override
    public void sessionCreated(IoSession session) {

    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        EsbMsg pack = (EsbMsg) message;
        if (pack.getMsgtype() != EsbMsg.MSGTYPE_CLUSTER) {
            pack.setSessionId(session.getId());
            plugin.forward(pack);
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        if (session != null) {
            session.close(true);
            acceptor.removeSession(session.getId());
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {

    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, IDLE);
        acceptor.addSession(session.getId(),session);
    }

}
