package io.vertx.fastdfs.impl;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import io.vertx.fastdfs.FdfsClient;
import io.vertx.fastdfs.FdfsClientOptions;
import io.vertx.fastdfs.FdfsFileId;
import io.vertx.fastdfs.FdfsFileInfo;
import io.vertx.fastdfs.FdfsGroupInfo;
import io.vertx.fastdfs.FdfsStorageInfo;
import io.vertx.fastdfs.options.FdfsTrackerOptions;
import io.vertx.fastdfs.utils.FdfsProtocol;

/**
 * 
 * @author GengTeng
 *         <p>
 *         me@gteng.org
 * 
 * @version 4.2
 */
public class FdfsClientImpl implements FdfsClient {

	private static final String POOL_LOCAL_MAP_NAME = "__vertx.FastDFS.pool";

	private Vertx vertx;
	private FdfsConnectionPool pool;
	private FdfsClientOptions options;
	private int currentTrackerIndex;
	private final int trackerCount;
	private final String poolName;
	private LocalMap<String, FdfsConnectionPool> map;

	public FdfsClientImpl(Vertx vertx, FdfsClientOptions options, String poolName) {
		this.vertx = vertx;
		this.options = options;
		this.poolName = poolName;
		this.pool = lookUpSharedPool(poolName);
		this.currentTrackerIndex = 0;
		this.trackerCount = options.getTrackers().size();
		setupCloseHook();
	}

	public FdfsClientImpl(Vertx vertx, JsonObject options, String poolName) {
		this(vertx, new FdfsClientOptions().fromJson(options), poolName);
	}

	public FdfsClientOptions options() {
		return options;
	}

