package com.chulung.forwarder.p2p.client.handler;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.chulung.forwarder.common.Config;
import com.chulung.forwarder.common.StatusCode;
import com.chulung.forwarder.handler.AbstractDatagramPacketHandler;
import com.chulung.forwarder.p2p.client.AbstractProxy;
import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.channel.ChannelHandlerContext;

public class ServerProxyHandler extends AbstractProxy {

	@Override
	protected void readDataWarpper(ChannelHandlerContext ctx, DataWrapper dw, InetSocketAddress sender)
			throws IOException {
		switch (dw.getStatusCode()) {
		case StatusCode.S_CONNECTING:
			LOGGER.info("注册ServerProxy成功");
			this.registering = false;
			break;
		case StatusCode.C_DATA:
			LOGGER.info("收到client 数据。。。");
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
			String clientId = ctx.channel().id().asLongText();
			while (registering) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				LOGGER.info("正在向tracker 服务器注册ServerProxy ..{}s", i++);
				try {
					writeAndFlush(ctx, new DataWrapper("serverId", StatusCode.S_CONNECTING), trackerServerAddr);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
