package com.chulung.forwarder.server.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chulung.forwarder.common.StatusCode;
import com.chulung.forwarder.handler.DataWapperHandler;
import com.chulung.forwarder.wrapper.DataWrapper;

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
public class ForwarderServerHandler extends SimpleChannelInboundHandler<DataWrapper> implements DataWapperHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ForwarderServerHandler.class);
	private ChannelHandlerContext serverProxyCtx;
	private Map<String, ChannelHandlerContext> clientProxyCtxMap = new ConcurrentHashMap<>();
	private TransferQueue<ReadQueueItem> ClientProxyReadQueue = new LinkedTransferQueue<>();
	private TransferQueue<ReadQueueItem> ServerProxyReadQueue = new LinkedTransferQueue<>();

	public ForwarderServerHandler() {
		new Thread(() -> {
			while (true) {
				try {
					ReadQueueItem readQueueItem = this.ClientProxyReadQueue.take();
					clientProxyRead(readQueueItem.ctx, readQueueItem.dataWrapper);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}).start();
		new Thread(() -> {
			while (true) {
				try {
					ReadQueueItem readQueueItem = this.ServerProxyReadQueue.take();
					serverProxyRead(readQueueItem.ctx, readQueueItem.dataWrapper);
				} catch (Exception e1) {
				}
			}
		}).start();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// 服务器代理异常，通知所有客户端代理
		if (ctx == serverProxyCtx) {
			LOGGER.error("服务器代理异常，通知所有客户端代理", cause);
			this.serverProxyCtx = null;
			clientProxyCtxMap.values().forEach(clientCTX -> {
				writeAndFlush(clientCTX, StatusCode.S_ERROR);
			});
		} else {
			LOGGER.error("客户端代理通道异常，通知服务端代理", cause);
			// 客户端代理异常则通知服务器代理
			this.clientProxyCtxMap.remove(ctx.channel().id().asLongText());
			if (serverProxyCtx != null) {
				writeAndFlush(serverProxyCtx, ctx.channel().id().asLongText(), StatusCode.C_LOST);
			}
		}
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DataWrapper msg) throws Exception {
		if (StatusCode.isFromClientProxy(msg.getStatusCode())) {
			this.ClientProxyReadQueue.transfer(new ReadQueueItem(ctx, msg));
		} else if (StatusCode.isFromServerProxy(msg.getStatusCode())) {
			this.ServerProxyReadQueue.transfer(new ReadQueueItem(ctx, msg));
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
				writeAndFlush(ctx, StatusCode.S_EXIST);
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
			writeAndFlush(serverProxyCtx, msg.getClientId(), StatusCode.C_LOST);
			this.clientProxyCtxMap.remove(msg.getClientId());
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
	private void clientProxyRead(ChannelHandlerContext ctx, DataWrapper msg) {
		if (this.serverProxyCtx == null) {
			LOGGER.error("服务端代理不存在");
			// 服务端代理不存在
			writeAndFlush(ctx, StatusCode.S_NOT_FOUND);
		} else if (msg.getStatusCode() == StatusCode.C_CONNECTING) {
			LOGGER.info("客户端代理已连接 addr={}", ctx.channel().remoteAddress());
			this.clientProxyCtxMap.put(ctx.channel().id().asLongText(), ctx);
			writeAndFlush(ctx, StatusCode.C_CONNECTING);
		} else {
			msg.setClientId(ctx.channel().id().asLongText());
			writeAndFlush(serverProxyCtx, msg);
		}
	}

	public static class ReadQueueItem {
		ChannelHandlerContext ctx;
		DataWrapper dataWrapper;

		public ReadQueueItem(ChannelHandlerContext ctx, DataWrapper dataWrapper) {
			this.ctx = ctx;
			this.dataWrapper = dataWrapper;
		}

	}
}
