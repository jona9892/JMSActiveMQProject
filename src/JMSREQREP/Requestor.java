package JMSREQREP;

import java.util.UUID;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

public class Requestor implements Runnable {
	
	public String _correlationId;
	
	public Requestor(String correlationId) {
	       this._correlationId = correlationId;
	   }
	
	public void run() {
		try {			
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

			// Lav en forbindelse
			Connection connection = connectionFactory.createConnection();
			connection.start();

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			Destination destination = session.createQueue("request-reply-queue");
			MessageProducer producer = session.createProducer(destination);

			// Lav en midlertidig queue, som holder på svar fra replier
			Destination replyDestination = session.createTemporaryQueue();

			// Lav en message
			TextMessage message = session.createTextMessage("What is your name " + _correlationId + "?");
			System.out.println("Question: " + message.getText());
			
			
			// Indsæt information omkring svar destination i message (JMS Header)
			message.setJMSReplyTo(replyDestination);
			
			// Giv den et correlationId som skal blive sammenlignet (JMS Header)
			message.setJMSCorrelationID(_correlationId);
			producer.send(message);

			// Consumer, som venter på svar
			MessageConsumer consumer = session.createConsumer(replyDestination);
			TextMessage reply = (TextMessage) consumer.receive();
			System.out.println("RECEIVED: " + reply.getText());

			session.close();
			connection.close();
		} catch (Exception e) {
			System.out.println("Caught: " + e);
			e.printStackTrace();
		}

	}
}
