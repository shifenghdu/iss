package com.db.iss.dispatcher;

import com.db.iss.core.plugin.EsbMsg;
import com.db.iss.dispatcher.IDemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by apple on 16/6/25.
 */
@Service
public class DemoService implements IDemo {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public String hello(String name) {
        logger.debug(name);
        return "hello " + name;
    }

    @Override
    public EsbMsg call(int a, int b, String s) {
        EsbMsg esbMsg = new EsbMsg();
        return esbMsg;
    }

    @Override
    public void get() {

    }
}