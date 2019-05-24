package com.dhr.jd.shop.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.util.FastDFSClient;

import entity.Result;

/**
 * @author ali 文件上传控制器
 */
@RestController
public class UploadFileController {

	// 注入文件服务器地址
	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;

	@RequestMapping("/upload")
	public Result fileUpload(MultipartFile file) throws Exception {
		// 获取文件名
		String filename = file.getOriginalFilename();
		// 截取后缀
		String suffix = filename.substring(filename.indexOf(".") + 1);
		// 上传文件
		if (file != null) {
			// 调用文件上传工具
			FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
			String uploadFile = client.uploadFile(file.getBytes(), suffix);
			// 拼接路径
			String filePath = FILE_SERVER_URL + uploadFile;
			return new Result(true, filePath);
		}
		return new Result(false, "上传失败，没有选择文件!");
	}

}
