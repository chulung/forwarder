package com.chulung.forwarder;

public class KeysBuild {
	private static volatile byte a = 0;
	private static volatile byte b = 0;
	private static volatile byte c = 0;

	public static byte[] getBytesKeys() {
		a++;
		if (a >= 127) {
			b++;
			a = 0;
		}
		if (b >= 127) {
			c++;
			b = 0;
		}
		return new byte[] { a, b, c };
	}
}
