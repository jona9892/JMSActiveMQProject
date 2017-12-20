package JMSPUPSUB;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class ConsumerMessageListener implements MessageListener {

	private String consumerName;

	public ConsumerMessageListener(String consumerName) {
		this.consumerName = consumerName;
	}

	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			try {
				String msgText = ((TextMessage) message).getText();

				if (!msgText.startsWith("[" + consumerName)) {
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
