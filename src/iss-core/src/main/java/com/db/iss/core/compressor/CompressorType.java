package com.db.iss.core.compressor;

/**
 * Created by andy on 16/6/25.
 * @author andy.shif
 * 压缩算法类型
 */
public enum CompressorType {
    NULL("null"),
    LZ4("LZ4");

    private String value;

    CompressorType(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
