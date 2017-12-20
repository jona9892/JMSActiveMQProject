package JMSPUPSUB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.management.RuntimeErrorException;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JMSChat implements MessageListener{
	
	private static String userId;
	
	public static void main(String[] args) throws JMSException, IOException {		
			
		if(args.length != 1) {
			System.out.println("User Name is required....");
		} else {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

			// Create a Connection
			Connection connection = connectionFactory.createConnection();
			connection.start();	
			
			// Create a Session
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			// Create topic using session
			Topic topic = session.createTopic("customerTopic"); 
			
			userId = args[0];
			
			TopicConnectionFactory topicConnectionFactory = connectionFactory;
			
			TopicConnection tc = topicConnectionFactory.createTopicConnection();		
			
			JMSChat jmsChat = new JMSChat();
			
			publish(tc, topic, userId);
			subscribe(tc, topic, jmsChat);
			
		}
		
		
	}
	
	static void subscribe(TopicConnection topicConnection, Topic chatTopic, JMSChat commandLineChat) throws JMSException {
		TopicSession tsession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		TopicSubscriber ts = tsession.createSubscriber(chatTopic);
		ts.setMessageListener(commandLineChat);
	}
	
	static void publish(TopicConnection topicConnection, Topic chatTopic, String userId) throws JMSException, IOException {
		TopicSession tsession = topicConnection.createTopicSession(false,  Session.AUTO_ACKNOWLEDGE);
		TopicPublisher topicPublisher = tsession.createPublisher(chatTopic);
		topicConnection.start();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		while(true) {
			String msgToSend = reader.readLine();
			if(msgToSend.equalsIgnoreCase("exit")) {
				topicConnection.close();
				System.exit(0);
			} else {
				TextMessage msg = (TextMessage) tsession.createTextMessage();
				msg.setText("\n[" + userId + " : " + msgToSend + "]");
				topicPublisher.publish(msg);
			}
		}
	}

	@Override
	public void onMessage(Message message) {
		if(message instanceof TextMessage){
			try{
				String msgText = ((TextMessage) message).getText();
				
				if(!msgText.startsWith("[" + userId)){
					System.out.println(msgText);
				}					
			} catch (JMSException jmsEx_p) {
				String errMsg = "An error occured extracting message";
				System.err.println(errMsg);
				jmsEx_p.printStackTrace();
			}
		} else {
			String errMsg = "Message is not of expected type TextMessage";
			System.err.println(errMsg);
			throw new RuntimeException(errMsg);
		}		
	}
	
}
