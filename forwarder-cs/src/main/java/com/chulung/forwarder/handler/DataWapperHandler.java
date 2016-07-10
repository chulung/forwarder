package com.chulung.forwarder.handler;

import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

public interface DataWapperHandler {
	default ChannelFuture writeAndFlush(ChannelHandlerContext ctx, String clientId, int statusCode, Object data) {
		return writeAndFlush(ctx, new DataWrapper(clientId, statusCode, data, 0));
	}

	default ChannelFuture writeAndFlush(ChannelHandlerContext ctx, String clientId, int statusCode) {
		return writeAndFlush(ctx, clientId, statusCode, null);
	}

	default ChannelFuture writeAndFlush(ChannelHandlerContext ctx, int statusCode) {
		return writeAndFlush(ctx,null, statusCode);
	}

	default ChannelFuture writeAndFlush(ChannelHandlerContext ctx, DataWrapper dataWrapper) {
		return ctx.writeAndFlush(dataWrapper);
	}
}
