package com.ipharmacare.iss.connector.msgpack;

import com.ipharmacare.iss.common.esb.EsbMsg;
import com.ipharmacare.iss.common.util.HexUtil;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonDecodc extends CumulativeProtocolDecoder implements
		ProtocolEncoder {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private ThreadLocal<MessagePack> msgpack = new ThreadLocal<>();

//    private MessagePack msgpack;

	public CommonDecodc() {
//		msgpack = new MessagePack();
//		msgpack.register(EsbMsg.class);
	}

    private MessagePack getMsgpack(){
        MessagePack m = msgpack.get();
        if(m == null){
            synchronized (msgpack) {
                m = new MessagePack();
                m.register(EsbMsg.class);
                msgpack.set(m);
            }
        }
        return m;
    }

	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		byte[] msg = getMsgpack().write(message);
		IoBuffer buffer = IoBuffer.allocate(msg.length + 4);
		byte[] head = HexUtil.intToBcd(msg.length, 4, 0);
		if (((EsbMsg) message).getMsgtype() != EsbMsg.MSGTYPE_CLUSTER) {
			if(logger.isDebugEnabled()) {
				logger.debug("send session[{}] message[{}]",
						session.getAttribute("address"),
						new String(HexUtil.hexToAscii(msg)));
			}
		}
		buffer.put(head);
		buffer.put(msg);
		buffer.flip();
		out.write(buffer);
	}

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
	
		if (in.remaining() > 4) {
			byte[] head = new byte[4];
			in.mark();
			in.get(head);
			int size = HexUtil.bcdToInt(head, 0);
			if (size > in.remaining()) {
				in.reset();
				return false;
			} else {
				byte[] msg = new byte[size];
				in.get(msg, 0, size);
				EsbMsg pack = getMsgpack().read(msg, EsbMsg.class);
				if (pack.getMsgtype() != EsbMsg.MSGTYPE_CLUSTER) {
					if(logger.isDebugEnabled()) {
						logger.debug("recv session[{}] message[{}]",
								session.getAttribute("address"),
								new String(HexUtil.hexToAscii(msg)));
					}
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
