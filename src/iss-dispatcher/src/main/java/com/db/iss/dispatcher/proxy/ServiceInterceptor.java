package com.db.iss.dispatcher.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by andy on 16/6/26.
 * @author andy.shif
 * 服务代理拦截
 */
public class ServiceInterceptor extends AbstractServiceProxy implements MethodInterceptor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Class<?> inter;

    public ServiceInterceptor(Class<?> inter){
        this.inter = inter;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if(method.getDeclaringClass().equals(inter)){
            return this.invoke(inter.getName(),method.getName(),objects,method.getReturnType());
        }else{
            return method.invoke(this,objects);
        }
    }

}
