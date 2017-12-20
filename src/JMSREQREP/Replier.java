package JMSREQREP;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Replier implements Runnable {
	
	public String _correlationId;
	public String _name;
	
	public Replier(String correlationId, String name) {
	       this._correlationId = correlationId;
	       this._name = name;
	   }
	
	public void run() {
		try {
			
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

			// Create a Connection
			QueueConnection connection = connectionFactory.createQueueConnection();
			connection.start();

	        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);        
	        
	        Destination destination = session.createQueue("request-reply-queue");
	        
	        String correlationIdfilter = "JMSCorrelationID = '" + _correlationId  + "'";
	        QueueReceiver consumer = session.createReceiver((Queue) destination, correlationIdfilter);	        

	        TextMessage requestMessage = (TextMessage)consumer.receive();       
	        
	        // Her modtager vi den request message, og kan nu lave et svar
	        String replyText = String.format("Answer: " + requestMessage.getJMSCorrelationID() +  " - " + _name + "!");
	        TextMessage replyMessage = session.createTextMessage(replyText);
	        
	        replyMessage.setJMSDestination(requestMessage.getJMSReplyTo());
	        replyMessage.setJMSCorrelationID(_correlationId);

	        MessageProducer producer = session.createProducer(requestMessage.getJMSReplyTo());
	        producer.send(replyMessage);

	        session.close();
	        connection.close();
		}
		catch (Exception e) {
			System.out.println("Caught: " + e);
			e.printStackTrace();
		}
    }
}
