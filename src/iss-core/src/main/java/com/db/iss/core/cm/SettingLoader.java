package com.db.iss.core.cm;

/**
 * Created by andy on 16/6/27.
 * @author andy.shif
 * 配置载入器
 */
public class SettingLoader {

    private static final String DEFAULT_CONFIG_FILE = "iss.properties";

    /**
     * 默认配置设置
     */
    private static Setting setting = new Setting(){{
        setProperty(SettingKey.SERIALIZER.getValue(),"json");
        setProperty(SettingKey.COMPRESSOR.getValue(),"lz4");
        setProperty(SettingKey.PIPE.getValue(),"cluster-mina|logger|dispatcher");
        setProperty(SettingKey.LISTEN.getValue(),String.valueOf((int)(Math.random() * 10000 + 10000))); //10000 - 20000
        setProperty(SettingKey.LOGSWICTH.getValue(),"off");
    }};

//    private static String[] checking = new String[]{
//            SettingKey.REGISTRY.getValue(),SettingKey.NODE.getValue()
//    };

    /**
     * 载入配置
     */
//    static {
//        Properties properties = new Properties();
//        try {
//            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE));
//        } catch (Throwable e) {
//            System.err.println("load classpath iss.properties failed");
//        }
//        setting.setProperties(properties);
//        checkSetting();
//    }

    public static Setting getSetting(){
        return setting;
    }

//    public static boolean checkSetting(){
//        for(String check : checking){
//            String s = setting.getProperty(check);
//            if(s == null || s.isEmpty()){
//                throw new SettingException(String.format("required settings not found %s",check));
//            }
//        }
//        return true;
//    }

}
