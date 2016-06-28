package com.db.iss.dispatcher.proxy;

import com.db.iss.core.exception.RemoteException;
import com.db.iss.core.plugin.EsbMsg;
import com.db.iss.dispatcher.IMessageSend;
import com.db.iss.dispatcher.ResponseMapper;
import com.db.iss.dispatcher.SerializerWrapper;
import com.db.iss.dispatcher.future.IFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by andy on 16/6/26.
 * @author shif.andy
 * 服务代理虚基类
 */
public abstract class AbstractServiceProxy implements IServiceProxy{

    private ResponseMapper responseMapper = ResponseMapper.getInstance();

    private SerializerWrapper serializerWrapper = SerializerWrapper.getInstance();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private AtomicLong total = new AtomicLong(0);

    private AtomicLong error = new AtomicLong(0);

    private IMessageSend messageSend;

    private long timeout = 10000L;

    protected Object invoke(String namespace,String method,Object[] args,Class<?> returnType) {
        total.incrementAndGet();
        try {
            if(logger.isDebugEnabled()){
                logger.debug("remote invoke namespace [{}] method [{}]",namespace,method);
            }
            EsbMsg request = new EsbMsg();
            request.setNamespace(namespace);
            request.setMethod(method);
            List<byte[]> params = new ArrayList<>();
            request.setContent(params);
            for (Object arg : args) {
                params.add(serializerWrapper.getSerializer().encode(arg));
            }

            IFuture<EsbMsg> future = messageSend.send(request);
            EsbMsg response = future.get(timeout);

            if(response == null){
                throw new RemoteException("remote return null");
            }else{
                List<byte[]> list = response.getContent();
                if(list != null && list.size() >= 1){
                    return serializerWrapper.getSerializer().decode(list.get(0),returnType);
                }
            }

        }catch (Throwable e){
            error.incrementAndGet();
            logger.error("invoke remote service failed",e);
        }
        return  null;
    }

    @Override
    public Long getDealCount() {
        return total.get();
    }

    @Override
    public Long getErrorCount() {
        return error.get();
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public void setIMessageSend(IMessageSend messageSend) {
        this.messageSend = messageSend;
    }
}
