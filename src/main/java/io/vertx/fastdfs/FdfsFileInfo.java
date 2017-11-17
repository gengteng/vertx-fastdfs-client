package io.vertx.fastdfs;

import java.time.Instant;

/**
 * FastDFS file information.
 * 
 * @author GengTeng
 * <p>
 * me@gteng.org
 * 
 * @version 3.5.0
 */
public final class FdfsFileInfo {
	private long size;
	private Instant timestamp;
	private long crc32;
	private String sourceIp;

	public long getSize() {
		return size;
	}

	public FdfsFileInfo setSize(long size) {
		this.size = size;
		return this;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public FdfsFileInfo setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	public long getCrc32() {
		return crc32;
	}

	public FdfsFileInfo setCrc32(long crc32) {
		this.crc32 = crc32;
		return this;
	}

	public String getSourceIp() {
		return sourceIp;
	}

	public FdfsFileInfo setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
		return this;
	}
}
