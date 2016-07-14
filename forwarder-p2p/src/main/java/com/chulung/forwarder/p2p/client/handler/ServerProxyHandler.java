package com.chulung.forwarder.p2p.client.handler;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class ServerProxyHandler extends SimpleChannelInboundHandler<DatagramPacket> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
		// 服务器推送对方IP和PORT
		ByteBuf buf = (ByteBuf) packet.copy().content();
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String str = new String(req, "UTF-8");
		String[] list = str.split(" ");
		// 如果是A 则发送
		if (list[0].equals("A")) {
			String ip = list[1];
			String port = list[2];
			ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("打洞信息".getBytes()),
					new InetSocketAddress(ip, Integer.parseInt(port))));
			Thread.sleep(1000);
			ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("P2P info..".getBytes()),
					new InetSocketAddress(ip, Integer.parseInt(port))));
		}
		System.out.println("接收到的信息:" + str);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("客户端向服务器发送自己的IP和PORT");
		ctx.writeAndFlush(
				new DatagramPacket(Unpooled.copiedBuffer("R".getBytes()), new InetSocketAddress("112.124.127.23", 7777)));
		super.channelActive(ctx);
	}
}
