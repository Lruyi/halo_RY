package cn.e3.mq;

import java.io.IOException;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringMQ {
	
	/**
	 * 测试:测试spring整合mq接收消息
	 * 模式:点对点模式
	 * @throws Exception 
	 */
	@Test
	public void springMqWithPTP() throws Exception{
		//加载spring配置文件
		ApplicationContext app = new ClassPathXmlApplicationContext("classpath*:applicationContext-consumer.xml");
		System.in.read();
	}
	
	
	/**
	 * 测试:测试spring整合mq接收消息
	 * 模式:发布订阅模式
	 * @throws Exception 
	 */
	@Test
	public void springMqWithPS() throws Exception{
		//加载spring配置文件
		ApplicationContext app = new ClassPathXmlApplicationContext("classpath*:applicationContext-consumer.xml");
		System.in.read();
	}

}
