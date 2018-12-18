package com.astrazeneca.rd.AutomatedDMTA.demo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astrazeneca.rd.AutomatedDMTA.model.Person;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


public class BasicServicesIntegrationTest {
	private static Logger logger = LoggerFactory.getLogger(BasicServicesIntegrationTest.class);

	/**
	 * Note that the URL below must match configuration settings elsewhere:
	 * 1. The port is defined in the pom file: cargo.servlet.port
	 * 2. The application context (root in this case) is defined in the cargo deployable properties
	 * 3. The REST resources' location is defined in an annotation in com.astrazeneca.rd.AutomatedDMTA.service.RestApplication   
	 */
	private static String BASE_URL = "/resources/people/";
/*	private static String BASE_URL = "http://localhost:9090/resources/people/";*/
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

/*	DISABLED UNTIL WE WORK OUT HOW TO ENSURE THIS TESTS WITH THE EMBEEDDED DATABASE

	@Test
	public void testHello() throws Exception {
		final WebClient webClient = new WebClient();

		final TextPage page = webClient.getPage(BASE_URL + "print");

		String data = page.getContent();

		assertNotNull(data);
		assertTrue("Hello from REST service".equals(data));
		webClient.closeAllWindows();
	}

	@BeforeClass
	//AppInitializer class loads 2 records in the DB - ensure that we are good...
	public static void testGetPersons() throws Exception {
		assertTrue(2L == getPersons());
	}
	
	@Test
	public void testGetPerson() throws Exception {
		Person p = getPerson(1);
		assertTrue("John".equals(p.getFirstName()));
		assertTrue("Deer".equals(p.getLastName()));
	}

	@Test
	public void testAddPerson() throws Exception {
		final WebClient webClient = new WebClient();
		
		long existingCount = getPersons();
		
		Person newPerson = new Person();
		newPerson.setFirstName("Joe");
		newPerson.setLastName("Somebody");
		String data = gson.toJson(newPerson);
		System.out.println ("================== CREATING NEW PERSON " + data);

		WebRequest request = new WebRequest(new URL(BASE_URL + "persons"), HttpMethod.POST);
		request.setAdditionalHeader("Content-Type", "application/json; charset=utf-8");
		request.setRequestBody(data);
		final Page page = webClient.getPage(request);
		System.out.println ("================== " + page);
		
		long newCount = getPersons();
		assertTrue(newCount == (existingCount + 1));
		webClient.closeAllWindows();
	}

	@Test
	public void testUpdatePerson() throws Exception {
		final WebClient webClient = new WebClient();
		long existingCount = getPersons();

		Person p = getPerson(1);
		assertTrue("John".equals(p.getFirstName()));
		assertTrue("Deer".equals(p.getLastName()));
		
		p.setFirstName("Jonny");
		p.setLastName("Moose");
		String data = gson.toJson(p);
		System.out.println ("================== UPDATING PERSON : " + data);

		WebRequest request = new WebRequest(new URL(BASE_URL + "persons/1"), HttpMethod.PUT);
		request.setAdditionalHeader("Content-Type", "application/json; charset=utf-8");
		request.setRequestBody(data);
		webClient.getPage(request);
		
		long newCount = getPersons();
		assertTrue(newCount == existingCount);		//Count shouldn't change
		
		//Retrive and validate
		p = getPerson(1);
		assertTrue("Jonny".equals(p.getFirstName()));
		assertTrue("Moose".equals(p.getLastName()));
		
		p.setFirstName("John");
		p.setLastName("Deer");
		data = gson.toJson(p);
		
		request.setRequestBody(data);
		webClient.getPage(request);

		p = getPerson(1);
		assertTrue("John".equals(p.getFirstName()));
		assertTrue("Deer".equals(p.getLastName()));

		webClient.closeAllWindows();
	}

	@Test
	public void testDeletePerson() throws Exception {
		final WebClient webClient = new WebClient();
		
		long existingCount = getPersons();
		
		Person newPerson = new Person();
		newPerson.setFirstName("Temporary");
		newPerson.setLastName("Person");
		String data = gson.toJson(newPerson);
		System.out.println ("================== CREATING NEW PERSON " + data);

		WebRequest request = new WebRequest(new URL(BASE_URL + "persons"), HttpMethod.POST);
		request.setAdditionalHeader("Content-Type", "application/json; charset=utf-8");
		request.setRequestBody(data);
		final Page page = webClient.getPage(request);
		System.out.println ("================== " + page);
		
		long newCount = getPersons();
		assertTrue(newCount == (existingCount + 1));
		
		request = new WebRequest(new URL(BASE_URL + "persons/"+newCount), HttpMethod.DELETE);
		final Page page2 = webClient.getPage(request);
		System.out.println ("================== " + page2);

		newCount = getPersons();
		assertTrue(newCount == existingCount);

		webClient.closeAllWindows();
	}
	
	private static long getPersons() throws Exception {
		final WebClient webClient = new WebClient();
		final Page page = webClient.getPage(BASE_URL + "persons");
		String data = page.getWebResponse().getContentAsString();
		System.out.println ("================== " + data);
		logger.debug ("================== " + data);
		assertNotNull(data);
		
		Type type = new TypeToken<List<Person>>(){}.getType();
		List<Person> p = gson.fromJson(data, type);
		logger.debug("================== " + p);
		assertNotNull(p);
		webClient.closeAllWindows();
		
		return p.size();
	}
	
	private static Person getPerson(long id) throws Exception {
		final WebClient webClient = new WebClient();

		final Page page = webClient.getPage(BASE_URL + "persons/"+id);
		String data = page.getWebResponse().getContentAsString();
		System.out.println ("================== PERSON JSON FROM SERVICE : " + data);
		logger.debug ("================== " + data);

		assertNotNull(data);
		
		Person p = gson.fromJson(data, Person.class);
		logger.debug("================== " + p);
		assertNotNull(p);
		
		assertTrue(id == p.getId());
		webClient.closeAllWindows();	
		
		return p;
	}
*/	
}
