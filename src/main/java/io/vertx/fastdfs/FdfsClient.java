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
import io.vertx.fastdfs.impl.FdfsTracker;
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

	/**
	   * Create a FastDFS client using the provided {@code vertx} instance.
	   *
	   * @param vertx the vertx instance
	   * @param options the FastDFS Client options
	   * @return the created FastDFS client
	   */
	public static FdfsClient create(Vertx vertx, FdfsClientOptions options) {
		return new FdfsClientImpl(vertx, options);
	}

	/**
	   * Create a FastDFS client using the provided {@code vertx} instance.
	   *
	   * @param vertx the vertx instance
	   * @param options the FastDFS Client options
	   * @return the created FastDFS client
	   */
	public static FdfsClient create(Vertx vertx, JsonObject options) {
		return new FdfsClientImpl(vertx, options);
	}

	/**
	   * upload a {@code ReadStream<Buffer>} object.
	   *
	   * @param stream the {@code ReadStream<Buffer>} object
	   * @param size the size
	   * @param ext the extension
	   * @param handler the handler that will receive the result
	   * @return the client
	   */
	FdfsClient upload(ReadStream<Buffer> stream, long size, String ext, Handler<AsyncResult<FdfsFileId>> handler);

	/**
	   * upload a local file.
	   *
	   * @param fileFullPathName full path to the file
	   * @param ext the extension
	   * @param handler the handler that will receive the result
	   * @return the client
	   */
	FdfsClient upload(String fileFullPathName, String ext, Handler<AsyncResult<FdfsFileId>> handler);
	
	/**
	   * upload a {@code Buffer} object.
	   *
	   * @param buffer the {@code Buffer} object
	   * @param ext the extension
	   * @param handler the handler that will receive the result
	   * @return the client
	   */
	FdfsClient upload(Buffer buffer, String ext, Handler<AsyncResult<FdfsFileId>> handler);
	
	/**
	   * upload a {@code ReadStream<Buffer>} object as appender.
	   *
	   * @param stream the {@code ReadStream<Buffer>} object
	   * @param size the size
	   * @param ext the extension
	   * @param handler the handler that will receive the result
	   * @return the client
	   */
	FdfsClient uploadAppender(ReadStream<Buffer> stream, long size, String ext, Handler<AsyncResult<FdfsFileId>> handler);

	/**
	   * upload a local file as appender.
	   *
	   * @param fileFullPathName full path to the file
	   * @param ext the extension
	   * @param handler the handler that will receive the result
	   * @return the client
	   */
	FdfsClient uploadAppender(String fileFullPathName, String ext, Handler<AsyncResult<FdfsFileId>> handler);
	
	/**
	   * upload a {@code Buffer} object as appender.
	   *
	   * @param buffer the {@code Buffer} object
	   * @param ext the extension
	   * @param handler the handler that will receive the result
	   * @return the client
	   */
	FdfsClient uploadAppender(Buffer buffer, String ext, Handler<AsyncResult<FdfsFileId>> handler);
	
	/**
	   * append a {@code ReadStream<Buffer>} object to a server file.
	   *
	   * @param stream the {@code ReadStream<Buffer>} object
	   * @param size the size
	   * @param fileId file ID
	   * @param handler the handler that will receive the result
	   * @return the client
	   */
	FdfsClient append(ReadStream<Buffer> stream, long size, FdfsFileId fileId, Handler<AsyncResult<Void>> handler);

	/**
	   * append a local file to a server file.
	   *
	   * @param fileFullPathName full path to the file
	   * @param fileId file ID
	   * @param handler the handler that will receive the result
	   * @return the client
	   */
	FdfsClient append(String fileFullPathName, FdfsFileId fileId, Handler<AsyncResult<Void>> handler);
	
	/**
	   * append a {@code Buffer} object to a server file.
	   *
	   * @param buffer the {@code Buffer} object
	   * @param fileId file ID
	   * @param handler the handler that will receive the result
	   * @return the client
	   */
	FdfsClient append(Buffer buffer, FdfsFileId fileId, Handler<AsyncResult<Void>> handler);
	
	/**
	   * modify a server file with a {@code ReadStream<Buffer>}.
	   *
	   * @param stream the {@code ReadStream<Buffer>} object
	   * @param size the size
	   * @param fileId file ID
	   * @param offset the offset
	   * @param handler the handler that will receive the result
	   * @return the client
	   */
	FdfsClient modify(ReadStream<Buffer> stream, long size, FdfsFileId fileId, long offset, Handler<AsyncResult<Void>> handler);

	/**
	   * modify a server file with a local file.
	   *
	   * @param fileFullPathName full path to the file
	   * @param fileId file ID
	   * @param offset the offset
	   * @param handler the handler that will receive the result
	   * @return the client
	   */
	FdfsClient modify(String fileFullPathName, FdfsFileId fileId, long offset, Handler<AsyncResult<Void>> handler);
	
	/**
	   * modify a server file with a {@code Buffer} object.
	   *
	   * @param buffer the {@code Buffer} object
	   * @param fileId file ID
	   * @param offset the offset
	   * @param handler the handler that will receive the result
	   * @return the client
	   */
	FdfsClient modify(Buffer buffer, FdfsFileId fileId, long offset, Handler<AsyncResult<Void>> handler);

	/**
	   * download a server file to a {@code WriteStream<Buffer>} object.
	   *
	   * @param fileId file ID
	   * @param stream the {@code WriteStream<Buffer>} object
	   * @param offset the offset
	   * @param bytes number of bytes
	   * @param handler the handler that will receive the result
	   * @return the client
	   */
	FdfsClient download(FdfsFileId fileId, WriteStream<Buffer> stream, long offset, long bytes, Handler<AsyncResult<Void>> handler);

	/**
	   * download a server file to a local file.
	   *
	   * @param fileId file ID
	   * @param fileFullPathName full path to the local file
	   * @param offset the offset
	   * @param bytes number of bytes
	   * @param handler the handler that will receive the result
	   * @return the client
	   */
	FdfsClient download(FdfsFileId fileId, String fileFullPathName, long offset, long bytes, Handler<AsyncResult<Void>> handler);
	
	/**
	   * download a server file to a {@code WriteStream<Buffer>} object.
	   *
	   * @param fileId file ID
	   * @param offset the offset
	   * @param bytes number of bytes
	   * @param handler the handler that will receive the {@code Buffer} result
	   * @return the client
	   */
	FdfsClient download(FdfsFileId fileId, long offset, long bytes, Handler<AsyncResult<Buffer>> handler);

	/**
	   * set meta data of a server file.
	   *
	   * @param fileId file ID
	   * @param metaData the meta data
	   * @param flag the flag
	   * @param handler the handler that will receive the result
	   * @return the client
	   */
	FdfsClient setMetaData(FdfsFileId fileId, JsonObject metaData, byte flag, Handler<AsyncResult<Void>> handler);

	/**
	   * get meta data of a server file.
	   *
	   * @param fileId file ID
	   * @param handler the handler that will receive the {@code JsonObject} result
	   * @return the client
	   */
	FdfsClient getMetaData(FdfsFileId fileId, Handler<AsyncResult<JsonObject>> handler);

	/**
	   * delete a server file.
	   *
	   * @param fileId file ID
	   * @param handler the handler that will receive the result
	   * @return the client
	   */
	FdfsClient delete(FdfsFileId fileId, Handler<AsyncResult<Void>> handler);

	/**
	   * get file info of a server file.
	   *
	   * @param fileId file ID
	   * @param handler the handler that will receive the {@code FdfsFileInfo} result
	   * @return the client
	   */
	FdfsClient fileInfo(FdfsFileId fileId, Handler<AsyncResult<FdfsFileInfo>> handler);

	/**
	   * get groups of the server.
	   *
	   * @param handler the handler that will receive the {@code List<FdfsGroupInfo>} result
	   * @return the client
	   */
	FdfsClient groups(Handler<AsyncResult<List<FdfsGroupInfo>>> handler);

	/**
	   * get storages of a group.
	   *
	   * @param group the group
	   * @param handler the handler that will receive the {@code List<FdfsStorageInfo>} result
	   * @return the client
	   */
	FdfsClient storages(String group, Handler<AsyncResult<List<FdfsStorageInfo>>> handler);
	
	/**
	   * get a tracker.
	   *
	   * @param handler the handler that will receive the {@code FdfsTracker} result
	   * @return the client
	   */
	FdfsClient getTracker(Handler<AsyncResult<FdfsTracker>> handler);
	
	/**
	   * get the options of this client.
	   *
	   * @return the options
	   */
	FdfsClientOptions getOptions();
	
	/**
	   * close the client
	   */
	void close();
}
