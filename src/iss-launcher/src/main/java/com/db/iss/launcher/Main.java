package com.db.iss.launcher;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by andy on 2016/6/19.
 */
public class Main {

    public Main run(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:ctx-app.xml");
        context.getBean(IDemo.class);

        return this;
    }

    public static void main(String args[]) throws InterruptedException {
        Main main = new Main().run();
        synchronized (main) {
            main.wait();
        }
    }

}
