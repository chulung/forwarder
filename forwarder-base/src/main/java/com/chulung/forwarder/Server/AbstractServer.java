package com.chulung.forwarder.Server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public abstract class AbstractServer {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	public AbstractServer() {
	}

	public void startServerAsync(int port) {
		new Thread(() -> {
			startServerSync(port);
		}).start();
	}

	public void startServerSync(int port) {
		ServerBootstrap bootstrap = new ServerBootstrap();
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			bootstrap.group(group).channel(NioServerSocketChannel.class).localAddress(port)
					.childHandler(this.getChildHandler());
			ChannelFuture f = bootstrap.bind().sync();
			logger.info(this.getClass().getName() + "started and listen on " + f.channel().localAddress());
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

}
