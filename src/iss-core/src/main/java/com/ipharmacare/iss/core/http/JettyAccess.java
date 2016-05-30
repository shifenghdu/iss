package com.ipharmacare.iss.core.http;

import com.ipharmacare.iss.core.Laucher;
import com.ipharmacare.iss.core.config.BaseConfig;
import com.ipharmacare.iss.core.router.IRouter;
import com.ipharmacare.iss.core.tcpshort.mina.AccessAcceptor;
import com.ipharmacare.iss.common.esb.EsbMsg;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Administrator on 2015/4/8 0008.
 */
@Service("p_http")
public class JettyAccess implements IHttpAccess {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IRouter router;

    @Autowired
    private BaseConfig config;

    private AccessAcceptor acceptor = null;

    private String pluginName = "http";

    private String nodeName;

    private static AtomicLong count = new AtomicLong(0);

    private int timeout;

    private int isHttps = 0;

    private HttpHandler handler;

    private Server httpServer;

    private String keypath;

    private String passWord;

    private String keyPassWord;

    private String trustPassWord;

    private String protocol;

    private int headsize;

    private int systemid;

    private int functionid;

    private String encode;

    @Override
    public boolean transMsg(EsbMsg pack) {
        handler.send(pack);
        return true;
    }

    @Override
    public void onStart(ApplicationContext context) {
        String port = config.getPluginConfig(pluginName).attributeValue("port");
        timeout = Integer.valueOf(config.getPluginConfig(pluginName)
                .attributeValue("timeout"));
        isHttps = Integer.valueOf(config.getPluginConfig(pluginName)
                .attributeValue("ishttps"));
        keypath = config.getPluginConfig(pluginName)
                .attributeValue("keypath");
        passWord = config.getPluginConfig(pluginName)
                .attributeValue("password");
        keyPassWord = config.getPluginConfig(pluginName)
                .attributeValue("keypassword");

        trustPassWord = config.getPluginConfig(pluginName)
                .attributeValue("trustpassword");

        protocol = config.getPluginConfig(pluginName)
                .attributeValue("protocol");
        headsize = Integer.valueOf(config.getPluginConfig(pluginName)
                .attributeValue("headsize"));

        systemid = Integer.valueOf(config.getPluginConfig(pluginName)
                .attributeValue("targetsystemid"));

        functionid = Integer.valueOf(config.getPluginConfig(pluginName)
                .attributeValue("targetfunctionid"));

        encode = config.getPluginConfig(pluginName).attributeValue("encode");
        this.nodeName = Laucher.nodeName;

        this.nodeName = Laucher.nodeName;
        if (!port.isEmpty()) {
            httpServer = new Server();
            // jettyThreadPool = new JettyThreadPool();
            // httpServer.setThreadPool(jettyThreadPool);
            Connector connector = null;
            if (isHttps == 0) {
                connector = new SelectChannelConnector();
                ((SelectChannelConnector) connector).setAcceptors(1);
                connector.setHost("0.0.0.0");
                connector.setPort(Integer.valueOf(port));
                connector.setMaxIdleTime((int) timeout);
                //connector.setRequestHeaderSize(10 * 1024);
                //connector.setRequestBufferSize(20 * 1024);
                //((SelectChannelConnector) connector).setMaxBuffers(1024 * 1024);
                ((SelectChannelConnector) connector).setAcceptQueueSize(Integer.MAX_VALUE);
                ((SelectChannelConnector) connector).setConfidentialScheme("http");
            } else {
                connector = new SslSocketConnector();
                ((SslSocketConnector) connector).setAcceptors(1);
                ((SslSocketConnector) connector).setHost("0.0.0.0");
                ((SslSocketConnector) connector).setPort(Integer.valueOf(port));
                ((SslSocketConnector) connector).setMaxIdleTime((int) timeout);
                ((SslSocketConnector) connector).setAcceptQueueSize(Integer.MAX_VALUE);
                //connector.setRequestHeaderSize(10 * 1024);
                //connector.setRequestBufferSize(20 * 1024);
                //((SslSocketConnector) connector).setMaxBuffers(1024 * 1024);
                ((SslSocketConnector) connector).setKeystore(System.getProperty(Laucher.SYSPATH) + "/key/" + keypath);
                ((SslSocketConnector) connector).setKeyPassword(keyPassWord);
                ((SslSocketConnector) connector).setPassword(passWord);
                ((SslSocketConnector) connector).setProtocol(protocol);
                //((SslSocketConnector) connector).setNeedClientAuth(true);
                //((SslSocketConnector) connector).setTruststore(System.getProperty(Laucher.SYSPATH) + "/key/trust/" + keypath);
                //((SslSocketConnector) connector).setTrustPassword(trustPassWord);
                ((SslSocketConnector) connector).setConfidentialScheme("https");

            }

            httpServer.setConnectors(new Connector[]{connector});
            handler = new HttpHandler(this);

            ServletHandler servletHandler = new ServletHandler();
            ServletHolder servletHolder = new ServletHolder();
            servletHolder.setServlet(handler);
            servletHandler.addServletWithMapping(servletHolder, "/");
            httpServer.setHandler(servletHandler);
            //httpServer.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", -1);
            try {
                httpServer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {

    }


    public boolean sendMsg(EsbMsg pack) {
        return router.transMsg(pack);
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getHeadsize() {
        return headsize;
    }

    public void setHeadsize(int headsize) {
        this.headsize = headsize;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public int getSystemid() {
        return systemid;
    }

    public void setSystemid(int systemid) {
        this.systemid = systemid;
    }

    public int getFunctionid() {
        return functionid;
    }

    public void setFunctionid(int functionid) {
        this.functionid = functionid;
    }
}
