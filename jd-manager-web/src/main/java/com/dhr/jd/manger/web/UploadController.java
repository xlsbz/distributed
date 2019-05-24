package com.dhr.jd.manger.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.util.FastDFSClient;

import entity.Result;

/**
 * @author ali 处理文件上传
 */
@RestController
public class UploadController {

	// 注入文件服务器地址
	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;

	@RequestMapping("/upload.do")
	public Result uploadFile(MultipartFile file) throws Exception {
		// 获取文件名
		String filename = file.getOriginalFilename();
		// 截取后缀
		String suffix = filename.substring(filename.indexOf(".") + 1);
		if (file != null) {
			FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
			String uploadFile = client.uploadFile(file.getBytes(), suffix);
			String filePath = FILE_SERVER_URL + uploadFile;
			return new Result(true, filePath);
		}

		return new Result(false, "上传失败!");
	}
}
