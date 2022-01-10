package io.vertx.fastdfs.impl;

import java.time.Instant;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.FileProps;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.Pump;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import io.vertx.fastdfs.FdfsFileId;
import io.vertx.fastdfs.FdfsFileInfo;
import io.vertx.fastdfs.exp.FdfsException;
import io.vertx.fastdfs.options.FdfsStorageOptions;
import io.vertx.fastdfs.utils.FdfsPacket;
import io.vertx.fastdfs.utils.FdfsProtocol;
import io.vertx.fastdfs.utils.FdfsUtils;

/**
 * 
 * @author GengTeng
 *         <p>
 *         me@gteng.org
 * 
 * @version 4.2
 */
public class FdfsStorageImpl implements FdfsStorage {

	private Vertx vertx;
	private FdfsConnectionPool pool;
	private FdfsStorageOptions options;

	public FdfsStorageImpl(Vertx vertx, FdfsConnectionPool pool, FdfsStorageOptions options) {
		this.vertx = vertx;
		this.pool = pool;
		this.options = options;
	}

	@Override
	public FdfsStorage upload(ReadStream<Buffer> stream, long size, String ext,
			Handler<AsyncResult<FdfsFileId>> handler) {
		uploadFile(FdfsProtocol.STORAGE_PROTO_CMD_UPLOAD_FILE, stream, size, ext).onComplete(handler);
		return this;
	}

	@Override
	public FdfsStorage upload(String fileFullPathName, String ext, Handler<AsyncResult<FdfsFileId>> handler) {
		uploadFile(FdfsProtocol.STORAGE_PROTO_CMD_UPLOAD_FILE, fileFullPathName, ext).onComplete(handler);
		return this;
	}

	@Override
	public FdfsStorage upload(Buffer buffer, String ext, Handler<AsyncResult<FdfsFileId>> handler) {
		uploadFile(FdfsProtocol.STORAGE_PROTO_CMD_UPLOAD_FILE, buffer, ext).onComplete(handler);
		return this;
	}

	@Override
	public FdfsStorage uploadAppender(ReadStream<Buffer> stream, long size, String ext,
			Handler<AsyncResult<FdfsFileId>> handler) {
		uploadFile(FdfsProtocol.STORAGE_PROTO_CMD_UPLOAD_APPENDER_FILE, stream, size, ext).onComplete(handler);
		return this;
	}

	@Override
	public FdfsStorage uploadAppender(String fileFullPathName, String ext, Handler<AsyncResult<FdfsFileId>> handler) {
		uploadFile(FdfsProtocol.STORAGE_PROTO_CMD_UPLOAD_APPENDER_FILE, fileFullPathName, ext).onComplete(handler);
		return this;
	}

	@Override
	public FdfsStorage uploadAppender(Buffer buffer, String ext, Handler<AsyncResult<FdfsFileId>> handler) {
		uploadFile(FdfsProtocol.STORAGE_PROTO_CMD_UPLOAD_APPENDER_FILE, buffer, ext).onComplete(handler);
		return this;
	}

