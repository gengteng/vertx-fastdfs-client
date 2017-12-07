package io.vertx.fastdfs.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import io.vertx.fastdfs.FdfsFileId;
import io.vertx.fastdfs.FdfsFileInfo;

/**
 * FastDFS storage.
 * 
 * @author GengTeng
 * @version 3.5.0
 */
public interface FdfsStorage {

	/**
	   * upload a {@code ReadStream<Buffer>} object.
	   *
	   * @param stream the {@code ReadStream<Buffer>} object
	   * @param size the size
	   * @param ext the extension
	   * @param handler the handler that will receive the result
	   * @return the storage
	   */
	FdfsStorage upload(ReadStream<Buffer> stream, long size, String ext, Handler<AsyncResult<FdfsFileId>> handler);

	/**
	   * upload a local file.
	   *
	   * @param fileFullPathName full path to the file
	   * @param ext the extension
	   * @param handler the handler that will receive the result
	   * @return the storage
	   */
	FdfsStorage upload(String fileFullPathName, String ext, Handler<AsyncResult<FdfsFileId>> handler);
	
	/**
	   * upload a {@code Buffer} object.
	   *
	   * @param buffer the {@code Buffer} object
	   * @param ext the extension
	   * @param handler the handler that will receive the result
	   * @return the storage
	   */
	FdfsStorage upload(Buffer buffer, String ext, Handler<AsyncResult<FdfsFileId>> handler);
	
	/**
	   * upload a {@code ReadStream<Buffer>} object as appender.
	   *
	   * @param stream the {@code ReadStream<Buffer>} object
	   * @param size the size
	   * @param ext the extension
	   * @param handler the handler that will receive the result
	   * @return the storage
	   */
	FdfsStorage uploadAppender(ReadStream<Buffer> stream, long size, String ext, Handler<AsyncResult<FdfsFileId>> handler);

	/**
	   * upload a local file as appender.
	   *
	   * @param fileFullPathName full path to the file
	   * @param ext the extension
	   * @param handler the handler that will receive the result
	   * @return the storage
	   */
	FdfsStorage uploadAppender(String fileFullPathName, String ext, Handler<AsyncResult<FdfsFileId>> handler);
	
	/**
	   * upload a {@code Buffer} object as appender.
	   *
	   * @param buffer the {@code Buffer} object
	   * @param ext the extension
	   * @param handler the handler that will receive the result
	   * @return the storage
	   */
	FdfsStorage uploadAppender(Buffer buffer, String ext, Handler<AsyncResult<FdfsFileId>> handler);
	
	/**
	   * append a {@code ReadStream<Buffer>} object to a server file.
	   *
	   * @param stream the {@code ReadStream<Buffer>} object
	   * @param size the size
	   * @param fileId file ID
	   * @param handler the handler that will receive the result
	   * @return the storage
	   */
	FdfsStorage append(ReadStream<Buffer> stream, long size, FdfsFileId fileId, Handler<AsyncResult<Void>> handler);

	/**
	   * append a local file to a server file.
	   *
	   * @param fileFullPathName full path to the file
	   * @param fileId file ID
	   * @param handler the handler that will receive the result
	   * @return the storage
	   */
	FdfsStorage append(String fileFullPathName, FdfsFileId fileId, Handler<AsyncResult<Void>> handler);
	
	/**
	   * append a {@code Buffer} object to a server file.
	   *
	   * @param buffer the {@code Buffer} object
	   * @param fileId file ID
	   * @param handler the handler that will receive the result
	   * @return the storage
	   */
	FdfsStorage append(Buffer buffer, FdfsFileId fileId, Handler<AsyncResult<Void>> handler);
	
	/**
	   * modify a server file with a {@code ReadStream<Buffer>}.
	   *
	   * @param stream the {@code ReadStream<Buffer>} object
	   * @param fileId file ID
	   * @param handler the handler that will receive the result
	   * @return the storage
	   */
	FdfsStorage modify(ReadStream<Buffer> stream, long size, FdfsFileId fileId, long offset, Handler<AsyncResult<Void>> handler);
	
