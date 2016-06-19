package com.db.iss.core.ftp;

import com.db.iss.core.Laucher;
import com.db.iss.core.config.BaseConfig;
import com.db.iss.core.router.IRouter;
import com.db.iss.common.esb.EsbMsg;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2015/4/11 0011.
 */
@Service("p_ftp")
public class FtpService implements IFtpService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private FtpServer server;

    @Autowired
    private IRouter router;

    @Autowired
    private BaseConfig config;

    private String pluginName = "ftp";

    private String nodeName;

    private int port;

    @Override
    public boolean transMsg(EsbMsg pack) {
        return true;
    }

    @Override
    public void onStart(ApplicationContext context) {

        if (!config.getPluginConfig(pluginName)
                .attributeValue("port").isEmpty()) {
            port = Integer.valueOf(config.getPluginConfig(pluginName)
                    .attributeValue("port"));
        } else {
            port = 21;
        }

        FtpServerFactory serverFactory = new FtpServerFactory();

        ListenerFactory factory = new ListenerFactory();

        // set the port of the listener
        factory.setPort(port);
        logger.debug("----------------Ftp server 端口 " + port + "----------------------------");
        // replace the default listener
        serverFactory.addListener("default", factory.createListener());

        //设置用户配置信息
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        userManagerFactory.setFile(new File(String.format("%s/etc/ftp-users.properties", System.getProperty(Laucher.SYSPATH))));

        logger.debug("----------------Ftp server 设置用户配置信息----------------------------");

        //注册FTP事件监听
//        Map<String, Ftplet> ftplets = new LinkedHashMap<String, Ftplet>();
//        ftplets.put(FtpletNotification.class.getName(), new FtpletNotification());
//        serverFactory.setFtplets(ftplets);
//        logger.debug("----------------Ftp server 注册FTP事件监听----------------------------");

        serverFactory.setUserManager(userManagerFactory.createUserManager());
        logger.info("----------------Ftp server 准备启动----------------------------");

        // start the server
        server = serverFactory.createServer();
        try {
            server.start();
            int port = serverFactory.getListener("default").getPort();
            String serverAddress = serverFactory.getListener("default").getServerAddress();
            if (serverAddress == null) {
                try {
                    serverAddress = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
            logger.info("----------------Ftp serverAddress----------------------------" + serverAddress);

            logger.info("----------------Ftp server 启动成功----------------------------");
        } catch (FtpException e) {
            logger.error("FTP server 启动失败 ", e);
        }
    }

    @Override
    public void onStop() {
        server.stop();
    }
}
