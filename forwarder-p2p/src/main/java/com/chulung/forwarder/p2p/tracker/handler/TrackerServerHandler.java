package com.chulung.forwarder.p2p.tracker.handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.chulung.forwarder.common.StatusCode;
import com.chulung.forwarder.handler.AbstractDatagramPacketHandler;
import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.channel.ChannelHandlerContext;

public class TrackerServerHandler extends AbstractDatagramPacketHandler {
	private Map<String, InetSocketAddress> serverProxyAddrMap = new ConcurrentHashMap<>();

	@Override
	protected void readDataWarpper(ChannelHandlerContext ctx, DataWrapper dw, InetSocketAddress sender)
			throws IOException {
		switch (dw.getStatusCode()) {
		case StatusCode.S_CONNECTING:
			LOGGER.info("收到serverProxy 注册信息　addr={} id={}", sender, dw.getClientId());
			this.serverProxyAddrMap.put(dw.getClientId(), sender);
			writeAndFlush(ctx, new DataWrapper(StatusCode.S_CONNECTING), sender);
			break;
		case StatusCode.C_GET_SADDR:
			InetSocketAddress serverAddr = this.serverProxyAddrMap.get(dw.getClientId());
			if (serverAddr != null) {
				this.writeAndFlush(ctx, new DataWrapper(serverAddr, StatusCode.S_ADDR), sender);
				this.writeAndFlush(ctx, new DataWrapper(sender, StatusCode.C_ADDR), serverAddr);
			}
			break;
		default:
			break;
		}
	}

}