	@Override
	public FdfsClient upload(ReadStream<Buffer> stream, long size, String ext,
			Handler<AsyncResult<FdfsFileId>> handler) {

		if (Buffer.buffer(ext, options.getCharset()).length() > FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN) {
			handler.handle(Future
					.failedFuture("ext is too long ( greater than " + FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN + ")"));
			return this;
		}

		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(storage -> {
					if (storage.succeeded()) {
						storage.result().upload(stream, size, ext, upload -> {
							handler.handle(upload);
						});
					} else {
						handler.handle(Future.failedFuture(storage.cause()));
					}
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsClient upload(String fileFullPathName, String ext, Handler<AsyncResult<FdfsFileId>> handler) {

		if (Buffer.buffer(ext, options.getCharset()).length() > FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN) {
			handler.handle(Future
					.failedFuture("ext is too long ( greater than " + FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN + ")"));
			return this;
		}

		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(storage -> {
					if (storage.succeeded()) {
						storage.result().upload(fileFullPathName, ext, upload -> {
							handler.handle(upload);
						});
					} else {
						handler.handle(Future.failedFuture(storage.cause()));
					}
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsClient upload(Buffer buffer, String ext, Handler<AsyncResult<FdfsFileId>> handler) {

		if (Buffer.buffer(ext, options.getCharset()).length() > FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN) {
			handler.handle(Future
					.failedFuture("ext is too long ( greater than " + FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN + ")"));
			return this;
		}

		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(storage -> {
					if (storage.succeeded()) {
						storage.result().upload(buffer, ext, upload -> {
							handler.handle(upload);
						});
					} else {
						handler.handle(Future.failedFuture(storage.cause()));
					}
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsClient uploadAppender(ReadStream<Buffer> stream, long size, String ext,
			Handler<AsyncResult<FdfsFileId>> handler) {

		if (Buffer.buffer(ext, options.getCharset()).length() > FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN) {
			handler.handle(Future
					.failedFuture("ext is too long ( greater than " + FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN + ")"));
			return this;
		}

		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(storage -> {
					if (storage.succeeded()) {
						storage.result().uploadAppender(stream, size, ext, uploadAppender -> {
							handler.handle(uploadAppender);
						});
					} else {
						handler.handle(Future.failedFuture(storage.cause()));
					}
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsClient uploadAppender(String fileFullPathName, String ext, Handler<AsyncResult<FdfsFileId>> handler) {

		if (Buffer.buffer(ext, options.getCharset()).length() > FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN) {
			handler.handle(Future
					.failedFuture("ext is too long ( greater than " + FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN + ")"));
			return this;
		}

		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(storage -> {
					if (storage.succeeded()) {
						storage.result().uploadAppender(fileFullPathName, ext, uploadAppender -> {
							handler.handle(uploadAppender);
						});
					} else {
						handler.handle(Future.failedFuture(storage.cause()));
					}
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsClient uploadAppender(Buffer buffer, String ext, Handler<AsyncResult<FdfsFileId>> handler) {

		if (Buffer.buffer(ext, options.getCharset()).length() > FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN) {
			handler.handle(Future
					.failedFuture("ext is too long ( greater than " + FdfsProtocol.FDFS_FILE_EXT_NAME_MAX_LEN + ")"));
			return this;
		}

		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(storage -> {
					if (storage.succeeded()) {
						storage.result().uploadAppender(buffer, ext, uploadAppender -> {
							handler.handle(uploadAppender);
						});
					} else {
						handler.handle(Future.failedFuture(storage.cause()));
					}
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsClient append(ReadStream<Buffer> stream, long size, FdfsFileId fileId,
			Handler<AsyncResult<Void>> handler) {
		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(fileId.group(), storage -> {
					if (storage.succeeded()) {
						storage.result().append(stream, size, fileId, append -> {
							handler.handle(append);
						});
					} else {
						handler.handle(Future.failedFuture(storage.cause()));
					}
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsClient append(String fileFullPathName, FdfsFileId fileId, Handler<AsyncResult<Void>> handler) {
		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(fileId.group(), storage -> {
					if (storage.succeeded()) {
						storage.result().append(fileFullPathName, fileId, append -> {
							handler.handle(append);
						});
					} else {
						handler.handle(Future.failedFuture(storage.cause()));
					}
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsClient append(Buffer buffer, FdfsFileId fileId, Handler<AsyncResult<Void>> handler) {
		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(fileId.group(), storage -> {
					if (storage.succeeded()) {
						storage.result().append(buffer, fileId, append -> {
							handler.handle(append);
						});
					} else {
						handler.handle(Future.failedFuture(storage.cause()));
					}
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsClient modify(ReadStream<Buffer> stream, long size, FdfsFileId fileId, long offset,
			Handler<AsyncResult<Void>> handler) {
		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(fileId.group(), storage -> {
					if (storage.succeeded()) {
						storage.result().modify(stream, size, fileId, offset, modify -> {
							handler.handle(modify);
						});
					} else {
						handler.handle(Future.failedFuture(storage.cause()));
					}
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsClient modify(String fileFullPathName, FdfsFileId fileId, long offset,
			Handler<AsyncResult<Void>> handler) {
		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(fileId.group(), storage -> {
					if (storage.succeeded()) {
						storage.result().modify(fileFullPathName, fileId, offset, modify -> {
							handler.handle(modify);
						});
					} else {
						handler.handle(Future.failedFuture(storage.cause()));
					}
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsClient modify(Buffer buffer, FdfsFileId fileId, long offset, Handler<AsyncResult<Void>> handler) {
		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(fileId.group(), storage -> {
					if (storage.succeeded()) {
						storage.result().modify(buffer, fileId, offset, modify -> {
							handler.handle(modify);
						});
					} else {
						handler.handle(Future.failedFuture(storage.cause()));
					}
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsClient download(FdfsFileId fileId, WriteStream<Buffer> stream, long offset, long bytes,
			Handler<AsyncResult<Void>> handler) {
		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getFetchStorage(fileId, storage -> {
					if (storage.succeeded()) {
						storage.result().download(fileId, stream, offset, bytes, download -> {
							handler.handle(download);
						});
					} else {
						handler.handle(Future.failedFuture(storage.cause()));
					}
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsClient download(FdfsFileId fileId, String fileFullPathName, long offset, long bytes,
			Handler<AsyncResult<Void>> handler) {
		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getFetchStorage(fileId, storage -> {
					if (storage.succeeded()) {
						storage.result().download(fileId, fileFullPathName, offset, bytes, download -> {
							handler.handle(download);
						});
					} else {
						handler.handle(Future.failedFuture(storage.cause()));
					}
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsClient download(FdfsFileId fileId, long offset, long bytes, Handler<AsyncResult<Buffer>> handler) {
		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getFetchStorage(fileId, storage -> {
					if (storage.succeeded()) {
						storage.result().download(fileId, offset, bytes, download -> {
							handler.handle(download);
						});
					} else {
						handler.handle(Future.failedFuture(storage.cause()));
					}
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});

		return this;
	}

	@Override
	public FdfsClient setMetaData(FdfsFileId fileId, JsonObject metaData, byte flag,
			Handler<AsyncResult<Void>> handler) {
		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getUpdateStorage(fileId, storage -> {
					if (storage.succeeded()) {
						storage.result().setMetaData(fileId, metaData, flag, setMetaData -> {
							handler.handle(setMetaData);
						});
					} else {
						handler.handle(Future.failedFuture(storage.cause()));
					}
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});
		return this;
	}

	@Override
	public FdfsClient getMetaData(FdfsFileId fileId, Handler<AsyncResult<JsonObject>> handler) {
		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getUpdateStorage(fileId, storage -> {
					if (storage.succeeded()) {
						storage.result().getMetaData(fileId, getMetaData -> {
							handler.handle(getMetaData);
						});
					} else {
						handler.handle(Future.failedFuture(storage.cause()));
					}
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});
		return this;
	}

	@Override
	public FdfsClient delete(FdfsFileId fileId, Handler<AsyncResult<Void>> handler) {
		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getUpdateStorage(fileId, storage -> {
					if (storage.succeeded()) {
						storage.result().delete(fileId, delete -> {
							handler.handle(delete);
						});
					} else {
						handler.handle(Future.failedFuture(storage.cause()));
					}
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});
		return this;
	}

	@Override
	public FdfsClient fileInfo(FdfsFileId fileId, Handler<AsyncResult<FdfsFileInfo>> handler) {
		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getUpdateStorage(fileId, storage -> {
					if (storage.succeeded()) {
						storage.result().fileInfo(fileId, fileInfo -> {
							handler.handle(fileInfo);
						});
					} else {
						handler.handle(Future.failedFuture(storage.cause()));
					}
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});
		return this;
	}

	@Override
	public FdfsClient groups(Handler<AsyncResult<List<FdfsGroupInfo>>> handler) {
		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().groups(groups -> {

					handler.handle(groups);
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});
		return this;
	}

	@Override
	public FdfsClient storages(String group, Handler<AsyncResult<List<FdfsStorageInfo>>> handler) {
		getTracker().onComplete(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().storages(group, storages -> {
					handler.handle(storages);
				});
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});
		return this;
	}

	private synchronized Future<FdfsTracker> getTracker() {
		this.currentTrackerIndex = (this.currentTrackerIndex + 1) % trackerCount;
		return getTracker(this.currentTrackerIndex, (this.currentTrackerIndex + trackerCount - 1) % trackerCount);
	}

	private Future<FdfsTracker> getTracker(int startIndex, int lastIndex) {

		Promise<FdfsTracker> promiseTracker = Promise.promise();

		FdfsTrackerOptions trackerOptions = getTrackerOptions(startIndex);

		createTracker(trackerOptions).onComplete(connResult -> {
			if (connResult.succeeded()) {
				this.currentTrackerIndex = startIndex;
				promiseTracker.complete(connResult.result());
			} else {
				if (startIndex == lastIndex) {
					promiseTracker.fail(connResult.cause());
				} else {
					getTracker((startIndex + 1) % trackerCount, lastIndex).onComplete(promiseTracker);
				}
			}
		});

		return promiseTracker.future();
	}

	private FdfsTrackerOptions getTrackerOptions(int index) {
		return new FdfsTrackerOptions(options).setAddress(this.options.getTrackers().get(index));
	}

	private Future<FdfsTracker> createTracker(FdfsTrackerOptions trackerOptions) {
		return Future.succeededFuture(new FdfsTrackerImpl(vertx, pool, trackerOptions));
	}

	private FdfsConnectionPool lookUpSharedPool(String poolName) {
		synchronized (vertx) {
			map = vertx.sharedData().getLocalMap(POOL_LOCAL_MAP_NAME);
			FdfsConnectionPool pool = map.get(poolName);
			if (pool == null) {
				pool = new FdfsConnectionPool(vertx,
						new NetClientOptions().setConnectTimeout((int) options.getConnectTimeout()),
						options.getPoolSize(), map, poolName);
			} else {
				pool.incRefCount();
			}
			return pool;
		}
	}

	@Override
	public FdfsClient getTracker(Handler<AsyncResult<FdfsTracker>> handler) {

		getTracker().onComplete(handler);

		return this;
	}

	@Override
	public FdfsClientOptions getOptions() {
		return options;
	}

	@Override
	public void close() {
		close(null);
	}
	
	@Override
	public void close(Handler<AsyncResult<Void>> completeHandler) {
		pool.close();

		if (map != null) {
			map.remove(poolName);
		}
		
		if (completeHandler != null) {
			completeHandler.handle(null);
		}
	}

	private void setupCloseHook() {
		Context ctx = Vertx.currentContext();
		if (ctx != null && ctx.owner() == vertx) {
			// ctx.addCloseHook(this::close);
			// TODO::
			// close();
		}
	}
}
