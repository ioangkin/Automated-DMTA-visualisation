/**
 * 
 */
package com.astrazeneca.rd.AutomatedDMTA.repository;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.astrazeneca.rd.AutomatedDMTA.model.Person;

/**
 * @author kzhd491
 *
 */
@ContextConfiguration("classpath:appContext-test.xml")
public class PersonRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger logger = LoggerFactory.getLogger(PersonRepositoryTest.class);

	@Autowired
	PersonRepository personRepository;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//Load data?
	}

	private Person createPerson(String fn, String ln) {
		Person p = new Person();
		
		p.setFirstName(fn);
		p.setLastName(ln);
		
		if (logger.isDebugEnabled()) {
			logger.debug("NEW PERSON : " + p);
		}
		
		Person saved = personRepository.save(p);
		assertNotNull(saved.getId());
		if (logger.isDebugEnabled()) {
			logger.debug("SAVED : " + saved);
		}
		return saved;
	}
	
	@Test
	public void savePerson() {
		Person saved = createPerson("John", "Deer");
		Person fromDB = personRepository.findOne(saved.getId());
		if (logger.isDebugEnabled()) {
			logger.debug("FROM DB : " + fromDB);
		}
		
		assertNotNull(fromDB);
		personRepository.delete(fromDB);
	}
	
	@Test 
	public void updatePerson() {
		Person saved = createPerson("John", "Doe");		
		saved.setLastName("Deer");
		Person updated = personRepository.save(saved);		
		if (logger.isDebugEnabled()) {
			logger.debug("UPDATED : " + updated);
		}
		assertNotNull(updated);		

		Person fromDB = personRepository.findOne(updated.getId());
		if (logger.isDebugEnabled()) {
			logger.debug("FROM DB : " + fromDB);
		}
		
		assertNotNull(fromDB);		
		personRepository.delete(fromDB);
	}

	@Test
	public void findAll() {
		int count0 = personRepository.findAll().size();
		Person saved0 = createPerson("John", "Deer");
		
		int count1 = personRepository.findAll().size();
		assertTrue(count0 == (count1 - 1));
		
		Person saved1 = createPerson("Jane", "Doe");
		int count2 = personRepository.findAll().size();
		assertTrue(count0 == (count2 - 2));
		
		personRepository.deleteAll();
	}
	
	@Test
	public void findByFirstnameAndLastName() {
		Person saved = createPerson("John", "Deer");		
		
		List<Person> fromDB = personRepository.findByFirstNameAndLastName("John", "Deer");
		assertTrue(fromDB.size() == 1);
		
		Person p = fromDB.get(0);
		assertTrue(p.getFirstName() == "John");
		assertTrue(p.getLastName() == "Deer");
		personRepository.deleteAll();
	}
	
	@Test
	public void findByFirstnameOrLastName() {
		Person saved1 = createPerson("John", "Deer");		
		Person saved2 = createPerson("John", "Doe");		
		Person saved3 = createPerson("Jean", "Deer");		
		
		List<Person> fromDB = personRepository.findByFirstNameOrLastName("John", "Deer");
		assertTrue(fromDB.size() == 3);
		
		fromDB = personRepository.findByFirstNameOrLastName("Mary", "Doe");
		assertTrue(fromDB.size() == 1);
		
		fromDB = personRepository.findByFirstNameOrLastName("John", null);
		assertTrue(fromDB.size() == 2);
		
		personRepository.deleteAll();
	}
	
	@Test
	public void findOne() {
		Person saved = createPerson("John", "Deer");		
		
		List<Person> fromDB = personRepository.findByFirstNameAndLastName("John", "Deer");
		assertTrue(fromDB.size() == 1);
		
		Person p = fromDB.get(0);
		
		Person pFromDB = personRepository.findOne(p.getId());
		assertNotNull(pFromDB);
		assertTrue(p.getFirstName() == pFromDB.getFirstName());
		assertTrue(p.getLastName() == pFromDB.getLastName());
		personRepository.deleteAll();
	}
	
	@Test
	public void findPage() {
		List<Person> saved = personRepository.save(generatePeople(20));
		List<Person> fromDB = personRepository.findAll();
		assertTrue(fromDB.size() == 20);
		
		Pageable pageable = new PageRequest(1, 5);

		Page<Person> page = personRepository.findAll(pageable);
		assertEquals(page.getNumberOfElements(), 5);
		assertEquals(page.getNumber(), 1);
		assertEquals(page.getTotalElements(), 20);
		assertEquals(page.getTotalPages(), 4);

		assertEquals(page.getContent().get(0).getFirstName(), "First6");
		assertEquals(page.getContent().get(0).getLastName(), "Last6");
		personRepository.deleteAll();
	}
	
	private List<Person> generatePeople(int number) {
		ArrayList<Person> people = new ArrayList<Person>(number);
		for (int i=1; i<=number; i++) {
			people.add(new Person("First"+i, "Last"+i));
		}
		return people;
	}

}

