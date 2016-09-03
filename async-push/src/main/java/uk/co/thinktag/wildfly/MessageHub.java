package uk.co.thinktag.wildfly;

import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;

@Stateless
@Startup
public class MessageHub {

	@Inject
	@JMSConnectionFactory("java:jboss/DefaultJMSConnectionFactory")
	JMSContext context;
	
	public String waitForMessage() {

		try {
			InitialContext ctx = new InitialContext();
			Topic topic = (Topic) ctx
					.lookup("java:/public.event");

			Destination dest = (Destination) topic;
			JMSConsumer consumer = context.createConsumer(dest);
			while (true) {
				Message m = consumer.receive(1000);
				if (m != null) {
					if (m instanceof TextMessage) {

						return m.getBody(String.class);

					} else {
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "shouldnt be here";

	}

	public void sendMessage(String msg) {

		try {
			InitialContext ctx = new InitialContext();
			Topic topic = (Topic) ctx
					.lookup("java:/public.event");
			
			Destination dest = (Destination) topic;

			TextMessage tm = context.createTextMessage();
			tm.setText(msg);		
			context.createProducer().send(dest, tm);

			context.createProducer().send(dest, context.createMessage());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
