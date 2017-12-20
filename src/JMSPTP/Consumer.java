package JMSPTP;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Consumer {
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

			// Create a MessageConsumer from the Session to the Queue
			MessageConsumer consumer = session.createConsumer(destination);
			
			// Wait for a message
			Message message = consumer.receive(2000);
			
			if(message instanceof TextMessage){
				TextMessage textMessage = (TextMessage) message;
				String text = textMessage.getText();
				System.out.println("Recieved: " + text);
			} else {
				System.out.println("Recieved: " + message);
			}
			
			consumer.close();
			session.close();
			connection.close();
			
		} catch (Exception e) {
			System.out.println("Caught: " + e);
			e.printStackTrace();
		}
	}
	
}
