package io.vertx.fastdfs.impl;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;

public class FdfsConnection implements ReadStream<Buffer>, WriteStream<Buffer> {

	private final NetClient client;
	private final SocketAddress address;

	private enum State {
		/**
		 * The connection is not active. The is a stop state.
		 */
		DISCONNECTED,
		/**
		 * The connection is in transit, from here it can become connected or and error
		 * can occur.
		 */
		CONNECTING,
		/**
		 * Connection is active from here it can become an error or disconnected.
		 */
		CONNECTED,

		/**
		 * Reserved.
		 */
		RESERVED;
	}

	private final Queue<Promise<FdfsConnection>> pending = new LinkedList<>();
	private final AtomicReference<State> state = new AtomicReference<>(State.DISCONNECTED);

	private volatile NetSocket socket;

	/**
	 * Create a RedisConnection.
	 * @param client a NetClient instance
	 * @param address the address to connect
	 */
	public FdfsConnection(NetClient client, SocketAddress address) {
		this.client = client;
		this.address = address;
	}

	public Future<FdfsConnection> get() {

		Promise<FdfsConnection> promise = Promise.promise();

		switch (state.get()) {
		case RESERVED:
			pending.add(promise);
			break;
		case CONNECTING:
			pending.add(promise);
			break;
		case CONNECTED:
			if (this.state.compareAndSet(State.CONNECTED, State.RESERVED)) {
				promise.complete(this);
			} else {
				pending.add(promise);
			}
			break;
		case DISCONNECTED:
			connect(ar -> {
				if (ar.succeeded()) {
					if (this.state.get() == State.CONNECTED) {
						completePromise(promise);
					} else {
						pending.add(promise);
					}
				} else {
					promise.fail(ar.cause());
				}
			});
			break;
		default:
			break;
		}

		return promise.future();
	}
	
	public SocketAddress address() {
		return address;
	}
	
	public void release() {
		if (!this.state.compareAndSet(State.RESERVED, State.CONNECTED)) {
			switch(this.state.get()) {
			case CONNECTING:
				break;
			case CONNECTED:
				removePending();
				break;
			case DISCONNECTED:
				connect(ar -> {
					if (ar.succeeded()) {
						removePending();
					} else {
						cleanPending(ar.cause());
					}
				});
			default:
				break;
			}
		} else {
			removePending();
		}
	}
	
	private void removePending() {
		Promise<FdfsConnection> promise = null;

		if ((promise = pending.poll()) != null) {
			completePromise(promise);
		}
	}
	
	private void cleanPending(Throwable e) {
		Promise<FdfsConnection> promise = null;

		while ((promise = pending.poll()) != null) {
			promise.fail(e);
		}
	}

	private void completePromise(Promise<FdfsConnection> promise) {
		this.state.set(State.RESERVED);
		promise.complete(this);
	}
	
	private FdfsConnection connect(Handler<AsyncResult<FdfsConnection>> handler) {
		
		if (this.state.compareAndSet(State.DISCONNECTED, State.CONNECTING)) {
			client.connect(address, ar -> {
				if (ar.succeeded()) {
					this.socket = ar.result().closeHandler(v -> {
						this.state.set(State.DISCONNECTED);
					});
					
					this.state.set(State.CONNECTED);
					
					if (handler != null) {
						handler.handle(Future.succeededFuture(this));
					}
					
	 			} else {
	 				if (handler != null) {
						handler.handle(Future.failedFuture(ar.cause()));
					}
				}
			});
		} else {
			if (handler != null) {
				handler.handle(Future.succeededFuture(this));
			}
		}
		
		return this;
	}
	
	@Override
	public FdfsConnection exceptionHandler(Handler<Throwable> handler) {
		socket.exceptionHandler(handler);
		return this;
	}

	@Override
	public FdfsConnection handler(Handler<Buffer> handler) {
		socket.handler(handler);
		return this;
	}

	@Override
	public FdfsConnection pause() {
		socket.pause();
		return this;
	}

	@Override
	public FdfsConnection resume() {
		socket.resume();
		return this;
	}

	@Override
	public Future<Void> write(Buffer buffer) {
		socket.write(buffer);
		return null;
	}

	@Override
	public FdfsConnection drainHandler(Handler<Void> handler) {
		socket.drainHandler(handler);
		return this;
	}

	/**
	 * Write a {@link String} to the connection, encoded in UTF-8.
	 *
	 * @param str
	 *            the string to write
	 * @return a reference to this, so the API can be used fluently
	 */
	public FdfsConnection write(String str) {
		socket.write(str);
		return this;
	}

	/**
	 * Write a {@link String} to the connection, encoded using the encoding
	 * {@code enc}.
	 *
	 * @param str
	 *            the string to write
	 * @param enc
	 *            the encoding to use
	 * @return a reference to this, so the API can be used fluently
	 */
	public FdfsConnection write(String str, String enc) {
		socket.write(str, enc);
		return this;
	}

	public SocketAddress remoteAddress() {
		return socket.remoteAddress();
	}

	/**
	 * @return the local address for this socket
	 */

	public SocketAddress localAddress() {
		return socket.localAddress();
	}

	/**
	 * Calls {@link #close()}
	 */
	@Override
	public Future<Void> end() {
		close();
		return null;
	}

	/**
	 * Close the NetSocket
	 */
	public void close() {
		this.state.set(State.DISCONNECTED);
		socket.close();
	}

	/**
	 * Set a handler that will be called when the NetSocket is closed
	 *
	 * @param handler
	 *            the handler
	 * @return a reference to this, so the API can be used fluently
	 */
	public FdfsConnection closeHandler(Handler<Void> handler) {
		socket.closeHandler(v -> {
			this.state.set(State.DISCONNECTED);
			if (handler != null) {
				handler.handle(v);
			}
		});
		return this;
	}
	
	@Override
	public boolean writeQueueFull() {
		return socket.writeQueueFull();
	}
	
	@Override
	public FdfsConnection endHandler(Handler<Void> handler) {
		socket.endHandler(v -> {
			this.state.set(State.DISCONNECTED);
			if (handler != null) {
				handler.handle(v);
			}
		});
		
		return this;
	}

	@Override
	public FdfsConnection setWriteQueueMaxSize(int maxSize) {
		socket.setWriteQueueMaxSize(maxSize);
		return this;
	}

	////
	@Override
	public ReadStream<Buffer> fetch(long l) {
		return null;
	}

	@Override
	public void write(Buffer buffer, Handler<AsyncResult<Void>> handler) {

	}

	@Override
	public Future<Void> end(Buffer data) {
		return null;
	}

	@Override
	public void end(Handler<AsyncResult<Void>> handler) {

	}

	@Override
	public void end(Buffer data, Handler<AsyncResult<Void>> handler) {

	}

}
