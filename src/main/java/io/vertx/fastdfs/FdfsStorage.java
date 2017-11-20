package io.vertx.fastdfs;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import io.vertx.fastdfs.impl.FdfsStorageImpl;
import io.vertx.fastdfs.options.FdfsStorageOptions;

/**
 * FastDFS storage.
 * 
 * @author GengTeng
 * @version 3.5.0
 */
public interface FdfsStorage {

	public static FdfsStorage create(Vertx vertx, NetSocket socket, FdfsStorageOptions options) {
		return new FdfsStorageImpl(vertx, socket, options);
	}

	FdfsStorage upload(ReadStream<Buffer> stream, long size, String ext, Handler<AsyncResult<FdfsFileId>> handler);

	FdfsStorage upload(String fileFullPathName, String ext, Handler<AsyncResult<FdfsFileId>> handler);
	
	FdfsStorage upload(Buffer buffer, String ext, Handler<AsyncResult<FdfsFileId>> handler);
	
	FdfsStorage uploadAppender(ReadStream<Buffer> stream, long size, String ext, Handler<AsyncResult<FdfsFileId>> handler);

	FdfsStorage uploadAppender(String fileFullPathName, String ext, Handler<AsyncResult<FdfsFileId>> handler);
	
	FdfsStorage uploadAppender(Buffer buffer, String ext, Handler<AsyncResult<FdfsFileId>> handler);
	
	FdfsStorage append(ReadStream<Buffer> stream, long size, FdfsFileId fileId, Handler<AsyncResult<Void>> handler);

	FdfsStorage append(String fileFullPathName, FdfsFileId fileId, Handler<AsyncResult<Void>> handler);
	
	FdfsStorage append(Buffer buffer, FdfsFileId fileId, Handler<AsyncResult<Void>> handler);
	
	FdfsStorage modify(ReadStream<Buffer> stream, long size, FdfsFileId fileId, long offset, Handler<AsyncResult<Void>> handler);

	FdfsStorage modify(String fileFullPathName, FdfsFileId fileId, long offset, Handler<AsyncResult<Void>> handler);
	
	FdfsStorage modify(Buffer buffer, FdfsFileId fileId, long offset, Handler<AsyncResult<Void>> handler);

	FdfsStorage download(FdfsFileId fileId, WriteStream<Buffer> stream, long offset, long bytes, Handler<AsyncResult<Void>> handler);

	FdfsStorage download(FdfsFileId fileId, String fileFullPathName, long offset, long bytes, Handler<AsyncResult<Void>> handler);
	
	FdfsStorage download(FdfsFileId fileId, long offset, long bytes, Handler<AsyncResult<Buffer>> handler);

	FdfsStorage setMetaData(FdfsFileId fileId, JsonObject metaData, byte flag, Handler<AsyncResult<Void>> handler);

	FdfsStorage setMetaData(FdfsFileId fileId, JsonObject metaData, Handler<AsyncResult<Void>> handler);

	FdfsStorage getMetaData(FdfsFileId fileId, Handler<AsyncResult<JsonObject>> handler);

	FdfsStorage delete(FdfsFileId fileId, Handler<AsyncResult<Void>> handler);

	FdfsStorage fileInfo(FdfsFileId fileId, Handler<AsyncResult<FdfsFileInfo>> handler);
	
	void close();
}
