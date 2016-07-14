package com.chulung.forwarder.handler;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.chulung.forwarder.codec.KryoPool;
import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public abstract class AbstractDatagramPacketHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	private KryoPool kryoPool = KryoPool.getInstance();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		readDataWarpper(ctx, (DataWrapper) kryoPool.decode(msg.content()));
	}

	protected ChannelFuture writeAndFlush(ChannelHandlerContext ctx, DataWrapper dataWrapper,
 InetSocketAddress address)
			throws IOException {
		ByteBuf data = Unpooled.buffer();
		kryoPool.encode(data, dataWrapper);
		return ctx.writeAndFlush(new DatagramPacket(data, address));
	}

	protected abstract void readDataWarpper(ChannelHandlerContext ctx, DataWrapper dw) throws IOException;
}
