package com.chulung.forwarder.p2p.client.handler;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.chulung.forwarder.common.StatusCode;
import com.chulung.forwarder.p2p.client.AbstractP2PProxy;
import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ClientProxyHandler extends AbstractP2PProxy {

	private InetSocketAddress serverInetCocketAddress;

	@Override
	protected void readLocalAppBuf(ChannelHandlerContext ctx, ByteBuf msg) throws IOException {
		if (serverInetCocketAddress == null) {
			ctx.close();
		}
		writeAndFlush(forwarderServerCtx, new DataWrapper(ctx.channel().id().asLongText(), StatusCode.C_DATA),
				serverInetCocketAddress);
	}

	@Override
	protected void readDataWarpper(ChannelHandlerContext ctx, DataWrapper dw, InetSocketAddress inetSocketAddress)
			throws IOException {
		switch (dw.getStatusCode()) {
		case StatusCode.S_ADDR:
			if (serverInetCocketAddress != null) {
				return;
			}
			this.registering = false;
			this.serverInetCocketAddress = new InetSocketAddress(dw.getAddr(), dw.getClientProxyPort());
			break;
		default:
			break;
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		new Thread(() -> {
			int i = 1;
			while (registering) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				LOGGER.info("正在向tracker 服务器获取serverProxy地址 ..{}s", i++);
				try {
					writeAndFlush(ctx, new DataWrapper("serverId", StatusCode.C_GET_SADDR), trackerServerAddr);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}