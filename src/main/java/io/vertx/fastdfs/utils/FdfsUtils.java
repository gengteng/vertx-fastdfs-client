package io.vertx.fastdfs.utils;

import java.util.Arrays;

import io.vertx.core.buffer.Buffer;

public final class FdfsUtils {

	public static Buffer newZero(long size) {
		byte[] bytes = new byte[(int) size];
		Arrays.fill(bytes, (byte) 0);

		return Buffer.buffer(bytes);
	}

	public static void printBytes(String flag, byte[] bytes) {
		System.out.println("------------" + flag + "=" + bytes.length + "------------");
		for (int i = 0; i < bytes.length; ++i) {
			System.out.print(bytes[i] + " ");
		}
		System.out.println("\n------------" + flag + "=" + bytes.length + "------------");
	}
	
	public static void printBytes(String flag, Buffer buffer) {
		printBytes(flag, buffer.getBytes());
	}

	/**
	 * 在trim的基础上再去掉\u0000
	 * 
	 * @param src
	 * @return
	 */
	public static String fdfsTrim(String src) {
		return src.trim().replaceAll("^\u0000+|\u0000+$", "");
	}
}
