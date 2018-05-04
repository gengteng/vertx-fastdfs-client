package io.vertx.fastdfs.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.Shareable;

/**
 * FastDFS Connection Pool.
 * 
 * @author GengTeng
 * <p>
 * me@gteng.org
 * 
 * @version 3.5.0
 */
public class FdfsConnectionPool implements Shareable {
	
	private final Vertx vertx;
	private final NetClient client;
	private final int poolSize;
	private int refCount = 1;

	private ConcurrentMap<SocketAddress, CircularConnectionPool> pools;
	
	public FdfsConnectionPool(Vertx vertx, NetClientOptions options, int poolSize, LocalMap<String, FdfsConnectionPool> map, String poolName) {
		this.vertx = vertx;
		this.client = vertx.createNetClient(options);
		this.poolSize = poolSize;
		
		this.pools = new ConcurrentHashMap<>();
		
		map.put(poolName, this);
	}

	public synchronized Future<FdfsConnection> get(SocketAddress address) {
		if (pools.containsKey(address)) {
			return pools.get(address).next().get();
		} else {
			CircularConnectionPool pool = new CircularConnectionPool(client, address, poolSize);
			
			pools.put(address, pool);
			
			return pool.next().get();
		}
	}
	
	public void incRefCount() {
		++refCount;
	}

	public void close() {
		close(null);
	}
	
	public void close(Handler<AsyncResult<Void>> completeHandler) {
		synchronized (vertx) {
			--refCount;
			
			if (refCount == 0) {
				pools.clear();
				client.close();
				
				if (completeHandler != null) {
					completeHandler.handle(null);
				}
			}
		}
	}

	public static class CircularConnectionPool {
		private FdfsConnection[] connections;
		private AtomicInteger current;
		private final int capacity;
		
		public CircularConnectionPool(NetClient client, SocketAddress address, int capacity) {
			this.current = new AtomicInteger(0);
			
			this.connections = new FdfsConnection[capacity];
			
			for (int i=0; i<capacity; ++i) {
				this.connections[i] = new FdfsConnection(client, address);
			}
			
			this.capacity = connections.length;
		}
		
		private int getAndIncrement() {
			return current.getAndUpdate(cur -> (cur + 1) % capacity);
		}
		
		public FdfsConnection next() {
			return this.connections[getAndIncrement()];
		}
	}
}
