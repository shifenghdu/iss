package com.ipharmacare.iss.core.dispatch;

import com.ipharmacare.iss.core.router.IRouter;
import com.ipharmacare.iss.common.dispatch.IBizProcessor;
import com.ipharmacare.iss.common.esb.EsbMsg;
import com.ipharmacare.iss.common.SystemConst;
import com.ipharmacare.iss.common.dispatch.IBizContext;
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

    @Override
    public byte[] call(int systemId, int functionId, byte[] msg) {
        return this.call(systemId, functionId, "", msg);
    }

    @Override
    public byte[] call(int systemId, int functionId, String tag, byte[] msg) {
        EsbMsg reqMsg = new EsbMsg();
        reqMsg.setContent(msg);
        reqMsg.setSystemid(systemId);
        reqMsg.setFunctionid(functionId);
        reqMsg.setTag(tag);
        reqMsg.setMsgtype(EsbMsg.MSGTYPE_REQ);
        reqMsg.setSendname(dispatcher.getPluginName());
        reqMsg.setSendarg(String.valueOf(Thread.currentThread().getId()));
        reqMsg.setIsCopySend(false);
        // concurrentHashMap.put(String.valueOf(Thread.currentThread().getId()),reqMsg);
        dispatcher.pollMsg(Thread.currentThread().getId(), reqMsg);

        long startTime = System.currentTimeMillis();
        int timeoutReal = 0;
        try {
            synchronized (reqMsg) {
                router.transMsg(reqMsg);
                timeoutReal = timeout != 0 ? timeout : 500;
                reqMsg.wait(timeoutReal);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("call wait end [{}]", reqMsg.hashCode());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long processTime = endTime - startTime;
        if (processTime >= timeoutReal) {
            // dispatcher.getMsg(Thread.currentThread().getId()).setRetcode(-1);
            throw new RuntimeException("调用远程服务超时");
//            return null;
        }
        EsbMsg dest = dispatcher.getMsg(Thread.currentThread().getId());
        if(dest.hashCode() == reqMsg.hashCode()){
            logger.error("系统被中断异常唤醒");
            return null;
        }
        return dest.getContent();
    }

    @Override
    public void post(int systemId, int functionId, byte[] msg) {
        this.post(systemId, functionId, "", msg);
    }

    @Override
    public void post(int systemId, int functionId, String tag, byte[] msg) {
        EsbMsg reqMsg = new EsbMsg();
        reqMsg.setContent(msg);
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
        reqMsg.setContent(msg);
        reqMsg.setSystemid(systemId);
        reqMsg.setFunctionid(functionId);
        reqMsg.setTag(tag);
        reqMsg.setMsgtype(EsbMsg.MSGTYPE_REQ);
        reqMsg.setSendname(dispatcher.getPluginName());
        reqMsg.setSendarg(String.valueOf(Thread.currentThread().getId()));
        reqMsg.setIsCopySend(true);
        reqMsg.setCopyCount(1);
        dispatcher.pollMsg(Thread.currentThread().getId(), reqMsg);
        long startTime = System.currentTimeMillis();
        int timeoutReal = 0;
        try {
            synchronized (reqMsg) {
                router.transMsg(reqMsg);
                timeoutReal = timeout != 0 ? timeout : 500;
                reqMsg.wait(timeoutReal);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("call wait end [{}]", reqMsg.hashCode());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long processTime = endTime - startTime;
        if (processTime >= timeoutReal) {
            // dispatcher.getMsg(Thread.currentThread().getId()).setRetcode(-1);
//            return null;
            throw new RuntimeException("调用远程服务超时");
        }
        List<EsbMsg> responses = reqMsg.getResponse();
        if(responses.size() == 0){
            logger.error("系统被中断异常唤醒");
            return null;
        }
        ArrayList<byte[]> results = new ArrayList<byte[]>();
        for (EsbMsg esbMsg : responses) {
            results.add(esbMsg.getContent());
        }
        return results;
    }

    @Override
    public void run() {
        MDC.put("node", dispatcher.getNodeName());
        EsbMsg msg = reqmsg;
        Thread.currentThread().setContextClassLoader(processor.getClass().getClassLoader());
        byte[] resp = processor.doProcess(this, msg.getContent());
        msg.changeToResponse();
        if (resp == null) {
            if (logger.isWarnEnabled())
                logger.warn("业务处理出错 system [{}] function [{}]", msg.getSystemid(), msg.getFunctionid());
            msg.setRetcode(SystemConst.ESB_BIZ_EXECUTE_ERR);
            msg.setRetmsg(SystemConst.ESB_BIZ_EXECUTE_ERR_MSG);
        } else {
            msg.setContent(resp);
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
