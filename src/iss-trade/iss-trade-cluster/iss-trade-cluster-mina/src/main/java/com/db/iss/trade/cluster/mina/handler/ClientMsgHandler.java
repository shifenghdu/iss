package com.db.iss.trade.cluster.mina.handler;

import com.db.iss.trade.api.plugin.AbstractTransportPlugin;
import com.db.iss.trade.api.plugin.EsbMsg;
import com.db.iss.trade.cluster.mina.ClusterConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.net.URL;


public class ClientMsgHandler extends IoHandlerAdapter {

    private EsbMsg heatbeatMsg;

    private final int IDLE = 10;

    private final String INNER_NAMESPACE="com.db.iss.cluster";

    private final String HEARTBEAT_METHOD="heartbeat";

    private Logger logger = LoggerFactory.getLogger(getClass());

    private AbstractTransportPlugin plugin;

    private ClusterConnector connector;

    public ClientMsgHandler(AbstractTransportPlugin plugin, ClusterConnector connector) {
        this.plugin = plugin;
        this.connector = connector;
        heatbeatMsg = new EsbMsg();
        heatbeatMsg.setMsgtype(EsbMsg.MSGTYPE_CLUSTER);
        heatbeatMsg.setNamespace(INNER_NAMESPACE);
        heatbeatMsg.setMethod(HEARTBEAT_METHOD);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        MDC.put("node", plugin.getNode());
        if (session != null) {
            logger.warn("exceptionCaught reconnect [{}]", session);
            session.close(true);
        }
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        MDC.put("node", plugin.getNode());
        EsbMsg pack = (EsbMsg) message;
        if (pack.getMsgtype() != EsbMsg.MSGTYPE_CLUSTER) {
            pack.setSessionId(session.getId());
            plugin.forward(pack);
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        session.write(heatbeatMsg);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, IDLE);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        MDC.put("node", plugin.getNode());
        logger.warn("sessionClosed reconnect [{}]", session);
        connector.reconnect(session.getAttribute("node").toString(), (URL)session.getAttribute("url"));
    }
}
