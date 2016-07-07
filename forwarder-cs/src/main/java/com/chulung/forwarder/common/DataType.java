package com.chulung.forwarder.common;

public enum DataType {
	/** 正常数据 */
	DATA,
	/** 客户端需关闭 */
	CLIENT_CLOSE,
	/** 客户端链接中 */
	CLIENT_CONNECTING,
	/** 服务端代理已存在 */
	SERVER_PROXY_EXIST,
	/** 服务器代理异常 */
	Server_PROXY_ERROR,
	/** 服务端代理不存在 */
	SERVER_PROXY_NOT_FOUNED,
}
