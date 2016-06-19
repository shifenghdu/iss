package com.db.iss.core.msgpack;

import javax.annotation.PostConstruct;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("s_codec")
public class CommonCodeFactory implements ProtocolCodecFactory {

	private CommonDecodc commonDecodc;

	@Autowired
	private MsgPackerFactory msgPackerFactory;

	@PostConstruct
	public void init() {
		commonDecodc = new CommonDecodc(msgPackerFactory);
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
