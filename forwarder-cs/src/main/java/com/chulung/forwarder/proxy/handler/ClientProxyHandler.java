package com.chulung.forwarder.proxy.handler;

import com.chulung.forwarder.common.ClientType;
import com.chulung.forwarder.common.Config;
import com.chulung.forwarder.common.DataType;
import com.chulung.forwarder.server.AbstractServer;
import com.chulung.forwarder.wrapper.DataWrapper;
import com.chulung.forwarder.wrapper.DataWrapperBuilder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;

@Sharable
public class ClientProxyHandler extends AbstractProxyHandler {

	private ChannelHandlerContext localAppCtx;
	private ClientProxyHandler handler;
	private long lastSendDataTime = 0;
	private long localAppOutTime = Config.getConfig().getLocalAppOutTime();

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof DataWrapper) {
			DataWrapper dataWrapper = (DataWrapper) msg;
			if (dataWrapper.getDataType() == DataType.CLIENT_CONNECTING) {
				handler = this;
				new LocalServer().start();
			} else if (dataWrapper.getDataType() == DataType.DATA) {
				if (lastSendDataTime != 0 && (System.currentTimeMillis() - lastSendDataTime) > localAppOutTime) {
					LOGGER.error("本地应用已断开");
					this.localAppCtx.close();
					this.forwarderServerCtx.close();
					return;
				}
				this.localAppCtx.writeAndFlush(dataWrapper.getData());
				System.out.println(System.currentTimeMillis() - dataWrapper.getCreateTime());
			} else if (dataWrapper.getDataType() == DataType.SERVER_PROXY_NOT_FOUNED) {
				LOGGER.error("服务端代理未找到");
				ctx.close();
				super.close();
			} else if (dataWrapper.getDataType() == DataType.Server_PROXY_ERROR) {
				LOGGER.error("服务端代理已断开");
				if (ctx != null) {
					ctx.close();
				}
			}
		} else if (msg instanceof ByteBuf) {
			lastSendDataTime = System.currentTimeMillis();
			localAppCtx = ctx;
			super.put(new DataWrapperBuilder().setData((ByteBuf) msg).setClientType(ClientType.ClientProxy)
					.setDataType(DataType.DATA).create());
		}

	}

	public class LocalServer extends AbstractServer {
		@Override
		public void start() {
			super.start();
			LOGGER.info("链接远程代理服务器成功  客户端代理服务启动,本地应用请链接 127.0.0.1:{}", Config.getConfig().getClientProxyPort());
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
