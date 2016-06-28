package com.db.iss.core.registry;

import com.alibaba.fastjson.JSON;
import com.db.iss.core.cm.SettingException;
import com.db.iss.core.cm.SettingKey;
import com.db.iss.core.cm.SettingLoader;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andy on 2016/6/28.
 * @author andy.shif
 * 默认注册中心实现
 */
@Service
public class DefaultRegistry implements IRegistry,IZkChildListener {

    private ZkClient zkClient;

    private final String DEFAULT_ROOT_NODE = "/com/db/iss/registry";

    private final int SESSION_TIME_OUT = 10000;

    private final int CONNECT_TIME_OUT = 10000;
    //节点名称
    private String node;
    //zookeeper地址
    private String registry;
    //本地Ip
    private String localIp;
    //服务监听端口
    private String listenPort;
    //本节点在注册中心标记
    private String nodeKey;
    //本地提供服务URL
    private URL listenUrl;
    //本地注册信息
    private RegistryNode registryNode;
    //本节点zk节点路径
    private String zkNodePath;
    //注册信息映射表
    private Map<String,RegistryNode> registryMap = new ConcurrentHashMap<>();


    @PostConstruct
    public void connectZkServer(){
        try {
            registry = SettingLoader.getSetting().getProperty(SettingKey.REGISTRY.getValue());
            node =  SettingLoader.getSetting().getProperty(SettingKey.NODE.getValue());
            listenPort = SettingLoader.getSetting().getProperty(SettingKey.LISTEN.getValue());
            localIp = getLocalHostIp();
            listenUrl = new URL(String.format("%s:%s",localIp,listenPort));
            nodeKey = String.format("%s#%s",node,localIp);
            zkNodePath = String.format("%s/%s",DEFAULT_ROOT_NODE,nodeKey);

            registryNode = new RegistryNode();
            registryNode.setNode(nodeKey);
            registryNode.setUrl(listenUrl);

            zkClient = new ZkClient(registry, SESSION_TIME_OUT, CONNECT_TIME_OUT);
            if(!zkClient.exists(DEFAULT_ROOT_NODE)){
                zkClient.createPersistent(DEFAULT_ROOT_NODE,true);
            }
            zkClient.subscribeChildChanges(DEFAULT_ROOT_NODE, this);

        }catch (Throwable e){
            throw new SettingException("connect to zookeeper failed",e);
        }
    }

    /**
     * 写入注册信息
     * @return
     */
    private boolean writeZk(){
        if(!zkClient.exists(zkNodePath)){
            zkClient.createEphemeral(zkNodePath);
        }
        zkClient.writeData(zkNodePath, JSON.toJSONBytes(registryNode));
        return true;
    }

    @Override
    public boolean register(String namespace) {
        registryNode.getNamespaces().add(namespace);
        return writeZk();
    }

    @Override
    public boolean unregister(String namespace) {
        registryNode.getNamespaces().remove(namespace);
        return writeZk();
    }

    @Override
    public RegistryNode getNode(String namespace) {
        return registryMap.get(namespace);
    }

    /**
     * 获取本机localhost地址
     * @return
     * @throws UnknownHostException
     */
    private String getLocalHostIp() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    /**
     * zk节点变更回调
     * @param s
     * @param list
     * @throws Exception
     */
    @Override
    public void handleChildChange(String s, List<String> list) throws Exception {
        Map<String,RegistryNode> map = new ConcurrentHashMap<>();
        for(String cur : list) {
            byte[] data = zkClient.readData(String.format("%s/%s", s, cur));
            RegistryNode node = JSON.parseObject(data,RegistryNode.class);
            for(String namespace : node.getNamespaces()){
                map.put(namespace,node);
            }
        }
        registryMap = map;
    }
}
