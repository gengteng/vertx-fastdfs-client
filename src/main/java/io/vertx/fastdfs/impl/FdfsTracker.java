package io.vertx.fastdfs.impl;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.fastdfs.FdfsFileId;
import io.vertx.fastdfs.FdfsGroupInfo;
import io.vertx.fastdfs.FdfsStorageInfo;

/**
 * FastDFS File ID.
 * 
 * @author GengTeng
 * @version 3.5.0
 */
public interface FdfsTracker {

	public static FdfsTracker create(Vertx vertx, FdfsConnectionPool pool, FdfsTrackerOptions options) {
		return new FdfsTrackerImpl(vertx, pool, options);
	}

	FdfsTracker getStoreStorage(Handler<AsyncResult<FdfsStorage>> handler);

	FdfsTracker getStoreStorage(String group, Handler<AsyncResult<FdfsStorage>> handler);

	FdfsTracker getFetchStorage(FdfsFileId fileId, Handler<AsyncResult<FdfsStorage>> handler);

	FdfsTracker getUpdateStorage(FdfsFileId fileId, Handler<AsyncResult<FdfsStorage>> handler);

	FdfsTracker groups(Handler<AsyncResult<List<FdfsGroupInfo>>> handler);

	FdfsTracker storages(String group, Handler<AsyncResult<List<FdfsStorageInfo>>> handler);
	
	FdfsTrackerOptions getOptions();
}
