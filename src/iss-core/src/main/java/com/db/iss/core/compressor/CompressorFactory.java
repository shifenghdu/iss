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
public class CompressorFactory {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<CompressorType,String> className = new ConcurrentHashMap<CompressorType,String>(){{
        put(CompressorType.LZ4,"com.db.iss.core.compressor.lz4.Lz4Compressor");
    }};


    public ICompressor getCompressor(CompressorType type){
        try {
            if(type == null) return null;
            return (ICompressor) Class.forName(className.get(type)).newInstance();
        } catch (Throwable e) {
            logger.error("get compressor failed",e);
        }
        return null;
    }


}
