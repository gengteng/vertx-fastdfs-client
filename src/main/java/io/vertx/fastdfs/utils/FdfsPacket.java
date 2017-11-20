package io.vertx.fastdfs.utils;

import io.vertx.core.buffer.Buffer;

/**
 * FastDFS server packet.
 * 
 * @author GengTeng
 * <p>
 * me@gteng.org
 * 
 * @version 3.5.0
 */
public final class FdfsPacket {
	private long bodyLength;
	private Buffer bodyBuffer;
	
	public FdfsPacket() {
	}

	public long getBodyLength() {
		return bodyLength;
	}

	public FdfsPacket setBodyLength(long bodyLength) {
		this.bodyLength = bodyLength;
		return this;
	}

	public Buffer getBodyBuffer() {
		return bodyBuffer;
	}

	public FdfsPacket setBodyBuffer(Buffer bodyBuffer) {
		this.bodyBuffer = bodyBuffer;
		return this;
	}
}
