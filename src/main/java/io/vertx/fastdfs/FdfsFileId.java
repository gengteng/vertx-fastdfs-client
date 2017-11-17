package io.vertx.fastdfs;

/**
 * FastDFS file id (filename and group).
 * 
 * @author GengTeng
 * <p>
 * me@gteng.org
 * 
 * @version 3.5.0
 */
public final class FdfsFileId {
	
	public static final String SEPARATOR = "/";
	
	private String group;
	private String name;

	private FdfsFileId() {
	}

	public static FdfsFileId create(String group, String name) {
		FdfsFileId fileId = new FdfsFileId();
		fileId.group = group;
		fileId.name = name;

		return fileId;
	}
	
	public static FdfsFileId parse(String fileId) {
		FdfsFileId id = new FdfsFileId();
		
		int index = fileId.indexOf(SEPARATOR);
		
		id.group = fileId.substring(0, index);
		id.name = fileId.substring(index + 1);
		
		return id;
	}

	public String group() {
		return group;
	}
	
	public FdfsFileId setGroup(String group) {
		this.group = group;
		return this;
	}

	public String name() {
		return name;
	}
	
	public FdfsFileId setName(String name) {
		this.name = name;
		return this;
	}
	
	@Override
	public String toString() {
		return group + SEPARATOR + name;
	}
}
