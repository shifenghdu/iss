package com.db.iss.connector;

import java.util.concurrent.atomic.AtomicLong;

import com.db.iss.common.esb.EsbMsg;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientMsgHandler extends IoHandlerAdapter {

    private EsbMsg heatbeatMsg;

    private final int IDLE = 10;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Connector owner = null;

    public AtomicLong cout = new AtomicLong(0);

    public ClientMsgHandler(Connector connector) {
        this.owner = connector;
        heatbeatMsg = new EsbMsg();
        heatbeatMsg.setMsgtype(EsbMsg.MSGTYPE_CLUSTER);
        heatbeatMsg.setFunctionid(1);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        if (session != null) {
            logger.warn(String.format("exceptionCaught [%s]", session), cause);
            session.close(true);
        }
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        EsbMsg pack = (EsbMsg) message;
        if (pack.getMsgtype() == EsbMsg.MSGTYPE_CLUSTER) {

        } else {
            long current = cout.incrementAndGet();
            pack.addTimetick(owner.getClientname(), "connector",
                    System.nanoTime());
            owner.onReceived(pack);
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
        logger.warn("sessionClosed reconnect [{}]", session);
        Thread.sleep(1000);
        owner.reconnect(session.getAttribute("address").toString());
    }
}
