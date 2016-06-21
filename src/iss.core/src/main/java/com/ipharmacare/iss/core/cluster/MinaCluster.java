package com.ipharmacare.iss.core.cluster;

import com.ipharmacare.iss.common.SystemConst;
import com.ipharmacare.iss.common.esb.EsbMsg;
import com.ipharmacare.iss.core.Laucher;
import com.ipharmacare.iss.core.cluster.mina.ClusterAcceptor;
import com.ipharmacare.iss.core.cluster.mina.ClusterConnector;
import com.ipharmacare.iss.core.config.BaseConfig;
import com.ipharmacare.iss.core.msgpack.CommonCodeFactory;
import com.ipharmacare.iss.core.router.IRouter;
import org.apache.mina.core.session.IoSession;
import org.dom4j.Element;
import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Service("p_cluster")
public class MinaCluster implements ICluster {

    private ConcurrentHashMap<String, NodeGroup> nodesMap = new ConcurrentHashMap<String, NodeGroup>();

    private ClusterAcceptor acceptor = null;

    private ClusterConnector connector = null;

    @Autowired
    private IRouter router;

    private String nodeName;

    @Autowired
    private BaseConfig config;

    private MessagePack msgpack;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String pluginName = "cluster";

    private final int RETRY_TIMES = 3;

    @Autowired
    private CommonCodeFactory codecFactory;

    public CommonCodeFactory getCodecFactory() {
        return codecFactory;
    }

    @Override
    public boolean transMsg(EsbMsg pack) {
        String next = pack.getNextnode();
        if (next == null) {
            pack.changeToResponse();
            pack.setRetcode(SystemConst.ESB_TRANS_ERR);
            pack.setRetmsg(SystemConst.ESB_TRANS_ERR_MSG);
            router.transMsg(pack);
            return true;
        }
        NodeGroup group = nodesMap.get(next);
        if (group == null) {
            pack.changeToResponse();
            pack.setRetcode(SystemConst.ESB_TRANS_ERR);
            pack.setRetmsg(SystemConst.ESB_TRANS_ERR_MSG);
            router.transMsg(pack);
            return true;
        } else {
            if (pack.getMsgtype() == EsbMsg.MSGTYPE_REQ) {//req
                IoSession session = group.getNode();
                if (session == null) {
                    logger.error(String.format("请求消息无法转发 System: [%d], Function: [%d], next: [%s] ", pack.getSystemid(), pack.getFunctionid(), pack.getNextnode()));
                    pack.changeToResponse();
                    pack.setRetcode(SystemConst.ESB_TRANS_ERR);
                    pack.setRetmsg(SystemConst.ESB_TRANS_ERR_MSG);
                    pack.popLastRouteInfo();
                    router.transMsg(pack);
                    return true;
                } else {
                    pack.addTimetick(getNodeName(), getPluginName(),
                            System.nanoTime());
                    if(!writeSession(session,pack,RETRY_TIMES)){
                        logger.error("请求消息写入失败: System: [{}], Function: [{}]", pack.getSystemid(), pack.getFunctionid());
                        return false;
                    }
                }
            } else {// resp
                if (pack.getNextSession() == 0) {
                    logger.error("返回消息Session路径丢失: System: [{}], Function: [{}]", pack.getSystemid(), pack.getFunctionid());
                } else {
                    IoSession session = group.getNode(pack.getNextSession());
                    if (session == null) {
                        logger.error("返回消息Session断开: System: [{}], Function: [{}]", pack.getSystemid(), pack.getFunctionid());
                        return true;
                    } else {
                        pack.addTimetick(getNodeName(), getPluginName(),
                                System.nanoTime());
                        if(!writeSession(session,pack,RETRY_TIMES)){
                            logger.error("返回消息写入失败: System: [{}], Function: [{}]", pack.getSystemid(), pack.getFunctionid());
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean writeSession(IoSession session,EsbMsg msg,int times){
        int t = 0;
        if(session == null || session.isClosing() || msg == null) return false;
        while (t < times) {
            if(session.write(msg).awaitUninterruptibly().isWritten()){
                return true;
            }
            t ++;
        }
        session.close(true);
        return false;
    }

    @PostConstruct
    public void init() {
        msgpack = new MessagePack();
        msgpack.register(EsbMsg.class);
    }

    @Override
    public void onStart(ApplicationContext context) {
        this.nodeName = Laucher.nodeName;
        Element cluster = config.getPluginConfig("cluster");
        if (cluster == null)
            return;
        acceptor = new ClusterAcceptor(this);
        acceptor.bind(Integer.valueOf(cluster.attributeValue("port")));
        connector = new ClusterConnector(this);

        @SuppressWarnings("unchecked")
        final List<Element> neighbors = cluster.element("neighbors").elements(
                "neighbor");
        new Thread(new Runnable() {
            public void run() {
                MDC.put("node", nodeName);
                while (!neighbors.isEmpty()) {
                    List<Element> copyList = new ArrayList<Element>(neighbors);
                    for (Element e : copyList) {
                        try {
                            int num = 1;
                            if (e.attributeValue("connect_num") != null && !e.attributeValue("connect_num").isEmpty()) {
                                num = Integer.valueOf(e.attributeValue("connect_num"));
                            }
                            for (int i = 0; i < num; i++) {
                                IoSession session = connector.connect(e
                                        .attributeValue("address"));
                                if (session == null)
                                    continue;
                                session.setAttribute("nodename",
                                        e.attributeValue("node"));
                                session.setAttribute("address",
                                        e.attributeValue("address"));
                                addNeighbor(e.attributeValue("node"), session);
                                EsbMsg msg = new EsbMsg();
                                msg.setMsgtype(2);
                                msg.setFunctionid(2);
                                msg.pushRouteInfo(nodeName);
                                if(writeSession(session,msg,RETRY_TIMES)){
                                    neighbors.remove(e);
                                }else{
                                    logger.error("send hello failed  [{}://{}]", e.attributeValue("node"), e.attributeValue("address"));
                                }
                            }
                        } catch (Throwable ex) {
                            logger.error("connect failed  [{}://{}]", e.attributeValue("node"), e.attributeValue("address"));
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    public void onStop() {

    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public synchronized void addNeighbor(String name, IoSession session) {
        logger.info("addNeighbor [{}] sesseion[{}]", name, session);
        NodeGroup group = nodesMap.get(name);
        if (group == null) {
            group = new NodeGroup();
            nodesMap.put(name, group);
        }
        group.addNode(session);
    }

    public void reconnect(final String name, final String addr) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                IoSession session = null;
                while (session == null) {
                    try {
                        Thread.sleep(10000);
                        session = connector.connect(addr);

                    } catch (Throwable e) {
                        logger.error(String.format("reconnect failed  [%s://%s]",name,addr),e);
                    }
                }
                session.setAttribute("nodename", name);
                session.setAttribute("address", addr);
                addNeighbor(name, session);
                EsbMsg msg = new EsbMsg();
                msg.setMsgtype(2);
                msg.setFunctionid(2);
                msg.pushRouteInfo(nodeName);
                if(!writeSession(session,msg,RETRY_TIMES)){
                    session.close(true);
                }
            }
        }).start();

    }

    public boolean sendMsg(EsbMsg pack) {
        return router.transMsg(pack);
    }

    public String getPluginName() {
        return pluginName;
    }

}
