package io.vertx.fastdfs.impl;

import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SocketAddress;
import io.vertx.fastdfs.options.AbstractFdfsOptions;

/**
 * 
 * @author GengTeng
 * <p>
 * me@gteng.org
 * 
 * @version 3.5.0
 */
public class FdfsTrackerOptions extends AbstractFdfsOptions {
	
	public static final String HOST = "host";
	public static final String PORT = "port";
	
	private SocketAddress address;

	public FdfsTrackerOptions() {
		super();
	}
	
	public FdfsTrackerOptions(AbstractFdfsOptions other) {
		super(other);
	}

	public SocketAddress getAddress() {
		return address;
	}

	public FdfsTrackerOptions setAddress(SocketAddress address) {
		this.address = address;
		return this;
	}
	
	@Override
	public FdfsTrackerOptions fromJson(JsonObject json) {
		super.fromJson(json);
		
		String host = json.getString(HOST, "");
		int port = json.getInteger(PORT, -1);

		if (!host.isEmpty() && port != -1) {
			this.address = SocketAddress.inetSocketAddress(port, host);
		}
		
		return this;
	}
	
	@Override
	public JsonObject toJson() {
		return super.toJson().put(HOST, address.host()).put(PORT, address.port());
	}
	
	@Override
	public FdfsTrackerOptions setCharset(String charset) {
		super.setCharset(charset);
		return this;
	}
	
	@Override
	public FdfsTrackerOptions setConnectTimeout(long connectTimeout) {
		super.setConnectTimeout(connectTimeout);
		return this;
	}
	
	@Override
	public FdfsTrackerOptions setNetworkTimeout(long networkTimeout) {
		super.setNetworkTimeout(networkTimeout);
		return this;
	}
	
	@Override
	public FdfsTrackerOptions setDefaultExt(String defaultExt) {
		super.setDefaultExt(defaultExt);
		return this;
	}
}
