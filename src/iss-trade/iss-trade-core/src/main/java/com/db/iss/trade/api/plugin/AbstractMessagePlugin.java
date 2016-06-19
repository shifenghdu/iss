package com.db.iss.trade.api.plugin;

import com.db.iss.trade.api.cm.Setting;
import com.db.iss.trade.api.enums.AlarmLevel;
import com.db.iss.trade.api.exception.PluginException;
import com.db.iss.trade.api.exception.SettingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private LinkedBlockingQueue<Runnable> inQueue;
    //线程池
    private ExecutorService threadPool;
    //线程数
    private Integer threadCount;
    //队列大小
    private Integer queueSize;
    //线程模型
    private ThreadMode mode;
    //pre 组件
    private IMessagePlugin prePlugin;
    //next 组件
    private IMessagePlugin nextPlugin;
    //处理消息总量
    private AtomicLong total;
    //失败处理数据量
    private AtomicLong error;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

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
    private void handleMessage(EsbMsg message,IMessagePlugin sender) throws PluginException{
        total.incrementAndGet();//总数统计
        try {
            if (sender.equals(prePlugin)) { // pre -> next
                nextPlugin.transMessage(onForward(message), this);
            } else if (sender.equals(nextPlugin)) { // next -> pre
                prePlugin.transMessage(onBackward(message), this);
            }
        }catch (Throwable e){
            error.incrementAndGet();//失败统计
            throw new PluginException("Plugin "+ getNamespace() + " handler message failed",e);
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
    protected abstract void onStart();
    protected abstract void onStop();
    protected abstract void onStetting(Setting setting);
    protected abstract EsbMsg onForward(EsbMsg message);
    protected abstract EsbMsg onBackward(EsbMsg message);
}
