package com.ipharmacare.iss.core.dispatch;

import com.ipharmacare.iss.common.SystemConst;
import com.ipharmacare.iss.common.dispatch.IBizMgr;
import com.ipharmacare.iss.common.dispatch.IBizProcessor;
import com.ipharmacare.iss.common.dispatch.IBizRegister;
import com.ipharmacare.iss.common.esb.EsbMsg;
import com.ipharmacare.iss.common.util.FileUtil;
import com.ipharmacare.iss.common.util.JarFileUtil;
import com.ipharmacare.iss.core.Laucher;
import com.ipharmacare.iss.core.config.BaseConfig;
import com.ipharmacare.iss.core.router.IRouter;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.jar.JarFile;

@Service("p_dispatcher")
public class Dispatcher implements IDispacher, IBizRegister {

    public static final String CORE_POOL_SIZE = "corePoolSize";

    public static final String MAX_POOL_SIZE = "maxPoolSize";

    public static final String KEEP_ALIVE_TIME = "keepAliveTime";

    public static final String REQUEST_TIME_OUT = "timeout";

    public static final String REQUEST_QUEUE_SIZE = "queueSize";

    private ExecutorService threadPool = null;

    // 单位微秒
    private Integer reqTimeout;

    private String pluginName = "dispatcher";

    private String nodeName;

    @Autowired
    private IRouter router;

    // 业务处理映射
    private Map<String, IBizProcessor> bizProcessMap = new ConcurrentHashMap<String, IBizProcessor>();

    private Map<Long, EsbMsg> msgMap = new java.util.concurrent.ConcurrentHashMap<Long, EsbMsg>();

    @Autowired
    private BaseConfig config;

//	private BizExecutor executor;

    private Map<String, ClassLoader> cls = new ConcurrentHashMap<String, ClassLoader>();

    public String getPluginName() {
        return pluginName;
    }

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public boolean transMsg(EsbMsg pack) {
        try {
            pack.addTimetick(nodeName, pluginName, System.nanoTime());
            dispatch(pack);
            return true;
        } catch (Exception e) {
            logger.error("业务调度出错", e);
            pack.changeToResponse();
            pack.setRetcode(SystemConst.ESB_BIZ_DISPATCH_ERR);
            pack.setRetmsg(SystemConst.ESB_BIZ_DISPATCH_ERR_MSG);
            router.transMsg(pack);
            return false;
        }
    }

    @Override
    public void onStop() {
        threadPool.shutdown();
    }

