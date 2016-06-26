package com.db.iss.core.cm;

import com.db.iss.core.plugin.AbstractDispatcherPlugin;
import com.db.iss.core.plugin.AbstractTransportPlugin;
import com.db.iss.core.plugin.IMessagePlugin;
import com.db.iss.core.plugin.IPlugin;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andy on 16/6/26.
 * @author andy.shif
 * 配置读取及插件启动入口
 */
@Service
public class Bootstrap implements ApplicationContextAware {

    private ApplicationContext context;

    private String pipe = "cluster-mina | dispatcher";

    private Map<String,IMessagePlugin> pluginMap = new ConcurrentHashMap<>();


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        this.loadPlugins();
        this.onStart();
    }

    /**
     * 加载spring容器中所有插件
     */
    private void loadPlugins(){
        Map<String,IMessagePlugin> ps = this.context.getBeansOfType(IMessagePlugin.class);
        for(Map.Entry<String,IMessagePlugin> entry : ps.entrySet()){
            IMessagePlugin plugin = entry.getValue();
            pluginMap.put(plugin.getName(),plugin);
        }
    }

    /**
     * 启动配置
     */
    private void onStart() {
        String[] names = pipe.split("\\|");
        Setting setting = new Setting();
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("iss.properties"));
        } catch (IOException e) {
            System.out.println("load classpath iss.properties failed");
        }
        setting.setProperties(properties);
        String value = setting.getProperty(SettingKey.PIPE.getValue());
        if(value != null && !value.isEmpty()){
            pipe = value;
        }

        int current = 0;
        IMessagePlugin pre = null;
        for(String name : names){
            name = name.trim().toLowerCase();
            IMessagePlugin plugin = pluginMap.get(name);
            if(current == 0 && !(plugin instanceof AbstractTransportPlugin)){
                throw new RuntimeException("pipe head must transport plugin");
            }
            if(current == (names.length - 1) && !(plugin instanceof AbstractDispatcherPlugin)){
                throw new RuntimeException("pipe tail must dispatcher plugin");
            }
            try {
                ((IConfigurable) plugin).setSetting(setting);
                plugin.setPre(pre);
                plugin.start();

            }catch (Throwable e){
                e.printStackTrace();
            }
            if(pre != null){
                pre.setNext(plugin);
            }
            pre = plugin;
            current ++;
        }

    }
}


