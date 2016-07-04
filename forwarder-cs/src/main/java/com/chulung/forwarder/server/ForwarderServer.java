package com.chulung.forwarder.server;

import com.chulung.forwarder.common.Config;
import com.chulung.forwarder.server.handler.ForwarderServerHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * 转发服务端
 * 
 * @author ChuKai
 *
 */
public class ForwarderServer extends AbstractServer {

	@Override
	public ChannelInitializer<Channel> getChildHandler() {
		return new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(
						new ObjectDecoder(ClassResolvers.softCachingResolver(this.getClass().getClassLoader())));
				ch.pipeline().addLast(new ObjectEncoder());
				ch.pipeline().addLast(new ForwarderServerHandler());
			}
		};
	}

	@Override
	public void start() {
		this.run();
	}

	public static void main(String[] args) throws Exception {
		new ForwarderServer().start();
	}

	@Override
	protected int getPort() {
		return Config.getConfig().getForwaderPort();
	}
}
