package com.chulung.forwarder.handler;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.chulung.forwarder.codec.KryoPool;
import com.chulung.forwarder.proxy.AbstractServerProxyHandler;
import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

public abstract class AbstractDatagramPacketHandler extends AbstractServerProxyHandler {
	private KryoPool kryoPool = KryoPool.getInstance();

	public AbstractDatagramPacketHandler() {
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof DatagramPacket) {
			ByteBuf buf = ((DatagramPacket) msg).content();
			buf.readBytes(new byte[4]);
			readDataWarpper(ctx, (DataWrapper) kryoPool.decode(buf), ((DatagramPacket) msg).sender());
		} else {
			readLocalAppBuf(ctx, (ByteBuf) msg);
		}
	}

	protected void readLocalAppBuf(ChannelHandlerContext ctx, ByteBuf msg) throws IOException {

	}

	protected ChannelFuture writeAndFlush(ChannelHandlerContext ctx, DataWrapper dataWrapper, InetSocketAddress address)
			throws IOException {
		ByteBuf data = Unpooled.buffer();
		kryoPool.encode(data, dataWrapper);
		return ctx.writeAndFlush(new DatagramPacket(data, address));
	}

	protected abstract void readDataWarpper(ChannelHandlerContext ctx, DataWrapper dw,
			InetSocketAddress inetSocketAddress) throws IOException;
}
