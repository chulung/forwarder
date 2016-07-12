package com.chulung.forwarder.server;

import com.chulung.forwarder.common.Config;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientPortMapServer extends AbstractServer {
	private SimpleChannelInboundHandler<?> handler;

	public ClientPortMapServer(SimpleChannelInboundHandler<?> handler) {
		this.handler = handler;
	}

	public void startServerAsync() {
		Config.getInstance().getPortsMap().keySet().forEach(port -> {
			this.startServerAsync(port);
		});
	}

	@Override
	protected ChannelInitializer<Channel> getChildHandler() {
		return new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(handler);
			}
		};
	}

}