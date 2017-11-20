package io.vertx.fastdfs.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import io.vertx.fastdfs.FdfsFileId;
import io.vertx.fastdfs.FdfsGroupInfo;
import io.vertx.fastdfs.FdfsStorageInfo;
import io.vertx.fastdfs.api.FdfsStorage;
import io.vertx.fastdfs.api.FdfsTracker;
import io.vertx.fastdfs.exp.FdfsException;
import io.vertx.fastdfs.options.FdfsStorageOptions;
import io.vertx.fastdfs.options.FdfsTrackerOptions;
import io.vertx.fastdfs.utils.FdfsPacket;
import io.vertx.fastdfs.utils.FdfsProtocol;
import io.vertx.fastdfs.utils.FdfsUtils;

/**
 * 
 * @author GengTeng
 * <p>
 * me@gteng.org
 * 
 * @version 3.5.0
 */
public class FdfsTrackerImpl implements FdfsTracker {

	private Vertx vertx;
	private NetSocket socket;
	private FdfsTrackerOptions options;

	public FdfsTrackerImpl(Vertx vertx, NetSocket socket, FdfsTrackerOptions options) {
		this.vertx = vertx;
		this.socket = socket;
		this.options = options;
	}

	@Override
	public FdfsTracker getStoreStorage(Handler<AsyncResult<FdfsStorage>> handler) {
		return getStoreStorage(null, handler);
	}

	@Override
	public FdfsTracker getStoreStorage(String group, Handler<AsyncResult<FdfsStorage>> handler) {

		boolean hasGroup = true;

		if (group == null) {
			hasGroup = false;
		}

		Buffer groupBuffer = hasGroup ? Buffer.buffer(group, options.getCharset()) : null;

		if (hasGroup && groupBuffer.length() > FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN) {
			handler.handle(Future.failedFuture(new FdfsException("group name [" + group + "] is too long")));
			return this;
		}

		byte command = hasGroup ? FdfsProtocol.TRACKER_PROTO_CMD_SERVICE_QUERY_STORE_WITH_GROUP_ONE
				: FdfsProtocol.TRACKER_PROTO_CMD_SERVICE_QUERY_STORE_WITHOUT_GROUP_ONE;
		long bodyLength = hasGroup ? FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN : 0;

		Buffer headerBuffer = FdfsProtocol.packHeader(command, (byte) 0, bodyLength);

		FdfsProtocol.recvPacket(socket, FdfsProtocol.TRACKER_PROTO_CMD_RESP,
				FdfsProtocol.TRACKER_QUERY_STORAGE_STORE_BODY_LEN, null).setHandler(ar -> {
					if (ar.succeeded()) {
						FdfsPacket resPacket = ar.result();
						parseStorage(resPacket.getBodyBuffer(), options.getCharset(), true).setHandler(handler);
					} else {
						handler.handle(Future.failedFuture(ar.cause()));
					}
				});

		socket.write(headerBuffer);

		if (hasGroup) {
			Buffer buffer = FdfsUtils.newZero(bodyLength);
			buffer.setBuffer(0, groupBuffer);
			socket.write(buffer);
		}

		if (socket.writeQueueFull()) {
			socket.pause();
			socket.drainHandler(v -> {
				socket.resume();
			});
		}

		return this;
	}

	@Override
	public FdfsTracker getFetchStorage(FdfsFileId fileId, Handler<AsyncResult<FdfsStorage>> handler) {
		getFetchOrUpdateStorage(FdfsProtocol.TRACKER_PROTO_CMD_SERVICE_QUERY_FETCH_ONE, fileId).setHandler(handler);
		return this;
	}

	@Override
	public FdfsTracker getUpdateStorage(FdfsFileId fileId, Handler<AsyncResult<FdfsStorage>> handler) {
		getFetchOrUpdateStorage(FdfsProtocol.TRACKER_PROTO_CMD_SERVICE_QUERY_UPDATE, fileId).setHandler(handler);
		return this;
	}

