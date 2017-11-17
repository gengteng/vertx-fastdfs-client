package io.vertx.fastdfs.exp;

public class FdfsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4005847610641497843L;
	
	public FdfsException(String message) {
        super(message);
    }

    public FdfsException(Throwable e) {
        super(e);
    }

    public FdfsException(String message, Throwable cause) {
        super(message, cause);
    }
}
