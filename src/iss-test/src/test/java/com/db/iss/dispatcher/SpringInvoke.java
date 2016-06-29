package com.db.iss.dispatcher;

import com.db.iss.core.cm.IConfigurable;
import com.db.iss.core.cm.Setting;
import com.db.iss.core.plugin.EsbMsg;
import com.db.iss.core.plugin.IMessagePlugin;
import com.db.iss.core.serializer.ISerializer;
import com.db.iss.core.serializer.SerializerFactory;
import com.db.iss.core.serializer.SerializerType;
import com.db.iss.dispatcher.annotation.Remote;
import com.db.iss.dispatcher.proxy.IServiceProxy;
import com.db.iss.dispatcher.proxy.reflect.IReflectProxy;
import com.db.iss.dispatcher.proxy.reflect.IReflectProxyFactory;
import com.db.iss.dispatcher.spring.IRemoteServiceProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by andy on 16/6/25.
 */
@Service
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-ctx.xml")
public class SpringInvoke implements ApplicationContextAware ,Runnable{

    private ApplicationContext context;

    @Autowired
    private IReflectProxyFactory methodProxyFactory;

    @Autowired
    private DispatcherPlugin messagePlugin;

    private SerializerFactory factory = new SerializerFactory();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IRemoteServiceProvider serviceProvider;

    private AtomicLong count;

    private CountDownLatch single;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Test
    public void proxyTests() throws Throwable {
        try {
            IReflectProxyFactory factory = methodProxyFactory;
            Class type = Class.forName("com.db.iss.dispatcher.IDemo");
            Object object = context.getBean(type);
            Method method = object.getClass().getDeclaredMethod("call",new Class[]{int.class,int.class,String.class});
            IReflectProxy invoke = factory.getProxy(method,object);

            long start = System.currentTimeMillis();
            for(int i=0; i< 10000000; i++) {
                invoke.invoke(new Object[]{1, 2, "123"});
            }
            long end = System.currentTimeMillis();
            System.out.println("bytecode " + (end - start) + " ms");

            start = System.currentTimeMillis();
            for(int i=0; i< 10000000; i++) {
                method.invoke(object, new Object[]{1, 2, "123"});
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
        Setting setting = new Setting();
        setting.setProperty("serializer","json");
        setting.setProperty("dispatcher.thread","10");
        setting.setProperty("dispatcher.queue","1000000");
        ((IConfigurable) messagePlugin).setSetting(setting);
        messagePlugin.start();
        int TIMES = 1000000;
        long start = System.currentTimeMillis();
        for(int i=0; i<TIMES; i++) {
            EsbMsg msg = new EsbMsg();
            msg.setNamespace(IDemo.class.getName());
            msg.setMethod("call");
            ISerializer serializer = factory.getSerializer(SerializerType.JSON);
            List<byte[]> params = new ArrayList<>();
            params.add(serializer.encode(1));
            params.add(serializer.encode(2));
            params.add(serializer.encode("andy.shif"));
            msg.setContent(params);
            messagePlugin.transMessage(msg, null);
        }
        long end = System.currentTimeMillis();
        logger.error("time [{}] tps",TIMES/(end - start)*1000);
        logger.error("deal count {}",messagePlugin.getDealCount());
        Thread.sleep(10000);
    }

    @Test
    public void serializerTest() throws Exception {
        SerializeObject esbMsg = new SerializeObject();
        int TIMES = 1000000;

        long start = System.currentTimeMillis();
        ISerializer serializer = factory.getSerializer(SerializerType.MSGPACK);
        for(int i=0 ; i< TIMES; i++) {
            byte[] msg = serializer.encode(esbMsg);
            SerializeObject object = (SerializeObject) serializer.decode(msg, SerializeObject.class);
        }

        long end = System.currentTimeMillis();

        logger.error("msgpack time [{}] tps",TIMES/(end - start)*1000);


        start = System.currentTimeMillis();
        serializer = factory.getSerializer(SerializerType.JSON);
        for(int i=0 ; i< TIMES; i++) {
            byte[] msg = serializer.encode(esbMsg);
            SerializeObject object = (SerializeObject) serializer.decode(msg, SerializeObject.class);
        }

        end = System.currentTimeMillis();

        logger.error("json time [{}] tps",TIMES/(end - start)*1000);
    }


    @Test
    public void serviceProxy() throws InterruptedException {
        int TIMES = 100000;
        int THREAD = 1;


        count = new AtomicLong(TIMES);
        single = new CountDownLatch(THREAD);


        long start = System.currentTimeMillis();

        for(int i=0 ;i< THREAD; i++){
            new Thread(this).start();
        }
        single.await();

        long end = System.currentTimeMillis();

        logger.error("invoke [{}] tps",TIMES/(end - start)*1000);

    }

    @Override
    public void run() {
        long cur = count.get();
        while(cur > 0){
            IDemo demo = serviceProvider.getService(IDemo.class);
            String response = demo.hello("andy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifshifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifshifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shifandy.shif");
            cur = count.decrementAndGet();
        }
        single.countDown();
    }
}
