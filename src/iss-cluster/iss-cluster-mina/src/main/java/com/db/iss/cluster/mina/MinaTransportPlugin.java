package com.db.iss.cluster.mina;

import com.db.iss.core.cm.IConfigManager;
import com.db.iss.core.cm.SettingException;
import com.db.iss.core.cm.SettingKey;
import com.db.iss.core.compressor.CompressorProvider;
import com.db.iss.core.plugin.AbstractTransportPlugin;
import com.db.iss.core.plugin.EsbMsg;
import com.db.iss.core.plugin.PluginException;
import com.db.iss.core.registry.RegistryNode;
import com.db.iss.core.serializer.SerializerProvider;

import java.io.IOException;

/**
 * Created by andy on 16/6/23.
 * @author andy.shif
 * mina transport 插件实现
 */
public class MinaTransportPlugin extends AbstractTransportPlugin {

    private ClusterConnector connector;

    private ClusterAcceptor acceptor;

    /**
     * inject
     */
    private SerializerProvider serializerProvider;

    private CompressorProvider compressorProvider;



    public MinaTransportPlugin() {
        super("cluster-mina", "v1.0.0",ThreadMode.SHARED);
    }

    private int listen;

    @Override
    protected void onStart() throws PluginException {
        onStetting();
        connector = new ClusterConnector(serializerProvider, compressorProvider, this);
        acceptor = new ClusterAcceptor(serializerProvider, compressorProvider, this);
        logger.info("iss listen on {}",listen);
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

    protected void onStetting() throws SettingException {
//        String serializer = configManager.getSettingValue(SettingKey.SERIALIZER.getValue());

//        if(serializer != null && serializer.equalsIgnoreCase(SerializerType.MSGPACK.getValue())){
//            type = SerializerType.MSGPACK;
//        }else if(serializer != null && serializer.equalsIgnoreCase(SerializerType.JSON.getValue())){
//            type = SerializerType.JSON;
//        }
//
//        String compressor = configManager.getSettingValue(SettingKey.COMPRESSOR.getValue());
//        if(compressor != null && compressor.equalsIgnoreCase(CompressorType.LZ4.getValue())){
//            compressorType = CompressorType.LZ4;
//        } else {
//            compressorType = CompressorType.NULL;
//        }

        listen = Integer.parseInt(configManager.getSettingValue(SettingKey.LISTEN.getValue()));
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

    public CompressorProvider getCompressorProvider() {
        return compressorProvider;
    }

    public void setCompressorProvider(CompressorProvider compressorProvider) {
        this.compressorProvider = compressorProvider;
    }

    public SerializerProvider getSerializerProvider() {
        return serializerProvider;
    }

    public void setSerializerProvider(SerializerProvider serializerProvider) {
        this.serializerProvider = serializerProvider;
    }
}
