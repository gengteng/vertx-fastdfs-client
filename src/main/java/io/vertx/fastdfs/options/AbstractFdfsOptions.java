package io.vertx.fastdfs.options;

import io.vertx.core.json.JsonObject;

/**
 * An abstract base class that all FastDFS options extend.
 * 
 * @author GengTeng
 *         <p>
 *         me@gteng.org
 * 
 * @version 3.5.0
 */
public abstract class AbstractFdfsOptions {

	public static final String CHARSET = "charset";
	public static final String CONNECT_TIMEOUT = "connectTimeout";
	public static final String NETWORK_TIMEOUT = "networkTimeout";
	public static final String DEFAULT_EXT = "defaultExt";
	public static final String POOLSIZE = "poolSize";

	public static final String DEFAULT_CHARSET = "utf8";
	public static final long DEFAULT_CONNECT_TIMEOUT = 10_000;
	public static final long DEFAULT_NETWORK_TIMEOUT = 10_000;
	public static final String DEFAULT_DEFAULT_EXT = "";
	public static final int DEFAULT_POOLSIZE = 15;

	protected String charset;
	protected long connectTimeout;
	protected long networkTimeout;
	protected String defaultExt;
	protected int poolSize;

	/**
	   * Default constructor
	   */
	public AbstractFdfsOptions() {
		charset = DEFAULT_CHARSET;
		connectTimeout = DEFAULT_CONNECT_TIMEOUT;
		networkTimeout = DEFAULT_NETWORK_TIMEOUT;
		defaultExt = DEFAULT_DEFAULT_EXT;
		poolSize = DEFAULT_POOLSIZE;
	}

	/**
	   * Copy constructor
	   */
	public AbstractFdfsOptions(AbstractFdfsOptions other) {
		charset = other.charset;
		connectTimeout = other.connectTimeout;
		networkTimeout = other.networkTimeout;
		defaultExt = other.defaultExt;
		poolSize = other.poolSize;
	}

	/**
	 * get charset.
	 * 
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * set charset.
	 * 
	 * @param charset the charset
	 * @return a reference to this, so the API can be used fluently
	 */
	public AbstractFdfsOptions setCharset(String charset) {
		this.charset = charset;
		return this;
	}

	/**
	 * get connect timeout.
	 * 
	 * @return the connect timeout
	 */
	public long getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * set connect timeout.
	 * 
	 * @param connectTimeout the connect timeout
	 * @return a reference to this, so the API can be used fluently
	 */
	public AbstractFdfsOptions setConnectTimeout(long connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	/**
	 * get network timeout.
	 * 
	 * @return the network timeout
	 */
	public long getNetworkTimeout() {
		return networkTimeout;
	}

	/**
	 * set network timeout.
	 * 
	 * @param networkTimeout the network timeout
	 * @return a reference to this, so the API can be used fluently
	 */
	public AbstractFdfsOptions setNetworkTimeout(long networkTimeout) {
		this.networkTimeout = networkTimeout;
		return this;
	}

	/**
	 * get default extension.
	 * 
	 * @return the default extension
	 */
	public String getDefaultExt() {
		return defaultExt;
	}

	/**
	 * set default extension.
	 * 
	 * @param defaultExt the default extension
	 * @return a reference to this, so the API can be used fluently
	 */
	public AbstractFdfsOptions setDefaultExt(String defaultExt) {
		this.defaultExt = defaultExt;
		return this;
	}

	/**
	 * get pool size.
	 * 
	 * @return the pool size
	 */
	public int getPoolSize() {
		return poolSize;
	}

	/**
	 * set the pool size.
	 * 
	 * @param poolSize the pool size
	 * @return a reference to this, so the API can be used fluently
	 */
	public AbstractFdfsOptions setPoolSize(int poolSize) {
		this.poolSize = poolSize;
		return this;
	}

	/**
	   * get value from a {@code JsonObject}
	   *
	   * @return the {@code JsonObject}
	   */
	public AbstractFdfsOptions fromJson(JsonObject json) {

		this.charset = json.getString(CHARSET, DEFAULT_CHARSET);
		this.connectTimeout = json.getLong(CONNECT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT);
		this.networkTimeout = json.getLong(NETWORK_TIMEOUT, DEFAULT_NETWORK_TIMEOUT);
		this.defaultExt = json.getString(DEFAULT_EXT, DEFAULT_DEFAULT_EXT);
		this.poolSize = json.getInteger(POOLSIZE, DEFAULT_POOLSIZE);

		return this;
	}

	/**
	   * Convert to a {@code JsonObject}
	   *
	   * @return the {@code JsonObject}
	   */
	public JsonObject toJson() {
		return new JsonObject().put(CHARSET, charset).put(CONNECT_TIMEOUT, connectTimeout)
				.put(NETWORK_TIMEOUT, networkTimeout).put(DEFAULT_EXT, defaultExt).put(POOLSIZE, poolSize);
	}

	/**
	   * encode this object as a JSON string.
	   *
	   * @return the string
	   */
	@Override
	public String toString() {
		return toJson().encode();
	}
	
	/**
	   * encode this object as a JSON string, with whitespace to make the object easier to read by a human, or other sentient organism.
	   *
	   * @return the string
	   */
	public String toStringPrettily() {
		return toJson().encodePrettily();
	}
}
