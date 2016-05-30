package com.ipharmacare.iss.core;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.ipharmacare.iss.common.plugin.IPlugin;
import org.dom4j.Element;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.ipharmacare.iss.core.config.BaseConfig;

public class Laucher {

    private static ApplicationContext ctx;

    public static final String CONFIG_ARGS = "iss.config";

    public static final String SYSPATH = "iss.path";

    public static String nodeName;

    public static boolean initlog() {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();
        try {
            System.out.println("Load log configuration [" + System.getProperty(SYSPATH)
                    + "/etc/logback.xml]");
            configurator.doConfigure(new File(System.getProperty(SYSPATH)
                    + "/etc/logback.xml"));
        } catch (JoranException e) {
            System.err.println("Load log configuration failed");
            return false;
        }
        return true;

    }

    public static void main(String[] args) {
        System.out
                .println("******************** Begin Start ISS Runtime Environment ********************");

        ctx = new ClassPathXmlApplicationContext(
                new String[]{"appcontext.xml"});
        if (!initlog())
            return;

        // 检查配置文件
        System.out.println("Load system configuration[" + System.getProperty(SYSPATH) + "/etc/"
                + System.getProperty(CONFIG_ARGS) + "]");
        if (System.getProperty(CONFIG_ARGS).isEmpty()) {
            System.err.println("Load system configuration failed");
            return;
        } else {
            File file = new File(System.getProperty(SYSPATH) + "/etc/"
                    + System.getProperty(CONFIG_ARGS));
            if (!file.exists()) {
                System.err.println("Load system configuration failed");
                return;
            }
        }

        // 启动插件
        BaseConfig config = ctx.getBean("s_config", BaseConfig.class);
        nodeName = config.getDocument().getRootElement().attributeValue("name");
        MDC.put("node", nodeName);
        System.out.println(String.format("Start node [%s] pid [%s]", nodeName,ManagementFactory.getRuntimeMXBean().getName()));
        @SuppressWarnings("unchecked")
        List<Element> elements = config.getDocument().getRootElement()
                .element("plugins").elements("plugin");
        for (Element element : elements) {
            String pluginName = "p_" + element.attributeValue("name");
            IPlugin plugin = ctx.getBean(pluginName, IPlugin.class);
            if (plugin != null) {
                plugin.onStart(ctx);
                System.out.println("plugin [" + element.attributeValue("name")
                        + "] start succed");
            } else {
                System.err.println("plugin [" + element.attributeValue("name")
                        + "] start failed");
            }
        }
        System.out
                .println("******************** Finish Start ISS Runtime Environment ********************");
    }
}
