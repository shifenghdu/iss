package com.db.iss.dispatcher.spring;

import com.db.iss.dispatcher.proxy.MethodProxyFactory;
import com.db.iss.dispatcher.proxy.IMethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andy on 16/6/25.
 * @author andy.shif
 * spring 服务容器
 */
@Service
public class ServiceMapper implements ApplicationContextAware {

    //代理映射map
    private Map<String,IMethodProxy> proxyMap = new ConcurrentHashMap<>();
    //spring上下文
    private ApplicationContext context;

    private Map<String,Class[]> paramsMap = new ConcurrentHashMap<>();

    @Autowired
    private MethodProxyFactory proxyFactory;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * 注册服务
     * @param method
     * @param inter
     * @throws Exception
     */
    public void register(Method method,Class<?> inter) throws Exception {
        Object impl = context.getBean(inter);
        String key = String.format("%s#%s",inter.getName(),method.getName());
        proxyMap.put(key, proxyFactory.getProxy(method,impl));
        paramsMap.put(key,method.getParameterTypes());
    }

    /**
     * 反射调用服务
     * @param namespace
     * @param method
     * @param args
     * @return
     */
    public Object invokeService(String namespace,String method,Object[] args) throws Throwable{
        IMethodProxy proxy = proxyMap.get(String.format("%s#%s",namespace,method));
        long start = System.currentTimeMillis();
        Object result = proxy.invoke(args);
        long end = System.currentTimeMillis();
        if(logger.isDebugEnabled()){
            logger.debug("spring reflect time [{}]",(end-start));
        }
        return result;
    }

    /**
     * 获取服务参数列表
     * @param namespace
     * @param method
     * @return
     */
    public Class[] getParamTypes(String namespace,String method){
        return paramsMap.get(String.format("%s#%s",namespace,method));
    }



}
