package com.chulung.forwarder.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public final class KryoEncoder extends MessageToByteEncoder<Object> {
	
	private final KryoPool kryoPool;
	
	public KryoEncoder(final KryoPool kryoSerializationFactory) {
		this.kryoPool = kryoSerializationFactory;
	}
	
	@Override
	protected void encode(final ChannelHandlerContext ctx, final Object msg, final ByteBuf out) throws Exception {
		int startIdx = out.writerIndex();
		kryoPool.encode(out, msg);
		int endIdx = out.writerIndex();
		out.setInt(startIdx, endIdx - startIdx - 4);
	}
}