	@Override
	public FdfsTracker groups(Handler<AsyncResult<List<FdfsGroupInfo>>> handler) {

		Buffer headerBuffer = FdfsProtocol.packHeader(FdfsProtocol.TRACKER_PROTO_CMD_SERVER_LIST_GROUP, (byte) 0, 0);

		FdfsProtocol.recvPacket(socket, FdfsProtocol.TRACKER_PROTO_CMD_RESP, 0, null).setHandler(ar -> {
			if (ar.succeeded()) {
				FdfsPacket packet = ar.result();
				Buffer bodyBuffer = packet.getBodyBuffer();
				if (bodyBuffer.length() % FdfsGroupInfo.BYTES != 0) {
					handler.handle(Future.failedFuture(
							new FdfsException("byte array length: " + bodyBuffer.length() + " is invalid")));
					return;
				}

				List<FdfsGroupInfo> list = new ArrayList<>();

				int count = bodyBuffer.length() / FdfsGroupInfo.BYTES;
				String charset = options.getCharset();
				for (int i = 0; i < count; ++i) {
					int offset = FdfsGroupInfo.BYTES * i;
					FdfsGroupInfo groupInfo = new FdfsGroupInfo();

					groupInfo.setName(FdfsUtils.fdfsTrim(
							bodyBuffer.getString(offset, offset + FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN, charset)));
					groupInfo.setTotalMB(bodyBuffer.getLong(FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN + 1));
					groupInfo.setFreeMB(bodyBuffer.getLong(
							FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN + 1 + FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE * 1));
					groupInfo.setTrunkFreeMB(bodyBuffer.getLong(
							FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN + 1 + FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE * 2));
					groupInfo.setStorageCount(bodyBuffer.getLong(
							FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN + 1 + FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE * 3));
					groupInfo.setStoragePort(bodyBuffer.getLong(
							FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN + 1 + FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE * 4));
					groupInfo.setStorageHttpPort(bodyBuffer.getLong(
							FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN + 1 + FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE * 5));
					groupInfo.setActiveCount(bodyBuffer.getLong(
							FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN + 1 + FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE * 6));
					groupInfo.setCurrentWriteServer(bodyBuffer.getLong(
							FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN + 1 + FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE * 7));
					groupInfo.setStorePathCount(bodyBuffer.getLong(
							FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN + 1 + FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE * 8));
					groupInfo.setSubdirCountPerPath(bodyBuffer.getLong(
							FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN + 1 + FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE * 9));
					groupInfo.setCurrentTrunkFileId(bodyBuffer.getLong(
							FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN + 1 + FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE * 10));

					list.add(groupInfo);
				}

				handler.handle(Future.succeededFuture(list));
			} else {
				handler.handle(Future.failedFuture(ar.cause()));
			}
		});

		socket.write(headerBuffer);
		if (socket.writeQueueFull()) {
			socket.pause();
			socket.drainHandler(v -> {
				socket.resume();
			});
		}

		return this;
	}

