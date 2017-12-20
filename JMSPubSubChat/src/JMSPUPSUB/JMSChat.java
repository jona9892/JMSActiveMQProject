package JMSPUPSUB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

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
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;

public class JMSChat implements MessageListener {

	private static String userId;
	private static String chatTopic;

	public static void main(String[] args) throws URISyntaxException, Exception {
		Connection connection = null;
		if (args.length != 2) {
			System.out.println("User Name and Topic is required....");
		} else {
			
			userId = args[0];
			chatTopic = args[1];
			
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

			// Create a Connection
			connection = connectionFactory.createConnection();
			connection.start();
			// Create a Session
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);					
			
			// Create topic using session
			Topic topic = session.createTopic(chatTopic);

			TopicConnectionFactory topicConnectionFactory = connectionFactory;

			TopicConnection tc = topicConnectionFactory.createTopicConnection();
			
			//Publish
			TopicSession tsession = tc.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			TopicPublisher topicPublisher = tsession.createPublisher(topic);
			tc.start();			
			
			ConsumerMessageListener consumer = new ConsumerMessageListener(userId);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			
			//Subscribe
			TopicSubscriber ts = tsession.createSubscriber(topic);
			ts.setMessageListener(consumer);
			
			while (true) {
				String msgToSend = reader.readLine();
				if (msgToSend.equalsIgnoreCase("exit")) {
					tc.close();
					System.exit(0);
				} else {
					TextMessage msg = (TextMessage) tsession.createTextMessage();
					msg.setText("\n[" + userId + " : " + msgToSend + "]");
					topicPublisher.publish(msg);
					
					
				}			

			}			
			

		}
	}

	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			try {
				String msgText = ((TextMessage) message).getText();

				if (!msgText.startsWith("[" + userId)) {
					System.out.println(msgText);
				}
			} catch (JMSException jmsEx_p) {
				String errMsg = "An error occured extracting message";

				jmsEx_p.printStackTrace();
			}
		} else {
			String errMsg = "Message is not of expected type TextMessage";
			System.err.println(errMsg);
			throw new RuntimeException(errMsg);
		}
	}

}