	@Override
	public FdfsStorage append(ReadStream<Buffer> stream, long size, FdfsFileId fileId,
			Handler<AsyncResult<Void>> handler) {

		stream.pause();
		
		Future<FdfsConnection> futureConn = getConnection();

		futureConn.compose(connection -> {
			Future<FdfsPacket> futureResponse = FdfsProtocol.recvPacket(vertx, options.getNetworkTimeout(), connection, FdfsProtocol.STORAGE_PROTO_CMD_RESP, 0,
					null);

			Buffer nameBuffer = Buffer.buffer(fileId.name(), options.getCharset());
			long bodyLength = 2 * FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE + nameBuffer.length() + size;
			Buffer headerBuffer = FdfsProtocol.packHeader(FdfsProtocol.STORAGE_PROTO_CMD_APPEND_FILE, (byte) 0,
					bodyLength);

			connection.write(headerBuffer);
			if (connection.writeQueueFull()) {
				connection.pause();
				connection.drainHandler(v -> {
					connection.resume();
				});
			}

			Buffer bodyBuffer = FdfsUtils.newZero(bodyLength - size);

			int offset = 0;
			bodyBuffer.setLong(offset, nameBuffer.length());
			offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
			bodyBuffer.setLong(offset, size);
			offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
			bodyBuffer.setBuffer(offset, nameBuffer);

			connection.write(bodyBuffer);
			if (connection.writeQueueFull()) {
				connection.pause();
				connection.drainHandler(v -> {
					connection.resume();
				});
			}

			Pump.pump(stream, connection).start();
			stream.resume();

			return futureResponse;
		}).onComplete(ar -> {
			if (futureConn.succeeded()) {
				futureConn.result().release();
			}
			
			if (ar.succeeded()) {
				handler.handle(Future.succeededFuture());
			} else {
				handler.handle(Future.failedFuture(ar.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsStorage append(String fileFullPathName, FdfsFileId fileId, Handler<AsyncResult<Void>> handler) {

		LocalFile.readFile(vertx.fileSystem(), fileFullPathName).onComplete(ar -> {
			if (ar.succeeded()) {
				LocalFile localFile = ar.result();

				append(localFile.getFile(), localFile.getSize(), fileId, append -> {
					localFile.closeFile();
					handler.handle(append);
				});
			} else {
				handler.handle(Future.failedFuture(ar.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsStorage append(Buffer buffer, FdfsFileId fileId, Handler<AsyncResult<Void>> handler) {
		
		Future<FdfsConnection> futureConn = getConnection();
		
		futureConn.compose(connection -> {
			Future<FdfsPacket> futureResponse = FdfsProtocol.recvPacket(vertx, options.getNetworkTimeout(), connection, FdfsProtocol.STORAGE_PROTO_CMD_RESP, 0,
					null);

			Buffer nameBuffer = Buffer.buffer(fileId.name(), options.getCharset());
			long bodyLength = 2 * FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE + nameBuffer.length() + buffer.length();
			Buffer headerBuffer = FdfsProtocol.packHeader(FdfsProtocol.STORAGE_PROTO_CMD_APPEND_FILE, (byte) 0,
					bodyLength);

			connection.write(headerBuffer);
			if (connection.writeQueueFull()) {
				connection.pause();
				connection.drainHandler(v -> {
					connection.resume();
				});
			}

			Buffer bodyBuffer = FdfsUtils.newZero(bodyLength);

			int offset = 0;
			bodyBuffer.setLong(offset, nameBuffer.length());
			offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
			bodyBuffer.setLong(offset, buffer.length());
			offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
			bodyBuffer.setBuffer(offset, nameBuffer);
			offset += nameBuffer.length();
			bodyBuffer.setBuffer(offset, buffer);

			connection.write(bodyBuffer);
			if (connection.writeQueueFull()) {
				connection.pause();
				connection.drainHandler(v -> {
					connection.resume();
				});
			}

			return futureResponse;
		}).onComplete(ar -> {
			
			if (futureConn.succeeded()) {
				futureConn.result().release();
			}

			if (ar.succeeded()) {
				handler.handle(Future.succeededFuture());
			} else {
				handler.handle(Future.failedFuture(ar.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsStorage modify(ReadStream<Buffer> stream, long size, FdfsFileId fileId, long offset,
			Handler<AsyncResult<Void>> handler) {

		stream.pause();
		
		Future<FdfsConnection> futureConn = getConnection();

		futureConn.compose(connection -> {
			Future<FdfsPacket> futureResponse = FdfsProtocol.recvPacket(vertx, options.getNetworkTimeout(), connection, FdfsProtocol.STORAGE_PROTO_CMD_RESP, 0,
					null);

			Buffer nameBuffer = Buffer.buffer(fileId.name(), options.getCharset());
			long bodyLength = 3 * FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE + nameBuffer.length() + size;
			Buffer headerBuffer = FdfsProtocol.packHeader(FdfsProtocol.STORAGE_PROTO_CMD_MODIFY_FILE, (byte) 0,
					bodyLength);

			connection.write(headerBuffer);
			if (connection.writeQueueFull()) {
				connection.pause();
				connection.drainHandler(v -> {
					connection.resume();
				});
			}

			Buffer bodyBuffer = FdfsUtils.newZero(bodyLength - size);

			int bufferOffset = 0;
			bodyBuffer.setLong(bufferOffset, nameBuffer.length());
			bufferOffset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
			bodyBuffer.setLong(bufferOffset, offset);
			bufferOffset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
			bodyBuffer.setLong(bufferOffset, size);
			bufferOffset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
			bodyBuffer.setBuffer(bufferOffset, nameBuffer);

			connection.write(bodyBuffer);
			if (connection.writeQueueFull()) {
				connection.pause();
				connection.drainHandler(v -> {
					connection.resume();
				});
			}

			Pump.pump(stream, connection).start();
			stream.resume();

			return futureResponse;
		}).onComplete(ar -> {
			
			if (futureConn.succeeded()) {
				futureConn.result().release();
			}

			if (ar.succeeded()) {
				handler.handle(Future.succeededFuture());
			} else {
				handler.handle(Future.failedFuture(ar.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsStorage modify(String fileFullPathName, FdfsFileId fileId, long offset,
			Handler<AsyncResult<Void>> handler) {

		LocalFile.readFile(vertx.fileSystem(), fileFullPathName).onComplete(ar -> {
			if (ar.succeeded()) {
				LocalFile localFile = ar.result();

				modify(localFile.getFile(), localFile.getSize(), fileId, offset, modify -> {
					localFile.closeFile();
					handler.handle(modify);
				});
			} else {
				handler.handle(Future.failedFuture(ar.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsStorage modify(Buffer buffer, FdfsFileId fileId, long offset, Handler<AsyncResult<Void>> handler) {
		
		Future<FdfsConnection> futureConn = getConnection();
		
		futureConn.compose(connection -> {
			Future<FdfsPacket> futureResponse = FdfsProtocol.recvPacket(vertx, options.getNetworkTimeout(), connection, FdfsProtocol.STORAGE_PROTO_CMD_RESP, 0,
					null);

			Buffer nameBuffer = Buffer.buffer(fileId.name(), options.getCharset());
			long bodyLength = 3 * FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE + nameBuffer.length() + buffer.length();
			Buffer headerBuffer = FdfsProtocol.packHeader(FdfsProtocol.STORAGE_PROTO_CMD_MODIFY_FILE, (byte) 0,
					bodyLength);

			connection.write(headerBuffer);
			if (connection.writeQueueFull()) {
				connection.pause();
				connection.drainHandler(v -> {
					connection.resume();
				});
			}

			Buffer bodyBuffer = FdfsUtils.newZero(bodyLength);

			int bufferOffset = 0;
			bodyBuffer.setLong(bufferOffset, nameBuffer.length());
			bufferOffset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
			bodyBuffer.setLong(bufferOffset, offset);
			bufferOffset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
			bodyBuffer.setLong(bufferOffset, buffer.length());
			bufferOffset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
			bodyBuffer.setBuffer(bufferOffset, nameBuffer);
			bufferOffset += nameBuffer.length();
			bodyBuffer.setBuffer(bufferOffset, buffer);

			connection.write(bodyBuffer);
			if (connection.writeQueueFull()) {
				connection.pause();
				connection.drainHandler(v -> {
					connection.resume();
				});
			}

			return futureResponse;
		}).onComplete(ar -> {
			
			if (futureConn.succeeded()) {
				futureConn.result().release();
			}

			if (ar.succeeded()) {
				handler.handle(Future.succeededFuture());
			} else {
				handler.handle(Future.failedFuture(ar.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsStorage download(FdfsFileId fileId, WriteStream<Buffer> stream, long offset, long bytes,
			Handler<AsyncResult<Void>> handler) {
		
		Future<FdfsConnection> futureConn = getConnection();

		futureConn.compose(connection -> {
			Future<FdfsPacket> futureResponse = FdfsProtocol.recvPacket(vertx, options.getNetworkTimeout(), connection, FdfsProtocol.STORAGE_PROTO_CMD_RESP, 0,
					stream);

			Buffer nameBuffer = Buffer.buffer(fileId.name(), options.getCharset());
			Buffer groupBuffer = Buffer.buffer(fileId.group(), options.getCharset());
			long bodyLength = FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE * 2 + FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN
					+ nameBuffer.length();
			Buffer headerBuffer = FdfsProtocol.packHeader(FdfsProtocol.STORAGE_PROTO_CMD_DOWNLOAD_FILE, (byte) 0,
					bodyLength);

			connection.write(headerBuffer);
			if (connection.writeQueueFull()) {
				connection.pause();
				connection.drainHandler(v -> {
					connection.resume();
				});
			}

			Buffer bodyBuffer = FdfsUtils.newZero(bodyLength);

			int bufferOffset = 0;
			bodyBuffer.setLong(bufferOffset, offset);
			bufferOffset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
			bodyBuffer.setLong(bufferOffset, bytes);
			bufferOffset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
			bodyBuffer.setBuffer(bufferOffset, groupBuffer);
			bufferOffset += FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN;
			bodyBuffer.setBuffer(bufferOffset, nameBuffer);

			connection.write(bodyBuffer);
			if (connection.writeQueueFull()) {
				connection.pause();
				connection.drainHandler(v -> {
					connection.resume();
				});
			}

			return futureResponse;
		}).onComplete(ar -> {
			
			if (futureConn.succeeded()) {
				futureConn.result().release();
			}

			if (ar.succeeded()) {
				handler.handle(Future.succeededFuture());
			} else {
				handler.handle(Future.failedFuture(ar.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsStorage download(FdfsFileId fileId, String fileFullPathName, long offset, long bytes,
			Handler<AsyncResult<Void>> handler) {

		vertx.fileSystem().open(fileFullPathName, new OpenOptions().setCreate(true).setWrite(true), ar -> {
			if (ar.succeeded()) {
				AsyncFile file = ar.result();

				download(fileId, file, offset, bytes, download -> {
					file.close();
					handler.handle(download);
				});
			} else {
				handler.handle(Future.failedFuture(ar.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsStorage download(FdfsFileId fileId, long offset, long bytes, Handler<AsyncResult<Buffer>> handler) {
		
		Future<FdfsConnection> futureConn = getConnection();
		
		futureConn.compose(connection -> {
			Future<FdfsPacket> futureResponse = FdfsProtocol.recvPacket(vertx, options.getNetworkTimeout(), connection, FdfsProtocol.STORAGE_PROTO_CMD_RESP, 0,
					null);

			Buffer nameBuffer = Buffer.buffer(fileId.name(), options.getCharset());
			Buffer groupBuffer = Buffer.buffer(fileId.group(), options.getCharset());
			long bodyLength = FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE * 2 + FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN
					+ nameBuffer.length();
			Buffer headerBuffer = FdfsProtocol.packHeader(FdfsProtocol.STORAGE_PROTO_CMD_DOWNLOAD_FILE, (byte) 0,
					bodyLength);

			connection.write(headerBuffer);
			if (connection.writeQueueFull()) {
				connection.pause();
				connection.drainHandler(v -> {
					connection.resume();
				});
			}

			Buffer bodyBuffer = FdfsUtils.newZero(bodyLength);

			int bufferOffset = 0;
			bodyBuffer.setLong(bufferOffset, offset);
			bufferOffset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
			bodyBuffer.setLong(bufferOffset, bytes);
			bufferOffset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
			bodyBuffer.setBuffer(bufferOffset, groupBuffer);
			bufferOffset += FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN;
			bodyBuffer.setBuffer(bufferOffset, nameBuffer);

			connection.write(bodyBuffer);
			if (connection.writeQueueFull()) {
				connection.pause();
				connection.drainHandler(v -> {
					connection.resume();
				});
			}

			return futureResponse;
		}).onComplete(ar -> {
			
			if (futureConn.succeeded()) {
				futureConn.result().release();
			}

			if (ar.succeeded()) {
				handler.handle(Future.succeededFuture(ar.result().getBodyBuffer()));
			} else {
				handler.handle(Future.failedFuture(ar.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsStorage setMetaData(FdfsFileId fileId, JsonObject metaData, byte flag,
			Handler<AsyncResult<Void>> handler) {
		
		Future<FdfsConnection> futureConn = getConnection();

		futureConn.compose(connection -> {
			Future<FdfsPacket> futureResponse = FdfsProtocol.recvPacket(vertx, options.getNetworkTimeout(), connection, FdfsProtocol.STORAGE_PROTO_CMD_RESP, 0,
					null);

			Buffer metaBuffer = FdfsProtocol.packMetaData(metaData, options.getCharset());
			Buffer nameBuffer = Buffer.buffer(fileId.name(), options.getCharset());
			long bodyLength = FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE + FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE + 1
					+ FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN + nameBuffer.length() + metaBuffer.length();
			Buffer headerBuffer = FdfsProtocol.packHeader(FdfsProtocol.STORAGE_PROTO_CMD_SET_METADATA, (byte) 0,
					bodyLength);

			connection.write(headerBuffer);
			if (connection.writeQueueFull()) {
				connection.pause();
				connection.drainHandler(v -> {
					connection.resume();
				});
			}

			Buffer groupBuffer = Buffer.buffer(fileId.group(), options.getCharset());
			Buffer bodyBuffer = FdfsUtils.newZero(bodyLength);

			int offset = 0;
			bodyBuffer.setLong(offset, nameBuffer.length());
			offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
			bodyBuffer.setLong(offset, metaBuffer.length());
			offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
			bodyBuffer.setByte(offset, flag);
			offset += 1;
			bodyBuffer.setBuffer(offset, groupBuffer);
			offset += FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN;
			bodyBuffer.setBuffer(offset, nameBuffer);
			offset += nameBuffer.length();
			bodyBuffer.setBuffer(offset, metaBuffer);

			connection.write(bodyBuffer);
			if (connection.writeQueueFull()) {
				connection.pause();
				connection.drainHandler(v -> {
					connection.resume();
				});
			}

			return futureResponse;
		}).onComplete(ar -> {
			
			if (futureConn.succeeded()) {
				futureConn.result().release();
			}
			
			if (ar.succeeded()) {
				handler.handle(Future.succeededFuture());
			} else {
				handler.handle(Future.failedFuture(ar.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsStorage setMetaData(FdfsFileId fileId, JsonObject metaData, Handler<AsyncResult<Void>> handler) {
		return setMetaData(fileId, metaData, FdfsProtocol.STORAGE_SET_METADATA_FLAG_OVERWRITE, handler);
	}

	@Override
	public FdfsStorage getMetaData(FdfsFileId fileId, Handler<AsyncResult<JsonObject>> handler) {

		Future<FdfsConnection> futureConn = getConnection();
		
		futureConn.compose(connection -> {
			Future<FdfsPacket> futureResponse = FdfsProtocol.recvPacket(vertx, options.getNetworkTimeout(), connection, FdfsProtocol.STORAGE_PROTO_CMD_RESP, 0,
					null);

			Buffer packet = FdfsProtocol.packFileId(FdfsProtocol.STORAGE_PROTO_CMD_GET_METADATA, fileId,
					options.getCharset());

			connection.write(packet);
			if (connection.writeQueueFull()) {
				connection.pause();
				connection.drainHandler(v -> {
					connection.resume();
				});
			}

			return futureResponse;
		}).onComplete(ar -> {
			
			if (futureConn.succeeded()) {
				futureConn.result().release();
			}
			
			if (ar.succeeded()) {
				FdfsPacket packet = ar.result();
				Buffer bodyBuffer = packet.getBodyBuffer();

				JsonObject meta = FdfsProtocol.parseMetaData(bodyBuffer, options.getCharset());
				handler.handle(Future.succeededFuture(meta));
			} else {
				handler.handle(Future.failedFuture(ar.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsStorage delete(FdfsFileId fileId, Handler<AsyncResult<Void>> handler) {

		Future<FdfsConnection> futureConn = getConnection();
		
		futureConn.compose(connection -> {
			Future<FdfsPacket> futureResponse = FdfsProtocol.recvPacket(vertx, options.getNetworkTimeout(), connection, FdfsProtocol.STORAGE_PROTO_CMD_RESP, 0,
					null);

			Buffer packet = FdfsProtocol.packFileId(FdfsProtocol.STORAGE_PROTO_CMD_DELETE_FILE, fileId,
					options.getCharset());

			connection.write(packet);
			if (connection.writeQueueFull()) {
				connection.pause();
				connection.drainHandler(v -> {
					connection.resume();
				});
			}

			return futureResponse;
		}).onComplete(ar -> {
			
			if (futureConn.succeeded()) {
				futureConn.result().release();
			}
			
			if (ar.succeeded()) {
				handler.handle(Future.succeededFuture());
			} else {
				handler.handle(Future.failedFuture(ar.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsStorage fileInfo(FdfsFileId fileId, Handler<AsyncResult<FdfsFileInfo>> handler) {
		
		Future<FdfsConnection> futureConn = getConnection();
		
		futureConn.compose(connection -> {

			Future<FdfsPacket> futureResponse = FdfsProtocol.recvPacket(vertx, options.getNetworkTimeout(), connection, FdfsProtocol.STORAGE_PROTO_CMD_RESP, 0,
					null);

			Buffer packet = FdfsProtocol.packFileId(FdfsProtocol.STORAGE_PROTO_CMD_QUERY_FILE_INFO, fileId,
					options.getCharset());

			connection.write(packet);
			if (connection.writeQueueFull()) {
				connection.pause();
				connection.drainHandler(v -> {
					connection.resume();
				});
			}

			return futureResponse;
		}).onComplete(ar -> {
			
			if (futureConn.succeeded()) {
				futureConn.result().release();
			}
			
			if (ar.succeeded()) {
				FdfsPacket packet = ar.result();
				Buffer bodyBuffer = packet.getBodyBuffer();

				final long FILE_INFO_EXPECTED_LENGTH_WITHOUT_SOURCE_IP = FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE * 3;
				final long FILE_INFO_EXPECTED_LENGTH_WITH_SOURCE_IP = FILE_INFO_EXPECTED_LENGTH_WITHOUT_SOURCE_IP
						+ FdfsProtocol.FDFS_IPADDR_SIZE;

				if (packet.getBodyLength() == FILE_INFO_EXPECTED_LENGTH_WITHOUT_SOURCE_IP) {
					handler.handle(Future.succeededFuture(new FdfsFileInfo().setSize(bodyBuffer.getLong(0))
							.setTimestamp(
									Instant.ofEpochSecond(bodyBuffer.getLong(FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE)))
							.setCrc32(bodyBuffer.getLong(FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE * 2))));
				} else if (packet.getBodyLength() == FILE_INFO_EXPECTED_LENGTH_WITH_SOURCE_IP) {
					handler.handle(Future.succeededFuture(new FdfsFileInfo().setSize(bodyBuffer.getLong(0))
							.setTimestamp(
									Instant.ofEpochSecond(bodyBuffer.getLong(FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE)))
							.setCrc32(bodyBuffer.getLong(FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE * 2)).setSourceIp(
									FdfsUtils.fdfsTrim(bodyBuffer.getString(FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE * 3,
											bodyBuffer.length(), options.getCharset())))));
				} else {
					handler.handle(Future.failedFuture(new FdfsException("receive fileinfo packet size"
							+ packet.getBodyLength() + " is invalid (" + FILE_INFO_EXPECTED_LENGTH_WITHOUT_SOURCE_IP
							+ " or " + FILE_INFO_EXPECTED_LENGTH_WITH_SOURCE_IP + " is ok)")));
				}

			} else {
				handler.handle(Future.failedFuture(ar.cause()));
			}
		});

		return this;
	}

	private Future<FdfsConnection> getConnection() {
		return pool.get(options.getAddress());
	}

	private Future<FdfsFileId> uploadFile(byte command, String fileFullPathName, String ext) {
		
		Buffer extBuffer = Buffer.buffer(ext, options.getCharset());
		if (extBuffer.length() > FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN) {
			return Future.failedFuture("ext is too long ( greater than " + FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN + ")");
		}

		Promise<FdfsFileId> promiseFileId = Promise.promise();

		LocalFile.readFile(vertx.fileSystem(), fileFullPathName).onComplete(ar -> {
			if (ar.succeeded()) {
				LocalFile localFile = ar.result();

				uploadFile(command, localFile.getFile(), localFile.getSize(), ext).onComplete(upload -> {

					localFile.closeFile();

					if (upload.succeeded()) {
						promiseFileId.complete(upload.result());
					} else {
						promiseFileId.fail(upload.cause());
					}
				});

			} else {
				promiseFileId.fail(ar.cause());
			}
		});

		return promiseFileId.future();
	}

	private Future<FdfsFileId> uploadFile(byte command, ReadStream<Buffer> stream, long size, String ext) {
		
		Buffer extBuffer = Buffer.buffer(ext, options.getCharset());
		if (extBuffer.length() > FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN) {
			return Future.failedFuture("ext is too long ( greater than " + FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN + ")");
		}
		
		stream.pause();

		Promise<FdfsFileId> promiseFileId = Promise.promise();
		Future<FdfsConnection> futureConn = getConnection();

		futureConn.compose(connection -> {
			Future<FdfsPacket> futurePacket = FdfsProtocol.recvPacket(vertx, options.getNetworkTimeout(), connection,
					FdfsProtocol.STORAGE_PROTO_CMD_RESP, 0, null);

			long bodyLength = 1 + FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE + FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN + size;
			Buffer header = FdfsProtocol.packHeader(command, (byte) 0, bodyLength);

			connection.write(header);

			Buffer body = FdfsUtils.newZero(bodyLength - size);

			body.setByte(0, options.getStorePathIndex());
			body.setLong(1, size);
			body.setBuffer(1 + FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE, extBuffer);

			connection.write(body);

			Pump.pump(stream, connection).start();
			stream.resume();

			return futurePacket;
		}).onComplete(ar -> {
			
			if (futureConn.succeeded()) {
				futureConn.result().release();
			}

			if (ar.succeeded()) {
				FdfsPacket packet = ar.result();
				Buffer body = packet.getBodyBuffer();

				if (body.length() <= FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN) {
					promiseFileId.fail(
							"response body length: " + body.length() + " <= " + FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN);
					return;
				}

				String charset = options.getCharset();
				String group = FdfsUtils.fdfsTrim(body.getString(0, FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN, charset));
				String id = FdfsUtils
						.fdfsTrim(body.getString(FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN, body.length(), charset));

				promiseFileId.complete(FdfsFileId.create(group, id));
			} else {
				promiseFileId.fail(ar.cause());
			}

		});

		return promiseFileId.future();
	}

	private Future<FdfsFileId> uploadFile(byte command, Buffer buffer, String ext) {
		
		Buffer extBuffer = Buffer.buffer(ext, options.getCharset());
		if (extBuffer.length() > FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN) {
			return Future.failedFuture("ext is too long ( greater than " + FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN + ")");
		}

		Promise<FdfsFileId> promiseFileId = Promise.promise();
		Future<FdfsConnection> futureConn = getConnection();

		futureConn.compose(connection -> {
			Future<FdfsPacket> futurePacket = FdfsProtocol.recvPacket(vertx, options.getNetworkTimeout(), connection,
					FdfsProtocol.STORAGE_PROTO_CMD_RESP, 0,null);

			long bodyLength = 1 + FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE + FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN
					+ buffer.length();
			Buffer header = FdfsProtocol.packHeader(command, (byte) 0, bodyLength);

			connection.write(header);

			Buffer bodyBuffer = FdfsUtils.newZero(bodyLength);

			int offset = 0;
			bodyBuffer.setByte(offset, options.getStorePathIndex());
			offset += 1;
			bodyBuffer.setLong(offset, buffer.length());
			offset += FdfsProtocol.FDFS_PROTO_PKG_LEN_SIZE;
			bodyBuffer.setBuffer(offset, extBuffer);
			offset += FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN;
			bodyBuffer.setBuffer(offset, buffer);

			connection.write(bodyBuffer);

			return futurePacket;
		}).onComplete(ar -> {
			
			if (futureConn.succeeded()) {
				futureConn.result().release();
			}

			if (ar.succeeded()) {
				FdfsPacket packet = ar.result();
				Buffer resBodyBuffer = packet.getBodyBuffer();

				if (resBodyBuffer.length() <= FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN) {
					promiseFileId.fail("response body length: " + resBodyBuffer.length() + " <= "
							+ FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN);
					return;
				}

				String charset = options.getCharset();
				String group = FdfsUtils
						.fdfsTrim(resBodyBuffer.getString(0, FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN, charset));
				String id = FdfsUtils.fdfsTrim(
						resBodyBuffer.getString(FdfsProtocol.FDFS_GROUP_NAME_MAX_LEN, resBodyBuffer.length(), charset));

				promiseFileId.complete(FdfsFileId.create(group, id));
			} else {
				promiseFileId.fail(ar.cause());
			}

		});

		return promiseFileId.future();
	}

	private static final class LocalFile {
		private long size;
		private AsyncFile file;

		public static Future<LocalFile> readFile(FileSystem fs, String filefullPathName) {
			LocalFile localFile = new LocalFile();

			return Future.<FileProps>future(future -> {
				fs.props(filefullPathName, future);
			}).compose(props -> {
				localFile.setSize(props.size());

				return Future.<AsyncFile>future(future -> {
					fs.open(filefullPathName, new OpenOptions().setRead(true).setWrite(false).setCreate(false), future);
				});
			}).compose(fileStream -> {

				localFile.setFile(fileStream);

				return Future.succeededFuture(localFile);
			});
		}

		public LocalFile closeFile() {
			file.close();
			return this;
		}

		public long getSize() {
			return size;
		}

		public AsyncFile getFile() {
			return file;
		}

		private LocalFile setSize(long size) {
			this.size = size;
			return this;
		}

		private LocalFile setFile(AsyncFile file) {
			this.file = file;
			return this;
		}

		private LocalFile() {
		}
	}

	@Override
	public FdfsStorageOptions getOptions() {
		return options;
	}
}
