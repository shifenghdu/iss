package com.db.iss.launcher;

import com.db.iss.common.loader.DirectoryClassLoader;
import org.msgpack.annotation.MessagePackBeans;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

/**
 * Created by andy on 2016/6/19.
 */
public class Main {

    public Main run(String[] classPath){
        Thread.currentThread().setContextClassLoader(new DirectoryClassLoader(classPath,this.getClass().getClassLoader()) );
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:ctx-app.xml");
        Map<String,Object> map = context.getBeansWithAnnotation(MessagePackBeans.class);
        return this;
    }

    public static void main(String args[]){
        new Main().run(args);
    }

}
