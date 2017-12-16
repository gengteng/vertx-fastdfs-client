package io.vertx.fastdfs.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.vertx.core.buffer.Buffer;

/**
 * 
 * @author GengTeng
 * <p>
 * me@gteng.org
 * 
 * @version 3.5.0
 */
public final class FdfsUtils {

	public static Buffer newZero(long size) {
		int _size = (int) size;
		ByteBuf byteBuf = Unpooled.buffer(_size, Integer.MAX_VALUE);
		byteBuf.setIndex(0, _size);
		return Buffer.buffer(byteBuf);
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
	 * @param src the source string
	 * @return new string
	 */
	public static String fdfsTrim(String src) {
		return src.trim().replaceAll("^\u0000+|\u0000+$", "");
	}
}
