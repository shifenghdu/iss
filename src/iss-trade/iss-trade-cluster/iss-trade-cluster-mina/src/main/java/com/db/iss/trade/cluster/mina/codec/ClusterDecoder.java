package com.db.iss.trade.cluster.mina.codec;

import com.db.iss.trade.api.compressor.ICompressor;
import com.db.iss.trade.api.plugin.PluginException;
import com.db.iss.trade.api.plugin.EsbMsg;
import com.db.iss.trade.api.serializer.ISerializer;
import com.db.iss.trade.api.util.HexUtil;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterDecoder extends CumulativeProtocolDecoder implements ProtocolEncoder {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ISerializer serialize;

    private ICompressor compressor;

    public ClusterDecoder(ISerializer serialize,ICompressor compressor) {
        this.serialize = serialize;
        this.compressor = compressor;
    }

    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        //序列化
        byte[] msg = serialize.encode(message);
        int originLen = msg.length;
        int compressLen = originLen;

        //压缩
        if(compressor != null){
            msg = compressor.compress(msg);
            compressLen = msg.length;
        }

        //编码长度
        IoBuffer buffer = IoBuffer.allocate(msg.length + 8);
        byte[] compress = HexUtil.intToBcd(compressLen, 4, 0);
        byte[] origin = HexUtil.intToBcd(originLen, 4, 0);

        if (logger.isTraceEnabled()) {
            if (((EsbMsg) message).getMsgtype() != EsbMsg.MSGTYPE_CLUSTER) {
                logger.trace("send session[{}] message[{}]", session.getAttribute("address"),
                        new String(HexUtil.hexToAscii(msg)));
            }
        }

        //写入数据
        buffer.put(compress);
        buffer.put(origin);
        buffer.put(msg);
        buffer.flip();
        out.write(buffer);
    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        if (in.remaining() > 8) {
            byte[] compress = new byte[4];
            byte[] origin = new byte[4];
            in.mark();
            in.get(compress);
            in.get(origin);
            //压缩长度
            int size = HexUtil.bcdToInt(compress, 0);
            //非压缩长度
            int originSize = HexUtil.bcdToInt(origin,0);
            if (size > in.remaining()) {
                in.reset();
                return false;
            } else {
                byte[] msg = new byte[size];
                in.get(msg, 0, size);

                //解压
                if(size != originSize) { //经压缩
                    if (compressor != null) {
                        try {
                            msg = compressor.decompress(msg, originSize);
                        }catch (Throwable e){
                            throw new PluginException("decompress failed please check compress algorithm",e);
                        }
                    }
                }

                //反序列化
                EsbMsg pack = (EsbMsg) serialize.decode(msg, EsbMsg.class);

                if (logger.isTraceEnabled()) {
                    if (pack.getMsgtype() != EsbMsg.MSGTYPE_CLUSTER) {
                        logger.trace("receive session[{}] message[{}]", session.getAttribute("address"),
                                new String(HexUtil.hexToAscii(msg)));
                    }
                }

                //版本不匹配
                if(!pack.getVersion().equalsIgnoreCase(EsbMsg.CURRENT_VERSION)){
                    throw new PluginException(String.format("esbmsg version %s not support",pack.getVersion()));
                }

                out.write(pack);
                if (in.remaining() > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
