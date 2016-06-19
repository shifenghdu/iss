package com.db.iss.connector;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.db.iss.common.dispatch.IBizContext;
import com.db.iss.common.esb.EsbMsg;

/**
 * Created by andy on 2015/12/29.
 */
public class BizContext implements IBizContext {

    private Connector connector = null;
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public BizContext(Connector connector) {
        this.connector = connector;
    }

    @Override
    public byte[] call(int systemId, int functionId, byte[] msg) {
        return this.call(systemId, functionId, "", msg);
    }

    @Override
    public byte[] call(int systemId, int functionId, String tag, byte[] msg) {
        try {
            EsbMsg esbMsg = new EsbMsg();
            esbMsg.setSystemid(systemId);
            esbMsg.setFunctionid(functionId);
            esbMsg.setTag(tag);
            esbMsg.setContent(msg);
            esbMsg.setIsCopySend(false);
            connector.send(esbMsg);
            EsbMsg rspMsg = connector.recv(connector.getTimeout());
            if(rspMsg != null)
                return rspMsg.getContent();
            else
                return null;
        } catch (Throwable e) {
            logger.error("调用服务失败 ",e);
            return null;
        }
    }

    @Override
    public void post(int systemId, int functionId, byte[] msg) {
        throw new RuntimeException("客戶端不支持异步调用");
    }

    @Override
    public void post(int systemId, int functionId, String tag, byte[] msg) {
        throw new RuntimeException("客戶端不支持异步调用");
    }

    @Override
    public List<byte[]> multiCall(int systemId, int functionId, byte[] msg) {
        return this.multiCall(systemId, functionId, "", msg);
    }

    @Override
    public List<byte[]> multiCall(int systemId, int functionId, String tag, byte[] msg) {
        try {
            EsbMsg esbMsg = new EsbMsg();
            esbMsg.setSystemid(systemId);
            esbMsg.setFunctionid(functionId);
            esbMsg.setTag(tag);
            esbMsg.setContent(msg);
            esbMsg.setIsCopySend(true);
            esbMsg.setCopyCount(1);
            connector.send(esbMsg);
            List<EsbMsg> rspMsg = connector.recvMulti(connector.getTimeout());
            ArrayList<byte[]> results = new ArrayList<byte[]>();
            if(rspMsg != null) {
                for (EsbMsg r : rspMsg) {
                    results.add(r.getContent());
                }
            }
            return results;
        } catch (Throwable e) {
        	logger.error("调用服务失败 ",e);
            return null;
        }
    }
}
