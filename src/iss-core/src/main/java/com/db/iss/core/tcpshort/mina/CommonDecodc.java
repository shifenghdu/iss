package com.db.iss.core.tcpshort.mina;

import com.db.iss.core.tcpshort.MinaAcceptor;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.db.iss.common.util.HexUtil;

public class CommonDecodc extends CumulativeProtocolDecoder implements
		ProtocolEncoder {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private MinaAcceptor owner;

	public CommonDecodc(MinaAcceptor owner) {
		this.owner = owner;
	}

	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		byte[] msg = (byte[]) message;
		IoBuffer buffer = IoBuffer.allocate(msg.length + owner.getHeadsize());
		byte[] head = null;
		if (owner.getEncode().equals("hex")) {
			head = HexUtil.intToBcd(msg.length, owner.getHeadsize(), 0);
		} else {// ascii
			/*
			head = StringUtil.lpadding(String.valueOf(msg.length),
					owner.getHeadsize(), '0').getBytes();
					*/
			head = String.format(String.format("%%0%dd", owner.getHeadsize()), msg.length).getBytes();
		}
		logger.debug("send session[{}] message[{}]",
				session.getAttribute("address"),
				new String(HexUtil.hexToAscii(msg)));
		buffer.put(head);
		buffer.put(msg);
		buffer.flip();
		out.write(buffer);
	}

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		// System.err.println("doDecode");
		if (in.remaining() > owner.getHeadsize()) {
			byte[] head = new byte[owner.getHeadsize()];
			in.mark();
			in.get(head);
			int size = 0;
			if (owner.getEncode().equals("hex")) {
				size = HexUtil.bcdToInt(head, 0);
			} else {
				size = Integer.valueOf(new String(head));
			}
			if (size > in.remaining()) {
				in.reset();
				return false;
			} else {
				byte[] msg = new byte[size];
				in.get(msg, 0, size);
				logger.debug("recv session[{}] message[{}]",
						session.getAttribute("address"),
						new String(HexUtil.hexToAscii(msg)));
				out.write(msg);
				if (in.remaining() > 0) {
					return true;
				}
			}
		}
		return false;
	}
}
