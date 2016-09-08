package com.ipharmacare.iss.core.cluster.keepalive;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by andy on 2016/7/12.
 * mina心跳超时回调
 */
public class KeepAliveRequestTimeoutHandlerImpl implements KeepAliveRequestTimeoutHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
        logger.error("receive heart beat time out session [{}]",session);
        session.close(true);
    }
}
