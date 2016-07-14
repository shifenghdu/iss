package com.db.iss.cluster.mina.codec;

import com.db.iss.core.compressor.CompressorProvider;
import com.db.iss.core.serializer.SerializerProvider;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class ClusterCodecFactory implements ProtocolCodecFactory {

	private ClusterDecoder clusterDecoder;

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return getDecoder();
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return getDecoder();
	}

	private ClusterDecoder getDecoder(){
		return clusterDecoder;
	}

	public ClusterCodecFactory(SerializerProvider serializerProvider, CompressorProvider compressorProvider){
		clusterDecoder = new ClusterDecoder(serializerProvider, compressorProvider);
	}

}
