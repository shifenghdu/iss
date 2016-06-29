package com.db.iss.dispatcher;

import com.db.iss.core.cm.Setting;
import com.db.iss.core.cm.SettingException;
import com.db.iss.core.cm.SettingKey;
import com.db.iss.core.plugin.AbstractDispatcherPlugin;
import com.db.iss.core.plugin.EsbMsg;
import com.db.iss.core.plugin.IMessagePlugin;
import com.db.iss.core.plugin.PluginException;
import com.db.iss.core.serializer.ISerializer;
import com.db.iss.core.serializer.SerializerFactory;
import com.db.iss.core.serializer.SerializerType;
import com.db.iss.dispatcher.future.DefaultFuture;
import com.db.iss.dispatcher.future.IFuture;
import com.db.iss.dispatcher.spring.ServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by andy on 16/6/25.
 * @author andy.shif
 * 调度插件
 */
@Service
public class DispatcherPlugin extends AbstractDispatcherPlugin implements IMessageSend{

    private SerializerWrapper serializerWrapper = SerializerWrapper.getInstance();

    @Autowired
    private ServiceMapper mapper;

    private ResponseMapper responseMapper = ResponseMapper.getInstance();

    public DispatcherPlugin() {
        super("dispatcher", "v0.0.1");
    }

    @Override
    protected EsbMsg onRequest(EsbMsg message) throws PluginException {
        long start = System.currentTimeMillis();
        try {
            message.changeToResponse();
            List<byte[]> contents = message.getContent();
            List<Object> params = new ArrayList<>();
            if (contents != null) {
                int current = 0;
                Class[] types = mapper.getParamTypes(message.getNamespace(), message.getMethod());
                long codecStart = System.currentTimeMillis();
                try {
                    for (byte[] content : contents) {
                        params.add(serializerWrapper.getSerializer().decode(content, types[current]));
                        current++;
                    }
                } catch (Throwable e) {
                    String info = String.format("service[%s] method[%s] deserialize failed", message.getNamespace(), message.getMethod());
                    logger.error(info, e);
                    throw new PluginException(info);
                }

                long codecEnd = System.currentTimeMillis();
                if (logger.isDebugEnabled()) {
                    logger.debug("decode time [{}] ms", (codecEnd - codecStart));
                }
            }
            Object result = mapper.invokeService(message.getNamespace(), message.getMethod(), params.toArray());
            if (result != null) {
                List<byte[]> list = new ArrayList<>();
                long codecStart = System.currentTimeMillis();
                try {
                    list.add(serializerWrapper.getSerializer().encode(result));
                } catch (Throwable e) {
                    String info = String.format("service[%s] method[%s] serialize failed", message.getNamespace(), message.getMethod());
                    logger.error(info, e);
                    throw new PluginException(info);
                }
                long codecEnd = System.currentTimeMillis();
                if (logger.isDebugEnabled()) {
                    logger.debug("encode time [{}] ms", (codecEnd - codecStart));
                }
                message.setContent(list);
            } else {
                message.setContent(null);
            }
        } catch (Throwable e) {
            logger.error("service execute error", e);
            message.setRetcode(EsbMsg.ESB_BIZ_EXECUTE_ERR);
            message.setRetmsg(e.getMessage());
        }
        long end = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("namespace [%s] method [%s] execute time [%d] ms", message.getNamespace(), message.getMethod(), (end - start)));
        }
        return message;
    }

    @Override
    protected void onResponse(EsbMsg message) throws PluginException {
        IFuture<EsbMsg> future = responseMapper.get(message.getNamespace(),message.getMethod());
        future.set(message);
    }

    @Override
    protected void onStart() throws PluginException {
        onStetting();
    }

    @Override
    protected void onStop() throws PluginException {

    }

    protected void onStetting() throws SettingException {
        String serializer = configManager.getSettingValue(SettingKey.SERIALIZER.getValue());
        if(serializer != null && serializer.equalsIgnoreCase(SerializerType.MSGPACK.getValue())){
            serializerWrapper.setSerializerType(SerializerType.MSGPACK);
        }else if(serializer != null && serializer.equalsIgnoreCase(SerializerType.JSON.getValue())){
            serializerWrapper.setSerializerType(SerializerType.JSON);
        }
    }

    @Override
    public IFuture<EsbMsg> send(EsbMsg message) throws PluginException {
        IFuture<EsbMsg> future = new DefaultFuture<>();
        responseMapper.put(message.getNamespace(),message.getMethod(),future);
        this.backward(message);
        return future;
    }
}
