package com.db.iss.trade.api.plugin;

import com.db.iss.trade.api.enums.AlarmLevel;
import com.db.iss.trade.api.exception.PluginException;

/**
 * Created by andy on 16/6/20.
 * @author andy.shif
 * 传输层插件
 */
public abstract class AbstractTransportPlugin extends AbstractMessagePlugin {

    AbstractTransportPlugin(String name, Integer version) {
        super(name, version);
    }

    AbstractTransportPlugin(String name, Integer version, ThreadMode mode) {
        super(name, version, mode);
    }

    @Override
    protected void handleMessage(EsbMsg message, IMessagePlugin sender) throws PluginException {
        this.onBackward(message);
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

    /**
     * 向next传递消息
     * @param message
     */
    protected void forward(EsbMsg message){
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
