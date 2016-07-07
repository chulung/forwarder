package com.chulung.forwarder.server.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chulung.forwarder.common.ClientType;
import com.chulung.forwarder.common.DataType;
import com.chulung.forwarder.wrapper.DataWrapper;
import com.chulung.forwarder.wrapper.DataWrapperBuilder;

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
public class ForwarderServerHandler extends SimpleChannelInboundHandler<DataWrapper> {
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
					// TODO Auto-generated catch block
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
			clientProxyCtxMap.values().forEach(ctxv -> {
				ctxv.writeAndFlush(new DataWrapperBuilder().setDataType(DataType.Server_PROXY_ERROR).create());
			});
			this.serverProxyCtx = null;
		} else {
			LOGGER.error("客户端代理通道异常，通知服务端代理", cause);
			// 客户端代理异常则通知服务器代理
			this.clientProxyCtxMap.remove(ctx.channel().id().asLongText());
			if (serverProxyCtx != null) {
				serverProxyCtx.writeAndFlush(new DataWrapperBuilder().setDataType(DataType.CLIENT_CLOSE)
						.setClientId(ctx.channel().id().asLongText()).create());
			}
		}
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DataWrapper msg) throws Exception {
		if (msg == null || msg.getClientType() == null) {
			ctx.close();
			// 远程客户端代理数据
		} else if (msg.getClientType() == ClientType.ClientProxy) {
			this.ClientProxyReadQueue.transfer(new ReadQueueItem(ctx, msg));
		} else if (msg.getClientType() == ClientType.ServerProxy) {
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
		if (msg.getDataType() == DataType.CLIENT_CONNECTING) {
			if (this.serverProxyCtx != null) {
				ctx.writeAndFlush(new DataWrapperBuilder().setDataType(DataType.SERVER_PROXY_EXIST).create());
				LOGGER.error("服务端代理已存在 address={}", this.serverProxyCtx.channel().remoteAddress());
				return;
			} else {
				LOGGER.info("服务端代理已连接 addr={}", ctx.channel().remoteAddress());
				this.serverProxyCtx = ctx;
			}
		}
		if (msg.getDataType() != DataType.DATA) {
			return;
		}
		ChannelHandlerContext clientCtx = this.clientProxyCtxMap.get(msg.getClientId());
		if (clientCtx != null) {
			clientCtx.writeAndFlush(msg);
		} else {
			// 远程客户端丢失，通知远程服务端代理抛弃对应通道
			LOGGER.error("远程客户端丢失 ClientId={}", msg.getClientId());
			this.serverProxyCtx.write(new DataWrapperBuilder().setDataType(DataType.CLIENT_CLOSE)
					.setClientId(msg.getClientId()).create());
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
			ctx.writeAndFlush(new DataWrapperBuilder().setDataType(DataType.SERVER_PROXY_NOT_FOUNED).create());
		} else if (msg.getDataType() == DataType.CLIENT_CONNECTING) {
			LOGGER.info("客户端代理已连接 addr={}", ctx.channel().remoteAddress());
			this.clientProxyCtxMap.put(ctx.channel().id().asLongText(), ctx);
			ctx.writeAndFlush(new DataWrapperBuilder().setDataType(DataType.CLIENT_CONNECTING).create());
		} else if (msg.getDataType() == DataType.DATA) {
			msg.setClientId(ctx.channel().id().asLongText());
			this.serverProxyCtx.writeAndFlush(msg);
		} else if (msg.getDataType() == DataType.CLIENT_CLOSE) {
			this.clientProxyCtxMap.remove(ctx.channel().id().asLongText());
			this.serverProxyCtx.write(new DataWrapperBuilder().setDataType(DataType.CLIENT_CLOSE)
					.setClientId(msg.getClientId()).create());
			ctx.close();
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
