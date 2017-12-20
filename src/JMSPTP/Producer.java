package JMSPTP;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Producer {
	public static void main(String[] args) throws Exception {

		try {
			
			// Create a ConnectionFactory
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

			// Create a Connection
			Connection connection = connectionFactory.createConnection();
			connection.start();
			
			// Create a Session
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Create the destination (Queue)
			Destination destination = session.createQueue("JMSQUEUE.TESTQ");

			// Create a MessageProducer from the Session to the Queue
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			// Create a message
			String text = "This is a message test from: " + Thread.currentThread().getName();
			TextMessage message = session.createTextMessage(text);
			
			//load up the message with instructions on how to get back here (JMS Header)
	        message.setJMSReplyTo(destination);
	        
	        
			// Tell the producer to send the message
			System.out.println("Sent messages: " + message.hashCode() + " : " + Thread.currentThread().getName());
			producer.send(message);

			// Clean up
			session.close();
			connection.close();
		} catch (Exception e) {
			System.out.println("Caught: " + e);
			e.printStackTrace();
		}
	}
}
