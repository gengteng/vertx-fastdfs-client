package io.vertx.fastdfs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SocketAddress;
import io.vertx.fastdfs.options.AbstractFdfsOptions;

/**
 * This object controls the connection setting to the FastDFS Server. There is no need to specify most of the settings
 * since it has built the following sensible defaults:
 * <p>
 * * `charset`: `UTF-8`
 * * `connectTimeout`: 10000
 * * `networkTimeout`: 10000
 * * `poolSize`: 15
 * * `trackers` : [
 * * ]
 * <p>
 * @author GengTeng
 *         <p>
 *         me@gteng.org
 * 
 * @version 3.5.0
 */
public class FdfsClientOptions extends AbstractFdfsOptions {
	
	private static final JsonObject DEFAULT_CONFIG = new JsonObject()
			.put(FdfsClientOptions.CHARSET, FdfsClientOptions.DEFAULT_CHARSET)
			.put(FdfsClientOptions.CONNECT_TIMEOUT, FdfsClientOptions.DEFAULT_CONNECT_TIMEOUT)
			.put(FdfsClientOptions.NETWORK_TIMEOUT, FdfsClientOptions.DEFAULT_NETWORK_TIMEOUT)
			.put(FdfsClientOptions.POOLSIZE, FdfsClientOptions.DEFAULT_POOLSIZE)
			.put(FdfsClientOptions.DEFAULT_EXT, FdfsClientOptions.DEFAULT_DEFAULT_EXT).put(FdfsClientOptions.TRACKERS,
					new JsonArray().add(new JsonObject().put(FdfsClientOptions.HOST, FdfsClientOptions.DEFAULT_HOST)
							.put(FdfsClientOptions.PORT, FdfsClientOptions.DEFAULT_PORT)));

	public static final String TRACKERS = "trackers";
	public static final String HOST = "host";
	public static final String PORT = "port";

	public static final String DEFAULT_HOST = "127.0.0.1";
	public static final int DEFAULT_PORT = 22122;

	private List<SocketAddress> trackers;

	/**
	   * Default constructor
	   */
	public FdfsClientOptions() {
		super();
		trackers = new ArrayList<>();
	}
	
	/**
	   * Default constructor
	   */
	public FdfsClientOptions(JsonObject json) {
		this();
		
		fromJson(json);
	}
	
	/**
	   * Copy constructor
	   * @param other  the options to copy
	   */
	public FdfsClientOptions(AbstractFdfsOptions other) {
		super(other);
		trackers = new ArrayList<>();
	}
	
	/**
	 * get the default {@code JsonObject} config
	 * 
	 * @return the {@code JsonObject}
	 */
	public static JsonObject defaultJsonConfig() {
		return DEFAULT_CONFIG.copy();
	}
	
	/**
	 * get trackers
	 * 
	 * @return the trackers
	 */
	public List<SocketAddress> getTrackers() {
		return trackers;
	}

	/**
	 * add trackers
	 * 
	 * @param trackers the trackers
	 * @return a reference to this, so the API can be used fluently
	 */
	public FdfsClientOptions addTrackers(SocketAddress... trackers) {
		this.trackers.addAll(Arrays.asList(trackers));
		return this;
	}

	/**
	 * add a tracker
	 * 
	 * @param trackers the tracker
	 * @return a reference to this, so the API can be used fluently
	 */
	public FdfsClientOptions addTracker(SocketAddress trackers) {
		this.trackers.add(trackers);
		return this;
	}

	/**
	 * add a tracker
	 * 
	 * @param host the host
	 * @param port the port
	 * @return a reference to this, so the API can be used fluently
	 */
	public FdfsClientOptions addTracker(String host, int port) {
		this.trackers.add(SocketAddress.inetSocketAddress(port, host));
		return this;
	}

	/**
	 * delete a tracker
	 * 
	 * @param index the index
	 * @return a reference to this, so the API can be used fluently
	 */
	public FdfsClientOptions delTracker(int index) {
		this.trackers.remove(index);
		return this;
	}

	@Override
	public FdfsClientOptions fromJson(JsonObject json) {

		super.fromJson(json);

		JsonArray array = json.getJsonArray(TRACKERS);

		if (array != null && array.size() > 0) {
			array.forEach(object -> {
				if (object instanceof JsonObject) {
					JsonObject tracker = (JsonObject) object;

					String host = tracker.getString(HOST, "");
					int port = tracker.getInteger(PORT, -1);

					if (!host.isEmpty() && port != -1) {
						trackers.add(SocketAddress.inetSocketAddress(port, host));
					}
				}
			});
		}
		
		return this;
	}

	@Override
	public JsonObject toJson() {
		return super.toJson().put(TRACKERS,
				new JsonArray(trackers.stream()
						.map(sockAddr -> new JsonObject().put(HOST, sockAddr.host()).put(PORT, sockAddr.port()))
						.collect(Collectors.toList())));
	}
	
	@Override
	public FdfsClientOptions setCharset(String charset) {
		super.setCharset(charset);
		return this;
	}
	
	@Override
	public FdfsClientOptions setConnectTimeout(long connectTimeout) {
		super.setConnectTimeout(connectTimeout);
		return this;
	}
	
	@Override
	public FdfsClientOptions setNetworkTimeout(long networkTimeout) {
		super.setNetworkTimeout(networkTimeout);
		return this;
	}
	
	@Override
	public FdfsClientOptions setDefaultExt(String defaultExt) {
		super.setDefaultExt(defaultExt);
		return this;
	}
	
	@Override
	public FdfsClientOptions setPoolSize(int poolSize) {
		super.setPoolSize(poolSize);
		return this;
	}
}
