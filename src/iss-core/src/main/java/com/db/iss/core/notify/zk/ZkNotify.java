package com.db.iss.core.notify.zk;

import com.alibaba.fastjson.JSON;
import com.db.iss.core.notify.INotify;
import com.db.iss.core.notify.INotifyHandler;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;

/**
 * Created by andy on 16/6/27.
 * @author andy.shif
 * zookeeper notify实现
 */
public class ZkNotify implements INotify{

    private ZkClient zkClient;

    private String namespace;

    public ZkNotify(String address){
        zkClient = new ZkClient(address,10000,1000);
    }

    @Override
    public void createNamespace(String namespace) {
        this.namespace = namespace.replaceAll("\\.","\\/");
        if(zkClient.exists(namespace)){
            zkClient.createPersistent(this.namespace,true);
        }
    }

    @Override
    public void publish(String topic, byte[] message) {
        zkClient.createEphemeral(String.format("%s/%s",this.namespace,topic), message);
    }

    @Override
    public void subscribe(final String topic, final INotifyHandler handler) {
        zkClient.subscribeChildChanges(this.namespace, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                for(String childs : currentChilds){
                    if(childs.equals(topic)){
                        byte[] msg = zkClient.readData(String.format("%s/%s",namespace,topic));
                        handler.onMessage(msg);
                    }
                }
            }
        });
    }
}
