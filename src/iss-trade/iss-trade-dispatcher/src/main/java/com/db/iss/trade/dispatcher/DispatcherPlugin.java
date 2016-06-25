package com.db.iss.trade.dispatcher;

import com.db.iss.trade.api.cm.Setting;
import com.db.iss.trade.api.cm.SettingException;
import com.db.iss.trade.api.plugin.AbstractDispatcherPlugin;
import com.db.iss.trade.api.plugin.EsbMsg;
import com.db.iss.trade.api.plugin.PluginException;
import com.db.iss.trade.api.serializer.ISerializer;
import com.db.iss.trade.api.serializer.SerializerFactory;
import com.db.iss.trade.api.serializer.SerializerType;
import com.db.iss.trade.dispatcher.spring.SpringServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by andy on 16/6/25.
 * @author andy.shif
 * 调度插件
 */
@Service
public class DispatcherPlugin extends AbstractDispatcherPlugin{

    private SerializerType serializerType = SerializerType.MSGPACK;

    private SerializerFactory serializerFactory = new SerializerFactory();

    private ThreadLocal<ISerializer> serializers = new ThreadLocal<>();

    @Autowired
    private SpringServiceMapper mapper;

    public DispatcherPlugin() {
        super("dispatcher", "v0.0.1");
    }

    private ISerializer getSerializer(){
        ISerializer serializer = serializers.get();
        if(serializer == null){
            serializer = serializerFactory.getSerializer(serializerType);
            serializers.set(serializer);
        }
        return serializer;
    }

    @Override
    protected EsbMsg onHandler(EsbMsg message) throws PluginException {
        long start = System.currentTimeMillis();
        try {
            if (message != null) {
                List<byte[]> contents = message.getContent();
                List<Object> params = new ArrayList<>();
                if (contents != null) {
                    int current = 0;
                    Class[] types = mapper.getParamTypes(message.getNamespace(), message.getMethod());
                    long codecStart = System.currentTimeMillis();
                    try {
                        for (byte[] content : contents) {
                            params.add(getSerializer().decode(content, types[current]));
                            current++;
                        }
                    } catch (Throwable e) {
                        String info = String.format("service[%s] method[%s] deserialize failed", message.getNamespace(), message.getMethod());
                        logger.error(info, e);
                        throw new PluginException(info);
                    }

                    long codecEnd = System.currentTimeMillis();
                    if(logger.isDebugEnabled()){
                        logger.debug("decode time [{}] ms",(codecEnd - codecStart));
                    }
                }
                Object result = mapper.invokeService(message.getNamespace(), message.getMethod(), params.toArray());
                if(result != null){
                    List<byte[]> list = new ArrayList<>();
                    long codecStart = System.currentTimeMillis();
                    try {
                        list.add(getSerializer().encode(result));
                    }catch (Throwable e){
                        String info = String.format("service[%s] method[%s] serialize failed", message.getNamespace(), message.getMethod());
                        logger.error(info, e);
                        throw new PluginException(info);
                    }
                    long codecEnd = System.currentTimeMillis();
                    if(logger.isDebugEnabled()){
                        logger.debug("encode time [{}] ms",(codecEnd - codecStart));
                    }
                    message.setContent(list);
                }else{
                    message.setContent(null);
                }
            }
        }catch (Throwable e){
            logger.error("service execute error",e);
            message.changeToResponse();
            message.setRetcode(EsbMsg.ESB_BIZ_EXECUTE_ERR);
            message.setRetmsg(e.getMessage());
        }
        long end = System.currentTimeMillis();
        if(logger.isDebugEnabled()){
            logger.debug(String.format("namespace [%s] method [%s] execute time [%d] ms",message.getNamespace(),message.getMethod(),(end-start)));
        }
        return message;
    }

    @Override
    protected void onStart() throws PluginException {

    }

    @Override
    protected void onStop() throws PluginException {

    }

    @Override
    protected void onStetting(Setting setting) throws SettingException {
        String serializer = setting.getProperty("serializer");
        if(serializer.equalsIgnoreCase("msgpack")){
            serializerType = SerializerType.MSGPACK;
        }else if(serializer.equalsIgnoreCase("json")){
            serializerType = SerializerType.JSON;
        }
    }
}