    // 分派任务给各个业务处理器
    public void dispatch(EsbMsg esbMsg) throws InterruptedException {
        IBizProcessor processor = bizProcessMap.get(String.format("%d#%d", esbMsg.getSystemid(), esbMsg.getFunctionid()));
        // 判断消息是否是请求消息,如请求放入线程池处理
        if (esbMsg.getMsgtype() == EsbMsg.MSGTYPE_REQ) {
            if(processor == null){
                throw new RuntimeException(String.format("未注册对应业务实现：system[%d] function[%d]",esbMsg.getSystemid(),esbMsg.getFunctionid()));
            }
            BizExecutor executor = new BizExecutor(this, router);
            executor.setTimeout(reqTimeout);
            executor.setProcessor(processor);
            executor.setReqmsg(esbMsg);
            threadPool.submit(executor);
        } else {
            if (esbMsg.getRetcode() == 0) {
                Long threadId = Long.valueOf((esbMsg.getSendarg()));
                if (esbMsg.getCopyCount() <= 1 && !esbMsg.isCopySend()) {

                    EsbMsg oldMsg = msgMap.get(threadId);
                    if (oldMsg != null) {
                        synchronized (oldMsg) {
                            msgMap.put(threadId, esbMsg);
                            oldMsg.notify();
                        }
                    }
                } else {
                    EsbMsg oldMsg = msgMap.get(threadId);
                    if (oldMsg != null) {
                        synchronized (oldMsg) {
                            oldMsg.getResponse().add(esbMsg);
                            if (oldMsg.getResponse().size() == esbMsg.getCopyCount()) {
                                oldMsg.notify();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onStart(ApplicationContext context) {
        nodeName = config.getDocument().getRootElement().attributeValue("name");
        Element dispatcherEle = config.getPluginConfig("dispatcher");
        int corePoolSize = Integer.valueOf(null != dispatcherEle
                .attributeValue(CORE_POOL_SIZE) ? dispatcherEle
                .attributeValue(CORE_POOL_SIZE) : "0");
        int maxPoolSize = Integer.valueOf(null != dispatcherEle
                .attributeValue(MAX_POOL_SIZE) ? dispatcherEle
                .attributeValue(MAX_POOL_SIZE) : "0");
        Long keepAliveTime = Long.valueOf(null != dispatcherEle
                .attributeValue(KEEP_ALIVE_TIME) ? dispatcherEle
                .attributeValue(KEEP_ALIVE_TIME) : "0");
        reqTimeout = Integer.valueOf(null != dispatcherEle
                .attributeValue(REQUEST_TIME_OUT) ? dispatcherEle
                .attributeValue(REQUEST_TIME_OUT) : "0");

        int queueSize = Integer.valueOf(null != dispatcherEle
                .attributeValue(REQUEST_QUEUE_SIZE) ? dispatcherEle
                .attributeValue(REQUEST_QUEUE_SIZE) : "0");

        String url = String.format("%s/ext/",
                System.getProperty(Laucher.SYSPATH));

        threadPool = new ThreadPoolExecutor(corePoolSize != 0 ? corePoolSize
                : 10, maxPoolSize != 0 ? maxPoolSize : 20,
                keepAliveTime != 0 ? keepAliveTime : 1000, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(queueSize != 0 ? queueSize : 1000));
        try {
//            urlList.add(new URL("file:" + url));
            File f = new File(url);
            File[] files = f.listFiles();
            for (File file : files) {
                String name = file.getName().substring(0, file.getName().lastIndexOf("."));
                boolean isLoad = false;
                List<Element> componentsList = dispatcherEle.element(
                        "bizcomponents").elements("bizcomponent");
                for (Element e : componentsList) {
                    String jar = e.attributeValue("jar");
                    if (jar.equals(name)) {
                        isLoad = true;
                        break;
                    }
                }
                if (file.exists() && isLoad) {
                    ArrayList<URL> urlList = new ArrayList<URL>();
                    JarFile jarFile = new JarFile(file);
                    FileUtil.removeDir(String.format("%s/work/%s/",
                            System.getProperty(Laucher.SYSPATH), name));
                    JarFileUtil.unzip(file.getAbsolutePath(), String.format("%s/work/%s/",
                            System.getProperty(Laucher.SYSPATH), name));
                    urlList.add(new URL("file:" + String.format("%s/work/%s/",
                            System.getProperty(Laucher.SYSPATH), name)));
                    File libs = new File(String.format("%s/work/%s/lib",
                            System.getProperty(Laucher.SYSPATH), name));
                    for (File lib : libs.listFiles()) {
                        if (lib.exists()) {
                            urlList.add(new URL("file:" + String.format("%s/work/%s/lib/%s",
                                    System.getProperty(Laucher.SYSPATH), name, lib.getName())));
                        }
                    }
//                    URLClassLoader cl = new URLClassLoader(
//                            urlList.toArray(new URL[]{}), getClass().getClassLoader());
                    URLClassLoader cl = new URLClassLoader(
                            urlList.toArray(new URL[]{}));
                    this.cls.put(name, cl);
                }
            }
            @SuppressWarnings("unchecked")
            List<Element> componentsList = dispatcherEle.element(
                    "bizcomponents").elements("bizcomponent");
            for (Element e : componentsList) {
                String className = e.attributeValue("class");
                String jar = e.attributeValue("jar");
                Thread.currentThread().setContextClassLoader(this.cls.get(jar));
                @SuppressWarnings("unchecked")
                Class<IBizMgr> componentClass = (Class<IBizMgr>) this.cls.get(jar)
                        .loadClass(className);

                IBizMgr bizMgr = componentClass.newInstance();
                bizMgr.onRegister(this,new BizExecutor(this,router,this.reqTimeout));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pollMsg(Long threadId, EsbMsg esbMsg) {
        msgMap.put(threadId, esbMsg);
    }

    @Override
    public EsbMsg getMsg(Long threadId) {
        return msgMap.get(threadId);
    }

    @Override
    public void register(int systemno, int functionid, IBizProcessor bizProcessor) {
        String key = String.format("%d#%d", systemno, functionid);
        if(bizProcessMap.containsKey(key)){
            logger.error("重复注册服务 System [{}]  Function [{}]",systemno,functionid);
        }else {
            bizProcessMap.put(key, bizProcessor);
        }
    }

    public String getNodeName() {
        return this.nodeName;
    }

}
