package com.chulung.forwarder.p2p.client.handler;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.chulung.forwarder.common.StatusCode;
import com.chulung.forwarder.p2p.client.AbstractP2PProxy;
import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.channel.ChannelHandlerContext;

public class ServerProxyHandler extends AbstractP2PProxy {

	@Override
	protected void readDataWarpper(ChannelHandlerContext ctx, DataWrapper dw, InetSocketAddress sender)
			throws IOException {
		switch (dw.getStatusCode()) {
		case StatusCode.S_CONNECTING:
			LOGGER.info("注册ServerProxy成功");
			this.registering = false;
			break;
		case StatusCode.C_DATA:
			handlerDataWrapper(dw);
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
