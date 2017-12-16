package io.vertx.fastdfs.utils;

import java.util.concurrent.atomic.AtomicLong;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.streams.WriteStream;
import io.vertx.fastdfs.FdfsFileId;
import io.vertx.fastdfs.exp.FdfsException;
import io.vertx.fastdfs.impl.FdfsConnection;

/**
 * FastDFS protocol constants and functions.
 * 
 * @author GengTeng
 *         <p>
 *         me@gteng.org
 * 
 * @version 3.5.0
 */
public final class FdfsProtocol {

	public static final byte FDFS_PROTO_CMD_QUIT = 82;
	public static final byte TRACKER_PROTO_CMD_SERVER_LIST_GROUP = 91;
	public static final byte TRACKER_PROTO_CMD_SERVER_LIST_STORAGE = 92;
	public static final byte TRACKER_PROTO_CMD_SERVER_DELETE_STORAGE = 93;

	public static final byte TRACKER_PROTO_CMD_SERVICE_QUERY_STORE_WITHOUT_GROUP_ONE = 101;
	public static final byte TRACKER_PROTO_CMD_SERVICE_QUERY_FETCH_ONE = 102;
	public static final byte TRACKER_PROTO_CMD_SERVICE_QUERY_UPDATE = 103;
	public static final byte TRACKER_PROTO_CMD_SERVICE_QUERY_STORE_WITH_GROUP_ONE = 104;
	public static final byte TRACKER_PROTO_CMD_SERVICE_QUERY_FETCH_ALL = 105;
	public static final byte TRACKER_PROTO_CMD_SERVICE_QUERY_STORE_WITHOUT_GROUP_ALL = 106;
	public static final byte TRACKER_PROTO_CMD_SERVICE_QUERY_STORE_WITH_GROUP_ALL = 107;
	public static final byte TRACKER_PROTO_CMD_RESP = 100;
	public static final byte FDFS_PROTO_CMD_ACTIVE_TEST = 111;
	public static final byte STORAGE_PROTO_CMD_UPLOAD_FILE = 11;
	public static final byte STORAGE_PROTO_CMD_DELETE_FILE = 12;
	public static final byte STORAGE_PROTO_CMD_SET_METADATA = 13;
	public static final byte STORAGE_PROTO_CMD_DOWNLOAD_FILE = 14;
	public static final byte STORAGE_PROTO_CMD_GET_METADATA = 15;
	public static final byte STORAGE_PROTO_CMD_UPLOAD_SLAVE_FILE = 21;
	public static final byte STORAGE_PROTO_CMD_QUERY_FILE_INFO = 22;
	public static final byte STORAGE_PROTO_CMD_UPLOAD_APPENDER_FILE = 23; // create appender file
	public static final byte STORAGE_PROTO_CMD_APPEND_FILE = 24; // append file
	public static final byte STORAGE_PROTO_CMD_MODIFY_FILE = 34; // modify appender file
	public static final byte STORAGE_PROTO_CMD_TRUNCATE_FILE = 36; // truncate appender file

	public static final byte FDFS_STORAGE_STATUS_INIT = 0;
	public static final byte FDFS_STORAGE_STATUS_WAIT_SYNC = 1;
	public static final byte FDFS_STORAGE_STATUS_SYNCING = 2;
	public static final byte FDFS_STORAGE_STATUS_IP_CHANGED = 3;
	public static final byte FDFS_STORAGE_STATUS_DELETED = 4;
	public static final byte FDFS_STORAGE_STATUS_OFFLINE = 5;
	public static final byte FDFS_STORAGE_STATUS_ONLINE = 6;
	public static final byte FDFS_STORAGE_STATUS_ACTIVE = 7;
	public static final byte FDFS_STORAGE_STATUS_NONE = 99;

	/**
	 * for overwrite all old metadata
	 */
	public static final byte STORAGE_SET_METADATA_FLAG_OVERWRITE = 'O';

	/**
	 * for replace, insert when the meta item not exist, otherwise update it
	 */
	public static final byte STORAGE_SET_METADATA_FLAG_MERGE = 'M';

	public static final int FDFS_PROTO_PKG_LEN_SIZE = 8;
	public static final int FDFS_PROTO_CMD_SIZE = 1;
	public static final int FDFS_GROUP_NAME_MAX_LEN = 16;
	public static final int FDFS_IPADDR_SIZE = 16;
	public static final int FDFS_DOMAIN_NAME_MAX_SIZE = 128;
	public static final int FDFS_VERSION_SIZE = 6;
	public static final int FDFS_STORAGE_ID_MAX_SIZE = 16;

