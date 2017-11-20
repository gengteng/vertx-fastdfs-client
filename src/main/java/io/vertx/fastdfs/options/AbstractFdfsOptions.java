package io.vertx.fastdfs.options;

import io.vertx.core.json.JsonObject;

/**
 * 
 * @author GengTeng
 * <p>
 * me@gteng.org
 * 
 * @version 3.5.0
 */
public abstract class AbstractFdfsOptions {

	public static final String CHARSET = "charset";
	public static final String CONNECT_TIMEOUT = "connectTimeout";
	public static final String NETWORK_TIMEOUT = "networkTimeout";
	public static final String DEFAULT_EXT = "defaultExt";

	public static final String DEFAULT_CHARSET = "utf8";
	public static final long DEFAULT_CONNECT_TIMEOUT = 10_000;
	public static final long DEFAULT_NETWORK_TIMEOUT = 10_000;
	public static final String DEFAULT_DEFAULT_EXT = "";

	protected String charset;
	protected long connectTimeout;
	protected long networkTimeout;
	protected String defaultExt;

	public AbstractFdfsOptions() {
		charset = DEFAULT_CHARSET;
		connectTimeout = DEFAULT_CONNECT_TIMEOUT;
		networkTimeout = DEFAULT_NETWORK_TIMEOUT;
		defaultExt = DEFAULT_DEFAULT_EXT;
	}
	
	public AbstractFdfsOptions(AbstractFdfsOptions other) {
		charset = other.charset;
		connectTimeout = other.connectTimeout;
		networkTimeout = other.networkTimeout;
		defaultExt = other.defaultExt;
	}

	public String getCharset() {
		return charset;
	}

	public AbstractFdfsOptions setCharset(String charset) {
		this.charset = charset;
		return this;
	}

	public long getConnectTimeout() {
		return connectTimeout;
	}

	public AbstractFdfsOptions setConnectTimeout(long connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	public long getNetworkTimeout() {
		return networkTimeout;
	}

	public AbstractFdfsOptions setNetworkTimeout(long networkTimeout) {
		this.networkTimeout = networkTimeout;
		return this;
	}

	public String getDefaultExt() {
		return defaultExt;
	}

	public AbstractFdfsOptions setDefaultExt(String defaultExt) {
		this.defaultExt = defaultExt;
		return this;
	}

	public AbstractFdfsOptions fromJson(JsonObject json) {
		
		this.charset = json.getString(CHARSET, DEFAULT_CHARSET);
		this.connectTimeout = json.getLong(CONNECT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT);
		this.networkTimeout = json.getLong(NETWORK_TIMEOUT, DEFAULT_NETWORK_TIMEOUT);
		this.defaultExt = json.getString(DEFAULT_EXT, DEFAULT_DEFAULT_EXT);

		return this;
	}

	public JsonObject toJson() {
		return new JsonObject().put(CHARSET, charset).put(CONNECT_TIMEOUT, connectTimeout)
				.put(NETWORK_TIMEOUT, networkTimeout).put(DEFAULT_EXT, defaultExt);
	}
	
	@Override
	public String toString() {
		return toJson().encodePrettily();
	}
}
