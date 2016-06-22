package com.db.iss.trade.cluster.mina;

import com.db.iss.trade.api.cm.Setting;
import com.db.iss.trade.api.exception.PluginException;
import com.db.iss.trade.api.exception.SettingException;
import com.db.iss.trade.api.plugin.AbstractTransportPlugin;
import com.db.iss.trade.api.plugin.EsbMsg;
import com.db.iss.trade.api.plugin.annotation.Plugin;
import com.db.iss.trade.cluster.mina.codec.SerializerType;
import org.springframework.stereotype.Service;

/**
 * Created by andy on 16/6/23.
 * @author andy.shif
 * mina transport 插件实现
 */
@Service
@Plugin("cluster")
public class MinaTransportPlugin extends AbstractTransportPlugin {

    private ClusterConnector connector;

    private ClusterAcceptor acceptor;

    private SerializerType type = SerializerType.MSGPACK;

    public MinaTransportPlugin() {
        super("cluster", "v1.0.0",ThreadMode.SHARED);
    }

    @Override
    protected void onStart() throws PluginException {
        connector = new ClusterConnector(type,this);
        acceptor = new ClusterAcceptor(type,this);
    }

    @Override
    protected void onStop() throws PluginException {
        connector = null;
        acceptor = null;
    }

    @Override
    protected void onStetting(Setting setting) throws SettingException {
        String serializer = setting.getProperty(namespace + ".serializer");
        if(serializer.equalsIgnoreCase("msgpack")){
            type = SerializerType.MSGPACK;
        }else if(serializer.equalsIgnoreCase("json")){
            type = SerializerType.JSON;
        }
    }

    @Override
    protected EsbMsg onBackward(EsbMsg message) throws PluginException {
        if(!connector.write(message.getNextnode(),registry.getUrl(message.getNextnode()),message)){
            throw new PluginException("write message to endpoint failed " +  message);
        }
        return null;
    }
}
