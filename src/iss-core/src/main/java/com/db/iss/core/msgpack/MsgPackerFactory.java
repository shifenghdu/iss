package com.db.iss.core.msgpack;

import org.msgpack.MessagePack;
import org.springframework.stereotype.Service;

import com.db.iss.common.esb.EsbMsg;

@Service("s_messagepacker")
public class MsgPackerFactory {

	private ThreadLocal<MessagePack> local = new ThreadLocal<MessagePack>();

	public MessagePack getMsgPacker() {
		if (local.get() == null) {
			MessagePack messagePack = null;
			synchronized (this) {
				messagePack = new MessagePack();
				messagePack.register(EsbMsg.class);
			}
			local.set(messagePack);
		}
		return local.get();
	}
}
