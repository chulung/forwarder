package com.chulung.forwarder.codec;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

public class KryoPool {

	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

	private KyroFactory kyroFactory;

	private int maxTotal = 100;

	private int minIdle = 10;

	private long maxWaitMillis = -1;

	private long minEvictableIdleTimeMillis = 600000;
	private static final KryoPool KRYO_POOL = new KryoPool();

	public static KryoPool getInstance() {
		return KRYO_POOL;
	}

	private KryoPool() {
		kyroFactory = new KyroFactory(maxTotal, minIdle, maxWaitMillis, minEvictableIdleTimeMillis);
	}

	public void encode(final ByteBuf out, final Object message) throws IOException {
		ByteBufOutputStream bout = new ByteBufOutputStream(out);
		//这里比较坑，原作者编码写入了一个4字节的占位符，解码的时候又没有处理，导致解码一直失败，折腾了我好几天，debug无数次
		bout.write(LENGTH_PLACEHOLDER);
		KryoSerialization kryoSerialization = new KryoSerialization(kyroFactory);
		kryoSerialization.serialize(bout, message);
	}

	public Object decode(final ByteBuf in) throws IOException {
		KryoSerialization kryoSerialization = new KryoSerialization(kyroFactory);
		return kryoSerialization.deserialize(new ByteBufInputStream(in));
	}
}
