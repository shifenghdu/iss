package com.db.iss.trade.dispatcher.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author andy.shif
 * 远程服务注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Remote {

    /**
     * 作者
     * @return
     */
    String author() default "";

    /**
     * 描述信息
     * @return
     */
    String describe() default "";


    /**
     * 接口版本
     * @return
     */
    String version()  default "";

}

