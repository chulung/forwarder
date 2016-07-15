package com.chulung.forwarder.p2p.client;

import com.chulung.forwarder.p2p.client.handler.ClientProxyHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class ClientProxy {
	private ClientProxyHandler handler = new ClientProxyHandler();

	public void startSync() {
//		new RemotePortMapServer(handler).startServerAsync();
		Bootstrap b = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			b.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true).handler(handler);
			b.bind(777).sync().channel().closeFuture().await();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) {
		new ClientProxy().startSync();
	}
}
