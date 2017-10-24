package com.astrazeneca.rd.AutomatedDMTA.resources;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;


/**
 * Servlet 3.0 containers hook into this Application class to get a list of classes to scan for services
 * @see https://jersey.java.net/nonav/documentation/latest/user-guide.html#deployment
 * @author mp4777q
 *
 */
@ApplicationPath("resources")
public class RestApplication extends ResourceConfig {
	public RestApplication() {
		HashSet<Class<?>> c = new HashSet<Class<?>>();
		c.add(PersonsRestService.class);
        Set<Class<?>> classes = Collections.unmodifiableSet(c);
		registerClasses(classes);
		
		property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
	}
}
