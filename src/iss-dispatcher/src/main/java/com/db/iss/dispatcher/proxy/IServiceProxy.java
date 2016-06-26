package com.db.iss.dispatcher.proxy;

import com.db.iss.dispatcher.IMessageSend;

/**
 * Created by andy on 16/6/26.
 * @author andy.shif
 * 服务代理接口(管理 监控)
 */
public interface IServiceProxy {

    Long getDealCount();

    Long getErrorCount();

    void setTimeout(long timeout);

    void setIMessageSend(IMessageSend messageSend);

}
