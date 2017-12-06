package io.vertx.fastdfs.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.SocketAddress;

public class FdfsConnectionPool {
	
	private final NetClient client;
	private final int poolSize;

	private ConcurrentMap<SocketAddress, FdfsPool> pools;
	
	public FdfsConnectionPool(Vertx vertx, NetClientOptions options, int poolSize) {
		this.client = vertx.createNetClient(options);
		this.poolSize = poolSize;
		
		this.pools = new ConcurrentHashMap<>();
	}

	public synchronized Future<FdfsConnection> get(SocketAddress address) {
		if (pools.containsKey(address)) {
			return pools.get(address).next().get();
		} else {
			FdfsPool pool = new FdfsPool(client, address, poolSize);
			
			pools.put(address, pool);
			
			return pool.next().get();
		}
	}

	public void close() {
		client.close();
		pools.clear();
	}

	public static class FdfsPool {
		private FdfsConnection[] connections;
		private volatile int current;
		private final int capacity;
		
		public FdfsPool(NetClient client, SocketAddress address, int capacity) {
			this.current = 0;
			
			this.connections = new FdfsConnection[capacity];
			
			for (int i=0; i<capacity; ++i) {
				this.connections[i] = new FdfsConnection(client, address);
			}
			
			this.capacity = connections.length;
		}
		
		public int getAndIncrease() {
			int cur = current;
			current = (current + 1) % capacity;
			return cur;
		}
		
		public FdfsConnection next() {
			return this.connections[getAndIncrease()];
		}
	}
}
