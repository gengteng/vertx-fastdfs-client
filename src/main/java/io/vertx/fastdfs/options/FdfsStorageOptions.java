package io.vertx.fastdfs.options;

import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SocketAddress;

/**
 * 
 * @author GengTeng
 * <p>
 * me@gteng.org
 * 
 * @version 4.2
 */
public class FdfsStorageOptions extends AbstractFdfsOptions {
	
	public static final String HOST = "host";
	public static final String PORT = "port";
	
	private String group;
	private byte storePathIndex;
	private SocketAddress address;
	
	public FdfsStorageOptions() {
		super();
	}
	
	public FdfsStorageOptions(AbstractFdfsOptions other) {
		super(other);
	}
	
	public byte getStorePathIndex() {
		return storePathIndex;
	}
	
	public FdfsStorageOptions setStorePathIndex(byte storePathIndex) {
		this.storePathIndex = storePathIndex;
		return this;
	}
	
	public SocketAddress getAddress() {
		return address;
	}

	public FdfsStorageOptions setAddress(SocketAddress address) {
		this.address = address;
		return this;
	}
	
	public String getGroup() {
		return group;
	}

	public FdfsStorageOptions setGroup(String group) {
		this.group = group;
		return this;
	}
	
	@Override
	public FdfsStorageOptions fromJson(JsonObject json) {
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
	public FdfsStorageOptions setCharset(String charset) {
		super.setCharset(charset);
		return this;
	}
	
	@Override
	public FdfsStorageOptions setConnectTimeout(long connectTimeout) {
		super.setConnectTimeout(connectTimeout);
		return this;
	}
	
	@Override
	public FdfsStorageOptions setNetworkTimeout(long networkTimeout) {
		super.setNetworkTimeout(networkTimeout);
		return this;
	}
	
	@Override
	public FdfsStorageOptions setDefaultExt(String defaultExt) {
		super.setDefaultExt(defaultExt);
		return this;
	}
}
