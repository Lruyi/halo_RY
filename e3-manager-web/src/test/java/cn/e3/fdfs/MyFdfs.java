package cn.e3.fdfs;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import cn.e3.manager.utils.FastDFSClient;

public class MyFdfs {

	/**
	 * 需求: 使用java客户端代码测试文件上传
	 * 条件:
	 * 使用fastDFS上传图片
	 * java客户端jar包
	 * @throws MyException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * 
	 */
	@Test
	public void testUploadPic() throws Exception{
		//指定客户端配置文件路径
		String clientPath = "E:\\Project88\\e3\\"
				+ "e3-manager-web\\src\\main\\resources\\client.conf";
		
		//指定上传图片
		String picPath = "F:\\相片\\新建文件夹\\IMG_0337.JPG";
		
		//加载客户端配置文件.连接远程图片服务器
		ClientGlobal.init(clientPath);
		
		//创建tracker调度服务客户端
		TrackerClient tc = new TrackerClient();
		
		//从客户端中获取路径
		TrackerServer trackerServer = tc.getConnection();
		
		StorageServer storageServer = null;
		//创建storage存储对象
		StorageClient sc = new StorageClient(trackerServer, storageServer );
		
		//使用存储客户端上传图片
		String[] urls = sc.upload_file(picPath, "jpg", null);
		for (String url : urls) {
			System.out.println(url);
		}
		
		
	}
	
	
	/**
	 * 需求:使用工具类测试文件上传
	 * @throws Exception 
	 */
	@Test
	public void uploadPic() throws Exception{
		//指定客户端配置文件
		String clientPath = "E:\\Project88\\e3\\"
				+ "e3-manager-web\\src\\main\\resources\\client.conf";
		
		//指定上传图片路径
		String picPath = "F:\\相片\\新建文件夹\\IMG_0320.JPG";
		
		//创建工具类对象上传文件
		FastDFSClient fClient = new FastDFSClient(clientPath);
		
		//上传
		String url = fClient.uploadFile(picPath);
		System.out.println(url);
	}
	
	
}
