package io.vertx.fastdfs;

import java.time.Instant;

/**
 * FastDFS storage information.
 * 
 * @author GengTeng
 * <p>
 * me@gteng.org
 * 
 * @version 3.5.0
 */
public final class FdfsStorageInfo {
	
	public static final int BYTES = 612;

	private byte status;
	private String id;
	private String ip;
	private String domainName;
	private String sourceIp;
	private String version;
	private Instant joinTime;
	private Instant upTime;
	private long totalMB;
	private long freeMB;
	private long uploadPriority;
	private long storePathCount;
	private long subdirCountPerPath;
	private long currentWritePath;
	private long storagePort;
	private long storageHttpPort;
	private int connectionAllocCount;
	private int connectionCurrentCount;
	private int connectionMaxCount;
	private long totalUploadCount;
	private long successUploadCount;
	private long totalAppendCount;
	private long successAppendCount;
	private long totalModifyCount;
	private long successModifyCount;
	private long totalTruncateCount;
	private long successTruncateCount;
	private long totalSetMetaCount;
	private long successSetMetaCount;
	private long totalDeleteCount;
	private long successDeleteCount;
	private long totalDownloadCount;
	private long successDownloadCount;
	private long totalGetMetaCount;
	private long successGetMetaCount;
	private long totalCreateLinkCount;
	private long successCreateLinkCount;
	private long totalDeleteLinkCount;
	private long successDeleteLinkCount;
	private long totalUploadBytes;
	private long successUploadBytes;
	private long totalAppendBytes;
	private long successAppendBytes;
	private long totalModifyBytes;
	private long successModifyBytes;
	private long totalDownloadloadBytes;
	private long successDownloadloadBytes;
	private long totalSyncInBytes;
	private long successSyncInBytes;
	private long totalSyncOutBytes;
	private long successSyncOutBytes;
	private long totalFileOpenCount;
	private long successFileOpenCount;
	private long totalFileReadCount;
	private long successFileReadCount;
	private long totalFileWriteCount;
	private long successFileWriteCount;
	private Instant lastSourceUpdate;
	private Instant lastSyncUpdate;
	private Instant lastSyncedTimestamp;
	private Instant lastHeartBeatTime;
	private boolean isTrunkServer;

	public byte getStatus() {
		return status;
	}

	public FdfsStorageInfo setStatus(byte status) {
		this.status = status;
		return this;
	}

	public String getId() {
		return id;
	}

	public FdfsStorageInfo setId(String id) {
		this.id = id;
		return this;
	}

	public String getIp() {
		return ip;
	}

	public FdfsStorageInfo setIp(String ip) {
		this.ip = ip;
		return this;
	}

	public String getDomainName() {
		return domainName;
	}

	public FdfsStorageInfo setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public String getSourceIp() {
		return sourceIp;
	}

	public FdfsStorageInfo setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public FdfsStorageInfo setVersion(String version) {
		this.version = version;
		return this;
	}

	public Instant getJoinTime() {
		return joinTime;
	}

	public FdfsStorageInfo setJoinTime(Instant joinTime) {
		this.joinTime = joinTime;
		return this;
	}

	public Instant getUpTime() {
		return upTime;
	}

	public FdfsStorageInfo setUpTime(Instant upTime) {
		this.upTime = upTime;
		return this;
	}

	public long getTotalMB() {
		return totalMB;
	}

	public FdfsStorageInfo setTotalMB(long totalMB) {
		this.totalMB = totalMB;
		return this;
	}

	public long getFreeMB() {
		return freeMB;
	}

	public FdfsStorageInfo setFreeMB(long freeMB) {
		this.freeMB = freeMB;
		return this;
	}

	public long getUploadPriority() {
		return uploadPriority;
	}

	public FdfsStorageInfo setUploadPriority(long uploadPriority) {
		this.uploadPriority = uploadPriority;
		return this;
	}

	public long getStorePathCount() {
		return storePathCount;
	}

	public FdfsStorageInfo setStorePathCount(long storePathCount) {
		this.storePathCount = storePathCount;
		return this;
	}

