package com.ipharmacare.iss.core.cluster.mina;

import com.ipharmacare.iss.common.esb.EsbMsg;
import com.ipharmacare.iss.common.plugin.IPlugin;
import com.ipharmacare.iss.core.cluster.MinaCluster;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class ServerMsgHandler extends IoHandlerAdapter {

//    private final int IDLE = 25;

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected MinaCluster owner = null;

    public ServerMsgHandler(IPlugin plugin) {
        this.owner = (MinaCluster) plugin;
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        MDC.put("node", owner.getNodeName());
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
        MDC.put("node", owner.getNodeName());
        EsbMsg pack = (EsbMsg) message;
        if (pack.getMsgtype() == EsbMsg.MSGTYPE_CLUSTER) {
            if (pack.getFunctionid() == 3) {
                pack.setFunctionid(4);
                session.write(pack);
            } else
            if (pack.getFunctionid() == 2
                    && pack.getRouteinfo().size() > 0) {
                String node = pack.popLastRouteInfo();
                owner.addNeighbor(node, session);
            }
        } else {
            pack.addTimetick(owner.getNodeName(), owner.getPluginName(),
                    System.nanoTime());
            pack.setNextSession(session.getId());
            owner.sendMsg(pack);
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
//        if (session != null) {
//            session.close(true);
//        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        MDC.put("node", owner.getNodeName());
        if (session != null) {
            session.close(true);
            logger.warn(String.format("Session[%s] idle disconnect", session));
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {

    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
//        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, IDLE);
    }

}