	/**
	   * modify a server file with a local file.
	   *
	   * @param fileFullPathName full path to the file
	   * @param fileId file ID
	   * @param handler the handler that will receive the result
	   * @return the storage
	   */
	FdfsStorage modify(String fileFullPathName, FdfsFileId fileId, long offset, Handler<AsyncResult<Void>> handler);
	
	/**
	   * modify a server file with a {@code Buffer} object.
	   *
	   * @param buffer the {@code Buffer} object
	   * @param fileId file ID
	   * @param handler the handler that will receive the result
	   * @return the storage
	   */
	FdfsStorage modify(Buffer buffer, FdfsFileId fileId, long offset, Handler<AsyncResult<Void>> handler);

	/**
	   * download a server file to a {@code WriteStream<Buffer>} object.
	   *
	   * @param fileId file ID
	   * @param stream the {@code WriteStream<Buffer>} object
	   * @param offset the offset
	   * @param bytes number of bytes
	   * @param handler the handler that will receive the result
	   * @return the storage
	   */
	FdfsStorage download(FdfsFileId fileId, WriteStream<Buffer> stream, long offset, long bytes, Handler<AsyncResult<Void>> handler);

	/**
	   * download a server file to a local file.
	   *
	   * @param fileId file ID
	   * @param stream full path to the local file
	   * @param offset the offset
	   * @param bytes number of bytes
	   * @param handler the handler that will receive the result
	   * @return the storage
	   */
	FdfsStorage download(FdfsFileId fileId, String fileFullPathName, long offset, long bytes, Handler<AsyncResult<Void>> handler);
	
	/**
	   * download a server file to a {@code WriteStream<Buffer>} object.
	   *
	   * @param fileId file ID
	   * @param offset the offset
	   * @param bytes number of bytes
	   * @param handler the handler that will receive the {@code Buffer} result
	   * @return the storage
	   */
	FdfsStorage download(FdfsFileId fileId, long offset, long bytes, Handler<AsyncResult<Buffer>> handler);

	/**
	   * set meta data of a server file.
	   *
	   * @param fileId file ID
	   * @param metaData the meta data
	   * @param flag the flag
	   * @param handler the handler that will receive the result
	   * @return the storage
	   */
	FdfsStorage setMetaData(FdfsFileId fileId, JsonObject metaData, byte flag, Handler<AsyncResult<Void>> handler);

	/**
	   * set meta data of a server file.
	   *
	   * @param fileId file ID
	   * @param metaData the meta data
	   * @param handler the handler that will receive the result
	   * @return the storage
	   */
	FdfsStorage setMetaData(FdfsFileId fileId, JsonObject metaData, Handler<AsyncResult<Void>> handler);

	/**
	   * get meta data of a server file.
	   *
	   * @param fileId file ID
	   * @param handler the handler that will receive the {@code JsonObject} result
	   * @return the storage
	   */
	FdfsStorage getMetaData(FdfsFileId fileId, Handler<AsyncResult<JsonObject>> handler);

	/**
	   * delete a server file.
	   *
	   * @param fileId file ID
	   * @param handler the handler that will receive the result
	   * @return the storage
	   */
	FdfsStorage delete(FdfsFileId fileId, Handler<AsyncResult<Void>> handler);

	/**
	   * get file info of a server file.
	   *
	   * @param fileId file ID
	   * @param handler the handler that will receive the {@code FdfsFileInfo} result
	   * @return the storage
	   */
	FdfsStorage fileInfo(FdfsFileId fileId, Handler<AsyncResult<FdfsFileInfo>> handler);
	
	/**
	   * get the options of this storage.
	   *
	   * @return the options
	   */
	FdfsStorageOptions getOptions();
}
