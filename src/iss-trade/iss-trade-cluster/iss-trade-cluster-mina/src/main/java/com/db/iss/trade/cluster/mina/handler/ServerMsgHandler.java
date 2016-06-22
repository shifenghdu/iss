package com.db.iss.trade.cluster.mina.handler;

import com.db.iss.trade.api.plugin.AbstractTransportPlugin;
import com.db.iss.trade.api.plugin.EsbMsg;
import com.db.iss.trade.cluster.mina.ClusterAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class ServerMsgHandler extends IoHandlerAdapter {

    private final int IDLE = 25;

    private final String INNER_NAMESPACE="com.db.iss.cluster";

    private final String HEARTBEAT_METHOD="heartbeat";

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
        MDC.put("node", plugin.getNode());
        if (session != null) {
            session.close(true);
            logger.error(String.format("session[%s] cause an exception", session),
                    cause);
        }
    }

    @Override
    public void sessionCreated(IoSession session) {

    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        MDC.put("node", plugin.getNode());
        EsbMsg pack = (EsbMsg) message;
        if (pack.getMsgtype() == EsbMsg.MSGTYPE_CLUSTER) {
            if (pack.getNamespace().equals(INNER_NAMESPACE) && pack.getMethod().equals(HEARTBEAT_METHOD)) {
                session.write(message);
            }
        } else {
            pack.setSessionId(session.getId());
            plugin.forward(pack);
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        if (session != null) {
            session.close(true);
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        MDC.put("node", plugin.getNode());
        if (session != null) {
            session.close(true);
            logger.warn(String.format("session [%s] idle disconnect", session));
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {

    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, IDLE);
    }

}
