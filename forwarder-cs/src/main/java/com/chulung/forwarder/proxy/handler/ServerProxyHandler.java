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
import com.chulung.forwarder.wrapper.DataWrapperBuilder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

@Sharable
public class ServerProxyHandler extends AbstractProxyHandler {
	private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	public Map<String, ChannelHandlerContext> clientProxyCtxMap = new HashMap<>();

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
							this.cachedThreadPool.execute(new LocalServer(dw));
							Thread.sleep(100);
						}
					}
				} else if (dw.getData() != null) {
					clientCtx.writeAndFlush(dw.getData());
				}
			} else if (dw.getDataType() == DataType.CLIENT_CLOSE) {
				ChannelHandlerContext context = this.clientProxyCtxMap.remove(dw.getClientId());
				if (context != null) {
					LOGGER.info("ctx={} 被关闭" + context);
					context.close();
				}
			}
		}
	}

	@Override
	protected ClientType getClientType() {
		return ClientType.ServerProxy;
	}

	public class LocalServer extends AbstractProxy {
		private DataWrapper dw;

		public LocalServer(DataWrapper dw) {
			this.dw = dw;
		}

		@Override
		public ChannelHandler getProxyHandler() {
			return new localServerHandler();
		}

		@Override
		protected InetSocketAddress getRemoteAddress() {
			return Config.getConfig().getLocalServerAddress();
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

			public localServerHandler() {
			}

			@Override
			public void channelActive(ChannelHandlerContext ctx) throws Exception {
				clientProxyCtxMap.put(dw.getClientId(), ctx);
				ctx.writeAndFlush(dw.getData());
			}

			@Override
			protected void channelRead0(ChannelHandlerContext ctx, ByteBuf data) throws Exception {
				put(new DataWrapperBuilder().setClientType(ClientType.ServerProxy).setClientId(dw.getClientId())
						.setDataType(DataType.DATA).setData(data).create());
			}

		}
	}

}
