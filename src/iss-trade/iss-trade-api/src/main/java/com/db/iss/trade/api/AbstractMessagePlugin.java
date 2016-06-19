package com.db.iss.trade.api;

import com.db.iss.trade.api.exception.PluginException;
import com.db.iss.trade.api.exception.SettingException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by andy on 2016/6/19.
 */
public abstract class AbstractMessagePlugin extends AbstractPlugin implements IMessagePlugin{

    //入口消息队列
    private LinkedBlockingQueue<Runnable> inQueue;
    //线程池
    private ExecutorService threadPool;

    private Integer threadCount;
    private Integer queueSize;
    private ThreadMode mode;


    protected enum ThreadMode {
        ISOLATE,SHARED;
    }

    AbstractMessagePlugin(String name, Integer version) {
        this(name,version,ThreadMode.SHARED);
    }

    AbstractMessagePlugin(String name,Integer version,ThreadMode mode){
        super(name,version);
        this.mode = mode;
    }

    @Override
    public void setSetting(Setting setting) throws SettingException {
        try {
            threadCount = Integer.valueOf(setting.getProperty(namespace + ".thread"));
            queueSize = Integer.valueOf(setting.getProperty(namespace + ".queue"));
            onStetting(setting);
        }catch (Throwable e){
            throw new SettingException("Setting configuration error",e);
        }
    }

    @Override
    public void start() throws PluginException {
        try {
            if (mode == ThreadMode.ISOLATE) {
                inQueue = new LinkedBlockingQueue<Runnable>(queueSize);
                threadPool = new ThreadPoolExecutor(threadCount, threadCount, 0, TimeUnit.SECONDS, inQueue);
            }
            onStart();
        }catch (Throwable e){
            throw new PluginException("Plugin " + getNamespace() + " start failed",e);
        }finally {
            if(threadPool != null){
                threadPool.shutdown();
            }
        }
    }

    @Override
    public void stop() throws PluginException {
        try {
            onStop();
            if (mode == ThreadMode.ISOLATE) {
                threadPool.shutdown();
            }
        }catch (Throwable e){
            throw new PluginException("Plugin " + getNamespace() + " stop failed",e);
        }finally {
            if(threadPool != null){
                threadPool.shutdown();
            }
        }
    }

    /**
     * 子类实现
     */
    protected abstract void onStart();
    protected abstract void onStop();
    protected abstract void onStetting(Setting setting);
    protected abstract void onMessage(EsbMsg message);
}
