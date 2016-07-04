package com.chulung.forwarder.proxy.handler;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.chulung.forwarder.common.ClientType;
import com.chulung.forwarder.common.Config;
import com.chulung.forwarder.common.DataType;
import com.chulung.forwarder.proxy.AbstractProxy;
import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerProxyHandler extends AbstractProxyHandler {
	private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	private Map<ChannelId, ChannelHandlerContext> clientProxyCtxMap = new HashMap<>();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg == null) {
			return;
		}
		if (msg instanceof DataWrapper) {
			DataWrapper dw = (DataWrapper) msg;
			if (dw.getDataType() == DataType.DATA) {
				ChannelHandlerContext clientCtx = this.clientProxyCtxMap.get(dw.getClientId());
				if (clientCtx == null) {
					synchronized (this) {
						clientCtx = this.clientProxyCtxMap.get(dw.getClientId());
						if (clientCtx == null) {
							this.cachedThreadPool.execute(new LocalServer(dw.getClientId()));
							Thread.sleep(100);
						}
						clientCtx = this.clientProxyCtxMap.get(dw.getClientId());
						if (clientCtx == null) {
							forwarderServerCtx
									.writeAndFlush(new DataWrapper(DataType.Server_PROXY_ERROR, dw.getClientId()));
						}
					}
				}
				if (dw.getData() != null) {
					clientCtx.writeAndFlush(dw.getData());
				}
			}
		}
	}

	@Override
	protected ClientType getClientType() {
		return ClientType.ServerProxy;
	}

	public class LocalServer extends AbstractProxy {
		private ChannelId remoteClientId;

		public LocalServer(ChannelId remoteClientId) {
			this.remoteClientId = remoteClientId;
		}

		@Override
		public ChannelHandler getProxyHandler() {
			return new localServerHandler(remoteClientId);
		}

		@Override
		protected InetSocketAddress getRemoteAddress() {
			return Config.getConfig().getLocalServerAddress();
		}

	}

	public class localServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
		private ChannelId remoteClientId;

		public localServerHandler(ChannelId remoteClientId) {
			this.remoteClientId = remoteClientId;
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			clientProxyCtxMap.put(remoteClientId, ctx);
		}

		@Override
		protected void channelRead0(ChannelHandlerContext arg0, ByteBuf arg1) throws Exception {
			DataWrapper dataWarpper = new DataWrapper(remoteClientId, arg1);
			forwarderServerCtx.writeAndFlush(dataWarpper);
		}
	}
}
