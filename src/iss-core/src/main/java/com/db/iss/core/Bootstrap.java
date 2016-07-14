package com.db.iss.core;

import com.db.iss.core.cm.IConfigManager;
import com.db.iss.core.cm.Setting;
import com.db.iss.core.cm.SettingKey;
import com.db.iss.core.cm.SettingLoader;
import com.db.iss.core.plugin.AbstractDispatcherPlugin;
import com.db.iss.core.plugin.AbstractTransportPlugin;
import com.db.iss.core.plugin.IMessagePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andy on 16/6/26.
 * @author andy.shif
 * 配置读取及插件启动入口
 */
public class Bootstrap implements ApplicationContextAware {

    private ApplicationContext context;

    private Map<String,IMessagePlugin> pluginMap = new ConcurrentHashMap<>();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private IConfigManager configManager;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        try {
            loadPlugins();
            onStart();
        }catch (Throwable e){
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * 加载spring容器中所有插件
     */
    private void loadPlugins(){
        Map<String,IMessagePlugin> ps = this.context.getBeansOfType(IMessagePlugin.class);
//        for(Map.Entry<String,IMessagePlugin> entry : ps.entrySet()){
//            IMessagePlugin plugin = entry.getValue();
//            pluginMap.put(plugin.getName(),plugin);
//        }
        pluginMap.putAll(ps);
    }

    /**
     * 装配插件
     */
    private void onStart() {
        Setting setting = SettingLoader.getSetting();
        String[] names = configManager.getSettingValue(SettingKey.PIPE.getValue()).split("\\|");
        String node = configManager.getSettingValue(SettingKey.NODE.getValue());
        logger.info("node [{}] wait for start",node);
        int current = 0;
        IMessagePlugin pre = null;
        for(String name : names){
            name = name.trim();
            IMessagePlugin plugin = pluginMap.get(name);
            if(plugin == null) throw new RuntimeException(String.format("can't load plugin [%s]",name));
            if(current == 0 && !(plugin instanceof AbstractTransportPlugin)){
                throw new RuntimeException("pipe head must transport plugin");
            }
            if(current == (names.length - 1) && !(plugin instanceof AbstractDispatcherPlugin)){
                throw new RuntimeException("pipe tail must dispatcher plugin");
            }

            plugin.setNode(node);
            plugin.setPre(pre);
            plugin.start();

            if(pre != null){
                pre.setNext(plugin);
            }
            pre = plugin;
            current ++;
            logger.info("plugin [{}] start success",name);
        }
        logger.info("node [{}] start success",node);
    }

    public IConfigManager getConfigManager() {
        return configManager;
    }

    public void setConfigManager(IConfigManager configManager) {
        this.configManager = configManager;
    }
}


