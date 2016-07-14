package com.chulung.forwarder.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;

import com.chulung.forwarder.codec.KryoPool;
import com.chulung.forwarder.proxy.AbstractServerProxyHandler;
import com.chulung.forwarder.wrapper.DataWrapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

public abstract class AbstractDatagramPacketHandler extends AbstractServerProxyHandler {
	private KryoPool kryoPool = KryoPool.getInstance();

	public AbstractDatagramPacketHandler() {
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof DatagramPacket) {
			 Object obj = null;      
		        try {        
		            ByteArrayInputStream bis = new ByteArrayInputStream (ByteBufUtil.getBytes(((DatagramPacket) msg).content()));        
		            ObjectInputStream ois = new ObjectInputStream (bis);        
		            obj = ois.readObject();      
		            ois.close();   
		            bis.close();   
		        } catch (IOException ex) {        
		            ex.printStackTrace();   
		        } catch (ClassNotFoundException ex) {        
		            ex.printStackTrace();   
		        }      
//			readDataWarpper(ctx, (DataWrapper) kryoPool.decode(((DatagramPacket) msg).content()));
			readDataWarpper(ctx, (DataWrapper) obj,((DatagramPacket) msg).sender());
		} else {
			readLocalAppBuf(ctx, (ByteBuf) msg);
		}
	}

	protected void readLocalAppBuf(ChannelHandlerContext ctx, ByteBuf msg) {

	}

	protected ChannelFuture writeAndFlush(ChannelHandlerContext ctx, DataWrapper dataWrapper, InetSocketAddress address)
			throws IOException {
		byte[] bytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(dataWrapper);
			oos.flush();
			bytes = bos.toByteArray();
			oos.close();
			bos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		ByteBuf data = Unpooled.copiedBuffer(bytes);
		// kryoPool.encode(data, dataWrapper);
		return ctx.writeAndFlush(new DatagramPacket(data, address));
	}

	protected abstract void readDataWarpper(ChannelHandlerContext ctx, DataWrapper dw, InetSocketAddress inetSocketAddress) throws IOException;
}
