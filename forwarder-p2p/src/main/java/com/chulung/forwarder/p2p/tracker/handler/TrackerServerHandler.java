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
	boolean flag = false;
	InetSocketAddress addr1 = null;
	InetSocketAddress addr2 = null;

	@Override
	protected void readDataWarpper(ChannelHandlerContext ctx, DataWrapper dw) throws IOException {
		switch (dw.getStatusCode()) {
		case StatusCode.S_CONNECTING:
			this.serverProxyAddrMap.put(dw.getClientId(), (InetSocketAddress) ctx.channel().remoteAddress());
			break;
		case StatusCode.C_CONNECTING:
			InetSocketAddress serverAddr = this.serverProxyAddrMap.get(dw.getClientId());
			if (serverAddr != null) {
				this.writeAndFlush(ctx, new DataWrapper(serverAddr, StatusCode.S_ADDR),
						(InetSocketAddress) ctx.channel().remoteAddress());
				this.writeAndFlush(ctx,
						new DataWrapper((InetSocketAddress) ctx.channel().remoteAddress(), StatusCode.C_ADDR),
						serverAddr);
			}
			break;
		default:
			break;
		}
	}

}
