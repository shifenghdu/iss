package com.db.iss.trade.api.plugin;

import com.db.iss.trade.api.exception.PluginException;

/**
 * Created by andy on 16/6/21.
 * @author andy.shif
 * 业务调度插件
 */
public abstract class AbstractDispatcherPlugin extends AbstractMessagePlugin {

    AbstractDispatcherPlugin(String name, Integer version) {
        super(name, version,ThreadMode.ISOLATE);
    }

    @Override
    protected void handleMessage(EsbMsg message, IMessagePlugin sender) throws PluginException {
        sender.transMessage(this.onHandler(message),this);
    }


    /**
     * 结束插件 无需实现
     * @param message
     * @return
     * @throws PluginException
     */
    @Deprecated
    @Override
    protected EsbMsg onForward(EsbMsg message) throws PluginException {
        return null;
    }

    /**
     * 结束插件 无需实现
     * @param message
     * @return
     * @throws PluginException
     */
    @Deprecated
    @Override
    protected EsbMsg onBackward(EsbMsg message) throws PluginException {
        return null;
    }

    /**
     * 向 pre 传递消息
     * @param message
     * @throws PluginException
     */
    protected void backward(EsbMsg message) throws PluginException{
        if(prePlugin != null){
            prePlugin.transMessage(message,this);
        }
    }

    /**
     * 业务处理
     * @param messgae
     * @return
     * @throws PluginException
     */
    protected abstract EsbMsg onHandler(EsbMsg messgae) throws PluginException;


}
