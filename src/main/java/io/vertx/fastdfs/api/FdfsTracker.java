package io.vertx.fastdfs.api;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;
import io.vertx.fastdfs.FdfsFileId;
import io.vertx.fastdfs.FdfsGroupInfo;
import io.vertx.fastdfs.FdfsStorageInfo;
import io.vertx.fastdfs.impl.FdfsTrackerImpl;
import io.vertx.fastdfs.options.FdfsTrackerOptions;

/**
 * FastDFS File ID.
 * 
 * @author GengTeng
 * @version 3.5.0
 */
public interface FdfsTracker {

	public static FdfsTracker create(Vertx vertx, NetSocket socket, FdfsTrackerOptions options) {
		return new FdfsTrackerImpl(vertx, socket, options);
	}

	FdfsTracker getStoreStorage(Handler<AsyncResult<FdfsStorage>> handler);

	FdfsTracker getStoreStorage(String group, Handler<AsyncResult<FdfsStorage>> handler);

	FdfsTracker getFetchStorage(FdfsFileId fileId, Handler<AsyncResult<FdfsStorage>> handler);

	FdfsTracker getUpdateStorage(FdfsFileId fileId, Handler<AsyncResult<FdfsStorage>> handler);

	FdfsTracker groups(Handler<AsyncResult<List<FdfsGroupInfo>>> handler);

	FdfsTracker storages(String group, Handler<AsyncResult<List<FdfsStorageInfo>>> handler);
}
