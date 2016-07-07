package com.chulung.forwarder.wrapper;

import com.chulung.forwarder.common.ClientType;
import com.chulung.forwarder.common.DataType;

import io.netty.buffer.ByteBuf;

public class DataWrapperBuilder {

	private DataWrapper dataWrapper = new DataWrapper();

	public DataWrapperBuilder setDataType(DataType dataType) {
		dataWrapper.setDataType(dataType);
		return this;
	}

	public DataWrapperBuilder setData(ByteBuf byteBuf) {
		dataWrapper.setData(byteBuf);
		return this;
	}

	public DataWrapperBuilder setClientId(String clientId) {
		dataWrapper.setClientId(clientId);
		return this;
	}

	public DataWrapperBuilder setClientType(ClientType clientType){
		dataWrapper.setClientType(clientType);
		return this;
	}

	public DataWrapper create() {
		return dataWrapper;
	}
}
