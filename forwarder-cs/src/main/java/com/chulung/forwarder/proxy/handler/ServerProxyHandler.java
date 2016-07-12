package com.chulung.forwarder.proxy.handler;

import com.chulung.forwarder.common.StatusCode;
import com.chulung.forwarder.proxy.AbstractServerProxyHandler;
import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

@Sharable
public class ServerProxyHandler extends AbstractServerProxyHandler {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		LOGGER.info("forwarderServer已连接，发送标识包");
		this.forwarderServerCtx = ctx;
		ctx.writeAndFlush(new DataWrapper(StatusCode.S_CONNECTING));
	}

	public void putForwarderData(DataWrapper create) {
		try {
			this.forwardDataQueue.transfer(create);
		} catch (InterruptedException e) {
		}

	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg == null) {
			return;
		}
		handlerDataWrapper((DataWrapper)msg);
	}

}
