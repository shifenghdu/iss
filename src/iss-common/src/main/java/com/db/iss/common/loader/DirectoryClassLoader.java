package com.db.iss.common.loader;


import sun.net.www.protocol.jar.JarURLConnection;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by andy on 2016/6/16.
 */
public class DirectoryClassLoader extends URLClassLoader{

    private static final String JAR_SUFFIX = ".jar";
    private static final String CLASS_SUFFIX = ".class";

    private Set<String> classNames;

    public DirectoryClassLoader(String[] dirs) {
        this(dirs,null);
    }

    public DirectoryClassLoader(String[] dirs, ClassLoader parent) {
        super(new URL[]{},parent);
        List<URL> urls =  getJarUrls(dirs);
        for(URL url : urls) {
            this.addURL(url);
        }
    }

    /**
     * 获取路径下所有jar URL
     * @param dirs
     * @return
     */
    private List<URL> getJarUrls(String[] dirs){
        List<URL> urls =  new ArrayList<URL>();
        if(dirs != null) {
            for (String dir : dirs) {
                File d = new File(dir);
                String[] files = d.list();
                for(String file : files){
                    File t = new File(String.format("%s/%s",dir,file));
                    if(t.isFile() && file.endsWith(JAR_SUFFIX)){
                        try {
                            //jar  URL   jar:file:xxxx/xxx.jar!/xxx.xxx.class
                            URL url = new URL(String.format("jar:file:%s/%s!/",dir,file));
                            urls.add(url);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    } else if(t.isDirectory()){
                        urls.addAll(getJarUrls(new String[]{String.format("%s/%s",dir,file)}));
                    }
                }
            }
        }
        return urls;
    }

    /**
     * 获取所有指定注解的class
     * @param annotation
     * @return
     */
    public <A extends Annotation> Set<Class> loadClassByAnnotation(String basePath,Class<A> annotation) throws IOException, ClassNotFoundException {
        Set<Class> result = new LinkedHashSet<Class>();
        if(classNames == null) {
            URL[] urls = getURLs();
            if (urls != null) {
                classNames = new LinkedHashSet<String>();
                for (URL url : urls) {
                    classNames.addAll(getJarClassNames(((JarURLConnection) url.openConnection()).getJarFile()));
                }
            }
        }

        for(String name : classNames){
            if(checkPackageMatch(basePath,name)){
                Class classz = loadClass(name);
                Annotation a = classz.getAnnotation(annotation);
                if(a != null){
                    result.add(classz);
                }
            }
        }
        return result;
    }

    /**
     * 仅获取jar包中的class jar包中jar包的class不支持
     * @param jarFile
     * @return
     * @throws IOException
     */
    private Set<String> getJarClassNames(JarFile jarFile) throws IOException {
        Set<String> result = new LinkedHashSet<String>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while(entries.hasMoreElements()){
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if(name.endsWith(CLASS_SUFFIX)){
                String nameSpace = toNameSpace(name);
                if(nameSpace != null){
                    result.add(nameSpace);
                }
            }
//            else if(name.endsWith(JAR_SUFFIX)){
////                URL url = new URL(String.format("jar:%s/%s!/",jarURLConnection.getJarFileURL().toString(),name));
//                entry.getExtra();
//                JarURLConnection connection = (JarURLConnection)url.openConnection();
//                System.out.println(connection.getJarFileURL());
//                result.addAll(getJarClasses(connection));
//            }
        }
        return result;
    }

    private String toNameSpace(String path){
        path = path.substring(0,path.length()-6);
        path = path.replace("/",".");
        //内部类含有$
        if(path.indexOf("$") != -1){
            return null;
        }
        return path;
    }

    private boolean checkPackageMatch(String src, String dest){
        return dest.startsWith(src);
    }

}
