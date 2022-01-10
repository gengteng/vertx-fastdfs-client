package io.vertx.fastdfs.impl;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.fastdfs.FdfsFileId;
import io.vertx.fastdfs.FdfsGroupInfo;
import io.vertx.fastdfs.FdfsStorageInfo;
import io.vertx.fastdfs.options.FdfsTrackerOptions;

/**
 * FastDFS tracker.
 * 
 * @author GengTeng
 * @version 4.2
 */
public interface FdfsTracker {

	/**
	   * get a store storage.
	   *
	   * @param handler the handler that will receive the {@code FdfsStorage} result
	   * @return the tracker
	   */
	FdfsTracker getStoreStorage(Handler<AsyncResult<FdfsStorage>> handler);

	/**
	   * get a store storage of a group.
	   *
	   * @param group the group
	   * @param handler the handler that will receive the {@code FdfsStorage} result
	   * @return the tracker
	   */
	FdfsTracker getStoreStorage(String group, Handler<AsyncResult<FdfsStorage>> handler);

	/**
	   * get a fetch storage of a server file.
	   *
	   * @param fileId the file id
	   * @param handler the handler that will receive the {@code FdfsStorage} result
	   * @return the tracker
	   */
	FdfsTracker getFetchStorage(FdfsFileId fileId, Handler<AsyncResult<FdfsStorage>> handler);

	/**
	   * get a update storage of a server file.
	   *
	   * @param fileId the file id
	   * @param handler the handler that will receive the {@code FdfsStorage} result
	   * @return the tracker
	   */
	FdfsTracker getUpdateStorage(FdfsFileId fileId, Handler<AsyncResult<FdfsStorage>> handler);

	/**
	   * get groups.
	   *
	   * @param handler the handler that will receive the {@code List<FdfsGroupInfo>} result
	   * @return the tracker
	   */
	FdfsTracker groups(Handler<AsyncResult<List<FdfsGroupInfo>>> handler);

	/**
	   * get storages of a group.
	   *
	   * @param group the group
	   * @param handler the handler that will receive the {@code List<FdfsStorageInfo>} result
	   * @return the tracker
	   */
	FdfsTracker storages(String group, Handler<AsyncResult<List<FdfsStorageInfo>>> handler);
	
	/**
	   * get the options of this tracker.
	   *
	   * @return the options
	   */
	FdfsTrackerOptions getOptions();
}
