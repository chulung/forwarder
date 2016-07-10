package com.chulung.forwarder.proxy.local;

import java.net.InetSocketAddress;

import com.chulung.forwarder.common.Config;
import com.chulung.forwarder.common.StatusCode;
import com.chulung.forwarder.proxy.AbstractProxy;
import com.chulung.forwarder.proxy.handler.ServerProxyHandler;
import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

public class RemoteAppProxy extends AbstractProxy {
	private DataWrapper firstData;
	private ServerProxyHandler serverProxyHandler;
	private String clientId;

	public RemoteAppProxy(DataWrapper dw, ServerProxyHandler serverProxyHandler) {
		this.firstData = dw;
		this.serverProxyHandler = serverProxyHandler;
		clientId = firstData.getClientId();
	}

	@Override
	public ChannelHandler getProxyHandler() {
		return new localServerHandler();
	}

	@Override
	protected InetSocketAddress getRemoteAddress() {
		return new InetSocketAddress(Config.getConfig().getMappedPort(firstData.getClientProxyPort()));
	}

	@Override
	protected ChannelHandler getChannelInitializer() {
		return new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(getProxyHandler());
			}
		};
	}

	public class localServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			serverProxyHandler.delelteRemoteAppProxyCtx(clientId);
		}

		public localServerHandler() {
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			LOGGER.info("clientId={} port={}",firstData.getClientId() ,firstData.getClientProxyPort());
			serverProxyHandler.putRemoteAppProxyCtx(firstData.getClientId() + firstData.getClientProxyPort(), ctx);
			ctx.writeAndFlush(firstData.getData());
		}

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, ByteBuf data) throws Exception {

			serverProxyHandler.putForwarderData(
					new DataWrapper(clientId, StatusCode.S_DATA, data, firstData.getClientProxyPort()));
		}

	}
}
