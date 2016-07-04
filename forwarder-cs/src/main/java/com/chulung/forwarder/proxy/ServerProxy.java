package com.chulung.forwarder.proxy;

import java.net.InetSocketAddress;

import com.chulung.forwarder.common.Config;
import com.chulung.forwarder.proxy.handler.ServerProxyHandler;

import io.netty.channel.ChannelHandler;

public class ServerProxy extends AbstractProxy {

	public ServerProxy() {
	}

	public static void main(String[] args) throws Exception {
		new ServerProxy().run();
	}

	@Override
	public ChannelHandler getProxyHandler() {
		return new ServerProxyHandler();
	}

	@Override
	protected InetSocketAddress getRemoteAddress() {
		return Config.getConfig().getForwarderAddress();
	}
}
