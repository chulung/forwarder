package com.chulung.forwarder.proxy;

import java.net.InetSocketAddress;

import com.chulung.forwarder.common.Config;
import com.chulung.forwarder.proxy.handler.ClientProxyHandler;

import io.netty.channel.ChannelHandler;

public class ClientProxy extends AbstractProxy {

	public static void main(String[] args) {
		new ClientProxy().run();
	}

	@Override
	public ChannelHandler getProxyHandler() {
		return new ClientProxyHandler();
	}

	@Override
	protected InetSocketAddress getRemoteAddress() {
		return Config.getConfig().getForwarderAddress();
	}

}
