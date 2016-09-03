package uk.co.thinktag.wildfly;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/push-msg")
public class PushMsg {

	@Resource(lookup="java:app/async-push/MessageHub") 
	MessageHub messageHub;

	@POST
	@Path("/{subscriberId}")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response sendMessage(
			@PathParam("subscriberId") String subscriberId,
			String msg) {
	
		messageHub.sendMessage(msg);
		
		return Response.status(200).entity(
				"Msg posted").build();
	}
}
