package com.db.iss.dispatcher;

import com.db.iss.core.plugin.EsbMsg;
import com.db.iss.core.plugin.PluginException;
import com.db.iss.dispatcher.future.IFuture;

/**
 * Created by andy on 16/6/26.
 * @author andy.shif
 * 消息发送接口
 */
public interface IMessageSend {

    IFuture<EsbMsg> send(EsbMsg message) throws PluginException;

}
