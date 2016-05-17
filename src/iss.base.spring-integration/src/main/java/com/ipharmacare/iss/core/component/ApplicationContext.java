package com.ipharmacare.iss.core.component;

import com.ipharmacare.iss.common.dispatch.IBizContext;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.InputStream;

/**
 * Created by andy on 2015/12/26.
 */
public class ApplicationContext {
    private static org.springframework.context.ApplicationContext applicationContext = null;
    private static Document document = null;
    private static String packagePath = null;

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<?> classz) throws BeansException {
        return (T) applicationContext.getBean(classz);
    }

    public  static String getScanPackage(){
        return packagePath;
    }

    public static void initFromConfig() {
        InputStream ins =  ApplicationContext.class.getClassLoader().getResourceAsStream("component.xml");
        SAXReader reader = new SAXReader();
        try {
            document = reader.read(ins);
        } catch (Exception e) {
            System.err.println("load config component.xml failed");
        }
        Element element = document.getRootElement().element("contexts");
        String[] ctxs = element.getStringValue().split(",");
        applicationContext = new ClassPathXmlApplicationContext(ctxs);

        element = document.getRootElement().element("scanner");
        packagePath = element.attributeValue("package").toString();
    }
}
