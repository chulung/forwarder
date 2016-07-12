package com.chulung.forwarder.server.handler;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chulung.forwarder.common.StatusCode;
import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 转发服务端handler
 * 
 * @author ChuKai
 *
 */
@Sharable
public class ForwarderServerHandler extends SimpleChannelInboundHandler<Object> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ForwarderServerHandler.class);
	private ChannelHandlerContext serverProxyCtx;
	private Map<String, ChannelHandlerContext> clientProxyCtxMap = new ConcurrentHashMap<>();

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// 服务器代理异常，通知所有客户端代理
		if (ctx == serverProxyCtx) {
			LOGGER.error("服务器代理异常，通知所有客户端代理", cause);
			this.serverProxyCtx = null;
			clientProxyCtxMap.values().forEach(clientCTX -> {
				clientCTX.close();
			});
		} else {
			LOGGER.error("客户端代理通道异常，通知服务端代理", cause);
			// 客户端代理异常则通知服务器代理
			this.clientProxyCtxMap.remove(ctx.channel().id().asLongText());
			if (serverProxyCtx != null) {
				this.serverProxyCtx.writeAndFlush(new DataWrapper(ctx.channel().id().asLongText(), StatusCode.C_LOST,
						null, ((InetSocketAddress) ctx.channel().localAddress()).getPort()));
			}
		}
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof DataWrapper) {
			serverProxyRead(ctx, (DataWrapper) msg);
		} else {
			clientProxyRead(ctx, (ByteBuf) msg);
		}
	}

	/**
	 * 读取服务端代理数据
	 * 
	 * @param ctx
	 * 
	 * @param ctx
	 * @param msg
	 * @param msg
	 */
	private void serverProxyRead(ChannelHandlerContext ctx, DataWrapper msg) {
		// 远程服务端代理数据
		// 远程服务端代理已存在，不允许多个
		if (msg.getStatusCode() == StatusCode.S_CONNECTING) {
			if (this.serverProxyCtx != null) {
				ctx.writeAndFlush(new DataWrapper(StatusCode.S_EXIST));
				LOGGER.error("服务端代理已存在 address={}", this.serverProxyCtx.channel().remoteAddress());
				return;
			} else {
				LOGGER.info("服务端代理已连接 address={}", ctx.channel().remoteAddress());
				this.serverProxyCtx = ctx;
			}
		}
		if (msg.getStatusCode() != StatusCode.S_DATA) {
			return;
		}
		ChannelHandlerContext clientCtx = this.clientProxyCtxMap.get(msg.getClientId());
		if (clientCtx != null) {
			clientCtx.writeAndFlush(msg);
		} else {
			// 远程客户端丢失，通知远程服务端代理抛弃对应通道
			LOGGER.error("远程客户端丢失 ClientId={}", msg.getClientId());
			serverProxyCtx.writeAndFlush(new DataWrapper(msg.getClientId(), StatusCode.C_LOST));
		}
	}

	/**
	 * 读取客户端代理数据
	 * 
	 * @param dataWrapper
	 * @param ctx2
	 * 
	 * @param ctx
	 * @param msg
	 */
	private void clientProxyRead(ChannelHandlerContext ctx, ByteBuf msg) {
		if (this.serverProxyCtx == null) {
			LOGGER.error("服务端代理不存在");
			// 服务端代理不存在
			ctx.close();
		}
		String clientId = ctx.channel().id().asLongText();
		this.clientProxyCtxMap.put(clientId, ctx);
		this.serverProxyCtx.writeAndFlush(new DataWrapper(clientId, StatusCode.C_DATA, msg,
				((InetSocketAddress) ctx.channel().localAddress()).getPort()));
	}
}
