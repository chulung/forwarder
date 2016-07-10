package com.chulung.forwarder.common;

public class StatusCode {
	/** 客户端代理连接 */
	public static final int C_CONNECTING = 101;
	/** 客户端数据 */
	public static final int C_DATA = 102;
	/** 客户端丢失 */
	public static final int C_LOST = 103;
	/** 客户端本地应用关闭连接 */
	public static final int C_APP_CLOSE = 104;

	/** 服务端代理连接 */
	public static final int S_CONNECTING = 201;
	/** 服务端数据 */
	public static final int S_DATA = 202;
	/** 服务端代理已存在 */
	public static final int S_EXIST = 203;
	/** 服务端代理关闭连接 */
	public static final int S_CLOSE = 204;
	/** 服务端代理出现错误 */
	public static final int S_ERROR = 205;
	/** 服务端代理未启动 */
	public static final int S_NOT_FOUND = 205;


	public static boolean isFromServerProxy(int statusCode) {
		return statusCode > 200 && statusCode < 300;
	}
	public static boolean isFromClientProxy(int statusCode) {
		return statusCode > 100 && statusCode < 200;
	}
}
