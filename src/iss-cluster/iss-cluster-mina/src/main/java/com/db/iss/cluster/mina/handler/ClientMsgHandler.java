package com.db.iss.cluster.mina.handler;

import com.db.iss.cluster.mina.ClusterConnector;
import com.db.iss.core.plugin.AbstractTransportPlugin;
import com.db.iss.core.plugin.EsbMsg;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientMsgHandler extends IoHandlerAdapter {

    private final int IDLE = 60 * 30; //连接存活时间 30min

    private Logger logger = LoggerFactory.getLogger(getClass());

    private AbstractTransportPlugin plugin;

    private ClusterConnector connector;

    public ClientMsgHandler(AbstractTransportPlugin plugin, ClusterConnector connector) {
        this.plugin = plugin;
        this.connector = connector;
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        if (session != null) {
            logger.warn("exceptionCaught [{}]", session);
            session.close(true);
        }
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
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        session.close(true); //超出存活时间关闭连接
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, IDLE);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        logger.warn("sessionClosed [{}]", session);
    }
}
