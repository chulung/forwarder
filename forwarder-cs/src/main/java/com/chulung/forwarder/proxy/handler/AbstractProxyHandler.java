package com.chulung.forwarder.proxy.handler;

import java.net.InetSocketAddress;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chulung.forwarder.common.Config;
import com.chulung.forwarder.handler.DataWapperHandler;
import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class AbstractProxyHandler extends SimpleChannelInboundHandler<Object> implements DataWapperHandler {
	protected ChannelHandlerContext forwarderServerCtx;
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private TransferQueue<DataWrapper> forwardDataQueue = new LinkedTransferQueue<>();
	private Thread thread = null;
	private boolean isRuning = true;
	private int activeMsgCode;

	public AbstractProxyHandler(int activeMsgCode) {
		this.activeMsgCode = activeMsgCode;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if (((InetSocketAddress) ctx.channel().remoteAddress()).equals(Config.getConfig().getForwarderAddress())) {
			LOGGER.info("forwarderServer已连接，发送标识包");
			this.forwarderServerCtx = ctx;
			writeAndFlush(ctx, activeMsgCode);
			thread = new Thread(() -> {
				while (isRuning) {
					try {
						if (forwarderServerCtx.channel().isActive()) {
							this.writeAndFlush(forwarderServerCtx, forwardDataQueue.take());
						} else {
							LOGGER.error("转发服务器通道已关闭");
							return;
						}
					} catch (Exception e) {
					}
				}
			});
			thread.start();
		}
	}

	public void close() {
		if (thread != null) {
			isRuning = false;
			thread.interrupt();
			this.forwardDataQueue.offer(new DataWrapper());
		}
	}

	public void putForwarderData(DataWrapper create) {
		try {
			this.forwardDataQueue.transfer(create);
		} catch (InterruptedException e) {
		}

	}
}
