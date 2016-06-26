package com.db.iss.dispatcher.spring;

import com.db.iss.dispatcher.annotation.Remote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by andy on 16/6/25.
 * @author andy.shif
 * spring服务扫描
 */
@Service
public class ServiceScanner implements ApplicationContextAware {

    @Autowired
    private ServiceMapper mapper;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ApplicationContext context;

//    @Autowired
//    private IRegistry registry;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        registerService();
    }

    private void registerService(){
        try {
            Map<String, Object> beanMap = context.getBeansWithAnnotation(Remote.class);
            if (beanMap != null) {
                for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
                    Object object = entry.getValue();
                    Class<?>[] inters = object.getClass().getInterfaces();
                    if (inters.length != 1) {
                        logger.warn("service {} none or more than one interface implement ", object.getClass().getName());
                        continue;
                    }
                    Class inter = inters[0];
                    Remote remote = (Remote) inter.getAnnotation(Remote.class);
                    String author = (String) remote.annotationType().getDeclaredMethod("author", null).invoke(remote, null);
                    String describe = (String) remote.annotationType().getDeclaredMethod("describe", null).invoke(remote, null);
                    String version = (String) remote.annotationType().getDeclaredMethod("version", null).invoke(remote, null);
                    Method[] methods = inter.getDeclaredMethods();
                    logger.debug("remote service [{}] version [{}] ",inter.getName(),version);
                    for (Method method : methods) {
                        try {
                            logger.trace("register service [{}] method [{}] ",inter.getName(),method.getName());
                            mapper.register(method, inter);
                        } catch (Throwable e) {
                            logger.error(String.format("register remote service %s failed", inter.getName()), e);
                        }
                    }
                }
            }
        }catch (Throwable e){
            logger.error("scan and register remote failed",e);
        }
    }
}
