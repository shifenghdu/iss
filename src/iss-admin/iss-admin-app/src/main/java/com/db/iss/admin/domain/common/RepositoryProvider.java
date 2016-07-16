package com.db.iss.admin.domain.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Created by andy on 16/7/16.
 * @author andy.shif
 * 仓储provider
 */
@Service
@Lazy(false)
public class RepositoryProvider implements ApplicationContextAware{

    /**
     * spring上下文
     */
    private static ApplicationContext context;

    /**
     * 注入spring上下文
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * 获取仓储实现
     * @param type
     * @param <T>
     * @return
     */
    public static <T extends Repository> T getRepository(Class<T> type){
        if(context == null) {
//            throw new RuntimeException("spring context has not being initialed");
            return null;
        }
        return (T) context.getBean(type);
    }
}
