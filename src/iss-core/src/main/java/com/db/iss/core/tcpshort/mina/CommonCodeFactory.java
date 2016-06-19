package com.db.iss.core.tcpshort.mina;

import com.db.iss.core.tcpshort.MinaAcceptor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class CommonCodeFactory implements ProtocolCodecFactory {

	private CommonDecodc commonDecodc;

	public CommonCodeFactory(MinaAcceptor owner) {
		commonDecodc = new CommonDecodc(owner);
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return commonDecodc;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return commonDecodc;
	}

}
