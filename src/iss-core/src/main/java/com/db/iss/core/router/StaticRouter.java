package com.db.iss.core.router;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.db.iss.common.esb.EsbMsg;
import com.db.iss.common.plugin.IPlugin;
import com.db.iss.core.Laucher;
import com.db.iss.common.SystemConst;
import com.db.iss.core.cluster.ICluster;
import com.db.iss.core.config.BaseConfig;

@Service("p_router")
public class StaticRouter implements IRouter, Runnable {

    private ApplicationContext context;

    private Vector<RouterItem> systemRouters = new Vector<RouterItem>();

    private Vector<RouterItem> functionRouters = new Vector<RouterItem>();

    @Autowired
    private BaseConfig config;

    @Autowired
    private ICluster cluster;

    private BlockingQueue<EsbMsg> inQueue = new LinkedBlockingDeque<EsbMsg>();

    private Logger logger = LoggerFactory.getLogger(getClass());

    private List<Thread> threads = new Vector<Thread>();

    private String nodeName;

    public String getNodeName() {
        return nodeName;
    }

    private String pluginName = "router";

    public static AtomicLong count = new AtomicLong(0);

    @Override
    public boolean transMsg(EsbMsg pack) {
        return inQueue.add(pack);
    }

    @Override
    public void onStart(ApplicationContext context) {
        this.context = context;
        this.nodeName = Laucher.nodeName;
        @SuppressWarnings("unchecked")
        List<Element> tables = config.getPluginConfig("router")
                .element("routetables").elements("routetable");
        for (Element item : tables) {
            // 系统路由
            if ((item.attributeValue("systemid") != null
                    && !item.attributeValue("systemid").isEmpty()) &&
                    (item.attributeValue("functionid") != null
                            && !item.attributeValue("functionid").isEmpty())
                    && item.attributeValue("node") != null
                    && !item.attributeValue("node").isEmpty()) {
                RouterItem routerItem = new RouterItem();
                routerItem.setSystemid(item.attributeValue("systemid"));
                routerItem.setFuctionid(item.attributeValue("functionid"));
                routerItem.setTag(item.attributeValue("tag"));
                routerItem.setNode(item.attributeValue("node"));
                if (item.attributeValue("truncate") != null) {
                    if (item.attributeValue("truncate").isEmpty()) {
                        routerItem.setTruncate("true");
                    } else {
                        routerItem.setTruncate(item.attributeValue("truncate"));
                    }
                } else {
                    routerItem.setTruncate("1");
                }
                systemRouters.add(routerItem);
            }
            // 功能路由
            if (item.attributeValue("functionid") != null
                    && !item.attributeValue("functionid").isEmpty()
                    && item.attributeValue("plugin") != null
                    && !item.attributeValue("plugin").isEmpty()) {
                RouterItem routerItem = new RouterItem();
                routerItem.setFuctionid(item.attributeValue("functionid"));
                routerItem.setPlugin(item.attributeValue("plugin"));
                if (item.attributeValue("truncate") != null) {
                    if (item.attributeValue("truncate").isEmpty()) {
                        routerItem.setTruncate("true");
                    } else {
                        routerItem.setTruncate(item.attributeValue("truncate"));
                    }
                } else {
                    routerItem.setTruncate("1");
                }
                functionRouters.add(routerItem);
            }
        }
        int threadcount = 0;
        String count = config.getPluginConfig("router")
                .attributeValue("thread");
        if (count.isEmpty()) {
            threadcount = Runtime.getRuntime().availableProcessors() + 1;
        } else {
            threadcount = Integer.valueOf(count);
        }
        for (int i = 0; i < threadcount; ++i) {
            Thread thread = new Thread(this, String.format("Router_%d", i));
            threads.add(thread);
            thread.start();
        }
        logger.debug("开启router线程数[{}]", threadcount);
    }

    @Override
    public void onStop() {

    }

