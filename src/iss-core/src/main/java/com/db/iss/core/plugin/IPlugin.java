package com.db.iss.core.plugin;

import com.db.iss.core.alarm.IAlarm;

/**
 * Created by andy on 2016/6/19.
 * @author andy.shif
 * 基础插件接口
 */
public interface IPlugin {

    /**
     * 版本号
     * @return
     */
    String getVersion();

    /**
     * 插件名称
     * @return
     */
    String getName();

    /**
     * 设置插件所属节点名称
     * @return
     */
    void setNode(String node);

    /**
     * 获取插件所属节点名称
     * @return
     */
    String getNode();


    /**
     * 插件状态
     * @return
     */
    PluginStatus getStatus();

    /**
     * 获取插件命名空间
     * @return
     */
    String getNamespace();


    /**
     * 插件启动
     * @throws PluginException
     */
    void start() throws PluginException;

    /**
     * 插件关闭
     * @throws PluginException
     */
    void stop() throws PluginException;

    /**
     * 设置警报器
     * @param alarm
     */
    void setAlarm(IAlarm alarm);

}
