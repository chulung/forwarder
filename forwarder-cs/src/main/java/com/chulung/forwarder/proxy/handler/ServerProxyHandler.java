package com.chulung.forwarder.proxy.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.chulung.forwarder.common.StatusCode;
import com.chulung.forwarder.proxy.local.RemoteAppProxy;
import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

@Sharable
public class ServerProxyHandler extends AbstractProxyHandler {
	public ServerProxyHandler() {
		super(StatusCode.S_CONNECTING);
	}

	private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	public Map<String, ChannelHandlerContext> clientProxyCtxMap = new HashMap<>();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg == null) {
			return;
		}
		if (msg instanceof DataWrapper) {
			DataWrapper dw = (DataWrapper) msg;
			if (dw.getStatusCode() == StatusCode.C_DATA) {
				ChannelHandlerContext clientCtx = this.clientProxyCtxMap
						.get(dw.getClientId() + dw.getClientProxyPort());
				if (clientCtx == null) {
					synchronized (this) {
						clientCtx = this.clientProxyCtxMap.get(dw.getClientId() + dw.getClientProxyPort());
						if (clientCtx == null) {
							this.cachedThreadPool.execute(new RemoteAppProxy(dw, this));
							Thread.sleep(100);
						}
					}
				} else if (dw.getData() != null) {
					clientCtx.writeAndFlush(dw.getData());
				}
			} else if (dw.getStatusCode() == StatusCode.C_APP_CLOSE || dw.getStatusCode() == StatusCode.C_LOST) {
				LOGGER.error("远程客户端已断开 client_id={} port={}", dw.getClientId(),dw.getClientProxyPort());
				delelteRemoteAppProxyCtx(dw.getClientId() + dw.getClientProxyPort());
			}
		}
	}

	public void putRemoteAppProxyCtx(String clientId, ChannelHandlerContext ctx) {
		this.clientProxyCtxMap.put(clientId, ctx);

	}

	public void delelteRemoteAppProxyCtx(String clientId) {
		ChannelHandlerContext context = this.clientProxyCtxMap.remove(clientId);
		if (context != null) {
			LOGGER.info("ctx={} 被关闭" + context);
			context.close();
		}
	}

}