	public static final String FDFS_RECORD_SEPERATOR = "\u0001";
	public static final String FDFS_FIELD_SEPERATOR = "\u0002";

	public static final byte FDFS_FILE_EXT_NAME_MAX_LEN = 6;
	public static final byte FDFS_FILE_PREFIX_MAX_LEN = 16;
	public static final byte FDFS_FILE_PATH_LEN = 10;
	public static final byte FDFS_FILENAME_BASE64_LENGTH = 27;
	public static final byte FDFS_TRUNK_FILE_INFO_LEN = 16;

	public static final byte ERR_NO_ENOENT = 2;
	public static final byte ERR_NO_EIO = 5;
	public static final byte ERR_NO_EBUSY = 16;
	public static final byte ERR_NO_EINVAL = 22;
	public static final byte ERR_NO_ENOSPC = 28;
	public static final byte ECONNREFUSED = 61;
	public static final byte ERR_NO_EALREADY = 114;

	// 成功的STATUS
	public static final byte HEADER_STATUS_SUCCESS = 0;

	public static final byte STORAGE_PROTO_CMD_RESP = TRACKER_PROTO_CMD_RESP;

	public static final int TRACKER_QUERY_STORAGE_FETCH_BODY_LEN = FDFS_GROUP_NAME_MAX_LEN + FDFS_IPADDR_SIZE - 1
			+ FDFS_PROTO_PKG_LEN_SIZE;
	public static final int TRACKER_QUERY_STORAGE_STORE_BODY_LEN = FDFS_GROUP_NAME_MAX_LEN + FDFS_IPADDR_SIZE
			+ FDFS_PROTO_PKG_LEN_SIZE;

	public static final int PROTO_HEADER_CMD_INDEX = FDFS_PROTO_PKG_LEN_SIZE;
	public static final int PROTO_HEADER_STATUS_INDEX = FDFS_PROTO_PKG_LEN_SIZE + 1;

	public static final int HEADER_BYTE_LENGTH = FDFS_PROTO_PKG_LEN_SIZE + 2;

	private FdfsProtocol() {
	}

	/**
	 * 封装协议头。
	 * 
	 * @param command command
	 * @param status status
	 * @param bodyLength bodyLength
	 * @return Buffer
	 */
	public static Buffer packHeader(byte command, byte status, long bodyLength) {
		return FdfsUtils.newZero(10).setLong(0, bodyLength).setByte(PROTO_HEADER_CMD_INDEX, command)
				.setByte(PROTO_HEADER_STATUS_INDEX, status);
	}

	/**
	 * 为Buffer包装一层
	 * 
	 * @author GengTeng
	 *
	 */
	private static class WrappedBuffer {
		private Buffer buffer;

		public WrappedBuffer allocate(int initialSizeHint) {
			buffer = Buffer.buffer(initialSizeHint);
			return this;
		}

		public Buffer buffer() {
			return buffer;
		}

		public WrappedBuffer appendBuffer(Buffer buffer) {
			this.buffer.appendBuffer(buffer);
			return this;
		}

		public WrappedBuffer appendBuffer(Buffer buffer, int offset, int len) {
			this.buffer.appendBuffer(buffer, offset, len);
			return this;
		}

		public long length() {
			return buffer.length();
		}
	}

