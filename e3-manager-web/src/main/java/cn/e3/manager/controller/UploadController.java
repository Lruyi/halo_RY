package cn.e3.manager.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.e3.manager.utils.FastDFSClient;
import cn.e3.utils.JsonUtils;
import cn.e3.utils.KindEditorModel;

@Controller
public class UploadController {

	@Value("${IMAGE_SERVER_URL}")
	private String IMAGE_SERVER_URL;

	/**
	 * 需求:使用fastDFS上传商品图片 
	 * 请求:/pic/upload 
	 * 参数:uploadFile 
	 * 返回值:json格式KindEditorModel
	 * //成功时 
	 * { "error" : 0, 
	 * "url" : "http://www.example.com/path/to/file.ext" }
	 * //失败时 
	 * { "error" : 1, 
	 * "message" : "错误信息" }
	 * @throws Exception
	 */
	@RequestMapping("/pic/upload")
	@ResponseBody
	public String uploadPic(MultipartFile uploadFile) {

		try {
			// 获取文件扩展名
			String originalFilename = uploadFile.getOriginalFilename();
			String extName = originalFilename.substring(originalFilename
					.lastIndexOf(".") + 1);

			// 创建fastFDS工具类,上传图片
			FastDFSClient fClient = new FastDFSClient("classpath:client.conf");

			// 上传图片
			// 返回图片虚拟磁盘路径: /group1/M00/00/00/wKhCQ1mj2iyAaMiwAAvea_OGt2M047.jpg

			String url = fClient.uploadFile(uploadFile.getBytes(), extName);
			// 组合图片上传绝对路径
			url = IMAGE_SERVER_URL + url;
			
			//图片上传成功
			KindEditorModel km = new KindEditorModel();
			//成功 error=0
			km.setError(0);
			km.setUrl(url);
			
			//把响应对象转成json字符串
			//Jackson
			String kmJson = JsonUtils.objectToJson(km);
			
			return kmJson;
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			//图片上传失败
			KindEditorModel km = new KindEditorModel();
			//失败 error=1
			km.setError(1);
			km.setMessage("上传失败!");
			
			String kmJson = JsonUtils.objectToJson(km);
			return kmJson;
		}

	}

}
