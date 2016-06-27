package com.db.iss.core.plugin;

import com.db.iss.core.alarm.AlarmLevel;
import com.db.iss.core.registry.IRegistry;

/**
 * Created by andy on 16/6/20.
 * @author andy.shif
 * 传输层插件
 */
public abstract class AbstractTransportPlugin extends AbstractMessagePlugin {

//    @Autowired
    protected IRegistry registry;

    public AbstractTransportPlugin(String name, String version) {
        super(name, version);
    }

    public AbstractTransportPlugin(String name, String version, ThreadMode mode) {
        super(name, version, mode);
    }

    @Override
    protected void handleMessage(EsbMsg message, IMessagePlugin sender) throws PluginException {
        this.writeEndpoint(message);
    }

    /**
     * 起始插件无需处理
     * @param message
     * @return
     */
    @Deprecated
    @Override
    protected EsbMsg onForward(EsbMsg message) throws PluginException {
        return null;
    }

    @Deprecated
    @Override
    protected EsbMsg onBackward(EsbMsg message) throws PluginException {
        return null;
    }

    protected abstract void writeEndpoint(EsbMsg message) throws PluginException;

    /**
     * 向next传递消息
     * @param message
     */
    public void forward(EsbMsg message){
        try {
            if (nextPlugin != null) {
                nextPlugin.transMessage(message, this);
            }
        }catch (Throwable e){
            String m = String.format("plugin %s deal message failed",namespace);
            sendAlarm(AlarmLevel.IMPORTANT,m);
            logger.error(m,e);
        }
    }

}
