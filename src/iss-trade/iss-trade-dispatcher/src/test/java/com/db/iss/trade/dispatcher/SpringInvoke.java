package com.db.iss.trade.dispatcher;

import com.db.iss.trade.api.cm.IConfigurable;
import com.db.iss.trade.api.cm.Setting;
import com.db.iss.trade.api.plugin.EsbMsg;
import com.db.iss.trade.api.plugin.IMessagePlugin;
import com.db.iss.trade.api.serializer.ISerializer;
import com.db.iss.trade.api.serializer.SerializerFactory;
import com.db.iss.trade.api.serializer.SerializerType;
import com.db.iss.trade.dispatcher.annotation.Remote;
import com.db.iss.trade.dispatcher.proxy.IMethodProxy;
import com.db.iss.trade.dispatcher.proxy.IMethodProxyFactory;
import com.db.iss.trade.dispatcher.proxy.MethodProxyFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by apple on 16/6/25.
 */
@Service
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-ctx.xml")
public class SpringInvoke implements ApplicationContextAware {

    private ApplicationContext context;

    @Autowired
    private IMethodProxyFactory methodProxyFactory;

    @Autowired
    private IMessagePlugin messagePlugin;

    private SerializerFactory factory = new SerializerFactory();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Test
    public void proxyTests() throws Throwable {
        try {
            IMethodProxyFactory factory = methodProxyFactory;
            Class type = Class.forName("com.db.iss.trade.dispatcher.IDemo");
            Object object = context.getBean(type);
            Method method = object.getClass().getDeclaredMethod("call",new Class[]{int.class,int.class,Object.class});
            IMethodProxy invoke = factory.getProxy(method,object);

            long start = System.currentTimeMillis();
            for(int i=0; i< 10000000; i++) {
                invoke.invoke(new Object[]{1, 2, type});
            }
            long end = System.currentTimeMillis();
            System.out.println("bytecode " + (end - start) + " ms");

            start = System.currentTimeMillis();
            for(int i=0; i< 10000000; i++) {
                method.invoke(object, new Object[]{1, 2, type});
            }
            end = System.currentTimeMillis();
            System.out.println("reflect " + (end - start) + " ms");

            start = System.currentTimeMillis();
            for(int i=0; i< 10000000; i++) {
                ((IDemo) object).call(1, 2, "123");
            }
            end = System.currentTimeMillis();
            System.out.println("direct " + (end - start) + " ms");


            method = object.getClass().getDeclaredMethod("get",null);
            invoke = factory.getProxy(method,object);
            invoke.invoke(null);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void scanTest(){
        Map<String,Object> beanMap = context.getBeansWithAnnotation(Remote.class);
        if(beanMap != null){
            for(Map.Entry<String,Object> entry : beanMap.entrySet()){
                Object object = entry.getValue();
                Class<?>[] inters = object.getClass().getInterfaces();
                for(Class inter : inters) {
                    System.out.println(inter);
                }
            }
        }
    }

    @Test
    public void serviceInvoke() throws Exception {
        messagePlugin.start();
        Setting setting = new Setting();
        setting.setProperty("serializer","mspack");
        setting.setProperty("dispatcher.thread","10");
        setting.setProperty("dispatcher.queue","1000");
        ((IConfigurable) messagePlugin).setSetting(setting);
        int TIMES = 1000000;
        long start = System.currentTimeMillis();
        for(int i=0; i<TIMES; i++) {
            EsbMsg msg = new EsbMsg();
            msg.setNamespace(IDemo.class.getName());
            msg.setMethod("call");
            ISerializer serializer = factory.getSerializer(SerializerType.MSGPACK);
            List<byte[]> params = new ArrayList<>();
            params.add(serializer.encode(1));
            params.add(serializer.encode(2));
            params.add(serializer.encode("andy.shif"));
            msg.setContent(params);
            messagePlugin.transMessage(msg, null);
        }
        long end = System.currentTimeMillis();
        logger.error("time [{}] tps",TIMES/(end - start)*1000);
        Thread.sleep(1000);
    }

    @Test
    public void serializerTest() throws Exception {
        EsbMsg esbMsg = new EsbMsg();
        ISerializer serializer = factory.getSerializer(SerializerType.MSGPACK);
        int TIMES = 1000000;

        long start = System.nanoTime();

        for(int i=0 ; i< TIMES; i++) {
            byte[] msg = serializer.encode(esbMsg);
            EsbMsg object = (EsbMsg) serializer.decode(msg, EsbMsg.class);
        }

        long end = System.nanoTime();

        logger.debug("time [{}] ps",(end - start)/TIMES);


    }
}
