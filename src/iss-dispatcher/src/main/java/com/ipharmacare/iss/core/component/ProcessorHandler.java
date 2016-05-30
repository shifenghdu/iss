package com.ipharmacare.iss.core.component;

import com.ipharmacare.iss.common.dispatch.IBizContext;
import com.ipharmacare.iss.common.dispatch.IBizProcessor;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * Created by andy on 2015/12/26.
 */
public class ProcessorHandler implements MethodInterceptor {
    private Class classz = null;
    private Method method = null;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String target = "doProcess";

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<?> target, Method method) {
        T t = null;
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(this.getClass().getClassLoader());
        enhancer.setSuperclass(IBizProcessor.class);
        enhancer.setCallback(this);
        t = (T) enhancer.create();
        this.classz = target;
        this.method = method;
        return t;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        try {
            if(!method.getName().equals(target)){
                return methodProxy.invokeSuper(o,objects);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("method {} {}", method.getName(),this.method.getName());
            }
            BizContextHolder.setBizContext((IBizContext) objects[0]);
            Object service = ApplicationContext.getBean(classz);
            Object parmas = null;
            if (objects[1] != null)
                parmas = PackerServcice.getInstance().unpack((byte[]) objects[1], this.method.getParameterTypes()[0]);

            Object result = null;
            if (logger.isDebugEnabled()) {
                logger.debug("invoke params_num {}  params {} ", this.method.getParameterTypes().length, parmas);
            }
            if (this.method.getParameterTypes().length == 1) {
                if (parmas == null)
                    throw new RuntimeException(String.format("服务参数丢失 [%s]", classz.getName()));
                result = this.method.invoke(service, new Object[]{parmas});
            } else if (this.method.getParameterTypes().length == 0) {
                result = this.method.invoke(service);
            } else {
                logger.error("服务不支持超过1个参数的调用");
                return null;
            }
            return PackerServcice.getInstance().pack(result);
        }catch (NoSuchBeanDefinitionException e){
            logger.error("未找到服务实现 [{}]", classz.getName());
            return null;
        }catch (Throwable e) {
            logger.error("服务处理异常", e);
            return null;
        }
    }
}
