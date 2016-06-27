package com.db.iss.core.notify.zk;

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

    private final int SESSION_TIME_OUT = 10000;

    private final int CONNECT_TIME_OUT = 10000;

    public ZkNotify(String address){
        zkClient = new ZkClient(address,SESSION_TIME_OUT,CONNECT_TIME_OUT);
    }

    @Override
    public void setNamespace(String namespace) {
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
