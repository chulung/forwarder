package com.chulung.forwarder.proxy;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public abstract class AbstractProxy implements Runnable {
	protected EventLoopGroup group = new NioEventLoopGroup();

	public void run() {
		try {
			Bootstrap b = new Bootstrap();

			b.group(group).channel(NioSocketChannel.class).remoteAddress(getRemoteAddress())
					.handler(getChannelInitializer());
			ChannelFuture f = b.connect().sync();
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

	protected abstract ChannelHandler getChannelInitializer();

	protected abstract InetSocketAddress getRemoteAddress();

	protected abstract ChannelHandler getProxyHandler();
}
