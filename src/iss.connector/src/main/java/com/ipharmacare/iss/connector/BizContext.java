package com.ipharmacare.iss.connector;

import com.ipharmacare.iss.common.dispatch.IBizContext;
import com.ipharmacare.iss.common.esb.EsbMsg;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4SafeDecompressor;
import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 2015/12/29.
 */
public class BizContext implements IBizContext {

    private Connector connector = null;
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private LZ4Factory factory = LZ4Factory.fastestInstance();

    public BizContext(Connector connector) {
        this.connector = connector;
    }


    public byte[] compress(byte[] src){
        LZ4Compressor compressor = factory.fastCompressor();
        int maxCompressedLength = compressor.maxCompressedLength(src.length);
        byte[] compressed = new byte[maxCompressedLength];
        int compressedLength = compressor.compress(src, 0, src.length, compressed, 0, maxCompressedLength);
        IoBuffer buffer = IoBuffer.allocate(compressedLength);
        buffer.put(compressed,0,compressedLength);
        buffer.flip();
        return buffer.array();
    }

    public byte[] decompress(byte[] src,int originLen){
        LZ4SafeDecompressor decompressor = factory.safeDecompressor();
        byte[] restored = new byte[originLen];
        decompressor.decompress(src, 0,src.length, restored, 0);
        return restored;
    }


    @Override
    public byte[] call(int systemId, int functionId, byte[] msg) {
        return this.call(systemId, functionId, "", msg);
    }

    @Override
    public byte[] call(int systemId, int functionId, String tag, byte[] msg) {
        try {
            EsbMsg esbMsg = new EsbMsg();
            esbMsg.setSystemid(systemId);
            esbMsg.setFunctionid(functionId);
            esbMsg.setTag(tag);
            if(msg != null) {
                esbMsg.setOriginLen(msg.length);
                esbMsg.setContent(compress(msg));
            }else{
                esbMsg.setOriginLen(0);
                esbMsg.setContent(msg);
            }
            esbMsg.setIsCopySend(false);
            connector.send(esbMsg);
            EsbMsg rspMsg = connector.recv(connector.getTimeout());
            if(rspMsg != null) {
                byte[] compressed =  rspMsg.getContent();
                int originLen = rspMsg.getOriginLen();
                return decompress(compressed,originLen);
            }else
                return null;
        } catch (Throwable e) {
            logger.error("调用服务失败 ",e);
            return null;
        }
    }

    @Override
    public void post(int systemId, int functionId, byte[] msg) {
        throw new RuntimeException("客戶端不支持异步调用");
    }

    @Override
    public void post(int systemId, int functionId, String tag, byte[] msg) {
        throw new RuntimeException("客戶端不支持异步调用");
    }

    @Override
    public List<byte[]> multiCall(int systemId, int functionId, byte[] msg) {
        return this.multiCall(systemId, functionId, "", msg);
    }

    @Override
    public List<byte[]> multiCall(int systemId, int functionId, String tag, byte[] msg) {
        try {
            EsbMsg esbMsg = new EsbMsg();
            esbMsg.setSystemid(systemId);
            esbMsg.setFunctionid(functionId);
            esbMsg.setTag(tag);
            if(msg != null) {
                esbMsg.setOriginLen(msg.length);
                esbMsg.setContent(compress(msg));
            }else{
                esbMsg.setOriginLen(0);
                esbMsg.setContent(msg);
            }
            esbMsg.setIsCopySend(true);
            esbMsg.setCopyCount(1);
            connector.send(esbMsg);
            List<EsbMsg> rspMsg = connector.recvMulti(connector.getTimeout());
            ArrayList<byte[]> results = new ArrayList<byte[]>();
            if(rspMsg != null) {
                for (EsbMsg r : rspMsg) {
                    byte[] compressed =  r.getContent();
                    int originLen = r.getOriginLen();
                    results.add(decompress(compressed,originLen));
                }
            }
            return results;
        } catch (Throwable e) {
        	logger.error("调用服务失败 ",e);
            return null;
        }
    }
}
