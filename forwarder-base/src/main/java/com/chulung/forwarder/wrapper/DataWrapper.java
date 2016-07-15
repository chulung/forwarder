package com.chulung.forwarder.wrapper;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

public class DataWrapper {
	private String clientId;
	private int port;
	private String addr;
	private int statusCode;
	private Object data;

	public DataWrapper(int statusCode) {
		super();
		this.statusCode = statusCode;
	}

	public DataWrapper(String clientId, int statusCode, ByteBuf data, int clientProxyPort) {
		if (data != null) {
			this.data = ByteBufUtil.getBytes((ByteBuf) data);
		}
		this.clientId = clientId;
		this.statusCode = statusCode;
		this.port = clientProxyPort;
	}

	public DataWrapper() {
	}

	public DataWrapper(InetSocketAddress addr, int statusCode) {
		super();
		this.port = addr.getPort();
		this.addr = addr.getHostName();
		this.statusCode = statusCode;
	}

	public DataWrapper(String clientId, int statusCode) {
		this.clientId = clientId;
		this.statusCode = statusCode;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public ByteBuf getData() {
		return Unpooled.wrappedBuffer((byte[]) data);
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setData(ByteBuf data) {
		if (data != null) {
			this.data = ByteBufUtil.getBytes(data);
		}
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatus(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getClientProxyPort() {
		return port;
	}

	public void setClientProxyPort(int clientProxyPort) {
		this.port = clientProxyPort;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

}
