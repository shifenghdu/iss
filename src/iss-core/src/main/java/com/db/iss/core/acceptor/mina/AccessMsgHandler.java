package com.db.iss.core.acceptor.mina;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.db.iss.common.esb.EsbMsg;
import com.db.iss.common.plugin.IPlugin;
import com.db.iss.core.acceptor.MinaAcceptor;
import org.slf4j.MDC;

public class AccessMsgHandler extends IoHandlerAdapter {

    private final int IDLE = 25;

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected MinaAcceptor owner = null;

    public static AtomicLong count = new AtomicLong(0);

    public AccessMsgHandler(IPlugin plugin) {
        this.owner = (MinaAcceptor) plugin;
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        MDC.put("node", owner.getNodeName());
        if (session != null) {
            logger.error(String.format("session[%s] cause an exception", session),
                    cause);
            session.close(true);
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
            if (pack.getFunctionid() == 1) {
                session.write(message);
            }
        } else {
            long current = count.incrementAndGet();
            pack.setSendname(owner.getPluginName());
            pack.setSendarg(String.valueOf(session.getId()));
            pack.addTimetick(owner.getNodeName(), owner.getPluginName(),
                    System.nanoTime());
            owner.addSession(session);
            if (logger.isInfoEnabled()) {
                if (current % 1000 == 0) {
                    logger.info(String.format("Acceptor收到消息数[%d]", current));
                }
            }
            owner.sendMsg(pack);
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {

    }

    @SuppressWarnings("deprecation")
    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        MDC.put("node", owner.getNodeName());
        if (session != null) {
            session.close(true);
            logger.warn(String.format("Session[%s] disconnect", session));
        }
    }

    // 请求包发包前填写当前节点路径
    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, IDLE);
    }

}
