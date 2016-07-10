package com.chulung.forwarder.common;

import java.awt.geom.IllegalPathStateException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config {
	private static final Config CONFIG = new Config();

	/**
	 * forwarder服务器host ip或域名
	 */
	private String forwarderHost ;
	private InetSocketAddress forwarderAddress;

	/**
	 * forwarder 服务器端口
	 */
	private int forwaderPort;
	/**
	 * 目标服务器端口
	 */
	private int[] targetPort;
	/** 客户端代理服务端口 */
	private int[] clientProxyPort;
	private Map<Integer, Integer> portMap = new HashMap<>();

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
			String[] targetPortStr = properties.getProperty("targetPort").split(",");
			String[] clientProxyPortStr = properties.getProperty("clientProxyPort").split(",");
			if (targetPortStr.length != clientProxyPortStr.length) {
				throw new IllegalArgumentException("targetPort,clientProxyPort 端口数不匹配");
			}
			this.targetPort = new int[targetPortStr.length];
			this.clientProxyPort = new int[targetPortStr.length];
			for (int i = 0; i < clientProxyPortStr.length; i++) {
				clientProxyPort[i] = Integer.parseInt(clientProxyPortStr[i]);
			}
			for (int i = 0; i < targetPortStr.length; i++) {
				targetPort[i] = Integer.parseInt(targetPortStr[i]);
				portMap.put(clientProxyPort[i], targetPort[i]);
			}

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

	public int[] getTargetPort() {
		return targetPort;
	}

	public void setTargetPort(int[] targetPort) {
		this.targetPort = targetPort;
	}

	public int getForwaderPort() {
		return forwaderPort;
	}

	public void setForwaderPort(int forwaderPort) {
		this.forwaderPort = forwaderPort;
	}

	public int[] getClientProxyPort() {
		return clientProxyPort;
	}

	public void setClientProxyPort(int[] clientProxyPort) {
		this.clientProxyPort = clientProxyPort;
	}

	public int getMappedPort(int clientPort) {
		return this.portMap.get(clientPort);
	}
}