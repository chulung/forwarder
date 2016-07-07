package com.chulung.forwarder.proxy.handler;

import java.net.InetSocketAddress;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chulung.forwarder.common.ClientType;
import com.chulung.forwarder.common.Config;
import com.chulung.forwarder.common.DataType;
import com.chulung.forwarder.wrapper.DataWrapper;
import com.chulung.forwarder.wrapper.DataWrapperBuilder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class AbstractProxyHandler extends SimpleChannelInboundHandler<Object> {
	protected ChannelHandlerContext forwarderServerCtx;
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private TransferQueue<DataWrapper> queue = new LinkedTransferQueue<>();
	private Thread thread = null;
	private boolean isRuning = true;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if (((InetSocketAddress) ctx.channel().remoteAddress()).equals(Config.getConfig().getForwarderAddress())) {
			LOGGER.info("forwarderServer已连接，发送标识包");
			this.forwarderServerCtx = ctx;
			ctx.writeAndFlush(new DataWrapperBuilder().setClientType(getClientType())
					.setDataType(DataType.CLIENT_CONNECTING).create());
			thread = new Thread(() -> {
				while (isRuning) {
					try {
						this.forwarderServerCtx.writeAndFlush(queue.take());
					} catch (Exception e) {
					}
				}
			});
			thread.start();
		}
	}

	protected abstract ClientType getClientType();

	public void close() {
		if (thread != null) {
			isRuning = false;
			thread.interrupt();
		}
	}

	public void put(DataWrapper create) {
		try {
			this.queue.transfer(create);
		} catch (InterruptedException e) {
		}

	}
}
