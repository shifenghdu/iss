package com.ipharmacare.iss.core.cluster.mina;

import com.ipharmacare.iss.common.esb.EsbMsg;
import com.ipharmacare.iss.common.plugin.IPlugin;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


public class ClientMsgHandler extends ServerMsgHandler {

    private EsbMsg heatbeatMsg;

    private final int IDLE = 10;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public ClientMsgHandler(IPlugin plugin) {
        super(plugin);
        heatbeatMsg = new EsbMsg();
        heatbeatMsg.setMsgtype(EsbMsg.MSGTYPE_CLUSTER);
        heatbeatMsg.setFunctionid(1);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        MDC.put("node", owner.getNodeName());
        if (session != null) {
            logger.warn("exceptionCaught reconnect [{}]", session);
            session.close(true);
        }
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        MDC.put("node", owner.getNodeName());
        EsbMsg pack = (EsbMsg) message;
        if (pack.getMsgtype() == EsbMsg.MSGTYPE_CLUSTER) {

        } else {
            pack.addTimetick(owner.getNodeName(), owner.getPluginName(),
                    System.nanoTime());
            pack.setNextSession(session.getId());
            owner.sendMsg(pack);
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        if(session != null && !session.isClosing()) {
            session.write(heatbeatMsg);
        }
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        if(session != null) {
            session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, IDLE);
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        MDC.put("node", owner.getNodeName());
        if(session != null) {
            logger.warn("sessionClosed reconnect [{}]", session);
            owner.reconnect(session.getAttribute("nodename").toString(), session
                    .getAttribute("address").toString());
        }else {
            logger.warn("sessionClosed reconnect [{}]", session);
        }
    }
}
