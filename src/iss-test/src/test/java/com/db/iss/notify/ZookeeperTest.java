package com.db.iss.notify;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by apple on 16/6/26.
 */
public class ZookeeperTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void test(){
        try {
            ZkClient zkClient = new ZkClient("121.40.225.35:2181",10000,1000);
            zkClient.subscribeChildChanges("/com/db/iss/register", new IZkChildListener() {
                @Override
                public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                    logger.debug(currentChilds.toString());
                }
            });
            if(!zkClient.exists("/com/db/iss/register")){
                zkClient.createPersistent("/com/db/iss/register",true);
            }

            zkClient.createEphemeral("/com/db/iss/register/com.db.trade.visa.IDeposit#group1#query");

            Thread.sleep(10000);
        }catch (Throwable e){
            e.printStackTrace();
        }

    }

    @Test
    public void getIp() throws UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();
        logger.debug("{}",addr.getHostAddress());//获得本机IP
    }
}
