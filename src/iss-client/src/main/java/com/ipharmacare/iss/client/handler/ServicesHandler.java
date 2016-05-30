package com.ipharmacare.iss.client.handler;

import com.ipharmacare.iss.client.ClientProxy;
import com.ipharmacare.iss.client.PackerServcice;
import com.ipharmacare.iss.common.annotation.Function;
import com.ipharmacare.iss.common.annotation.Remote;
import com.ipharmacare.iss.common.dispatch.IBizContext;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andy on 2015/12/26.
 */
public class ServicesHandler implements MethodInterceptor {

    private IBizContext context = null;
    private Object proxy = null;
    private int systemid = 0;
    private ConcurrentHashMap<Integer, Integer> fMap = new ConcurrentHashMap<Integer, Integer>();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @SuppressWarnings("unchecked")
    public <T> T getProxy(IBizContext context, Class<?> classz) {
        try {
            if (proxy == null) {
                if(context == null){
                    throw new Exception("IBizContext 业务上下文为空");
                }
                Enhancer enhancer = new Enhancer();
                enhancer.setClassLoader(this.getClass().getClassLoader());
                enhancer.setSuperclass(classz);
                enhancer.setCallback(this);
                proxy = (T) enhancer.create();
                this.context = context;
                Remote remote = classz.getAnnotation(Remote.class);
                systemid = (Integer) remote.annotationType().getDeclaredMethod("system", null).invoke(remote, null);
            }
            return (T) proxy;
        } catch (Throwable e) {
            logger.error("创建代理失败",e);
            return null;
        }
    }

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        try {
            if(method.getName().equals("toString")){
                return String.format("%s[%d]",this.getClass().getName(),super.hashCode());
            }
            int functionid = 0;
            if (!fMap.contains(method.hashCode())) {
                Function function = method.getAnnotation(Function.class);
                if (function == null) {
                    throw new RuntimeException(String.format("服务未进行注解注册 [%s] ", method.getName()));
                }
                functionid = (Integer) function.annotationType().getDeclaredMethod("function", null).invoke(function, null);
                fMap.put(method.hashCode(), functionid);
            } else {
                functionid = fMap.get(method.hashCode());
            }
            byte[] req = null;
            if (objects.length >= 1) {
                req = PackerServcice.getInstance().pack(objects[0]);
            }
            long begin = System.currentTimeMillis();
            byte[] rsp = context.call(systemid, functionid, ClientProxy.getUserTag(), req);
            long end = System.currentTimeMillis();
            if (logger.isInfoEnabled()) {
                logger.info("调用服务[{}] 耗时 [{}] ms", method.getName(), (end - begin));
            }
            if (rsp != null) {
                return PackerServcice.getInstance().unpack(rsp, method.getReturnType());
            } else {
                logger.error("服务返回空数据 {}", method.getName());
                return null;
            }
        } catch (Throwable e) {
            logger.error("服务调用错误", e);
            return null;
        }
    }
}
