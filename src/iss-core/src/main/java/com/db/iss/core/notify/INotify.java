package com.db.iss.core.notify;

/**
 * Created by andy on 16/6/20.
 * @author andy.shif
 * 消息发布接口
 */
public interface INotify {

    /**
     * 设置namespace
     * @param namespace
     */
    void setNamespace(String namespace);

    void publish(String topic,byte[] message);

    void subscribe(String topic,INotifyHandler handler);

}
