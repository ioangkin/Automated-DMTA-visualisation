/**
 * 
 */
package com.astrazeneca.rd.AutomatedDMTA.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.astrazeneca.rd.AutomatedDMTA.model.Person;
import com.astrazeneca.rd.AutomatedDMTA.repository.PersonRepository;

/**
 * @author kzhd491
 *
 */
public class AppInitializer_for_Person {
	private static Logger logger = LoggerFactory.getLogger(AppInitializer_for_Person.class);

	@Autowired
	private PersonRepository personRepository;

	public void init() {
		// Create test persons...
		personRepository.deleteAll(); // Comment this out to keep old data

		Person p1 = new Person();
		p1.setFirstName("John");
		p1.setLastName("Deer");
		personRepository.save(p1);

		Person p2 = new Person();
		p2.setFirstName("Jane");
		p2.setLastName("Doe");
		personRepository.save(p2);

		logger.debug("Added 2 TEST users...");
	}
}
