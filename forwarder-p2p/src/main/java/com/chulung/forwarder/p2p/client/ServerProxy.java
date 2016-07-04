package com.chulung.forwarder.p2p.client;

import com.chulung.forwarder.p2p.client.handler.EchoClientHandler2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class ServerProxy {
	public static void main(String[] args) {
		Bootstrap b = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			b.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true)
					.handler(new EchoClientHandler2());

			b.bind(7779).sync().channel().closeFuture().await();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
}
