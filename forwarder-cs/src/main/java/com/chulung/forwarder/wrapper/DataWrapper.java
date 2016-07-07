package com.chulung.forwarder.wrapper;

import java.io.Serializable;

import com.chulung.forwarder.common.ClientType;
import com.chulung.forwarder.common.DataType;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

public class DataWrapper implements Serializable {
	private static final long serialVersionUID = 1178533155322062994L;
	private long createTime =System.currentTimeMillis();
	private String clientId;
	private ClientType clientType;
	private DataType dataType;
	private byte[] data;
	private String msg;

	@Override
	public String toString() {
		return "DataWrapper [clientId=" + clientId + ", clientType=" + clientType + ", dataType=" + dataType + ", data="
				+ (data == null ? null : data) + ", msg=" + msg + "]";
	}

	public ClientType getClientType() {
		return clientType;
	}

	public void setClientType(ClientType clientType) {
		this.clientType = clientType;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public ByteBuf getData() {
		return Unpooled.wrappedBuffer(data);
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setData(ByteBuf data) {
		this.data = ByteBufUtil.getBytes(data);
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

}
