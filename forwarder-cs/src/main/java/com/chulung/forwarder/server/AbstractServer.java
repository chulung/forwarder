package com.chulung.forwarder.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public abstract class AbstractServer implements Runnable {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	private int[] ports;
	private int port;

	public void start() {
		ports = this.getPort();
		for (int i = 0; i < ports.length; i++) {
			port = ports[i];
			new Thread(this).start();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void run() {
		ServerBootstrap bootstrap = new ServerBootstrap();
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			bootstrap.group(group).channel(NioServerSocketChannel.class).localAddress(port)
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

	protected abstract int[] getPort();
}
