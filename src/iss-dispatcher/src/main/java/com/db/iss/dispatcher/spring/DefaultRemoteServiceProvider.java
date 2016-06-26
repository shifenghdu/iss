package com.db.iss.dispatcher.spring;

import com.db.iss.core.exception.RemoteException;
import com.db.iss.dispatcher.DispatcherPlugin;
import com.db.iss.dispatcher.IMessageSend;
import com.db.iss.dispatcher.proxy.AbstractServiceProxy;
import com.db.iss.dispatcher.proxy.IServiceProxy;
import com.db.iss.dispatcher.proxy.ServiceInterceptor;
import net.sf.cglib.proxy.Enhancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andy on 16/6/26.
 * @author andy.shif
 * 远程服务provider
 */
@Service
public class DefaultRemoteServiceProvider implements IRemoteServiceProvider{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<Class,Object> proxyMap = new ConcurrentHashMap<>();

    @Autowired
    private IMessageSend messageSend;

    @Override
    public <T> T getService(Class<T> type) {
        Object proxy = proxyMap.get(type);
        if(proxy == null){
            proxy = createService(type);
            proxyMap.put(type,proxy);
        }
        return (T)proxy;
    }

    @Override
    public IServiceProxy getServiceManager(Class<?> type) {
        return (IServiceProxy) getService(type);
    }

    private <T> T createService(Class<T> inter){
        try{
            Enhancer enhancer = new Enhancer();
            enhancer.setClassLoader(Thread.currentThread().getContextClassLoader());
            enhancer.setInterfaces(new Class[]{inter,IServiceProxy.class});
            enhancer.setCallback(new ServiceInterceptor(inter));
            T proxy = (T) enhancer.create();
            ((IServiceProxy)proxy).setIMessageSend(messageSend);
            return proxy;
        }catch (Throwable e){
            String info = String.format("create [%s] service proxy failed",inter.getName());
            logger.error(info,e);
            throw new RemoteException(info,e);
        }
    }

}
