package io.vertx.fastdfs;

/**
 * FastDFS group information.
 * 
 * @author GengTeng
 * <p>
 * me@gteng.org
 * 
 * @version 3.5.0
 */
public final class FdfsGroupInfo {
	
	public static final int BYTES = 105;
	
	private String name;
	private long totalMB;
	private long freeMB;
	private long trunkFreeMB;
	private long storageCount;
	private long storagePort;
	private long storageHttpPort;
	private long activeCount;
	private long currentWriteServer;
	private long storePathCount;
	private long subdirCountPerPath;
	private long currentTrunkFileId;

	public String getName() {
		return name;
	}

	public FdfsGroupInfo setName(String name) {
		this.name = name;
		return this;
	}

	public long getTotalMB() {
		return totalMB;
	}

	public FdfsGroupInfo setTotalMB(long totalMB) {
		this.totalMB = totalMB;
		return this;
	}

	public long getFreeMB() {
		return freeMB;
	}

	public FdfsGroupInfo setFreeMB(long freeMB) {
		this.freeMB = freeMB;
		return this;
	}

	public long getTrunkFreeMB() {
		return trunkFreeMB;
	}

	public FdfsGroupInfo setTrunkFreeMB(long trunkFreeMB) {
		this.trunkFreeMB = trunkFreeMB;
		return this;
	}

	public long getStorageCount() {
		return storageCount;
	}

	public FdfsGroupInfo setStorageCount(long storageCount) {
		this.storageCount = storageCount;
		return this;
	}

	public long getStoragePort() {
		return storagePort;
	}

	public FdfsGroupInfo setStoragePort(long storagePort) {
		this.storagePort = storagePort;
		return this;
	}

	public long getStorageHttpPort() {
		return storageHttpPort;
	}

	public FdfsGroupInfo setStorageHttpPort(long storageHttpPort) {
		this.storageHttpPort = storageHttpPort;
		return this;
	}

	public long getActiveCount() {
		return activeCount;
	}

	public FdfsGroupInfo setActiveCount(long activeCount) {
		this.activeCount = activeCount;
		return this;
	}

	public long getCurrentWriteServer() {
		return currentWriteServer;
	}

	public FdfsGroupInfo setCurrentWriteServer(long currentWriteServer) {
		this.currentWriteServer = currentWriteServer;
		return this;
	}

	public long getStorePathCount() {
		return storePathCount;
	}

	public FdfsGroupInfo setStorePathCount(long storePathCount) {
		this.storePathCount = storePathCount;
		return this;
	}

	public long getSubdirCountPerPath() {
		return subdirCountPerPath;
	}

	public FdfsGroupInfo setSubdirCountPerPath(long subdirCountPerPath) {
		this.subdirCountPerPath = subdirCountPerPath;
		return this;
	}

	public long getCurrentTrunkFileId() {
		return currentTrunkFileId;
	}

	public FdfsGroupInfo setCurrentTrunkFileId(long currentTrunkFileId) {
		this.currentTrunkFileId = currentTrunkFileId;
		return this;
	}
}
