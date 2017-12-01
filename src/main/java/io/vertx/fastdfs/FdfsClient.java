package io.vertx.fastdfs;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import io.vertx.fastdfs.impl.FdfsClientImpl;
import io.vertx.fastdfs.FdfsGroupInfo;

/**
 * FastDFS client.
 * 
 * @author GengTeng
 * <p>
 * me@gteng.org
 * 
 * @version 3.5.0
 */
public interface FdfsClient {

	public static FdfsClient create(Vertx vertx, FdfsClientOptions options) {
		return new FdfsClientImpl(vertx, options);
	}

	public static FdfsClient create(Vertx vertx, JsonObject options) {
		return new FdfsClientImpl(vertx, options);
	}

	FdfsClient upload(ReadStream<Buffer> stream, long size, String ext, Handler<AsyncResult<FdfsFileId>> handler);

	FdfsClient upload(String fileFullPathName, String ext, Handler<AsyncResult<FdfsFileId>> handler);
	
	FdfsClient upload(Buffer buffer, String ext, Handler<AsyncResult<FdfsFileId>> handler);
	
	FdfsClient uploadAppender(ReadStream<Buffer> stream, long size, String ext, Handler<AsyncResult<FdfsFileId>> handler);

	FdfsClient uploadAppender(String fileFullPathName, String ext, Handler<AsyncResult<FdfsFileId>> handler);
	
	FdfsClient uploadAppender(Buffer buffer, String ext, Handler<AsyncResult<FdfsFileId>> handler);
	
	FdfsClient append(ReadStream<Buffer> stream, long size, FdfsFileId fileId, Handler<AsyncResult<Void>> handler);

	FdfsClient append(String fileFullPathName, FdfsFileId fileId, Handler<AsyncResult<Void>> handler);
	
	FdfsClient append(Buffer buffer, FdfsFileId fileId, Handler<AsyncResult<Void>> handler);
	
	FdfsClient modify(ReadStream<Buffer> stream, long size, FdfsFileId fileId, long offset, Handler<AsyncResult<Void>> handler);

	FdfsClient modify(String fileFullPathName, FdfsFileId fileId, long offset, Handler<AsyncResult<Void>> handler);
	
	FdfsClient modify(Buffer buffer, FdfsFileId fileId, long offset, Handler<AsyncResult<Void>> handler);

	FdfsClient download(FdfsFileId fileId, WriteStream<Buffer> stream, long offset, long bytes, Handler<AsyncResult<Void>> handler);

	FdfsClient download(FdfsFileId fileId, String fileFullPathName, long offset, long bytes, Handler<AsyncResult<Void>> handler);
	
	FdfsClient download(FdfsFileId fileId, long offset, long bytes, Handler<AsyncResult<Buffer>> handler);

	FdfsClient setMetaData(FdfsFileId fileId, JsonObject metaData, byte flag, Handler<AsyncResult<Void>> handler);

	FdfsClient getMetaData(FdfsFileId fileId, Handler<AsyncResult<JsonObject>> handler);

	FdfsClient delete(FdfsFileId fileId, Handler<AsyncResult<Void>> handler);

	FdfsClient fileInfo(FdfsFileId fileId, Handler<AsyncResult<FdfsFileInfo>> handler);

	FdfsClient groups(Handler<AsyncResult<List<FdfsGroupInfo>>> handler);

	FdfsClient storages(String group, Handler<AsyncResult<List<FdfsStorageInfo>>> handler);
	
	FdfsClient getTracker(Handler<AsyncResult<FdfsTracker>> handler);
	
	FdfsClientOptions getOptions();
	
	void close();
}
