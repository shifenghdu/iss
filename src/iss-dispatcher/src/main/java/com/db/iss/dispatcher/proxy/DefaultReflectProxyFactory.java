package com.db.iss.dispatcher.proxy;

import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andy on 16/6/25.
 * @author andy.shif
 * 字节码反射动态代理工厂
 */
@Service
public class DefaultReflectProxyFactory implements IReflectProxyFactory {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<String,IReflectProxy> proxyMap = new ConcurrentHashMap<>();

    @Override
    public IReflectProxy getProxy(Method method, Object target) throws Exception{
        String proxyName = String.format("%s#%s$Proxy",target.getClass().getName(),method.getName());
        IReflectProxy proxy = proxyMap.get(proxyName);
        if(proxy != null){
            return proxy;
        }
        proxy = createPoxy(proxyName,method,target);
        proxyMap.put(proxyName,proxy);
        return proxy;
    }


    public IReflectProxy createPoxy(String proxyName, Method method, Object target) throws Exception{
        if(logger.isDebugEnabled()){
            logger.debug("createPoxy {}",proxyName);
        }
        //创建代理对象
        ClassPool pool = ClassPool.getDefault();
        CtClass proxyClass = pool.makeClass(proxyName);
        proxyClass.addInterface(pool.get(IReflectProxy.class.getName()));
        CtClass targetClass = pool.get(target.getClass().getName());

        //设置被代理对象
        CtField field = new CtField(targetClass, "target", proxyClass);
        proxyClass.addField(field);

        //添加默认空构造
        CtConstructor constructor = new CtConstructor(new CtClass[]{}, proxyClass);
        constructor.setBody("{}");
        proxyClass.addConstructor(constructor);


        //添加设置被代理对象方法
        CtMethod m = new CtMethod(CtClass.voidType, "setTarget",
                new CtClass[] { pool.get("java.lang.Object") }, proxyClass);
        m.setBody("target = (" + target.getClass().getName() + ")$1;");
        proxyClass.addMethod(m);

        //构建反射函数
        StringBuffer buffer = new StringBuffer();
        buildMethod(buffer,target,method);
        logger.debug("proxy method code: {}",buffer.toString());
        m = CtNewMethod.make(buffer.toString(),proxyClass);
        proxyClass.addMethod(m);

        //实例化代理对象
        IReflectProxy proxy = (IReflectProxy) proxyClass.toClass().newInstance();
        proxy.setTarget(target);
        return proxy;
    }


    /**
     * 构建反射调用函数代码
     * @param buffer
     * @param object
     * @param method
     * @throws NoSuchMethodException
     */
    private void buildMethod(StringBuffer buffer,Object object,Method method) throws NoSuchMethodException{
        buffer.append("public Object invoke (Object[] args) throws Throwable {");
        object.getClass().getDeclaredMethod(method.getName(),method.getParameterTypes());
        if(method.getReturnType() == void.class){
            buffer.append(" target.");
        }else {
            buffer.append(" return target.");
        }
        buffer.append(method.getName());
        buffer.append(" ( ");
        Class<?>[] paramTypes = method.getParameterTypes();
        int counter = 0;
        for(Class<?> paramType : paramTypes){
            buffer.append("(");
            addPrefix(paramType.getName(),buffer);
            buffer.append("args[");
            buffer.append(counter);
            buffer.append("])");
            addSuffix(paramType.getName(),buffer);
            if(counter != paramTypes.length-1){
                buffer.append(",");
            }
            counter++;
        }
        buffer.append(" );");

        if(method.getReturnType() == void.class){
            buffer.append(" return null; }");
        }else{
            buffer.append(" } ");
        }
    }

    /**
     * 类型转换Prefix
     * @param name
     * @param buffer
     */
    private void addPrefix(String name,StringBuffer buffer){
        if(name.equals("int")){
            buffer.append("(Integer)");
        }else if(name.equals("byte")){
            buffer.append("(Byte)");
        }else if(name.equals("short")){
            buffer.append("(Short)");
        }else if(name.equals("long")){
            buffer.append("(Long)");
        }else if(name.equals("float")){
            buffer.append("(Float)");
        } else if(name.equals("double")){
            buffer.append("(Double)");
        } else if(name.equals("char")){
            buffer.append("(Character)");
        } else if(name.equals("boolean")){
            buffer.append("(Boolean)");
        } else {
            buffer.append("(");
            buffer.append(name);
            buffer.append(")");
        }
    }

    /**
     * 类型转换Suffix
     * @param name
     * @param buffer
     */
    private void addSuffix(String name,StringBuffer buffer){
        if(name.equals("int")){
            buffer.append(".intValue()");
        }else if(name.equals("byte")){
            buffer.append(".byteValue()");
        }else if(name.equals("short")){
            buffer.append(".shortValue()");
        }else if(name.equals("long")){
            buffer.append(".longValue()");
        }else if(name.equals("float")){
            buffer.append(".floatValue()");
        } else if(name.equals("double")){
            buffer.append(".doubleValue()");
        } else if(name.equals("char")){
            buffer.append(".charValue()");
        } else if(name.equals("boolean")){
            buffer.append(".booleanValue()");
        } else {

        }
    }
}
