package com.db.iss.core.registry.url;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * Created by andy on 16/6/28.
 * @author andy.shif
 */
public class TcpProtocolHandler extends URLStreamHandler {

    protected int getDefaultPort() {
            return 9001;
        }


    protected URLConnection openConnection(URL url) throws IOException {
        return this.openConnection(url, null);
    }

    protected URLConnection openConnection(URL url, Proxy proxy) throws IOException {
        return null;
    }
}
