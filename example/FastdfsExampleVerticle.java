package io.vertx.fastdfs.example;

import java.time.ZoneId;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.fastdfs.FdfsClient;
import io.vertx.fastdfs.FdfsClientOptions;
import io.vertx.fastdfs.FdfsFileId;
import io.vertx.fastdfs.FdfsFileInfo;
import io.vertx.fastdfs.FdfsStorage;
import io.vertx.fastdfs.FdfsTracker;

public class FastdfsExampleVerticle extends AbstractVerticle {

	private FdfsClient client;

	@Override
	public void start() throws Exception {

		FdfsClientOptions options = new FdfsClientOptions().addTracker("10.230.135.78", 22122).setCharset("ISO8859-1");
		
		// 也可以使用JsonObject作为配置，标准的配置文件格式为 FdfsClientOptions.defaultJsonConfig()
		// 或者参考 default-fastdfs.json 文件
		
		client = FdfsClient.create(vertx, options);

		// 只执行一条操作
		client.upload(Buffer.buffer("Hello, FastDFS!!!"), "txt", ar -> {
			if (ar.succeeded()) {
				System.out.println("upload ok: " + ar.result());
			} else {
				System.err.println("upload: " + ar.cause());
			}
		});

		// 在一个存储Storage上连续执行多个操作：
		// 获取 Tracker -> 获取存储 Storage -> 上传 -> 查询文件信息并打印时间戳 -> 下载 -> 删除
		// 目前没有连接池机制，可通过保留获取的 tracker/storage 自行实现，只要确保一个 tracker/storage 只被一个线程使用即可
		// 下面是地狱，建议使用 Future :)
		client.getTracker(ar -> {
			if (ar.succeeded()) {
				FdfsTracker tracker = ar.result();
				tracker.getStoreStorage(ar2 -> {
					if (ar2.succeeded()) {

						// 得到storage就可以把tracker关闭了
						tracker.close();

						FdfsStorage storage = ar2.result();

						storage.upload("upload.txt", "txt", upload -> {
							if (upload.succeeded()) {

								FdfsFileId fileId = upload.result();

								System.out.println("upload ok: " + fileId);

								storage.fileInfo(fileId, info -> {
									if (info.succeeded()) {
										FdfsFileInfo fileInfo = info.result();
										System.out.println("fileInfo ok: " + fileInfo.getTimestamp().atZone(ZoneId.systemDefault()));
									} else {
										System.err.println("fileInfo err: " + info.cause());
									}

									storage.download(fileId, "download.txt", 0, 0, download -> {
										if (download.succeeded()) {

											System.out.println("download ok");

											storage.delete(fileId, delete -> {

												// 不再使用的storage要关闭
												storage.close();

												if (delete.succeeded()) {
													System.out.println("delete ok");
												} else {
													System.err.println("delete err: " + delete.cause());
												}
											});
										} else {
											// 不再使用的storage要关闭
											storage.close();
											
											System.out.println("download err: " + download.cause());
										}
									});
								});

							} else {
								// 不再使用的storage要关闭
								storage.close();
								
								System.err.println("upload err: " + upload.cause());
							}
						});
					} else {
						System.err.println("getStoreStorage err: " + ar2.cause());
					}
				});
			} else {
				System.err.println("getTracker err: " + ar.cause());
			}
		});
	}
}
