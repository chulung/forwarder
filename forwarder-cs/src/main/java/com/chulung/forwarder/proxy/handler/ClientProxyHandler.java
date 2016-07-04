package com.chulung.forwarder.proxy.handler;

import com.chulung.forwarder.common.ClientType;
import com.chulung.forwarder.common.Config;
import com.chulung.forwarder.common.DataType;
import com.chulung.forwarder.server.AbstractServer;
import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;

public class ClientProxyHandler extends AbstractProxyHandler {

	private ChannelHandlerContext localAppCtx;
	private ClientProxyHandler handler;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof DataWrapper) {
			this.forwarderServerCtx = ctx;
			DataWrapper dataWrapper = (DataWrapper) msg;
			if (dataWrapper.getDataCode() == DataType.CLIENT_CONNECTING) {
				new LocalServer().start();
			} else if (dataWrapper.getDataCode() == DataType.DATA) {
				this.localAppCtx.write(dataWrapper.getData());
			} else if (dataWrapper.getDataCode() == DataType.SERVER_PROXY_NOT_FOUNED) {
				LOGGER.error("服务端代理未找到");
				forwarderServerCtx.close();
			}
		} else if (msg instanceof ByteBuf) {
			localAppCtx = ctx;
			if (forwarderServerCtx != null) {
				forwarderServerCtx.write(new DataWrapper((ByteBuf) msg));
			} else {
				LOGGER.error("转发服务器未连接");
				ctx.close();
			}
		}

	}

	public class LocalServer extends AbstractServer {
		@Override
		public void start() {
			super.start();
			LOGGER.info("链接远程代理服务器成功  客户端代理服务启动,本地应用请链接 127.0.0.1:{}" + Config.getConfig().getClientProxyPort());
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
		protected int getPort() {
			return Config.getConfig().getClientProxyPort();
		}
	}

	@Override
	protected ClientType getClientType() {
		return ClientType.ClientProxy;
	}
}
