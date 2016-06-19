package com.db.iss.trade.api;

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
    void transMessage(EsbMsg msg);

}
