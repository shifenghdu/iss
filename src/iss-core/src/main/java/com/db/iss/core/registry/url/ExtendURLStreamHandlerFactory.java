package com.db.iss.core.registry.url;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * Created by andy on 16/6/28.
 * @author andy.shif
 *
 */
public class ExtendURLStreamHandlerFactory implements URLStreamHandlerFactory {

    private static String PREFIX = "sun.net.www.protocol";

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if(protocol.equalsIgnoreCase("tcp")) return new TcpProtocolHandler();
        String var2 = PREFIX + "." + protocol + ".Handler";
        try {
            Class var3 = Class.forName(var2);
            return (URLStreamHandler)var3.newInstance();
        } catch (ClassNotFoundException var4) {
            var4.printStackTrace();
        } catch (InstantiationException var5) {
            var5.printStackTrace();
        } catch (IllegalAccessException var6) {
            var6.printStackTrace();
        }
        throw new InternalError("could not load " + protocol + " system protocol handler");
    }
}