	public long getSubdirCountPerPath() {
		return subdirCountPerPath;
	}

	public FdfsStorageInfo setSubdirCountPerPath(long subdirCountPerPath) {
		this.subdirCountPerPath = subdirCountPerPath;
		return this;
	}

	public long getCurrentWritePath() {
		return currentWritePath;
	}

	public FdfsStorageInfo setCurrentWritePath(long currentWritePath) {
		this.currentWritePath = currentWritePath;
		return this;
	}

	public long getStoragePort() {
		return storagePort;
	}

	public FdfsStorageInfo setStoragePort(long storagePort) {
		this.storagePort = storagePort;
		return this;
	}

	public long getStorageHttpPort() {
		return storageHttpPort;
	}

	public FdfsStorageInfo setStorageHttpPort(long storageHttpPort) {
		this.storageHttpPort = storageHttpPort;
		return this;
	}

	public int getConnectionAllocCount() {
		return connectionAllocCount;
	}

	public void setConnectionAllocCount(int connectionAllocCount) {
		this.connectionAllocCount = connectionAllocCount;
	}

	public int getConnectionCurrentCount() {
		return connectionCurrentCount;
	}

	public void setConnectionCurrentCount(int connectionCurrentCount) {
		this.connectionCurrentCount = connectionCurrentCount;
	}

	public int getConnectionMaxCount() {
		return connectionMaxCount;
	}

	public void setConnectionMaxCount(int connectionMaxCount) {
		this.connectionMaxCount = connectionMaxCount;
	}

	public long getTotalUploadCount() {
		return totalUploadCount;
	}

	public FdfsStorageInfo setTotalUploadCount(long totalUploadCount) {
		this.totalUploadCount = totalUploadCount;
		return this;
	}

	public long getSuccessUploadCount() {
		return successUploadCount;
	}

	public FdfsStorageInfo setSuccessUploadCount(long successUploadCount) {
		this.successUploadCount = successUploadCount;
		return this;
	}

	public long getTotalAppendCount() {
		return totalAppendCount;
	}

	public FdfsStorageInfo setTotalAppendCount(long totalAppendCount) {
		this.totalAppendCount = totalAppendCount;
		return this;
	}

	public long getSuccessAppendCount() {
		return successAppendCount;
	}

	public FdfsStorageInfo setSuccessAppendCount(long successAppendCount) {
		this.successAppendCount = successAppendCount;
		return this;
	}

	public long getTotalModifyCount() {
		return totalModifyCount;
	}

	public FdfsStorageInfo setTotalModifyCount(long totalModifyCount) {
		this.totalModifyCount = totalModifyCount;
		return this;
	}

	public long getSuccessModifyCount() {
		return successModifyCount;
	}

	public FdfsStorageInfo setSuccessModifyCount(long successModifyCount) {
		this.successModifyCount = successModifyCount;
		return this;
	}

	public long getTotalTruncateCount() {
		return totalTruncateCount;
	}

	public FdfsStorageInfo setTotalTruncateCount(long totalTruncateCount) {
		this.totalTruncateCount = totalTruncateCount;
		return this;
	}

	public long getSuccessTruncateCount() {
		return successTruncateCount;
	}

	public FdfsStorageInfo setSuccessTruncateCount(long successTruncateCount) {
		this.successTruncateCount = successTruncateCount;
		return this;
	}

	public long getTotalSetMetaCount() {
		return totalSetMetaCount;
	}

	public FdfsStorageInfo setTotalSetMetaCount(long totalSetMetaCount) {
		this.totalSetMetaCount = totalSetMetaCount;
		return this;
	}

	public long getSuccessSetMetaCount() {
		return successSetMetaCount;
	}

	public FdfsStorageInfo setSuccessSetMetaCount(long successSetMetaCount) {
		this.successSetMetaCount = successSetMetaCount;
		return this;
	}

	public long getTotalDeleteCount() {
		return totalDeleteCount;
	}

	public FdfsStorageInfo setTotalDeleteCount(long totalDeleteCount) {
		this.totalDeleteCount = totalDeleteCount;
		return this;
	}

