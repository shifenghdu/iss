package com.ipharmacare.iss.connector.msgpack;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;


public class CommonCodeFactory implements ProtocolCodecFactory {

	private CommonDecodc commonDecodc = new CommonDecodc();

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return commonDecodc;
	}

	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return commonDecodc;
	}

}
