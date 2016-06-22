package com.chulung.forwarder.server;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.chulung.forwarder.server.ForwarderServer;

public class ForwarderServerTest {

	@Test
	public void testStart() throws IOException {
		new ForwarderServer(7778).start();
	}

}
