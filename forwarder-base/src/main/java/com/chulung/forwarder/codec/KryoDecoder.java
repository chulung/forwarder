package com.chulung.forwarder.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class KryoDecoder extends LengthFieldBasedFrameDecoder {
	
	private final KryoPool kryoPool;
	
	public KryoDecoder(final KryoPool kryoSerializationFactory) {
		super(10485760, 0, 4, 0, 4);
		this.kryoPool = kryoSerializationFactory;
	}
	
	@Override
	protected Object decode(final ChannelHandlerContext ctx, final ByteBuf in) throws Exception {
		ByteBuf frame = (ByteBuf) super.decode(ctx, in);
		if (frame == null) {
			return null;
		}
		try {
			return kryoPool.decode(frame);
		} finally {
			if (null != frame) {
				frame.release();
			}
		}
	}
}
