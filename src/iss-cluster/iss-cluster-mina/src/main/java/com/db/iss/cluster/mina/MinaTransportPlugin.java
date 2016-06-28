package com.db.iss.cluster.mina;

import com.db.iss.core.cm.Setting;
import com.db.iss.core.cm.SettingException;
import com.db.iss.core.cm.SettingKey;
import com.db.iss.core.compressor.CompressorType;
import com.db.iss.core.plugin.AbstractTransportPlugin;
import com.db.iss.core.plugin.EsbMsg;
import com.db.iss.core.plugin.PluginException;
import com.db.iss.core.registry.RegistryNode;
import com.db.iss.core.serializer.SerializerType;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by andy on 16/6/23.
 * @author andy.shif
 * mina transport 插件实现
 */
@Service
public class MinaTransportPlugin extends AbstractTransportPlugin {

    private ClusterConnector connector;

    private ClusterAcceptor acceptor;

    private SerializerType type = SerializerType.JSON;

    private CompressorType compressorType = CompressorType.LZ4;

    public MinaTransportPlugin() {
        super("cluster-mina", "v1.0.0",ThreadMode.SHARED);
    }

    private int listen;

    @Override
    protected void onStart() throws PluginException {
        connector = new ClusterConnector(type,compressorType,this);
        acceptor = new ClusterAcceptor(type,compressorType,this);
        try {
            acceptor.bind(listen);
        } catch (IOException e) {
            throw new PluginException(String.format("listen on port %s failed",listen),e);
        }
    }

    @Override
    protected void onStop() throws PluginException {
        connector = null;
        acceptor = null;
    }

    @Override
    protected void onStetting(Setting setting) throws SettingException {

        String serializer = setting.getProperty(SettingKey.SERIALIZER.getValue());

        if(serializer != null && serializer.equalsIgnoreCase(SerializerType.MSGPACK.getValue())){
            type = SerializerType.MSGPACK;
        }else if(serializer != null && serializer.equalsIgnoreCase(SerializerType.JSON.getValue())){
            type = SerializerType.JSON;
        }

        String compressor = setting.getProperty(SettingKey.COMPRESSOR.getValue());
        if(compressor != null && compressor.equalsIgnoreCase(CompressorType.LZ4.getValue())){
            compressorType = CompressorType.LZ4;
        } else {
            compressorType = CompressorType.NULL;
        }

        listen = Integer.parseInt(setting.getProperty(SettingKey.LISTEN.getValue()));
    }

    @Override
    protected void writeEndpoint(EsbMsg message) throws PluginException {
        if(message.getMsgtype() == EsbMsg.MSGTYPE_REQ) { //request
            RegistryNode node = registry.getNode(message.getNamespace());
            if (node != null) {
                if (!connector.write(node.getNode(), node.getUrl(), message)) {
                    throw new PluginException("write request message to endpoint failed " + message);
                }
            } else {
                logger.error("namespace {} not found", message.getNamespace());
            }
        } else { //response
            if(!acceptor.write(message)){
                throw new PluginException("write response message to endpoint failed " + message);
            }
        }
    }
}