    public boolean doRouter(EsbMsg pack) {
        pack.addTimetick(getNodeName(), getPluginName(), System.nanoTime());

        long current = count.incrementAndGet();
        if (logger.isInfoEnabled()) {
            if (current % 1000 == 0) {
                logger.info(String.format("Router转发消息数[%d]", current));
            }
        }
        if (logger.isDebugEnabled())
            logger.debug("路由处理消息 pack[{}]", pack);

        // 路由消息至远程节点
        if (pack.getMsgtype() == EsbMsg.MSGTYPE_REQ) {
            pack.appendLastRouteInfo(pack.getNextSession().toString());
            boolean isMatch = false;
            List<RouterItem> matchItems = new ArrayList<RouterItem>();
            for (RouterItem item : systemRouters) {
                boolean result1 = false;
                boolean result2 = false;
                boolean result3 = false;
                if (item.getSystemid() != null && !item.getSystemid().isEmpty()) {
                    result1 = Pattern.matches(item.getSystemid(),
                            String.valueOf(pack.getSystemid()));
                } else {
                    result1 = true;
                }
                if (item.getFuctionid() != null && !item.getFuctionid().isEmpty()) {
                    result2 = Pattern.matches(item.getFuctionid(),
                            String.valueOf(pack.getFunctionid()));
                } else {
                    result2 = true;
                }
                if (item.getTag() != null && !item.getTag().isEmpty()) {
                    result3 = Pattern.matches(item.getTag(),
                            String.valueOf(pack.getTag()));
                } else {
                    result3 = true;
                }
                if (result1 && result2 && result3) {
                    matchItems.add(item);
                    if (item.getTruncate().equalsIgnoreCase("true")) {
                        break;
                    }
                }
            }
            if (matchItems.size() > 0) {
                pack.pushRouteInfo(nodeName);
                if(pack.isCopySend()) {
                    for (RouterItem item : matchItems) {
                        EsbMsg msg = new EsbMsg();
                        BeanUtils.copyProperties(pack, msg);
                        msg.setNextnode(item.getNode());
                        msg.setIsCopySend(pack.isCopySend());
                        msg.setCopyCount(matchItems.size());
                        if (logger.isDebugEnabled())
                            logger.debug("路由至远程节点  pack[{}]", msg);
                        cluster.transMsg(msg);
                    }
                }else{
                    //非并行调用获取第一条匹配路由
                    pack.setNextnode(matchItems.get(0).getNode());
                    if (logger.isDebugEnabled())
                        logger.debug("路由至远程节点  pack[{}]", pack);
                    cluster.transMsg(pack);
                }
                return true;
            }

            // 路由消息至本地插件
            for (RouterItem item : functionRouters) {
                boolean result = Pattern.matches(item.getFuctionid(),
                        String.valueOf(pack.getFunctionid()));
                if (result) {
                    isMatch = true;
                    String tag = String.format("p_%s", item.getPlugin());
                    IPlugin plugin = context.getBean(tag, IPlugin.class);
                    if (plugin != null) {
                        if (logger.isDebugEnabled())
                            logger.debug("路由至本地插件  pack[{}]", pack);
                        plugin.transMsg(pack);
                    } else {
                        backError(pack);
                    }
                    if (item.getTruncate().equals("true")) {
                        break;
                    }
                }
            }
            if (!isMatch) {
                backError(pack);
            }
        } else if (pack.getMsgtype() == EsbMsg.MSGTYPE_RESP) {
            if (pack.getRouteinfo().size() == 0) {
                // 已至源节点
                String tag = String.format("p_%s", pack.getSendname());
                IPlugin plugin = context.getBean(tag, IPlugin.class);
                if (plugin != null) {
                    if (logger.isDebugEnabled())
                        logger.debug("收到远程节点返回 pack[{}]", pack);
                    plugin.transMsg(pack);
                } else {
                    if (logger.isWarnEnabled())
                        logger.warn("无法返回丢弃 msg[{}]", pack);
                }
            } else {
                // 中间节点
                String next = pack.popLastRouteInfo();
                if (next != null) {
                    String[] rs = next.split("\\;");
                    pack.setNextnode(rs[0]);
                    if (rs.length > 1) {
                        pack.setNextSession(Long.valueOf(rs[1]));
                    } else {
                        pack.setNextSession((long) 0);
                    }
                    if (logger.isDebugEnabled())
                        logger.debug("转发返回 pack[{}]", pack);
                    cluster.transMsg(pack);
                }
            }
        }
        return true;
    }

    @Override
    public void run() {
        MDC.put("node", nodeName);
        while (true) {
            EsbMsg pack = null;
            try {
                pack = inQueue.take();
                doRouter(pack);
            } catch (Exception e) {
                logger.error("路由分发消息出错", e);
                continue;
            }
        }

    }

    private void backError(EsbMsg pack) {
        if (pack.getRouteinfo().size() != 0) {
            pack.changeToResponse();
            pack.setRetcode(SystemConst.ESB_TRANS_ERR);
            pack.setRetmsg(SystemConst.ESB_TRANS_ERR_MSG);
            cluster.transMsg(pack);
        } else {
            if (logger.isWarnEnabled())
                logger.warn("消息无法路由也无法返回丢弃 msg[{}]", pack);
        }
    }

    public String getPluginName() {
        return pluginName;
    }
}
