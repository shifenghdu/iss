package com.db.iss.trade.api.compressor;

/**
 * Created by andy on 16/6/25.
 * @author andy.shif
 * 压缩接口
 */
public interface ICompressor {

    /**
     * 压缩
     * @param data
     * @return
     */
    byte[] compress(byte[] data);


    /**
     * 解压
     * @param compressed
     * @param uncompressSize
     * @return
     */
    byte[] decompress(byte[] compressed, int uncompressSize);

}
