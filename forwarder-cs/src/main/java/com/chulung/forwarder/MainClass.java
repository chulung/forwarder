package com.chulung.forwarder;

import com.chulung.forwarder.proxy.ServerProxy;
import com.chulung.forwarder.server.ForwarderServer;

public class MainClass {

	public static void main(String[] args) {
		if (args.length != 1) {
			throw new IllegalArgumentException();
		}
		switch (args[0]) {
		case "ForwarderServer":
			new ForwarderServer().startSync();
			;
			break;
		case "ServerProxy":
			new ServerProxy().startSync();
			break;
		default:
			break;
		}
	}

}
