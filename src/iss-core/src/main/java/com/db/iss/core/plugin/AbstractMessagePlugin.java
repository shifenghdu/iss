package com.db.iss.core.plugin;

import com.db.iss.core.alarm.AlarmLevel;
import com.db.iss.core.cm.Setting;
import com.db.iss.core.cm.SettingException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by andy on 2016/6/19.
 * @author andy.shif
 * 流水线插件基类
 */
public abstract class AbstractMessagePlugin extends AbstractPlugin implements IMessagePlugin{

    //入口消息队列
    protected LinkedBlockingQueue<Runnable> inQueue;
    //线程池
    protected ExecutorService threadPool;
    //线程数
    protected Integer threadCount = 10;
    //队列大小
    protected Integer queueSize = 10000;
    //线程模型
    protected ThreadMode mode;
    //pre 组件
    protected IMessagePlugin prePlugin;
    //next 组件
    protected IMessagePlugin nextPlugin;
    //处理消息总量
    protected AtomicLong total = new AtomicLong(0);
    //失败处理数据量
    protected AtomicLong error = new AtomicLong(0);



    protected enum ThreadMode {
        ISOLATE,SHARED;
    }

    public AbstractMessagePlugin(String name, String version) {
        this(name,version,ThreadMode.SHARED);
    }

    public AbstractMessagePlugin(String name,String version,ThreadMode mode){
        super(name,version);
        this.mode = mode;
    }

    @Override
    public void setSetting(Setting setting) throws SettingException {
        try {
            threadCount = Integer.valueOf(setting.getProperty(name + ".thread"));
            queueSize = Integer.valueOf(setting.getProperty(name + ".queue"));
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
        }catch (Throwable e) {
            throw new PluginException("Plugin " + getNamespace() + " start failed", e);
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

    @Override
    public void setPre(IMessagePlugin plugin) {
        this.prePlugin = plugin;
    }

    @Override
    public void setNext(IMessagePlugin plugin) {
        this.nextPlugin = plugin;
    }

    @Override
    public void transMessage(EsbMsg msg, IMessagePlugin sender) throws PluginException {
        if(mode == ThreadMode.ISOLATE){
            if(threadPool != null){
                threadPool.submit(new ExecuteTask(msg,sender));
            }else{
                throw new PluginException("Plugin "+ getNamespace() + " Thread pool is not initialize");
            }
        }else{ // SHARED
            handleMessage(msg,sender);
        }
    }

    /**
     * 消息处理
     * @param message
     * @param sender
     */
    protected void handleMessage(EsbMsg message,IMessagePlugin sender) throws PluginException{
        total.incrementAndGet();//总数统计
        try {
            if (sender.equals(prePlugin)) { // pre -> next
                nextPlugin.transMessage(onForward(message), this);
            } else if (sender.equals(nextPlugin)) { // next -> pre
                prePlugin.transMessage(onBackward(message), this);
            }
        }catch (Throwable e){
            error.incrementAndGet();//失败统计
            throw new PluginException(String.format("Plugin [%s] handler message failed",getNamespace()),e);
        }
    }

    /**
     * 插件消息执行任务
     */
    protected class ExecuteTask implements Runnable{
        private EsbMsg message;
        private IMessagePlugin sender;

        ExecuteTask(EsbMsg message,IMessagePlugin sender){
            this.message = message;
            this.sender = sender;
        }

        @Override
        public void run() {
            try {
                handleMessage(message, sender);
            }catch (Throwable e){
                sendAlarm(AlarmLevel.IMPORTANT,"handler message failed %s",message.toString());
                logger.error("Plugin "+ getNamespace() + " handler message failed",e);
            }
        }
    }

    @Override
    public Long getDealCount() {
        return total.get();
    }

    @Override
    public Long getErrorCount() {
        return error.get();
    }

    @Override
    public Long getQueueCount() {
        return Long.valueOf(inQueue.size());
    }

    @Override
    public Long getQueueSize() {
        return Long.valueOf(queueSize);
    }

    /**
     * 子类实现
     */
    protected abstract void onStart() throws PluginException;
    protected abstract void onStop() throws PluginException;
    protected abstract void onStetting(Setting setting) throws SettingException;
    protected abstract EsbMsg onForward(EsbMsg message) throws PluginException;
    protected abstract EsbMsg onBackward(EsbMsg message) throws PluginException;
}
