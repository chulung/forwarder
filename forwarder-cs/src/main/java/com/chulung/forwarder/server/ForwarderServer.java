package com.chulung.forwarder.server;

import com.chulung.forwarder.Server.AbstractServer;
import com.chulung.forwarder.Server.RemotePortMapServer;
import com.chulung.forwarder.codec.KryoDecoder;
import com.chulung.forwarder.codec.KryoEncoder;
import com.chulung.forwarder.codec.KryoPool;
import com.chulung.forwarder.common.Config;
import com.chulung.forwarder.server.handler.ForwarderServerHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * 转发服务端
 * 
 * @author ChuKai
 *
 */
public class ForwarderServer extends AbstractServer {
	private ForwarderServerHandler forwarderServerHandler = new ForwarderServerHandler();

	public void startSync() {
		new RemotePortMapServer(forwarderServerHandler).startServerAsync();
		this.startServerSync(Config.getInstance().getForwaderChannelPort());
	}

	public void startAsync() {
		this.startServerAsync(Config.getInstance().getForwaderChannelPort());
	}

	@Override
	public ChannelInitializer<Channel> getChildHandler() {
		return new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new KryoEncoder(KryoPool.getInstance()));
				ch.pipeline().addLast(new KryoDecoder(KryoPool.getInstance()));
				ch.pipeline().addLast(forwarderServerHandler);
			}
		};
	}

	public static void main(String[] args) throws Exception {
		new ForwarderServer().startSync();
	}

}
