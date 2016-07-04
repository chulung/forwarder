package com.chulung.forwarder.proxy.handler;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.chulung.forwarder.common.ClientType;
import com.chulung.forwarder.common.Config;
import com.chulung.forwarder.proxy.AbstractProxy;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

public class ServerProxyHandler extends AbstractProxyHandler {
	private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	private AbstractProxyHandler proxyHandler = this;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

	}

	@Override
	protected ClientType getClientType() {
		return ClientType.ServerProxy;
	}

	public class LoaclServer extends AbstractProxy {

		@Override
		public ChannelHandler getProxyHandler() {
			return proxyHandler;
		}

		@Override
		protected InetSocketAddress getRemoteAddress() {
			return Config.getConfig().getLocalServerAddress();
		}

	}

}
