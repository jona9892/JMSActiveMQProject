package JMS.PUPSUB.Example;

import java.net.URISyntaxException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JMSTopicExample {
	public static void main(String[] args) throws URISyntaxException, Exception {

		Connection connection = null;

		// Producer

		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(

				"tcp://localhost:61616");

		connection = connectionFactory.createConnection();

		Session session = connection.createSession(false,

				Session.AUTO_ACKNOWLEDGE);

		Topic topic = (Topic) session.createTopic("customerTopic");

		// Consumer1 subscribes to customerTopic

		MessageConsumer consumer1 = (MessageConsumer) session.createConsumer((Destination) topic);

		consumer1.setMessageListener(new ConsumerMessageListener("Consumer1"));

		// Consumer2 subscribes to customerTopic

		MessageConsumer consumer2 = (MessageConsumer) session.createConsumer((Destination) topic);

		consumer2.setMessageListener(new ConsumerMessageListener("Consumer2"));

		connection.start();

		// Publish

		String payload = "Important Task";

		Message msg = session.createTextMessage(payload);

		MessageProducer producer = session.createProducer((Destination) topic);

		System.out.println("Sending text '" + payload + "'");

		producer.send(msg);

		Thread.sleep(3000);

		session.close();

	}

}
