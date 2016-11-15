package com.chulung.forwarder.proxy.local;

import java.net.InetSocketAddress;

import com.chulung.forwarder.common.Config;
import com.chulung.forwarder.common.StatusCode;
import com.chulung.forwarder.proxy.AbstractProxy;
import com.chulung.forwarder.proxy.AbstractServerProxyHandler;
import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

/**
 * 远程应用代理，由它代替客户端应用向应用服务器发起请求，
 * 同时将应用服务器的响应回传给中转服务器，再由中转服务器响应给客户端应用
 */
public class RemoteAppProxy extends AbstractProxy implements Runnable {
	/**
	 * 第一个数据包将用来建立连接，同时也包含客户端应用的地址等信息
	 */
	private DataWrapper firstData;
	private AbstractServerProxyHandler serverProxyHandler;
	private String clientId;
	private LocalServerHandler localServerHandler = new LocalServerHandler();

	public RemoteAppProxy(DataWrapper dw, AbstractServerProxyHandler serverProxyHandler) {
		this.firstData = dw;
		this.serverProxyHandler = serverProxyHandler;
		clientId = firstData.getClientId();
	}

	@Override
	protected ChannelHandler getChannelInitializer() {
		return new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(localServerHandler);
			}
		};
	}

	@Sharable
	public class LocalServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			serverProxyHandler.delelteRemoteAppProxyCtx(clientId);
		}

		public LocalServerHandler() {
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			LOGGER.info("clientId={} port={}", firstData.getClientId(), firstData.getClientProxyPort());
			serverProxyHandler.putRemoteAppProxyCtx(firstData.getClientId() + firstData.getClientProxyPort(), ctx);
			ctx.writeAndFlush(firstData.getData());
		}

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, ByteBuf data) throws Exception {

			serverProxyHandler.putForwarderData(
					new DataWrapper(clientId, StatusCode.S_DATA, data, firstData.getClientProxyPort()));
		}

	}

	@Override
	public void run() {
		startBoot(new InetSocketAddress(Config.getInstance().getPortsMap().get(firstData.getClientProxyPort())));
	}
}
