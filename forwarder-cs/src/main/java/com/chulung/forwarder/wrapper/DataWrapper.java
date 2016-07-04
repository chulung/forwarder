package com.chulung.forwarder.wrapper;

import java.io.Serializable;
import java.util.Arrays;

import com.chulung.forwarder.common.ClientType;
import com.chulung.forwarder.common.DataType;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelId;

public class DataWrapper implements Serializable {
	private static final long serialVersionUID = 1178533155322062994L;
	private ChannelId clientId;
	private ClientType clientType;
	private DataType dataType;
	private ByteBuf data;
	private String msg;

	public DataWrapper() {
	}

	public DataWrapper(ClientType clientType, com.chulung.forwarder.common.DataType dataType) {
		super();
		this.clientType = clientType;
		this.dataType = dataType;
	}

	public DataWrapper(DataType dataType) {
		super();
		this.dataType = dataType;
	}

	public DataWrapper(DataType code, ChannelId clientId) {
		super();
		this.dataType = code;
		this.clientId = clientId;
	}

	public DataWrapper(ByteBuf src) {
		this.data = src;
	}

	@Override
	public String toString() {
		return "DataWrapper [clientId=" + clientId + ", clientType=" + clientType + ", dataType=" + dataType + ", data="
				+ data + ", msg=" + msg + "]";
	}

	public ClientType getClientType() {
		return clientType;
	}

	public void setClientType(ClientType clientType) {
		this.clientType = clientType;
	}

	public DataType getDataCode() {
		return dataType;
	}

	public void setDataCode(DataType dataCode) {
		this.dataType = dataCode;
	}

	public ByteBuf getData() {
		return data;
	}

	public ChannelId getClientId() {
		return clientId;
	}

	public void setClientId(ChannelId clientId) {
		this.clientId = clientId;
	}

	public void setSrc(ByteBuf data) {
		this.data = data;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
