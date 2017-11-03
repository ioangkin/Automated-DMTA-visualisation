package com.astrazeneca.rd.AutomatedDMTA.resources;

import java.security.Principal;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.astrazeneca.rd.AutomatedDMTA.model.Compound;
import com.astrazeneca.rd.AutomatedDMTA.service.CompoundService;

/**
 * @see http://my.safaribooksonline.com/book/web-development/web-services/9781449383312/crud-web-services/building_crud_services 
 * @see https://s3.amazonaws.com/tfpearsonecollege/bestpractices/RESTful+Best+Practices.pdf
 * @author mp4777q
 *
 */
@Path("/people")
@Produces({"application/json"})
public class CompoundRestService {
	private static Logger logger = LoggerFactory.getLogger(CompoundRestService.class);
	
	@Context
	UriInfo uriInfo;
	
	@Context
	SecurityContext securityContext;

	@Autowired 
	private CompoundService compoundService;
	
	/*
	 * Methods:
	 * GET: 
	 * 		/persons
	 * 		/persons/paged
	 * 		## add filter method later
	 * 		/persons/{personId}
	 * POST:
	 * 		/persons
	 * 		/search
	 * PUT:
	 * 		/persons/{personId}
	 * DELETE:
	 * 		/persons/{personId}
	 * PATCH:
	 * 		not yet implemented
	 */

	@GET
    @Path("/compounds")
	public List<Compound> getAllCompounds() {
		logger.debug("GET: list all compounds");
		return compoundService.getAllCompounds();
	}

	@GET
	@Path("/compounds/{compoundId}")
	public Compound getCompound(@PathParam("compoundId") Long compoundId) {
		logger.debug("GET: get compound");
		return compoundService.getCompoundById(compoundId);
	}

	@POST
	@Path("/compounds")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveCompound(Compound compound) {
		logger.debug("POST: save compound");
		Compound saved = compoundService.saveCompound(compound);
		logger.debug("SAVED: "+saved.toString());
		ResponseBuilder resp = Response.created(uriInfo.getAbsolutePathBuilder().path(saved.getId().toString()).build());
		return resp.entity(saved).build();
	}
	
	@GET
    @Path("/compounds/paged")
	public Response getAllApplications(@BeanParam PageableBean pageableBean) {
		logger.debug("GET: list all applications");
		int pageSize = pageableBean.getPageSize();
		
		Page<Compound> page = compoundService.getAllCompounds(pageableBean.getPageRequest());			

		GenericEntity<List<Compound>> entity = new GenericEntity<List<Compound>>(page.getContent()) {};

		//print some information about pages and page:
		logger.debug("hasContent: " + page.hasContent() + ", isFirstPage: " + page.isFirstPage() + ", isLastPage: " + page.isLastPage()
			+ ", hasNextPage: " + page.hasNextPage() + ", hasPreviousPage: " + page.hasPreviousPage());
		logger.debug("pageNumber: " + page.getNumber() + ", pageSize: " + pageSize + ", Collection Size: " + page.getTotalElements()
			+ ", Page Amount: " + page.getTotalPages());

		ResponseBuilder resp = Response.ok();
		if (page.hasPreviousPage()) {
			resp.link(uriInfo.getAbsolutePathBuilder().queryParam("pageNumber", page.getNumber() - 1).queryParam("pageSize", pageSize).build(), "previous");
		}
		if (page.hasNextPage()) {
			resp.link(uriInfo.getAbsolutePathBuilder().queryParam("pageNumber", page.getNumber() + 1).queryParam("pageSize", pageSize).build(), "next");
		}
		resp.link(uriInfo.getAbsolutePathBuilder().queryParam("pageNumber", 0).queryParam("pageSize", pageSize).build(), "first");
		resp.link(uriInfo.getAbsolutePathBuilder().queryParam("pageNumber", page.getTotalPages() - 1).queryParam("pageSize", pageSize).build(), "last");
		

		resp.link(uriInfo.getAbsolutePathBuilder().queryParam("pageNumber", page.getNumber()).queryParam("pageSize", pageSize).build(), "self");

		resp.header("X-Page", page.getNumber());
		resp.header("X-Per-Page", pageSize);
		resp.header("X-Page-Count", page.getTotalPages());
		resp.header("X-Total-Count", page.getTotalElements());

		Response r = resp.entity(entity).build();
		return r;
	}

	/*
	TODO: Manu, not sure how to handle this method as it is a search using operations (AND/OR) but I only have one attrib.
	*/
	@GET
	@Path("/search")
	@Consumes(MediaType.APPLICATION_JSON)
	public List<Compound> search(@DefaultValue("and") @QueryParam("op") String op, @QueryParam("compoundId") String compoundId, @QueryParam("smile") String smile) throws Exception {
		logger.debug("GET: search for compounds: compoundiD="+compoundId+" "+op+" smile="+smile);
		if ("and".equalsIgnoreCase(op)) {
			return compoundService.getCompoundById(compoundId, smile);
		} else if ("or".equalsIgnoreCase(op)) {
			return compoundService.getCompoundByAnyAttrib(compoundId, smile);
		} else {
			throw new Exception("Unknown operator");
		}
	}
	
	@PUT
    @Path("/compounds/{compoundId}")	
	@Consumes(MediaType.APPLICATION_JSON)
	public Compound updateCompound(Compound compound) {
		logger.debug("PUT: update compound");
		return compoundService.saveCompound(compound);
	}
	
	@DELETE
    @Path("/compounds/{compoundId}")	
	public Response deleteCompound(@PathParam("compoundId") Long compoundId) {
		logger.debug("DELETE: delete compound");
		compoundService.deleteCompound(compoundId);
		return Response.noContent().build();
	}
	
    @GET
    @Path("/print")
    @Produces(MediaType.TEXT_PLAIN)
    public String getHello() {
        return "Hello from REST service";
    }    

	@GET
	@Path("/whoami")
	public String test() {
		Principal principal = securityContext.getUserPrincipal();
		if (principal != null) {
			String user = principal.getName();
			logger.info("Show security info. Principal = "+user);
			StringBuilder sb = new StringBuilder();
			sb.append("Principal: ").append(user);
			return sb.toString();
		} else {
			return "Authentication is not enabled"; 
		}
	}
}