	/**
	 * 从socket接收并解析报文。
	 * 
	 * @param vertx {@code Vertx} 实例
	 * @param timeoutMillis 超时时间
	 * @param connection
	 *            等待接收报文的socket
	 * @param expectedCommand
	 *            期望的command
	 * @param expectedBodyLength
	 *            期望的body长度，设置为0则不进行检查.
	 * @param bodyWriteStream
	 *            报文体将被写入的流，如果该参数不为null，则报文体内容将被写入该流，不作为返回值返回。
	 * @return 异步FdfsPacket对象，如果bodyWriteStream为null，则报文体也保存到该对象中；否则该对象仅包含报文长度。
	 */
	public static Future<FdfsPacket> recvPacket(Vertx vertx, long timeoutMillis, FdfsConnection connection, byte expectedCommand, long expectedBodyLength,
			WriteStream<Buffer> bodyWriteStream) {

		return Future.future(future -> {

			Buffer headerBuffer = Buffer.buffer(HEADER_BYTE_LENGTH);
			Future<Long> futureBodyLength = Future.future();
			WrappedBuffer bodyBuffer = new WrappedBuffer();
			AtomicLong bodyReceived = new AtomicLong();
			AtomicLong lastReceiveTime = new AtomicLong(System.currentTimeMillis());
			
			vertx.setPeriodic(timeoutMillis, l -> {
				if (System.currentTimeMillis() - lastReceiveTime.get() > timeoutMillis) {
					
					vertx.cancelTimer(l);
					
					if (!future.isComplete()) {
						if (futureBodyLength.isComplete() && futureBodyLength.result() == bodyReceived.get()) {
							future.complete(new FdfsPacket().setBodyLength(futureBodyLength.result())
									.setBodyBuffer(bodyBuffer.buffer()));
						} else {
							future.fail(new FdfsException("receive timeout"));
						}
					}
				} else {
					if (future.isComplete()) {
						vertx.cancelTimer(l);
					}
				}
			});

			connection.handler(buffer -> {
				
				lastReceiveTime.set(System.currentTimeMillis());

				if (!futureBodyLength.isComplete()) {
					final long currentLength = headerBuffer.length() + buffer.length();
					final int lengthToFillHeader = HEADER_BYTE_LENGTH - headerBuffer.length();
					if (currentLength >= HEADER_BYTE_LENGTH) {
						headerBuffer.appendBuffer(buffer, 0, lengthToFillHeader);

						parseHeader(headerBuffer, expectedCommand, expectedBodyLength).setHandler(futureBodyLength); // 非异步，直接返回

						if (!futureBodyLength.succeeded()) {
							future.fail(futureBodyLength.cause());
							// closeSocket(socket);
							return;
						}

						if (futureBodyLength.result() == 0) {
							future.complete(new FdfsPacket().setBodyLength(futureBodyLength.result()));
							// closeSocket(socket);
							return;
						}

						if (bodyWriteStream != null) {
							if (currentLength > HEADER_BYTE_LENGTH) {
								Buffer bodytoWrite = buffer.getBuffer(lengthToFillHeader, buffer.length());
								bodyWriteStream.write(bodytoWrite);
								if (bodyWriteStream.writeQueueFull()) {
									connection.pause();
									connection.drainHandler(v -> {
										connection.resume();
									});
								}

								bodyReceived.addAndGet(bodytoWrite.length());
								if (bodyReceived.get() >= futureBodyLength.result()) {
									future.complete(new FdfsPacket().setBodyLength(futureBodyLength.result()));
									// closeSocket(socket);
								}
							}

							return;
						}

						bodyBuffer.allocate(futureBodyLength.result().intValue());

						if (currentLength > HEADER_BYTE_LENGTH) {
							int lengthToFillBody = buffer.length() - lengthToFillHeader;
							bodyBuffer.appendBuffer(buffer, lengthToFillHeader, lengthToFillBody);
							bodyReceived.addAndGet(lengthToFillBody);

							if (bodyBuffer.length() >= futureBodyLength.result()) {
								future.complete(new FdfsPacket().setBodyLength(futureBodyLength.result())
										.setBodyBuffer(bodyBuffer.buffer()));
								// closeSocket(socket);
							}
						}
					} else { // if (currentLength < HEADER_BYTE_LENGTH)
						headerBuffer.appendBuffer(buffer);
					}
				} else { // if (futureBodyLength.isComplete())

					if (bodyWriteStream != null) {
						bodyWriteStream.write(buffer);
						if (bodyWriteStream.writeQueueFull()) {
							connection.pause();
							connection.drainHandler(v -> {
								connection.resume();
							});
						}

						bodyReceived.addAndGet(buffer.length());
						if (bodyReceived.get() >= futureBodyLength.result()) {
							future.complete(new FdfsPacket().setBodyLength(futureBodyLength.result()));
							// closeSocket(socket);
						}
						return;
					}

					bodyBuffer.appendBuffer(buffer);
					bodyReceived.addAndGet(buffer.length());
					// 读取完毕
					if (bodyBuffer.length() >= futureBodyLength.result()) {
						future.complete(new FdfsPacket().setBodyLength(futureBodyLength.result())
								.setBodyBuffer(bodyBuffer.buffer()));
						// closeSocket(socket);
					}
				}
			});

			connection.exceptionHandler(e -> {
				if (!future.isComplete()) {
					if (futureBodyLength.isComplete() && futureBodyLength.result() == bodyReceived.get()) {
						future.complete(new FdfsPacket().setBodyLength(futureBodyLength.result())
								.setBodyBuffer(bodyBuffer.buffer()));
					} else {
						future.fail(new FdfsException(e));
					}
				}
			});

			connection.endHandler(v -> {
				if (!future.isComplete()) {
					if (futureBodyLength.isComplete() && futureBodyLength.result() == bodyReceived.get()) {
						future.complete(new FdfsPacket().setBodyLength(futureBodyLength.result())
								.setBodyBuffer(bodyBuffer.buffer()));
					} else {
						future.fail(new FdfsException("socket closed before recv complete"));
					}
				}
			});
		});
	}

