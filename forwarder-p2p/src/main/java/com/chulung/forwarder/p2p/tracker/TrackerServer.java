package com.chulung.forwarder.p2p.tracker;

import com.chulung.forwarder.common.Config;
import com.chulung.forwarder.p2p.tracker.handler.TrackerServerHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class TrackerServer {
	private TrackerServerHandler handler = new TrackerServerHandler();

	public void start() {
		Bootstrap b = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			b.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true).handler(handler);
			b.bind(Config.getInstance().getForwaderChannelPort()).sync().channel().closeFuture().await();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) {
		new TrackerServer().start();
	}
}
