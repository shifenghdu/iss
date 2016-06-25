package com.db.iss.trade.api.compressor;

/**
 * Created by andy on 16/6/25.
 * @author andy.shif
 * 压缩算法类型
 */
public enum CompressorType {

    LZ4(0,"LZ4");

    private int value;
    private String name;

    CompressorType(int value, String name){
        this.value = value;
        this.name = name;
    }
}
