package com.chulung.forwarder.client;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class ForwardClientTest {

	@Test
	public void test() throws IOException {
		new ForwardClient("127.0.0.1", 7778, "127.0.0.1", 7777).start();
		
	}

}
