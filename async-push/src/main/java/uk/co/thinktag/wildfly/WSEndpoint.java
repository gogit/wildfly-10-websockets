package uk.co.thinktag.wildfly;

import javax.enterprise.concurrent.ManagedExecutorService;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.jboss.logging.Logger;

@ServerEndpoint("/callback/{message}")
public class WSEndpoint {

	Logger log = Logger.getLogger(this.getClass());

	//doesn't seem to work maybe missing some annotation using initial context lookup
	//@Resource(lookup = "java:jboss/ee/concurrency/executor/default")
	//ManagedExecutorService managedExecutorService;

	//@Resource(lookup = "java:app/async-push/MessageHub")
	//MessageHub messageHub;

	@OnMessage
	public void receiveMessage(String message, Session session) {
		log.info("Received : " + message + ", session:" + session.getId());
	}

	@OnOpen
	public void open(Session session) {
		log.info("Open session:" + session.getId());
		final Session s = session;
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			ManagedExecutorService mes = (ManagedExecutorService) ctx
					.lookup("java:jboss/ee/concurrency/executor/default");

			final MessageHub messageHub = (MessageHub) ctx.lookup(
					"java:app/async-push/MessageHub");
			
			mes.execute(new Runnable() {
				@Override
				public void run() {
					try {
						String msg = messageHub.waitForMessage();

						s.getBasicRemote().sendText(msg);
						
						//10 seconds
						Thread.sleep(10000);
						//clean up
						s.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} catch (NamingException e1) {
			e1.printStackTrace();
		}

	}

	@OnClose
	public void close(Session session, CloseReason c) {
		log.info("Closing:" + session.getId());
	}

}