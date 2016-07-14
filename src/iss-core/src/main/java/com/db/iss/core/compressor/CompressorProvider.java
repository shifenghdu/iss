package com.db.iss.core.compressor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andy on 16/6/25.
 * @author andy.shif
 * 压缩实现工厂
 */
public class CompressorProvider {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 实现注册
     */
    private Map<String,String> className = new ConcurrentHashMap<String,String>(){{
        put(CompressorType.LZ4.getValue(),"com.db.iss.core.compressor.lz4.Lz4Compressor");
    }};

    /**
     * 压缩组件实现类型
     */
    private String type = CompressorType.NULL.getValue();


    /**
     * 获取压缩组件实现
     * @return
     */
    public ICompressor getCompressor(){
        try {
            if(type.equals(CompressorType.NULL.getValue())){
                return null;
            }
            return (ICompressor) Class.forName(className.get(type)).newInstance();
        } catch (Throwable e) {
            logger.error("get compressor failed",e);
        }
        return null;
    }


}
