package com.ipharmacare.iss.core.http;

import com.ipharmacare.iss.common.esb.EsbMsg;
import com.ipharmacare.iss.common.util.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Administrator on 2015/4/8 0008.
 */
public class HttpHandler extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static AtomicLong count = new AtomicLong(0);

    private JettyAccess owner;

    private Map<String, EsbMsg> respMap = new ConcurrentHashMap<String, EsbMsg>();


    public HttpHandler(JettyAccess jettyAccess) {
        this.owner = jettyAccess;
    }


    /**
     * convert get message to post
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("HttpHandler client: [{}] do get request", req.getRemoteAddr());
    }

    /**
     * do post message
     *
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("HttpHandler client: [{}] length: [{}] begin dopost", req.getRemoteAddr(), req.getContentLength());
        try {
            InputStream in = req.getInputStream();
            byte[] reqMsg = new byte[req.getContentLength()];
            int len = 0;
            while (len < req.getContentLength()) {
                len += in.read(reqMsg, len, req.getContentLength() - len);
            }
            in.close();

            long current = count.incrementAndGet();

            logger.info("收到数据 [{}]", new String(HexUtil.hexToAscii(reqMsg)));
            EsbMsg pack = new EsbMsg();
            pack.setSystemid(owner.getSystemid());
            pack.setFunctionid(owner.getFunctionid());
            pack.setSendname(owner.getPluginName());
            pack.setSendarg(String.valueOf(req.hashCode()));
            logger.debug("接收数据HASHCODE [{}]", pack.getSendarg());
            pack.addTimetick(owner.getNodeName(), owner.getPluginName(),
                    System.nanoTime());
            pack.setContent((byte[]) reqMsg);
            if (current % 1000 == 0) {
                logger.info(String.format("Http收到消息数[%d]", current));
            }
            respMap.put(String.valueOf(req.hashCode()), pack);
            long cost = 0;
            synchronized (pack) {
                owner.sendMsg(pack);
                long begin = System.currentTimeMillis();
                pack.wait();
                long end = System.currentTimeMillis();
                cost = end - begin;
                logger.debug("服务处理耗时 [{}]ms", cost);
            }
            EsbMsg rspmsg = respMap.get(String.valueOf(req.hashCode()));
            OutputStream out = resp.getOutputStream();
            logger.debug("返回数据长度 [{}]", rspmsg.getContent().length);
            out.write(rspmsg.getContent());
            out.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.debug("HttpListener client: [{}] end dopost", req.getRemoteAddr());
    }

    public void send(EsbMsg msg) {
        logger.debug("发送数据长度 [{}]", msg.getContent().length);
        logger.debug("发送数据HASHCODE [{}]", msg.getSendarg());
        EsbMsg omsg = respMap.get(msg.getSendarg());
        synchronized (omsg) {
            respMap.put(msg.getSendarg(), msg);
            omsg.notify();
        }
    }

}
