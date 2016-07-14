package com.chulung.forwarder.p2p.client;

import java.net.InetSocketAddress;

import com.chulung.forwarder.common.Config;
import com.chulung.forwarder.handler.AbstractDatagramPacketHandler;

public abstract class AbstractProxy extends AbstractDatagramPacketHandler {

	protected InetSocketAddress trackerServerAddr = new InetSocketAddress(Config.getInstance().getForwarderHost(),
			Config.getInstance().getForwaderChannelPort());
	protected boolean registering = true;

}
