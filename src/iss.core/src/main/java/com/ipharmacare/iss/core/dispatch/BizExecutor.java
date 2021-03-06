package com.ipharmacare.iss.core.dispatch;

import com.ipharmacare.iss.common.SystemConst;
import com.ipharmacare.iss.common.dispatch.IBizContext;
import com.ipharmacare.iss.common.dispatch.IBizProcessor;
import com.ipharmacare.iss.common.esb.EsbMsg;
import com.ipharmacare.iss.core.router.IRouter;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4SafeDecompressor;
import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.List;

public class BizExecutor implements IBizContext, Runnable {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Dispatcher dispatcher;

    protected IRouter router;

    private EsbMsg reqmsg;

    private IBizProcessor processor;

    private int timeout;// 单位毫秒

    private LZ4Factory factory = LZ4Factory.fastestInstance();

    public IBizProcessor getProcessor() {
        return processor;
    }

    public void setProcessor(IBizProcessor processor) {
        this.processor = processor;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public BizExecutor(Dispatcher dispatcher, IRouter router) {
        this.router = router;
        this.dispatcher = dispatcher;
        this.timeout = 0;
    }

    public BizExecutor(Dispatcher dispatcher, IRouter router, int timeout) {
        this.router = router;
        this.dispatcher = dispatcher;
        this.timeout = timeout;
    }

    public byte[] compress(byte[] src){
        LZ4Compressor compressor = factory.fastCompressor();
        int maxCompressedLength = compressor.maxCompressedLength(src.length);
        byte[] compressed = new byte[maxCompressedLength];
        int compressedLength = compressor.compress(src, 0, src.length, compressed, 0, maxCompressedLength);
        IoBuffer buffer = IoBuffer.allocate(compressedLength);
        buffer.put(compressed,0,compressedLength);
        buffer.flip();
        return buffer.array();
    }

    public byte[] decompress(byte[] src,int originLen){
        LZ4SafeDecompressor decompressor = factory.safeDecompressor();
        byte[] restored = new byte[originLen];
        decompressor.decompress(src, 0,src.length, restored, 0);
        return restored;
    }

    @Override
    public byte[] call(int systemId, int functionId, byte[] msg) {
        return this.call(systemId, functionId, "", msg);
    }

    @Override
    public byte[] call(int systemId, int functionId, String tag, byte[] msg) {
        EsbMsg reqMsg = new EsbMsg();
        setContent(reqMsg,msg);
        reqMsg.setSystemid(systemId);
        reqMsg.setFunctionid(functionId);
        reqMsg.setTag(tag);
        reqMsg.setMsgtype(EsbMsg.MSGTYPE_REQ);
        reqMsg.setSendname(dispatcher.getPluginName());
        reqMsg.setSendarg(String.valueOf(Thread.currentThread().getId()));
        reqMsg.setIsCopySend(false);
        dispatcher.pollMsg(Thread.currentThread().getId(), reqMsg);
        EsbMsg dest = reqMsg;
        synchronized (reqMsg) {
            router.transMsg(reqMsg);
            long total = 0;
            while (reqMsg.getResponse().size() == 0) {
                int timeoutReal = timeout != 0 ? timeout : 500;
                try {
                    long startTime = System.currentTimeMillis();
                    if(logger.isDebugEnabled())
                        logger.debug("wait on object [{}] begin",reqMsg.hashCode());
                    reqMsg.wait(timeoutReal);
                    if(logger.isDebugEnabled())
                        logger.debug("wait on object [{}] end",reqMsg.hashCode());
                    long endTime = System.currentTimeMillis();
                    long processTime = endTime - startTime;
                    total += processTime;
                    if (total >= timeoutReal) {
                        throw new RuntimeException("调用远程服务超时");
                    }
                } catch (InterruptedException e) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("中断异常");
                    }
                    continue;
                }
//                dest = dispatcher.getMsg(Thread.currentThread().getId());
            }
        }
        dest = reqMsg.getResponse().get(0);
        byte[] compressed = dest.getContent();
        int originLen = dest.getOriginLen();
        return decompress(compressed, originLen);
    }

    private void setContent(EsbMsg reqMsg,byte[] msg){
        if(msg != null) {
            reqMsg.setOriginLen(msg.length);
            reqMsg.setContent(compress(msg));
        }else{
            reqMsg.setOriginLen(0);
            reqMsg.setContent(msg);
        }
    }

    @Override
    public void post(int systemId, int functionId, byte[] msg) {
        this.post(systemId, functionId, "", msg);
    }

    @Override
    public void post(int systemId, int functionId, String tag, byte[] msg) {
        EsbMsg reqMsg = new EsbMsg();
        setContent(reqMsg,msg);
        reqMsg.setSystemid(systemId);
        reqMsg.setFunctionid(functionId);
        reqMsg.setTag(tag);
        reqMsg.setMsgtype(EsbMsg.MSGTYPE_REQ);
        reqMsg.setSendname(dispatcher.getPluginName());
    }

    @Override
    public List<byte[]> multiCall(int systemId, int functionId, byte[] msg) {
        return this.multiCall(systemId, functionId, "", msg);
    }

    @Override
    public List<byte[]> multiCall(int systemId, int functionId, String tag, byte[] msg) {
        EsbMsg reqMsg = new EsbMsg();
        setContent(reqMsg,msg);
        reqMsg.setSystemid(systemId);
        reqMsg.setFunctionid(functionId);
        reqMsg.setTag(tag);
        reqMsg.setMsgtype(EsbMsg.MSGTYPE_REQ);
        reqMsg.setSendname(dispatcher.getPluginName());
        reqMsg.setSendarg(String.valueOf(Thread.currentThread().getId()));
        reqMsg.setIsCopySend(true);
        reqMsg.setCopyCount(1);
        dispatcher.pollMsg(Thread.currentThread().getId(), reqMsg);
        synchronized (reqMsg) {
            router.transMsg(reqMsg);
            int total = 0;
            while (reqMsg.getResponse().size() == 0) {
                int timeoutReal = timeout != 0 ? timeout : 500;
                try {
                    long startTime = System.currentTimeMillis();
                    reqMsg.wait(timeoutReal);
                    long endTime = System.currentTimeMillis();
                    long processTime = endTime - startTime;
                    total += processTime;
                } catch (InterruptedException e) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("中断异常");
                    }
                    continue;
                }
                if (total >= timeoutReal) {
                    throw new RuntimeException("调用远程服务超时");
                }
            }
        }
        List<EsbMsg> responses = reqMsg.getResponse();
        ArrayList<byte[]> results = new ArrayList<byte[]>();
        for (EsbMsg esbMsg : responses) {
            byte[] compressed = esbMsg.getContent();
            int originLen = esbMsg.getOriginLen();
            results.add(decompress(compressed,originLen));
        }
        return results;
    }

    @Override
    public void run() {
        MDC.put("node", dispatcher.getNodeName());
        EsbMsg msg = reqmsg;
        Thread.currentThread().setContextClassLoader(processor.getClass().getClassLoader());
        byte[] compressed = msg.getContent();
        int originLen = msg.getOriginLen();
        byte[] data = null;
        if(compressed != null) {
            data = decompress(compressed, originLen);
        }
        byte[] resp = processor.doProcess(this, data);
        msg.changeToResponse();
        if (resp == null) {
            if (logger.isWarnEnabled())
                logger.warn("业务处理出错 system [{}] function [{}]", msg.getSystemid(), msg.getFunctionid());
            msg.setRetcode(SystemConst.ESB_BIZ_EXECUTE_ERR);
            msg.setRetmsg(SystemConst.ESB_BIZ_EXECUTE_ERR_MSG);
        } else {
            msg.setOriginLen(resp.length);
            msg.setContent(compress(resp));
        }
        router.transMsg(msg);
    }

    public EsbMsg getReqmsg() {
        return reqmsg;
    }

    public void setReqmsg(EsbMsg reqmsg) {
        this.reqmsg = reqmsg;
    }

}
