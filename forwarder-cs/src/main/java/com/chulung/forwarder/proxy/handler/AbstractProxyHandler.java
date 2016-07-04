package com.chulung.forwarder.proxy.handler;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chulung.forwarder.common.ClientType;
import com.chulung.forwarder.common.Config;
import com.chulung.forwarder.common.DataType;
import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class AbstractProxyHandler extends SimpleChannelInboundHandler<Object> {
	protected ChannelHandlerContext forwarderServerCtx;
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if (((InetSocketAddress) ctx.channel().remoteAddress()).equals(Config.getConfig().getForwarderAddress())) {
			LOGGER.info("forwarderServer已连接，发送标识包");
			ctx.writeAndFlush(new DataWrapper(getClientType(), DataType.CLIENT_CONNECTING));
		}
	}

	protected abstract ClientType getClientType();
}
