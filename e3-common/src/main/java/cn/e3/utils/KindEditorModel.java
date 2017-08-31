package cn.e3.utils;

import java.io.Serializable;

public class KindEditorModel implements Serializable{
	
	/*//成功时
	{
	        "error" : 0,
	        "url" : "http://www.example.com/path/to/file.ext",
	        "message":null
	}
	//失败时
	{
	        "error" : 1,
	        "url":null,
	        "message" : "错误信息"
	}*/
	
	private int error;
	
	private String url;
	
	private String message;

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
