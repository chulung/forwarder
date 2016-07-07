package com.chulung.forwarder.common;

import java.awt.geom.IllegalPathStateException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

public class Config {
	private static final Config CONFIG = new Config();

	/**
	 * forwarder服务器host ip或域名
	 */
	private String forwarderHost = "127.0.0.1";
	private InetSocketAddress forwarderAddress;

	/**
	 * forwarder 服务器端口
	 */
	private int forwaderPort;
	/**
	 * 目标服务器host 如果服务器在本机，即为127.0.0.1
	 */
	private String targetHost;
	/**
	 * 目标服务器端口
	 */
	private int targetPort;
	private InetSocketAddress localServerAddress;
	/** 客户端代理服务端口 */
	private int clientProxyPort;

	private long localAppOutTime;

	public static Config getConfig() {
		return CONFIG;
	}

	private Config() {
		Properties properties = new Properties();
		try {
			properties.load(this.getClass().getResourceAsStream("/config.properties"));
			this.forwarderHost = properties.getProperty("forwarderHost");
			this.forwaderPort = Integer.parseInt(properties.getProperty("forwaderPort"));
			this.forwarderAddress = new InetSocketAddress(forwarderHost, forwaderPort);
			this.targetHost = properties.getProperty("targetHost");
			this.targetPort = Integer.parseInt(properties.getProperty("targetPort"));
			this.clientProxyPort = Integer.parseInt(properties.getProperty("clientProxyPort"));
			this.setLocalServerAddress(new InetSocketAddress(targetHost, targetPort));
			this.setLocalAppOutTime(Long.parseLong(properties.getProperty("localAppOutTime")));
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalPathStateException("Can't load config.properties!");
		}
	}

	public InetSocketAddress getForwarderAddress() {
		return forwarderAddress;
	}

	public void setForwarderAddress(InetSocketAddress forwarderAddress) {
		this.forwarderAddress = forwarderAddress;
	}

	public String getForwarderHost() {
		return forwarderHost;
	}

	public void setForwarderHost(String forwarderHost) {
		this.forwarderHost = forwarderHost;
	}

	public String getTargetHost() {
		return targetHost;
	}

	public void setTargetHost(String targetHost) {
		this.targetHost = targetHost;
	}

	public int getTargetPort() {
		return targetPort;
	}

	public void setTargetPort(int targetPort) {
		this.targetPort = targetPort;
	}

	public int getForwaderPort() {
		return forwaderPort;
	}

	public void setForwaderPort(int forwaderPort) {
		this.forwaderPort = forwaderPort;
	}

	public int getClientProxyPort() {
		return clientProxyPort;
	}

	public void setClientProxyPort(int clientProxyPort) {
		this.clientProxyPort = clientProxyPort;
	}

	public InetSocketAddress getLocalServerAddress() {
		return localServerAddress;
	}

	public void setLocalServerAddress(InetSocketAddress localServerAddress) {
		this.localServerAddress = localServerAddress;
	}

	public long getLocalAppOutTime() {
		return localAppOutTime;
	}

	public void setLocalAppOutTime(long localAppOutTime) {
		this.localAppOutTime = localAppOutTime;
	}

}
