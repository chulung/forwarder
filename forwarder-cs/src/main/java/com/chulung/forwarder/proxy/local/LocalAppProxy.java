package com.chulung.forwarder.proxy.local;

import com.chulung.forwarder.common.Config;
import com.chulung.forwarder.proxy.handler.ClientProxyHandler;
import com.chulung.forwarder.server.AbstractServer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class LocalAppProxy extends AbstractServer {
	private ClientProxyHandler handler;

	public LocalAppProxy(ClientProxyHandler handler) {
		this.handler = handler;
	}

	@Override
	public void start() {
		super.start();
		logger.info("链接远程代理服务器成功  客户端代理服务启动,本地应用请链接 127.0.0.1:{}", Config.getConfig().getClientProxyPort());
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

	@Override
	protected int[] getPort() {
		return Config.getConfig().getClientProxyPort();
	}
}