package com.chulung.forwarder.common;

public class StatusCode {
	public static final int C_DATA = 102;
	/** 客户端丢失 */
	public static final int C_LOST = 103;
	/** 客户端代理链接 */
	public static final int C_CONNECTING = 104;
	/** 客户端地址信息 */
	public static final int C_ADDR = 105;
	/** 客户端获取server地址请求 */
	public static final int C_GET_SADDR = 106;
	/** 服务端代理连接 */
	public static final int S_CONNECTING = 201;
	/** 服务端数据 */
	public static final int S_DATA = 202;
	/** 服务端代理已存在 */
	public static final int S_EXIST = 203;
	/** 服务端代理关闭连接 */
	public static final int S_CLOSE = 204;
	/** 服务端地址信息 */
	public static final int S_ADDR = 205;

	public static boolean isFromServerProxy(int statusCode) {
		return statusCode > 200 && statusCode < 300;
	}

	public static boolean isFromClientProxy(int statusCode) {
		return statusCode > 100 && statusCode < 200;
	}
}
