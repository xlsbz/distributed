package com.dhr.shop.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dhr.shop.upload.FastDFSClient;
import com.dhr.shop.utils.JsonUtils;

/**
 * @ClassName: ImageController
 * @Description: TODO(图片上传)
 * @author Mr DU
 * @date 2019年3月30日
 *
 */
@Controller
public class ImageController {

	// 注入服务器地址属性
	@Value("${TAO_SHOP_IMAGE_SERVER}")
	private String TAO_SHOP_IMAGE_SERVER;

	/**
	 * @Title: fileUpload @Description: TODO(图片上传 兼容火狐) @param @param
	 *         uploadFile @param @return @return Map<String,Object> @throws
	 */
	@RequestMapping(value = "/pic/upload", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=utf-8")
	@ResponseBody
	public String fileUpload(MultipartFile uploadFile) {
		// 5.返回map
		Map<String, Object> map = null;
		try {
			// 1.获取文件扩展名
			String suffix = uploadFile.getOriginalFilename();
			String exeName = suffix.substring(suffix.lastIndexOf(".") + 1);
			// 2.创建FastDFS客户端
			FastDFSClient client = new FastDFSClient("resource/fdfs_client.conf");
			// 3.执行上传
			String path = client.uploadFile(uploadFile.getBytes(), exeName);
			// 4.拼接返回的url和ip地址
			String url = TAO_SHOP_IMAGE_SERVER + path;
			// System.out.println(url);
			map = new HashMap<String, Object>();
			map.put("error", 0);
			map.put("url", url);
		} catch (Exception e) {
			e.printStackTrace();
			map = new HashMap<>();
			map.put("error", 1);
			map.put("message", "图片长传失败");
			return JsonUtils.objectToJson(map);
		}
		return JsonUtils.objectToJson(map);
	}
}
