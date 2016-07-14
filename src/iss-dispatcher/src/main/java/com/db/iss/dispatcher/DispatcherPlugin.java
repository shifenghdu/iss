package com.db.iss.dispatcher;

import com.db.iss.core.cm.SettingException;
import com.db.iss.core.compressor.CompressorProvider;
import com.db.iss.core.plugin.AbstractDispatcherPlugin;
import com.db.iss.core.plugin.EsbMsg;
import com.db.iss.core.plugin.PluginException;
import com.db.iss.core.registry.IRegistry;
import com.db.iss.core.serializer.SerializerProvider;
import com.db.iss.dispatcher.future.DefaultFuture;
import com.db.iss.dispatcher.future.IFuture;
import com.db.iss.dispatcher.spring.ServiceMapper;
import com.db.iss.dispatcher.spring.ServiceScanner;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 16/6/25.
 * @author andy.shif
 * 调度插件
 */
public class DispatcherPlugin extends AbstractDispatcherPlugin implements IMessageSend ,ApplicationContextAware {

    private SerializerWrapper serializerWrapper;

    private ServiceMapper mapper = new ServiceMapper();

    private ResponseMapper responseMapper = ResponseMapper.getInstance();

    private ServiceScanner serviceScanner;

    /**
     * inject begin
     */
    private SerializerProvider serializerProvider;

    private CompressorProvider compressorProvider;

    private IRegistry registry;

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
        if (logger.isInfoEnabled()) {
            logger.warn(String.format("namespace [%s] method [%s] execute time [%d] ms", message.getNamespace(), message.getMethod(), (end - start)));
        }
        return message;
    }

    @Override
    protected void onResponse(EsbMsg message) throws PluginException {
        IFuture<EsbMsg> future = responseMapper.get(message.getNamespace(),message.getMethod(),message.getPackageid());
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
//        String serializer = configManager.getSettingValue(SettingKey.SERIALIZER.getValue());
//        if(serializer != null && serializer.equalsIgnoreCase(SerializerType.MSGPACK.getValue())){
//            serializerWrapper.setSerializerType(SerializerType.MSGPACK);
//        }else if(serializer != null && serializer.equalsIgnoreCase(SerializerType.JSON.getValue())){
//            serializerWrapper.setSerializerType(SerializerType.JSON);
//        }
    }

    @Override
    public IFuture<EsbMsg> send(EsbMsg message) throws PluginException {
        IFuture<EsbMsg> future = new DefaultFuture<>();
        responseMapper.put(message.getNamespace(),message.getMethod(),future);
        this.backward(message);
        return future;
    }

    public ServiceMapper getMapper() {
        return mapper;
    }

    public void setMapper(ServiceMapper mapper) {
        this.mapper = mapper;
    }

    public SerializerWrapper getSerializerWrapper() {
        return serializerWrapper;
    }

    public void setSerializerWrapper(SerializerWrapper serializerWrapper) {
        this.serializerWrapper = serializerWrapper;
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
        setSerializerWrapper(new SerializerWrapper(serializerProvider));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        serviceScanner = new ServiceScanner(mapper,registry);
        serviceScanner.setApplicationContext(applicationContext);
    }
}
