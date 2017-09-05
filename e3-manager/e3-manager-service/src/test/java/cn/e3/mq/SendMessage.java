package cn.e3.mq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;

public class SendMessage {

	/**
	 * 需求: 商品服务发送消息
	 * 时机: 添加  修改  删除,发送消息-->同步索引库
	 * 测试: 测试发送消息
	 * 模式: 点对点
	 * @throws Exception 
	 */
	@Test
	public void sendMessagePTP() throws Exception{
		
		//创建一个消息发送工厂类,指定消息服务通信协议TCP,host,端口号
		ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://192.168.80.131:61616");
		//从工厂中获取连接对象
		Connection connection = cf.createConnection();
		//开启连接
		connection.start();
		//从连接对象中获取当前会话session
		//参数1:transacted--activeMQ消息中间件事务,手动指定自定义事务,一旦使用自定义事务,第二个事务将被忽略
		//参数2:acknowledgeMode--事务,是activeMQ提供的事务,自动应答模式事务策略
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		//在activeMQ	消息服务器创建消息发送空间,并且给消息空间起一个名称
		Queue queue = session.createQueue("myqueue");
		
		//指定消息发送者,并且指定消息发送目的地
		MessageProducer producer = session.createProducer(queue);
		
		//创建消息对象
		TextMessage message = new ActiveMQTextMessage();
		//设置发送内容
		message.setText("大美和善!");
		
		//发送消息
		producer.send(message);
		//关闭资源
		producer.close();
		session.close();
		connection.close();
		
	}
	
	
	/**
	 * 需求:测试发送消息
	 * 模式:发布订阅模式
	 * @throws Exception 
	 */
	@Test
	public void sendMessagePS() throws Exception{
		
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
		//创建消息发送者,指定消息发送目的地
		MessageProducer producer = session.createProducer(topic);
		
		//创建消息对象,封装消息
		TextMessage message = new ActiveMQTextMessage();
		message.setText("印度阿三,垃圾!");
		
		//发送消息
		producer.send(message);
		//关闭资源
		producer.close();
		session.close();
		connection.close();
	}
}
