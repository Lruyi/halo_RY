package cn.e3.mq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;

public class ReceiveMessage {
	
	/**
	 * 需求:搜索服务接收消息,同步索引库
	 * 测试:测试接收消息
	 * 模式:同步模式
	 * @throws Exception 
	 */
	@Test
	public void receiveMessageTB() throws Exception{
		//创建消息发送工厂类,指定消息服务通信协议TCP,host,端口号
		ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://192.168.80.131:61616");
		//从工厂中获取连接对象
		Connection connection = cf.createConnection();
		//开启连接
		connection.start();
		//从连接对象中获取当前回话session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//指定消息接收地址
		Queue queue = session.createQueue("myqueue");
		//指定消息接收者,并指定接收消息地址
		MessageConsumer consumer = session.createConsumer(queue);
		
		//同步模式接收
		Message message = consumer.receive(1000);//这是延迟1000毫秒接收
		
		if (message instanceof TextMessage) {
			TextMessage m = (TextMessage) message;
			System.out.println("接收消息:"+m.getText());
		}
		
		//关闭资源
		consumer.close();
		session.close();
		connection.close();
		
		
	}
	
	
	/**
	 * 需求:搜索服务接收消息,同步索引库
	 * 测试:测试接收消息
	 * 模式:异步模式(监听器模式监听消息)
	 * @throws Exception 
	 */
	@Test
	public void receiveMessageYB() throws Exception{
		//创建消息发送工厂类,指定消息服务通信协议TCP,host,端口号
		ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://192.168.80.131:61616");
		//从工厂中获取连接对象
		Connection connection = cf.createConnection();
		//开启连接
		connection.start();
		//从连接对象中获取当前回话session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//指定消息接收地址
		Queue queue = session.createQueue("myqueue");
		//指定消息接收者,并指定接收消息地址
		MessageConsumer consumer = session.createConsumer(queue);
		
		//异步模式接收
		//使用监听器接收消息
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
				// 
				if (message instanceof TextMessage) {
					TextMessage m = (TextMessage) message;
					try {
						System.out.println("接收消息:"+m.getText());
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		//模拟服务,监听器一直处于监听状态
		System.in.read();
		
		//关闭资源
		consumer.close();
		session.close();
		connection.close();
		
		
	}
	
	/**
	 * 需求:搜索服务接收消息,同步索引库
	 * 测试:测试接收消息
	 * 模式:发布订阅模式
	 * @throws Exception 
	 */
	@Test
	public void receiveMessagePS() throws Exception{
		//创建消息工厂类,指定消息服务通信需要TCP,host,端口号
		ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://192.168.80.131:61616");
		//从工厂中获取连接
		Connection connection = cf.createConnection();
		//开启连接
		connection.start();
		//从连接总获取当前会话session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//开辟一块消息发送空间,并给空间起个名称
		//发布订阅模式:数据架构---topic
		Topic topic = session.createTopic("mytopic");
		//创建消息接收者,指定消息接收目的地
		MessageConsumer consumer = session.createConsumer(topic);
		
		//异步模式接收消息(监听器模式)
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
				//接受消息(监听器匿名内部类的方法会指定触发)
				if (message instanceof TextMessage) {
					TextMessage m = (TextMessage) message;
					try {
						System.out.println("接收消息:"+m.getText());
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		//模拟服务,让端口处于阻塞状态,监听消息空间
		System.in.read();
		
		//关闭资源
		consumer.close();
		session.close();
		connection.close();

		
	}
	
}
