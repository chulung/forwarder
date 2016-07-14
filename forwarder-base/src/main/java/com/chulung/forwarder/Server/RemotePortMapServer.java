package com.chulung.forwarder.Server;

import com.chulung.forwarder.common.Config;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;

public class RemotePortMapServer extends AbstractServer {
	private SimpleChannelInboundHandler<?> handler;

	public RemotePortMapServer(SimpleChannelInboundHandler<?> handler) {
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