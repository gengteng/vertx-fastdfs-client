package io.vertx.fastdfs.impl;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import io.vertx.fastdfs.FdfsClient;
import io.vertx.fastdfs.FdfsClientOptions;
import io.vertx.fastdfs.FdfsFileId;
import io.vertx.fastdfs.FdfsFileInfo;
import io.vertx.fastdfs.FdfsGroupInfo;
import io.vertx.fastdfs.FdfsStorageInfo;
import io.vertx.fastdfs.api.FdfsTracker;
import io.vertx.fastdfs.options.FdfsTrackerOptions;

/**
 * 
 * @author GengTeng
 * <p>
 * me@gteng.org
 * 
 * @version 3.5.0
 */
public class FdfsClientImpl implements FdfsClient {

	private Vertx vertx;
	private FdfsClientOptions options;
	private int currentTrackerIndex;
	private final int trackerCount;

	public FdfsClientImpl(Vertx vertx, FdfsClientOptions options) {
		this.vertx = vertx;
		this.options = options;
		this.currentTrackerIndex = 0;
		this.trackerCount = options.getTrackers().size();
	}

	public FdfsClientImpl(Vertx vertx, JsonObject options) {
		this.vertx = vertx;
		this.options = new FdfsClientOptions().fromJson(options);
		this.currentTrackerIndex = 0;
		this.trackerCount = this.options.getTrackers().size();
	}

	public FdfsClientOptions options() {
		return options;
	}

	@Override
	public FdfsClient upload(ReadStream<Buffer> stream, long size, String ext,
			Handler<AsyncResult<FdfsFileId>> handler) {
		getTracker().setHandler(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(storage -> {
					if (storage.succeeded()) {
						storage.result().upload(stream, size, ext, handler);
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
		getTracker().setHandler(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(storage -> {
					if (storage.succeeded()) {
						storage.result().upload(fileFullPathName, ext, handler);
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
		getTracker().setHandler(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(storage -> {
					if (storage.succeeded()) {
						storage.result().uploadAppender(stream, size, ext, handler);
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
		getTracker().setHandler(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(storage -> {
					if (storage.succeeded()) {
						storage.result().uploadAppender(fileFullPathName, ext, handler);
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
		getTracker().setHandler(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(fileId.group(), storage -> {
					if (storage.succeeded()) {
						storage.result().append(stream, size, fileId, handler);
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
		getTracker().setHandler(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(fileId.group(), storage -> {
					if (storage.succeeded()) {
						storage.result().append(fileFullPathName, fileId, handler);
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
		getTracker().setHandler(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(fileId.group(), storage -> {
					if (storage.succeeded()) {
						storage.result().modify(stream, size, fileId, offset, handler);
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
		getTracker().setHandler(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getStoreStorage(fileId.group(), storage -> {
					if (storage.succeeded()) {
						storage.result().modify(fileFullPathName, fileId, offset, handler);
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
		getTracker().setHandler(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getFetchStorage(fileId, storage -> {
					if (storage.succeeded()) {
						storage.result().download(fileId, stream, offset, bytes, handler);
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
		getTracker().setHandler(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getFetchStorage(fileId, storage -> {
					if (storage.succeeded()) {
						storage.result().download(fileId, fileFullPathName, offset, bytes, handler);
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
		getTracker().setHandler(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getUpdateStorage(fileId, storage -> {
					if (storage.succeeded()) {
						storage.result().setMetaData(fileId, metaData, flag, handler);
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
		getTracker().setHandler(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getUpdateStorage(fileId, storage -> {
					if (storage.succeeded()) {
						storage.result().getMetaData(fileId, handler);
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
		getTracker().setHandler(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getUpdateStorage(fileId, storage -> {
					if (storage.succeeded()) {
						storage.result().delete(fileId, handler);
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
		getTracker().setHandler(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().getUpdateStorage(fileId, storage -> {
					if (storage.succeeded()) {
						storage.result().fileInfo(fileId, handler);
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
		getTracker().setHandler(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().groups(handler);
			} else {
				handler.handle(Future.failedFuture(tracker.cause()));
			}
		});
		return this;
	}

	@Override
	public FdfsClient storages(String group, Handler<AsyncResult<List<FdfsStorageInfo>>> handler) {
		getTracker().setHandler(tracker -> {
			if (tracker.succeeded()) {
				tracker.result().storages(group, handler);
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
		Future<FdfsTracker> futureTracker = Future.future();

		FdfsTrackerOptions trackerOptions = getTrackerOptions(startIndex);

		getConnectedTracker(trackerOptions).setHandler(connResult -> {
			if (connResult.succeeded()) {
				this.currentTrackerIndex = startIndex;
				futureTracker.complete(connResult.result());
			} else {
				if (startIndex == lastIndex) {
					futureTracker.fail(connResult.cause());
				} else {
					getTracker((startIndex + 1) % trackerCount, lastIndex).setHandler(futureTracker);
				}
			}
		});

		return futureTracker;
	}

	private FdfsTrackerOptions getTrackerOptions(int index) {
		return new FdfsTrackerOptions().setAddress(this.options.getTrackers().get(index))
				.setCharset(this.options.getCharset()).setConnectTimeout(this.options.getConnectTimeout());
	}

	private Future<FdfsTracker> getConnectedTracker(FdfsTrackerOptions trackerOptions) {
		return Future.future(future -> {
			vertx.createNetClient(new NetClientOptions().setIdleTimeout((int) trackerOptions.getNetworkTimeout())
					.setConnectTimeout((int) trackerOptions.getConnectTimeout()))
					.connect(trackerOptions.getAddress(), ar -> {
						if (ar.succeeded()) {
							future.complete(FdfsTracker.create(vertx, ar.result(), trackerOptions));
						} else {
							future.fail(ar.cause());
						}
					});
		});
	}
}
