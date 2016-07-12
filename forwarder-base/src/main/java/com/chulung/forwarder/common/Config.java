package com.chulung.forwarder.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config {
	private static final Config config = new Config();

	/**
	 * forwarder服务器host ip或域名
	 */
	private String forwarderHost;
	/**
	 * serverProxy连接的forwarder端口
	 */
	private int forwaderChannelPort;
	private Map<Integer, Integer> portsMap = new HashMap<>();

	public static Config getInstance() {
		return config;
	}

	private Config() {
		Properties properties = new Properties();
		try {
			properties.load(Config.class.getResourceAsStream("/config.properties"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		setForwarderHost(properties.getProperty("forwarderHost"));
		setForwaderChannelPort(Integer.parseInt(properties.getProperty("forwaderChannelPort")));
		String portsMapStr = properties.getProperty("portsMap");
		if (!portsMapStr.matches("^(\\d{1,5}:\\d{1,5},?)*(\\d{1,5}:\\d{1,5})$")) {
			throw new RuntimeException("portsMap 格式错误!");
		}
		String[] arr = portsMapStr.split(",");
		for (int i = 0; i < arr.length; i++) {
			String[] s = arr[i].split(":");
			this.portsMap.put(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
		}
	}

	public String getForwarderHost() {
		return forwarderHost;
	}

	public void setForwarderHost(String forwarderHost) {
		this.forwarderHost = forwarderHost;
	}

	public int getForwaderChannelPort() {
		return forwaderChannelPort;
	}

	public void setForwaderChannelPort(int forwaderChannelPort) {
		this.forwaderChannelPort = forwaderChannelPort;
	}

	public Map<Integer, Integer> getPortsMap() {
		return portsMap;
	}

	public void setPortsMap(Map<Integer, Integer> portsMap) {
		this.portsMap = portsMap;
	}
}