package com.chulung.forwarder.proxy;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public abstract class AbstractProxy {
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	public void startBoot(InetSocketAddress inetSocketAddress) {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			LOGGER.info("新代理已启动，address={}", inetSocketAddress);
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).remoteAddress(inetSocketAddress)
					.handler(getChannelInitializer());
			ChannelFuture f = b.connect().sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			this.LOGGER.error("", e);
		} finally {
			try {
				group.shutdownGracefully().sync();
			} catch (InterruptedException e) {
				this.LOGGER.error("", e);
			}
		}
	}

	protected abstract ChannelHandler getChannelInitializer();

}
