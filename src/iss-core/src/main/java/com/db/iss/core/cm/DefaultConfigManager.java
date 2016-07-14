package com.db.iss.core.cm;

import com.alibaba.fastjson.JSON;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andy on 16/6/29.
 * @author andy.shif
 * 默认配置服务实现
 */
public class DefaultConfigManager implements IConfigManager, IZkDataListener,Runnable{

    private Map<String,ISettingChangeHandler> handlerMap = new ConcurrentHashMap<>();

    private Setting setting = SettingLoader.getSetting();

    private Properties remoteSetting;

    private ZkClient zkClient;

    private final String DEFAULT_CONFIG_ROOT_NODE = "/com/db/iss/config";

    private final int SESSION_TIME_OUT = 10000;

    private final int CONNECT_TIME_OUT = 10000;

    private String registry;

    private String nodePath;

    private String node;

    private String pipe;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void connectZkServer(){
        try {
//            registry = SettingLoader.getSetting().getProperty(SettingKey.REGISTRY.getValue());
//            node = SettingLoader.getSetting().getProperty(SettingKey.NODE.getValue());
            zkClient = new ZkClient(registry, SESSION_TIME_OUT, CONNECT_TIME_OUT);
            nodePath = String.format("%s/%s",DEFAULT_CONFIG_ROOT_NODE,node);
            if(!zkClient.exists(nodePath)){
                zkClient.createPersistent(nodePath,true);
            }
            refreshSetting();
            zkClient.subscribeDataChanges(nodePath, this);

        }catch (Throwable e){
            throw new SettingException("connect to zookeeper failed",e);
        }
    }

    @Override
    public String getSettingValue(String key) {
        return setting.getProperty(key);
    }

    @Override
    public void subscribeSettingChange(String key, ISettingChangeHandler handler) {
        handlerMap.put(key,handler);
    }

    public void refreshSetting(){
        byte[] data = zkClient.readData(nodePath);
        if(data != null){
            remoteSetting = JSON.parseObject(data,Properties.class);
            setting.setProperties(remoteSetting);
        }else {
            logger.warn("refresh settings failed");
        }
    }


    @Override
    public void handleDataChange(String dataPath, Object data) throws Exception {
        logger.info("update settings [{}]",data);
        remoteSetting = JSON.parseObject((String) data,Properties.class);
        setting.setProperties(remoteSetting);
        new Thread(this).start();
    }

    @Override
    public void handleDataDeleted(String dataPath) throws Exception {

    }

    @Override
    public void run() {
        for(Map.Entry<Object,Object> entry : remoteSetting.entrySet()){
            String key = (String)entry.getKey();
            ISettingChangeHandler handler = handlerMap.get(key);
            if(handler != null){
                handler.onChange(key,(String)entry.getValue());
            }
        }
    }

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
        setting.setProperty(SettingKey.REGISTRY.getValue(),registry);
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
        setting.setProperty(SettingKey.NODE.getValue(),node);
    }

    public String getPipe() {
        return pipe;
    }

    public void setPipe(String pipe) {
        this.pipe = pipe;
        setting.setProperty(SettingKey.PIPE.getValue(),pipe);
    }
}
