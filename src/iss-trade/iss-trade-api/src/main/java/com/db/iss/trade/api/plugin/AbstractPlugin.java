package com.db.iss.trade.api.plugin;

import com.db.iss.trade.api.alarm.IAlarm;
import com.db.iss.trade.api.cm.IConfigurable;
import com.db.iss.trade.api.enums.AlarmLevel;
import com.db.iss.trade.api.enums.PluginStatus;

/**
 * Created by andy on 2016/6/19.
 * @author andy.shif
 * 插件基类
 */
public abstract class AbstractPlugin implements IPlugin,IConfigurable {

    //版本号
    private Integer version;
    //插件状态
    private PluginStatus status;
    //插件名称
    private String name;
    //节点名称
    private String node;
    //插件命名空间
    protected final String namespace = String.format("%s.plugins.%s",node,name);
    //警报器
    private IAlarm alarm;


    AbstractPlugin(String name,Integer version){
        this.name = name;
        this.version = version;
    }

    @Override
    public PluginStatus getStatus() {
        return status;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setNode(String node) {
        this.node = node;
    }

    @Override
    public String getNode() {
        return node;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setAlarm(IAlarm alarm) {
        this.alarm = alarm;
    }

    /**
     * 发送警报
     * @param level
     * @param message
     * @param args
     */
    protected void sendAlarm(AlarmLevel level, String message, String ... args){
        if(alarm != null){
            StringBuffer sb = new StringBuffer("plugin ");
            sb.append(getNamespace());
            sb.append(" alarm message ");
            sb.append(message);
            alarm.sendAlarm(level,sb.toString(),args);
        }
    }
}
