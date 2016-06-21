package com.db.iss.trade.cluster.mina.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ClusterCodecFactory implements ProtocolCodecFactory {

	private ThreadLocal<ClusterDecoder> clusterDecoder;

	private SerializerType type = SerializerType.MSGPACK;

	@Autowired
	private SerializerFactory serializerFactory;

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return getDecoder();
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return getDecoder();
	}

	private ClusterDecoder getDecoder(){
		ClusterDecoder decoder = clusterDecoder.get();
		if(decoder == null){
			decoder = new ClusterDecoder(serializerFactory.getSerializer(type));
		}
		return decoder;
	}

	public void setSerializerType(SerializerType type){
		this.type = type;
	}


}
