package uk.co.thinktag.wildfly;

import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/command-test")
public class SyncPost {
	
	@GET
	@Path("/{param}")
	@Produces("text/plain")
	public Response printMessage(@PathParam("param") String msg) {
		String result = "message posted successfully : " + msg;
		
		return Response.status(200).header("XASYNC-SUBS-ID", getUUID()).entity(
				result).build();
	}
	
	
	private String getUUID(){
		return UUID.randomUUID().toString();
	}

}