package com.ipharmacare.iss.core.cluster.keepalive;

import com.ipharmacare.iss.common.esb.EsbMsg;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

/**
 * Created by andy on 2016/7/12.
 * mina客户端心跳工厂
 */
public class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {

    /**
     * 判断是否是心跳请求包
     * @param session
     * @param message
     * @return
     */
    @Override
    public boolean isRequest(IoSession session, Object message) {
        if(message != null
                &&((EsbMsg)message).getMsgtype() == EsbMsg.MSGTYPE_CLUSTER
                && ((EsbMsg)message).getFunctionid() == 3){
            return true;
        }
        return false;
    }

    /**
     * 判断是否是心跳返回包
     * @param session
     * @param message
     * @return
     */
    @Override
    public boolean isResponse(IoSession session, Object message) {
        if(message != null
                &&((EsbMsg)message).getMsgtype() == EsbMsg.MSGTYPE_CLUSTER
                && ((EsbMsg)message).getFunctionid() == 4){
            return true;
        }
        return false;
    }

    /**
     * 获取请求包
     * @param session
     * @return
     */
    @Override
    public Object getRequest(IoSession session) {
        EsbMsg request = new EsbMsg();
        request.setMsgtype(EsbMsg.MSGTYPE_CLUSTER);
        request.setFunctionid(3);
        return request;
    }

    /**
     * 获取返回包
     * @param session
     * @param request
     * @return
     */
    @Override
    public Object getResponse(IoSession session, Object request) {
        EsbMsg response = new EsbMsg();
        response.setMsgtype(EsbMsg.MSGTYPE_CLUSTER);
        response.setFunctionid(4);
        return request;
    }
}
