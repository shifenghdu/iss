package com.db.iss.trade.api.compressor.lz4;

import com.db.iss.trade.api.compressor.ICompressor;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4SafeDecompressor;

import java.nio.ByteBuffer;

/**
 * Created by andy on 16/6/25.
 * @author andy.shif
 * Lz4压缩实现
 */
public class Lz4Compressor implements ICompressor {

    private LZ4Factory factory = LZ4Factory.fastestInstance();


    @Override
    public byte[] compress(byte[] data) {
        LZ4Compressor compressor = factory.fastCompressor();
        int maxCompressedLength = compressor.maxCompressedLength(data.length);
        byte[] compressed = new byte[maxCompressedLength];
        int compressedLength = compressor.compress(data, 0, data.length, compressed, 0, maxCompressedLength);
        ByteBuffer buffer = ByteBuffer.allocate(compressedLength);
        buffer.put(compressed,0,compressedLength);
        buffer.flip();
        return buffer.array();
    }

    @Override
    public byte[] decompress(byte[] compressed, int uncompressSize) {
        LZ4SafeDecompressor decompressor = factory.safeDecompressor();
        byte[] restored = new byte[uncompressSize];
        decompressor.decompress(compressed, 0,compressed.length, restored, 0);
        return restored;
    }
}
