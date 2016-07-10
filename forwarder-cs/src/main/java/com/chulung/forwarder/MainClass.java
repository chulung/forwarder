package com.chulung.forwarder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chulung.forwarder.proxy.ClientProxy;
import com.chulung.forwarder.proxy.ServerProxy;
import com.chulung.forwarder.server.ForwarderServer;

public class MainClass {
	private static Logger logger=LoggerFactory.getLogger(MainClass.class);
	public static void main(String[] args) {
		if (args.length != 1) {
			throw new IllegalArgumentException();
		}
		logger.error("================================{}",args[0]);
		System.out.println(args[0]);
		switch (args[0]) {
		case "ForwarderServer":
			new ForwarderServer().start();
			break;
		case "ClientProxy":
			new ClientProxy().run();
			break;
		case "ServerProxy":
			new ServerProxy().run();
			break;
		default:
			break;
		}
	}

}
