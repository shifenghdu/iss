package com.db.iss.core.plugin;

/**
 * Created by andy on 16/6/21.
 * @author andy.shif
 * 业务调度插件
 */
public abstract class AbstractDispatcherPlugin extends AbstractMessagePlugin {

    public AbstractDispatcherPlugin(String name, String version) {
        super(name, version,ThreadMode.ISOLATE);
    }

    @Override
    protected void handleMessage(EsbMsg message, IMessagePlugin sender) throws PluginException {
        total.incrementAndGet();
        try {
            if(message.getMsgtype() == EsbMsg.MSGTYPE_REQ) {
                sender.transMessage(this.onRequest(message), this);
            }else{
                this.onResponse(message);
            }
        }catch (Throwable e){
            error.incrementAndGet();
            throw new PluginException(String.format("Plugin %s handler message failed",getNamespace()),e);
        }
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
     * 请求处理
     * @param message
     * @return
     * @throws PluginException
     */
    protected abstract EsbMsg onRequest(EsbMsg message) throws PluginException;


    /**
     *  返回处理
     * @param message
     * @return
     * @throws PluginException
     */
    protected abstract void onResponse(EsbMsg message) throws PluginException;


}