	@Override
	public FdfsTracker storages(String group, Handler<AsyncResult<List<FdfsStorageInfo>>> handler) {
		final long bodyLength = FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN;
		Buffer headerBuffer = FdfsProtocol.packHeader(FdfsProtocol.TRACKER_PROTO_CMD_SERVER_LIST_STORAGE, (byte) 0,
				bodyLength);
		Buffer bodyBuffer = FdfsUtils.newZero(bodyLength);
		bodyBuffer.setBuffer(0, Buffer.buffer(group, options.getCharset()));

		FdfsProtocol.recvPacket(socket, FdfsProtocol.TRACKER_PROTO_CMD_RESP, 0, null).setHandler(ar -> {
			if (ar.succeeded()) {
				FdfsPacket res = ar.result();
				Buffer resBodyBuffer = res.getBodyBuffer();
				if (resBodyBuffer.length() % FdfsStorageInfo.BYTES != 0) {
					handler.handle(Future.failedFuture(
							new FdfsException("byte array length: " + resBodyBuffer.length() + " is invalid")));
					return;
				}

				List<FdfsStorageInfo> list = new ArrayList<>();

				int count = resBodyBuffer.length() / FdfsStorageInfo.BYTES;
				String charset = options.getCharset();
				for (int i = 0; i < count; ++i) {
					int offset = FdfsStorageInfo.BYTES * i;
					FdfsStorageInfo storageInfo = new FdfsStorageInfo();

					storageInfo.setStatus(resBodyBuffer.getByte(offset));
					offset += 1;
					storageInfo.setIp(FdfsUtils.fdfsTrim(
							resBodyBuffer.getString(offset, offset + FdfsProtocol.FDFS_IPADDR_SIZE, charset)));
					offset += FdfsProtocol.FDFS_IPADDR_SIZE;
					storageInfo.setDomainName(FdfsUtils.fdfsTrim(
							resBodyBuffer.getString(offset, offset + FdfsProtocol.FDFS_DOMAIN_NAME_MAX_SIZE, charset)));
					offset += FdfsProtocol.FDFS_DOMAIN_NAME_MAX_SIZE;
					storageInfo.setSourceIp(FdfsUtils.fdfsTrim(
							resBodyBuffer.getString(offset, offset + FdfsProtocol.FDFS_IPADDR_SIZE, charset)));
					offset += FdfsProtocol.FDFS_IPADDR_SIZE;
					storageInfo.setVersion(FdfsUtils.fdfsTrim(
							resBodyBuffer.getString(offset, offset + FdfsProtocol.FDFS_VERSION_SIZE, charset)));
					offset += FdfsProtocol.FDFS_VERSION_SIZE;
					storageInfo.setJoinTime(Instant.ofEpochSecond(resBodyBuffer.getLong(offset)));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setUpTime(Instant.ofEpochSecond(resBodyBuffer.getLong(offset)));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTotalMB(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setFreeMB(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setUploadPriority(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setStorePathCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSubdirCountPerPath(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setCurrentWritePath(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setStoragePort(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setStorageHttpPort(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setConnectionAllocCount(resBodyBuffer.getInt(offset));
					offset += Integer.BYTES;
					storageInfo.setConnectionCurrentCount(resBodyBuffer.getInt(offset));
					offset += Integer.BYTES;
					storageInfo.setConnectionMaxCount(resBodyBuffer.getInt(offset));
					offset += Integer.BYTES;
					storageInfo.setTotalUploadCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSuccessUploadCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTotalAppendCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSuccessAppendCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTotalModifyCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSuccessModifyCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTotalTruncateCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSuccessTruncateCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTotalSetMetaCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSuccessSetMetaCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTotalDeleteCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSuccessDeleteCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTotalDownloadCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSuccessDownloadCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTotalGetMetaCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSuccessGetMetaCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTotalCreateLinkCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSuccessCreateLinkCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTotalDeleteLinkCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSuccessDeleteLinkCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTotalUploadBytes(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSuccessUploadBytes(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTotalAppendBytes(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSuccessAppendBytes(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTotalModifyBytes(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSuccessModifyBytes(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTotalDownloadloadBytes(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSuccessDownloadloadBytes(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTotalSyncInBytes(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSuccessSyncInBytes(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTotalSyncOutBytes(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSuccessSyncOutBytes(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTotalFileOpenCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSuccessFileOpenCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTotalFileReadCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSuccessFileReadCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTotalFileWriteCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setSuccessFileWriteCount(resBodyBuffer.getLong(offset));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setLastSourceUpdate(Instant.ofEpochSecond(resBodyBuffer.getLong(offset)));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setLastSyncUpdate(Instant.ofEpochSecond(resBodyBuffer.getLong(offset)));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setLastSyncedTimestamp(Instant.ofEpochSecond(resBodyBuffer.getLong(offset)));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setLastHeartBeatTime(Instant.ofEpochSecond(resBodyBuffer.getLong(offset)));
					offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
					storageInfo.setTrunkServer(resBodyBuffer.getByte(offset) != (byte) 0);

					list.add(storageInfo);
				}

				handler.handle(Future.succeededFuture(list));
			} else {
				handler.handle(Future.failedFuture(ar.cause()));
			}
		});

		socket.write(headerBuffer);
		socket.write(bodyBuffer);

		if (socket.writeQueueFull()) {
			socket.pause();
			socket.drainHandler(v -> {
				socket.resume();
			});
		}

		return this;
	}

	private Future<FdfsStorage> parseStorage(Buffer bodyBuffer, String charset, boolean hasPathIndex) {
		FdfsStorageOptions storageOptions = new FdfsStorageOptions().copyBasic(options);

		try {
			String group = FdfsUtils
					.fdfsTrim(bodyBuffer.getString(0, FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN, options.getCharset()));
			String ip = FdfsUtils.fdfsTrim(bodyBuffer.getString(FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN,
					FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN + FdfsProtocol.FDFS_IPADDR_SIZE - 1, options.getCharset()));
			long port = bodyBuffer.getLong(FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN + FdfsProtocol.FDFS_IPADDR_SIZE - 1);

			storageOptions.setGroup(group).setAddress(SocketAddress.inetSocketAddress((int) port, ip));

			if (hasPathIndex && bodyBuffer.length() > FdfsProtocol.TRACKER_QUERY_STORAGE_FETCH_BODY_LEN) {
				byte storePathIndex = bodyBuffer.getByte(FdfsProtocol.TRACKER_QUERY_STORAGE_FETCH_BODY_LEN);
				storageOptions.setStorePathIndex(storePathIndex);
			}

			return Future.succeededFuture(FdfsStorage.create(vertx, storageOptions));
		} catch (Exception e) {
			return Future.failedFuture(e);
		}
	}

	private Future<FdfsStorage> getFetchOrUpdateStorage(byte command, FdfsFileId fileId) {

		Buffer packet = FdfsProtocol.packFileId(command, fileId, options.getCharset());

		Future<FdfsStorage> futureFdfsStorage = Future.future();

		FdfsProtocol
				.recvPacket(socket, FdfsProtocol.TRACKER_PROTO_CMD_RESP, FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN
						+ FdfsProtocol.FDFS_IPADDR_SIZE - 1 + FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE, null)
				.setHandler(ar -> {
					if (ar.succeeded()) {
						FdfsPacket resPacket = ar.result();
						parseStorage(resPacket.getBodyBuffer(), options.getCharset(), true)
								.setHandler(futureFdfsStorage);
					} else {
						futureFdfsStorage.fail(ar.cause());
					}
				});

		socket.write(packet);
		if (socket.writeQueueFull()) {
			socket.pause();
			socket.drainHandler(v -> {
				socket.resume();
			});
		}

		return futureFdfsStorage;
	}
	
	@Override
	public void close() {
		if (socket != null) {
			FdfsProtocol.closeSocket(socket);
		}
	}
}
