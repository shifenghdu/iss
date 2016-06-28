package com.db.iss.core.plugin;

import com.db.iss.core.alarm.IAlarm;
import com.db.iss.core.cm.IConfigurable;
import com.db.iss.core.alarm.AlarmLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by andy on 2016/6/19.
 * @author andy.shif
 * 插件基类
 */
public abstract class AbstractPlugin implements IPlugin,IConfigurable {

    //版本号
    protected String version;
    //插件状态
    protected PluginStatus status;
    //插件名称
    protected String name;
    //节点名称
    protected String node;
    //插件命名空间
    protected String namespace;
    //警报器
    private IAlarm alarm;

    protected Logger logger = LoggerFactory.getLogger(this.getClass());


    AbstractPlugin(String name,String version){
        this.name = name;
        this.version = version;
    }

    @Override
    public PluginStatus getStatus() {
        return status;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setNode(String node) {
        this.node = node;
        namespace = String.format("%s.plugins.%s",node,name);
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
