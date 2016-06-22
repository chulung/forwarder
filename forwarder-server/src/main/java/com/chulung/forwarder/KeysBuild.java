package com.chulung.forwarder;

public class KeysBuild {
	private static volatile byte a = 0;
	private static volatile byte b = 0;
	private static volatile byte c = 0;

	public static byte[] getBytesKeys() {
		a++;
		if (a >= 255) {
			a = 0;
			b++;
		}
		if (b >= 255) {
			b = 0;
			c++;
		}
		return new byte[] { a, b, c };
	}

	public static  String toString(byte[] b) {
		String s = "";
		for (byte c : b) {
			s += c;
		}
		assert b.length==3;
		return s;
	}
}
