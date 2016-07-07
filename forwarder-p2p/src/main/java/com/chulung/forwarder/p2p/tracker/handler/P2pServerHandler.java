package com.chulung.forwarder.p2p.tracker.handler;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class P2pServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	boolean flag = false;
	InetSocketAddress addr1 = null;
	InetSocketAddress addr2 = null;

	/**
	 * channelRead0 是对每个发送过来的UDP包进行处理
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
		ByteBuf buf = (ByteBuf) packet.copy().content();
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String str = new String(req, "UTF-8");
		if (str.equalsIgnoreCase("L")) {
			// 保存到addr1中 并发送addr2
			addr1 = packet.sender();
			System.out.println("L 命令， 保存到addr1中 ");
		} else if (str.equalsIgnoreCase("R")) {
			// 保存到addr2中 并发送addr1
			addr2 = packet.sender();
			System.out.println("R 命令， 保存到addr2中 ");
		} else if (str.equalsIgnoreCase("M")) {
			// addr1 -> addr2
			String remot = "A " + addr2.getAddress().toString().replace("/", "") + " " + addr2.getPort();
			ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(remot.getBytes()), addr1));
			// addr2 -> addr1
			remot = "A " + addr1.getAddress().toString().replace("/", "") + " " + addr1.getPort();
			ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(remot.getBytes()), addr2));
			System.out.println("M 命令");
		}

	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("服务器启动...");

		super.channelActive(ctx);
	}

}