	public long getSuccessDeleteCount() {
		return successDeleteCount;
	}

	public FdfsStorageInfo setSuccessDeleteCount(long successDeleteCount) {
		this.successDeleteCount = successDeleteCount;
		return this;
	}

	public long getTotalDownloadCount() {
		return totalDownloadCount;
	}

	public FdfsStorageInfo setTotalDownloadCount(long totalDownloadCount) {
		this.totalDownloadCount = totalDownloadCount;
		return this;
	}

	public long getSuccessDownloadCount() {
		return successDownloadCount;
	}

	public FdfsStorageInfo setSuccessDownloadCount(long successDownloadCount) {
		this.successDownloadCount = successDownloadCount;
		return this;
	}

	public long getTotalGetMetaCount() {
		return totalGetMetaCount;
	}

	public FdfsStorageInfo setTotalGetMetaCount(long totalGetMetaCount) {
		this.totalGetMetaCount = totalGetMetaCount;
		return this;
	}

	public long getSuccessGetMetaCount() {
		return successGetMetaCount;
	}

	public FdfsStorageInfo setSuccessGetMetaCount(long successGetMetaCount) {
		this.successGetMetaCount = successGetMetaCount;
		return this;
	}

	public long getTotalCreateLinkCount() {
		return totalCreateLinkCount;
	}

	public FdfsStorageInfo setTotalCreateLinkCount(long totalCreateLinkCount) {
		this.totalCreateLinkCount = totalCreateLinkCount;
		return this;
	}

	public long getSuccessCreateLinkCount() {
		return successCreateLinkCount;
	}

	public FdfsStorageInfo setSuccessCreateLinkCount(long successCreateLinkCount) {
		this.successCreateLinkCount = successCreateLinkCount;
		return this;
	}

	public long getTotalDeleteLinkCount() {
		return totalDeleteLinkCount;
	}

	public FdfsStorageInfo setTotalDeleteLinkCount(long totalDeleteLinkCount) {
		this.totalDeleteLinkCount = totalDeleteLinkCount;
		return this;
	}

	public long getSuccessDeleteLinkCount() {
		return successDeleteLinkCount;
	}

	public FdfsStorageInfo setSuccessDeleteLinkCount(long successDeleteLinkCount) {
		this.successDeleteLinkCount = successDeleteLinkCount;
		return this;
	}

	public long getTotalUploadBytes() {
		return totalUploadBytes;
	}

	public FdfsStorageInfo setTotalUploadBytes(long totalUploadBytes) {
		this.totalUploadBytes = totalUploadBytes;
		return this;
	}

	public long getSuccessUploadBytes() {
		return successUploadBytes;
	}

	public FdfsStorageInfo setSuccessUploadBytes(long successUploadBytes) {
		this.successUploadBytes = successUploadBytes;
		return this;
	}

	public long getTotalAppendBytes() {
		return totalAppendBytes;
	}

	public FdfsStorageInfo setTotalAppendBytes(long totalAppendBytes) {
		this.totalAppendBytes = totalAppendBytes;
		return this;
	}

	public long getSuccessAppendBytes() {
		return successAppendBytes;
	}

	public FdfsStorageInfo setSuccessAppendBytes(long successAppendBytes) {
		this.successAppendBytes = successAppendBytes;
		return this;
	}

	public long getTotalModifyBytes() {
		return totalModifyBytes;
	}

	public FdfsStorageInfo setTotalModifyBytes(long totalModifyBytes) {
		this.totalModifyBytes = totalModifyBytes;
		return this;
	}

	public long getSuccessModifyBytes() {
		return successModifyBytes;
	}

	public FdfsStorageInfo setSuccessModifyBytes(long successModifyBytes) {
		this.successModifyBytes = successModifyBytes;
		return this;
	}

	public long getTotalDownloadloadBytes() {
		return totalDownloadloadBytes;
	}

	public FdfsStorageInfo setTotalDownloadloadBytes(long totalDownloadloadBytes) {
		this.totalDownloadloadBytes = totalDownloadloadBytes;
		return this;
	}

	public long getSuccessDownloadloadBytes() {
		return successDownloadloadBytes;
	}

