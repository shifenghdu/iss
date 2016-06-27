package com.db.iss.core.notify;

import com.db.iss.core.cm.SettingKey;
import com.db.iss.core.cm.SettingLoader;
import com.db.iss.core.notify.zk.ZkNotify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by andy on 16/6/27.
 * @author andy.shif
 * 默认notify工厂实现
 */
public class DefaultNotifyFactory implements INotifyFactory {

    private String registry = SettingLoader.getSetting().getProperty(SettingKey.REGISTRY.getValue());

    private INotify notify;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public INotify getNotify() {
        if(notify == null) {
            return parseAndCreate();
        }else{
            return notify;
        }
    }

    public synchronized INotify parseAndCreate(){
        if(notify == null) {
            if (registry != null) {
                String[] ads = registry.split("\\,");
                StringBuffer buffer = new StringBuffer();
                if(ads != null){
                    int current = 0;
                    String protocol = "";
                    for(String ad : ads){
                        try {
                            URL url = new URL(ad);
                            buffer.append(url.getHost());
                            buffer.append(":");
                            buffer.append(url.getPort());
                            protocol = url.getProtocol();
                            if(current != (ads.length - 1)){
                                buffer.append(",");
                            }
                        } catch (MalformedURLException e) {
                            logger.error("parse registry config failed",e);
                        }
                    }
                    return createNotify(protocol,buffer.toString());
                }else{
                    logger.error("registry config format error");
                    return null;
                }
            }else{
                logger.error("registry config not found");
                return null;
            }
        } else {
            return notify;
        }
    }


    public INotify createNotify(String protocol,String address){
        if(protocol.equalsIgnoreCase("zookeeper")){
            return new ZkNotify(address);
        }else{
            return new ZkNotify(address);
        }
    }

}
