package com.chulung.forwarder.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chulung.forwarder.common.StatusCode;
import com.chulung.forwarder.proxy.local.RemoteAppProxy;
import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class AbstractServerProxyHandler extends SimpleChannelInboundHandler<Object> {
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	protected Thread thread = null;
	protected boolean isRuning = true;
	protected TransferQueue<Object> forwardDataQueue = new LinkedTransferQueue<>();
	protected ChannelHandlerContext forwarderServerCtx;
	protected ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	protected Map<String, ChannelHandlerContext> clientProxyCtxMap = new HashMap<>();

	public void startForwardQueue() {
		thread = new Thread(() -> {
			while (isRuning) {
				try {
					if (forwarderServerCtx.channel().isActive()) {
						forwarderServerCtx.writeAndFlush(forwardDataQueue.take());
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

	public void close() {
		if (thread != null) {
			isRuning = false;
			thread.interrupt();
			this.forwardDataQueue.offer(new Object());
		}
	}

	public void handlerDataWrapper(DataWrapper dw) {
		if (dw.getStatusCode() == StatusCode.C_DATA) {
			ChannelHandlerContext clientCtx = this.clientProxyCtxMap.get(dw.getClientId() + dw.getClientProxyPort());
			if (clientCtx == null) {
				synchronized (this) {
					clientCtx = this.clientProxyCtxMap.get(dw.getClientId() + dw.getClientProxyPort());
					if (clientCtx == null) {
						this.cachedThreadPool.execute(new RemoteAppProxy(dw, this));
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			} else if (dw.getData() != null) {
				clientCtx.writeAndFlush(dw.getData());
			}
		} else if (dw.getStatusCode() == StatusCode.C_LOST) {
			LOGGER.error("远程客户端已断开 client_id={} port={}", dw.getClientId(), dw.getClientProxyPort());
			delelteRemoteAppProxyCtx(dw.getClientId() + dw.getClientProxyPort());
		}
	}

	public void putForwarderData(DataWrapper create) {
		try {
			this.forwardDataQueue.transfer(create);
		} catch (InterruptedException e) {
		}

	}

}
