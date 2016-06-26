package com.db.iss.core.plugin;

/**
 * Created by andy on 2016/6/19.
 * @author andy.shif
 * 消息插件
 */
public interface IMessagePlugin extends IPlugin{

    /**
     * 消息处理总数量
     * @return
     */
    Long getDealCount();

    /**
     * 消息处理失败数量
     * @return
     */
    Long getErrorCount();


    /**
     * 入口队列积压消息数量
     * @return
     */
    Long getQueueCount();

    /**
     * 入口队列大小
     * @return
     */
    Long getQueueSize();

    /**
     * 消息传递接口
     * @param msg
     */
    void transMessage(EsbMsg msg,IMessagePlugin sender) throws PluginException;

    /**
     * 设置前置插件
     * @param plugin
     */
    void setPre(IMessagePlugin plugin);

    /**
     * 设置后置插件
     * @param plugin
     */
    void setNext(IMessagePlugin plugin);

}
