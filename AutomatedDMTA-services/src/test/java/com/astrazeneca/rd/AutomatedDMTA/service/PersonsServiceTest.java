package com.astrazeneca.rd.AutomatedDMTA.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import com.astrazeneca.rd.AutomatedDMTA.model.Person;
import com.astrazeneca.rd.AutomatedDMTA.repository.PersonRepository;

@ContextConfiguration("classpath:appContext-services-test.xml")
@Transactional
public class PersonsServiceTest extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger logger = LoggerFactory.getLogger(PersonsServiceTest.class);
	@Autowired PersonService service;

	//This is used to clear the test database... Unit Tests sould use the HelloRestService
	@Autowired private PersonRepository personRepository;

	private void createPerson(String fn, String ln) {
		int currentCount = service.getAllPersons().size();
		if (logger.isDebugEnabled()) {
			logger.debug("CURRENT PERSON COUNT : " + currentCount);
		}
		
		Person p = new Person();
		
		p.setFirstName(fn);
		p.setLastName(ln);
		service.savePerson(p);
		
		int newCount = service.getAllPersons().size();
		if (logger.isDebugEnabled()) {
			logger.debug("NEW PERSON COUNT : " + newCount);
		}
		
		assertTrue(currentCount == (newCount - 1));
	}

	@Test
	public void testSavePerson() {
		createPerson("Test", "Person");
		personRepository.deleteAll();
	}

	@Test
	public void testFindPerson() {
		createPerson("Test", "Person");
		List <Person> persons = service.getAllPersons();
		assertTrue(persons.size() == 1);
		
		Person p = persons.get(0);
		assertTrue("Test".equals(p.getFirstName()));
		Person p1 = service.getPersonById(p.getId());
		assertNotNull(p1);
		
		createPerson("Test1", "Person1");
		persons = service.getAllPersons();
		assertTrue(persons.size() == 2);
		
		personRepository.deleteAll();
	}
	
	@Test
	public void testUpdatePerson() {
		createPerson("Test", "Person");
		List <Person> persons = service.getAllPersons();
		assertTrue(persons.size() == 1);

		Person p = persons.get(0);
		p.setFirstName("TestUpdated");
		service.savePerson(p);
		
		persons = service.getAllPersons();
		p = persons.get(0);
		assertTrue("TestUpdated".equals(p.getFirstName()));
		personRepository.deleteAll();
	}
	
	@Test
	public void testDeletePerson() {
		createPerson("Test", "Person");
		List <Person> persons = service.getAllPersons();
		assertTrue(persons.size() == 1);
		
		Person p = persons.get(0);
		service.deletePerson(p.getId());
		
		persons = service.getAllPersons();
		assertTrue(persons.size() == 0);
	}
}
