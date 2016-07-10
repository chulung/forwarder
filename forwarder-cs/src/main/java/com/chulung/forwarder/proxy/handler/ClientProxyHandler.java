package com.chulung.forwarder.proxy.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.chulung.forwarder.common.StatusCode;
import com.chulung.forwarder.common.Util;
import com.chulung.forwarder.proxy.local.LocalAppProxy;
import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

@Sharable
public class ClientProxyHandler extends AbstractProxyHandler {

	public ClientProxyHandler() {
		super(StatusCode.C_CONNECTING);
	}

	private Map<Integer, ChannelHandlerContext> localAppCtxMap = new ConcurrentHashMap<>();

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof DataWrapper) {
			DataWrapper dataWrapper = (DataWrapper) msg;
			if (dataWrapper.getStatusCode() == StatusCode.C_CONNECTING) {
				new LocalAppProxy(this).start();
			} else if (dataWrapper.getStatusCode() == StatusCode.S_DATA) {
				ChannelHandlerContext localAppCtx = this.localAppCtxMap.get(((DataWrapper) msg).getClientProxyPort());
				if (localAppCtx != null && localAppCtx.channel().isActive()) {
					localAppCtx.writeAndFlush(dataWrapper.getData());
				} else {
					LOGGER.error("本地应用已断开 port={}", dataWrapper.getClientProxyPort());
					if (localAppCtx != null) {
						localAppCtxMap.remove(Util.getLocalPort(localAppCtx));
						localAppCtx.close();
					}
					super.putForwarderData(
							new DataWrapper(null, StatusCode.C_APP_CLOSE, null, dataWrapper.getClientProxyPort()));
				}
			} else if (dataWrapper.getStatusCode() == StatusCode.S_NOT_FOUND) {
				LOGGER.error("服务端代理未找到");
				ctx.close();
				super.close();
			} else if (dataWrapper.getStatusCode() == StatusCode.S_ERROR) {
				LOGGER.error("服务端代理错误，已断开");
				if (ctx != null) {
					ctx.close();
				}
			}
		} else if (msg instanceof ByteBuf) {
			int localPort = Util.getLocalPort(ctx);
			ChannelHandlerContext context = localAppCtxMap.get(localPort);
			if (context!=null && context!=ctx) {
				ctx.close();
			}
			if (context==null) {
				LOGGER.info("本地应用已连接 port={}",localPort);
			}
			localAppCtxMap.putIfAbsent(localPort, ctx);
			// channelid由forwarder设置
			super.putForwarderData(new DataWrapper(null, StatusCode.C_DATA, msg, localPort));
		}

	}

}
