package com.dhr.shop.upload;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

/*
 * 
 * @ClassName: FastDFSTest  
 * @Description: TODO(测试图片服务器)  
 * @author Mr DU  
 * @date 2019年3月30日  
*
 */
public class FastDFSTest {

	public void testFileUpload() throws Exception {
		// 1.加载配置文件就是tracker的地址
		ClientGlobal
				.init("D:\\workspaceEclips2018\\taoshop-manager-web\\src\\main\\resources\\resource\\fdfs_client.conf");
		// 2.创建一个trackerClient对象 new
		TrackerClient trackerClient = new TrackerClient();
		// 3.使用trackerClient创建一个连接对象，获得trackerServer
		TrackerServer trackerServer = trackerClient.getConnection();
		// 4.创建一个storageServer的引用，值为null
		StorageServer storageServer = null;
		// 5.创建一个storageClient对象，需要两个参数trackerServer和storgaeServer
		StorageClient storageClient = new StorageClient(trackerServer, storageServer);
		// 6.使用storageClient长传图片
		String[] upload_file = storageClient.upload_file("E:\\2.png", "png", null);
		// 7.返回数组包含组名和图片路径
		for (String string : upload_file) {
			System.out.println(string);
		}

	}

	// public void testFile() throws Exception {
	// FastDFSClient client = new FastDFSClient("resource/fdfs_client.conf");
	// String string = client.uploadFile("E:\\2.png", "png", null);
	// System.out.println(string);
	// }

}