package com.db.iss.core.registry;

import com.alibaba.fastjson.JSON;
import com.db.iss.core.cm.SettingException;
import com.db.iss.core.cm.SettingKey;
import com.db.iss.core.cm.SettingLoader;
import com.db.iss.core.exception.ISSException;
import com.db.iss.core.registry.url.ExtendURLStreamHandlerFactory;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
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

    private final String DEFAULT_STATUS_ROOT_NODE = "/com/db/iss/registry/nodes";
    private final String DEFAULT_INFO_ROOT_NODE = "/com/db/iss/registry/info";

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
    private String zkNodeStatusPath;
    //本节点zk节点路径
    private String zkNodeInfoPath;
    //注册信息映射表
    private Map<String,List<RegistryNode>> registryMap = new ConcurrentHashMap<>();
    //当前轮询节点
    private Map<String,Integer> indexMap = new ConcurrentHashMap<>();

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @PostConstruct
    public void connectZkServer(){
        try {
            registry = SettingLoader.getSetting().getProperty(SettingKey.REGISTRY.getValue());
            node =  SettingLoader.getSetting().getProperty(SettingKey.NODE.getValue());
            listenPort = SettingLoader.getSetting().getProperty(SettingKey.LISTEN.getValue());
            localIp = getLocalHostIp();
            URL.setURLStreamHandlerFactory(new ExtendURLStreamHandlerFactory());
            listenUrl = new URL(String.format("tcp://%s:%s",localIp,listenPort));
            nodeKey = String.format("%s#%s",node,localIp);
            zkNodeStatusPath = String.format("%s/%s",DEFAULT_STATUS_ROOT_NODE,nodeKey);
            zkNodeInfoPath = String.format("%s/%s",DEFAULT_INFO_ROOT_NODE,nodeKey);


            registryNode = new RegistryNode();
            registryNode.setNode(nodeKey);
            registryNode.setUrl(listenUrl);

            zkClient = new ZkClient(registry, SESSION_TIME_OUT, CONNECT_TIME_OUT);
            if(!zkClient.exists(zkNodeInfoPath)){
                zkClient.createPersistent(zkNodeInfoPath,true);
            }
            if(!zkClient.exists(DEFAULT_STATUS_ROOT_NODE)){
                zkClient.createPersistent(DEFAULT_STATUS_ROOT_NODE,true);
            }
            zkClient.subscribeChildChanges(DEFAULT_STATUS_ROOT_NODE, this);
            loadRemoteRegistry();
        }catch (Throwable e){
            throw new SettingException("connect to zookeeper failed",e);
        }
    }

    private void loadRemoteRegistry(){
        List<String> children = zkClient.getChildren(DEFAULT_STATUS_ROOT_NODE);
        Map<String,List<RegistryNode>> map = new ConcurrentHashMap<>();
        for(String child : children){
            byte[] data = zkClient.readData(String.format("%s/%s", DEFAULT_INFO_ROOT_NODE, child));
            if(data != null) {
                RegistryNode node = JSON.parseObject(data, RegistryNode.class);
                for (String namespace : node.getNamespaces()) {
                    if(map.containsKey(namespace)){
                        map.get(namespace).add(node);
                    }else{
                        List<RegistryNode> nodes = new ArrayList<RegistryNode>();
                        nodes.add(node);
                        map.put(namespace, nodes);
                    }

                }
            }
        }
        logger.debug("load zk registry info {}",map);
        registryMap = map;
    }

    /**
     * 写入注册信息
     * @return
     */
    private boolean writeZk(){
        logger.debug("publish registry {}",JSON.toJSONString(registryNode));

        //先写入数据在通知
        zkClient.writeData(zkNodeInfoPath, JSON.toJSONBytes(registryNode));
        if(!zkClient.exists(zkNodeStatusPath)){
            zkClient.createEphemeral(zkNodeStatusPath);
        }

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
        Integer index = indexMap.get(namespace);
        if(index == null){
            index = 0;
            indexMap.put(namespace,index);
        }
        List<RegistryNode> registryNodes = registryMap.get(namespace);
        if(registryNodes != null) {
            if (index < registryNodes.size()) {
                return registryNodes.get(index);
            } else {
                index = 0;
                indexMap.put(namespace, index);
                return registryNodes.get(0);
            }
        }else {
            throw new ISSException(String.format("namespace [%s] registry info not found",namespace));
        }
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
    public void handleChildChange(final String s, final List<String> list) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                logger.debug("receive zk node change {} {}", s, list);
                Map<String,List<RegistryNode>> map = new ConcurrentHashMap<>();
                for(String cur : list) {
                    byte[] data = zkClient.readData(String.format("%s/%s", DEFAULT_INFO_ROOT_NODE, cur));
                    if(data != null) {
                        RegistryNode node = JSON.parseObject(data, RegistryNode.class);
                        for (String namespace : node.getNamespaces()) {
                            if(map.containsKey(namespace)){
                                map.get(namespace).add(node);
                            }else{
                                List<RegistryNode> nodes = new ArrayList<RegistryNode>();
                                nodes.add(node);
                                map.put(namespace, nodes);
                            }

                        }
                    }
                }
                logger.debug("receive zk registry info {}",map);
                registryMap = map;
            }
        }).start();

    }
}
