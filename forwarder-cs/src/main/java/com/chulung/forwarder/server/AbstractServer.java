package com.chulung.forwarder.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public abstract class AbstractServer implements Runnable {
	private ServerBootstrap bootstrap = new ServerBootstrap();
	private EventLoopGroup group = new NioEventLoopGroup();

	public void start() {
		new Thread(this).start();
	}

	public void run() {
		try {
			bootstrap.group(group).channel(NioServerSocketChannel.class).localAddress(this.getPort())
					.childHandler(this.getChildHandler());
			// Binds server, waits for server to close, and releases resources
			ChannelFuture f = bootstrap.bind().sync();
			System.out.println(this.getClass().getName() + "started and listen on " + f.channel().localAddress());
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				group.shutdownGracefully().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected abstract ChannelInitializer<Channel> getChildHandler();

	protected abstract int getPort();
}
