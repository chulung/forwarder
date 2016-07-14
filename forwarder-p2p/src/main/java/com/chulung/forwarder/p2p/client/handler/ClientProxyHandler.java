package com.chulung.forwarder.p2p.client.handler;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.chulung.forwarder.common.StatusCode;
import com.chulung.forwarder.handler.AbstractDatagramPacketHandler;
import com.chulung.forwarder.p2p.client.AbstractProxy;
import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ClientProxyHandler extends AbstractProxy {

	private InetSocketAddress serverInetCocketAddress;

	@Override
	protected void readLocalAppBuf(ChannelHandlerContext ctx, ByteBuf msg) {
	}

	@Override
	protected void readDataWarpper(ChannelHandlerContext ctx, DataWrapper dw, InetSocketAddress inetSocketAddress)
			throws IOException {
		switch (dw.getStatusCode()) {
		case StatusCode.S_ADDR:
			if (serverInetCocketAddress!=null) {
				return;
			}
			this.registering = false;
			this.serverInetCocketAddress = new InetSocketAddress(dw.getAddr(), dw.getClientProxyPort());
			LOGGER.info("获取到server addr={},开始推送数据",serverInetCocketAddress);
			new Thread(() -> {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						writeAndFlush(ctx, new DataWrapper("", StatusCode.C_DATA), serverInetCocketAddress);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
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