	public FdfsStorageInfo setSuccessDownloadloadBytes(long successDownloadloadBytes) {
		this.successDownloadloadBytes = successDownloadloadBytes;
		return this;
	}

	public long getTotalSyncInBytes() {
		return totalSyncInBytes;
	}

	public FdfsStorageInfo setTotalSyncInBytes(long totalSyncInBytes) {
		this.totalSyncInBytes = totalSyncInBytes;
		return this;
	}

	public long getSuccessSyncInBytes() {
		return successSyncInBytes;
	}

	public FdfsStorageInfo setSuccessSyncInBytes(long successSyncInBytes) {
		this.successSyncInBytes = successSyncInBytes;
		return this;
	}

	public long getTotalSyncOutBytes() {
		return totalSyncOutBytes;
	}

	public FdfsStorageInfo setTotalSyncOutBytes(long totalSyncOutBytes) {
		this.totalSyncOutBytes = totalSyncOutBytes;
		return this;
	}

	public long getSuccessSyncOutBytes() {
		return successSyncOutBytes;
	}

	public FdfsStorageInfo setSuccessSyncOutBytes(long successSyncOutBytes) {
		this.successSyncOutBytes = successSyncOutBytes;
		return this;
	}

	public long getTotalFileOpenCount() {
		return totalFileOpenCount;
	}

	public FdfsStorageInfo setTotalFileOpenCount(long totalFileOpenCount) {
		this.totalFileOpenCount = totalFileOpenCount;
		return this;
	}

	public long getSuccessFileOpenCount() {
		return successFileOpenCount;
	}

	public FdfsStorageInfo setSuccessFileOpenCount(long successFileOpenCount) {
		this.successFileOpenCount = successFileOpenCount;
		return this;
	}

	public long getTotalFileReadCount() {
		return totalFileReadCount;
	}

	public FdfsStorageInfo setTotalFileReadCount(long totalFileReadCount) {
		this.totalFileReadCount = totalFileReadCount;
		return this;
	}

	public long getSuccessFileReadCount() {
		return successFileReadCount;
	}

	public FdfsStorageInfo setSuccessFileReadCount(long successFileReadCount) {
		this.successFileReadCount = successFileReadCount;
		return this;
	}

	public long getTotalFileWriteCount() {
		return totalFileWriteCount;
	}

	public FdfsStorageInfo setTotalFileWriteCount(long totalFileWriteCount) {
		this.totalFileWriteCount = totalFileWriteCount;
		return this;
	}

	public long getSuccessFileWriteCount() {
		return successFileWriteCount;
	}

	public FdfsStorageInfo setSuccessFileWriteCount(long successFileWriteCount) {
		this.successFileWriteCount = successFileWriteCount;
		return this;
	}

	public Instant getLastSourceUpdate() {
		return lastSourceUpdate;
	}

	public FdfsStorageInfo setLastSourceUpdate(Instant lastSourceUpdate) {
		this.lastSourceUpdate = lastSourceUpdate;
		return this;
	}

	public Instant getLastSyncUpdate() {
		return lastSyncUpdate;
	}

	public FdfsStorageInfo setLastSyncUpdate(Instant lastSyncUpdate) {
		this.lastSyncUpdate = lastSyncUpdate;
		return this;
	}

	public Instant getLastSyncedTimestamp() {
		return lastSyncedTimestamp;
	}

	public FdfsStorageInfo setLastSyncedTimestamp(Instant lastSyncedTimestamp) {
		this.lastSyncedTimestamp = lastSyncedTimestamp;
		return this;
	}

	public Instant getLastHeartBeatTime() {
		return lastHeartBeatTime;
	}

	public FdfsStorageInfo setLastHeartBeatTime(Instant lastHeartBeatTime) {
		this.lastHeartBeatTime = lastHeartBeatTime;
		return this;
	}

	public boolean isTrunkServer() {
		return isTrunkServer;
	}

	public FdfsStorageInfo setTrunkServer(boolean isTrunkServer) {
		this.isTrunkServer = isTrunkServer;
		return this;
	}
}