	/**
	 * 解析报文头。
	 * 
	 * @param headerBuffer header Buffer
	 * @param expectedCommand expectedCommand
	 * @param expectedBodyLength expectedBodyLength
	 * @return async result of the length of the packet body
	 */
	public static Future<Long> parseHeader(Buffer headerBuffer, byte expectedCommand, long expectedBodyLength) {
		if (headerBuffer.length() != HEADER_BYTE_LENGTH) {
			return Future.failedFuture(new FdfsException("receive packet size" + headerBuffer.length()
					+ " is not equal to the expected header size: " + HEADER_BYTE_LENGTH));
		}

		byte command = headerBuffer.getByte(PROTO_HEADER_CMD_INDEX);
		if (command != expectedCommand) {
			return Future.failedFuture(new FdfsException(
					"receive command: " + command + " is not equal to the expected command: " + expectedCommand));
		}

		byte status = headerBuffer.getByte(PROTO_HEADER_STATUS_INDEX);
		if (status != HEADER_STATUS_SUCCESS) {
			return Future.failedFuture(new FdfsException("receive packet errno is: " + status));
		}

		long bodyLength = headerBuffer.getLong(0);
		if (expectedBodyLength > 0 && bodyLength != expectedBodyLength) {
			return Future.failedFuture(new FdfsException("receive packet body length: " + bodyLength
					+ " is not equal to the expected: " + expectedBodyLength));
		}

		return Future.succeededFuture(bodyLength);
	}

	/**
	 * 发送关闭指令并关掉socket。
	 * 
	 * @param socket the socket to close
	 */
	public static void closeSocket(NetSocket socket) {
		socket.end(packHeader(FDFS_PROTO_CMD_QUIT, (byte) 0, 0));
	}

	/**
	 * 封装只有fileId的包，下载和删除时使用。
	 * 
	 * @param command command
	 * @param fileId fileId
	 * @param charset charset
	 * @return the packet buffer
	 */
	public static Buffer packFileId(byte command, FdfsFileId fileId, String charset) {
		Buffer groupBuffer = Buffer.buffer(fileId.group(), charset);
		Buffer nameBuffer = Buffer.buffer(fileId.name(), charset);
		int bodyLength = FDFS_GROUP_NAME_MAX_LEN + nameBuffer.length();
		Buffer headerBuffer = packHeader(command, (byte) 0, bodyLength);

		Buffer bodyBuffer = FdfsUtils.newZero(bodyLength);
		bodyBuffer.setBuffer(0, groupBuffer);
		bodyBuffer.setBuffer(FDFS_GROUP_NAME_MAX_LEN, nameBuffer);

		return headerBuffer.appendBuffer(bodyBuffer);
	}

	/**
	 * 封装metadata为Buffer。
	 * 
	 * @param meta meta
	 * @param charset charset
	 * @return Buffer
	 */
	public static Buffer packMetaData(JsonObject meta, String charset) {
		StringBuilder builder = new StringBuilder();

		meta.forEach(pair -> {
			builder.append(pair.getKey());
			builder.append(FDFS_FIELD_SEPERATOR);
			builder.append(pair.getValue());
			builder.append(FDFS_RECORD_SEPERATOR);
		});

		if (builder.length() > 0) {
			builder.deleteCharAt(builder.length() - 1);
		}

		return Buffer.buffer(builder.toString(), charset);
	}

	/**
	 * 解析Buffer为metadata(JsonObject)。
	 * 
	 * @param buffer buffer
	 * @param charset charset
	 * @return JsonObject
	 */
	public static JsonObject parseMetaData(Buffer buffer, String charset) {
		JsonObject json = new JsonObject();

		if (buffer == null || buffer.length() == 0) {
			return json;
		}

		String meta = buffer.toString(charset);

		if (meta == null || meta.isEmpty()) {
			return json;
		}

		String[] md = meta.split(FDFS_RECORD_SEPERATOR);

		for (String item : md) {
			String[] kv = item.split(FDFS_FIELD_SEPERATOR);
			if (kv.length >= 2) {
				json.put(FdfsUtils.fdfsTrim(kv[0]), FdfsUtils.fdfsTrim(kv[1]));
			}
		}

		return json;
	}

	public static Future<NetSocket> getConnection(NetClient client, SocketAddress address) {
		return Future.future(future -> {
			client.connect(address, future);
		});
	}
}
