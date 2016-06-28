package com.db.iss.cluster.mina.codec;

import com.db.iss.core.compressor.CompressorFactory;
import com.db.iss.core.compressor.CompressorType;
import com.db.iss.core.serializer.SerializerFactory;
import com.db.iss.core.serializer.SerializerType;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class ClusterCodecFactory implements ProtocolCodecFactory {

	private ThreadLocal<ClusterDecoder> clusterDecoder = new ThreadLocal<>();

	private SerializerType type = SerializerType.MSGPACK;

	private CompressorType compressorType;

	private SerializerFactory serializerFactory = new SerializerFactory();

	private CompressorFactory compressorFactory = new CompressorFactory();

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
			decoder = new ClusterDecoder(serializerFactory.getSerializer(type),compressorFactory.getCompressor(compressorType));
		}
		return decoder;
	}

	public ClusterCodecFactory(SerializerType type,CompressorType compressorType){
		this.type = type;
		this.compressorType = compressorType;
	}

}
