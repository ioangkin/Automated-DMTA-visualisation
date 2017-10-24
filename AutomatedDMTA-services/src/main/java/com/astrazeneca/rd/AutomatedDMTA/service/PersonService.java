package com.astrazeneca.rd.AutomatedDMTA.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.astrazeneca.rd.AutomatedDMTA.model.Person;
import com.astrazeneca.rd.AutomatedDMTA.repository.PersonRepository;

/**
 * Business service for the Person repository
 * @author mp4777q
 *
 */
@Component
public class PersonService {
	private static Logger logger = LoggerFactory.getLogger(PersonService.class);
	
	@Autowired 
	private PersonRepository personRepository;
	
	public Person savePerson(Person person) {
		return personRepository.save(person);
	}
	
	public void deletePerson(Long personId) {
		personRepository.delete(personId);
	}
	
	public Person getPersonById(Long personId) {
		return personRepository.findOne(personId);
	}

	public List<Person> getAllPersons() {
		return personRepository.findAll();
	}

	public Page<Person> getAllPersons(Pageable pageable) {
		return personRepository.findAll(pageable);
	}
	
	public List<Person> getPersonsByNames(String firstName, String lastName) {
		return personRepository.findByFirstNameAndLastName(firstName, lastName);
	}

	public List<Person> getPersonsByAnyNames(String firstName, String lastName) {
		return personRepository.findByFirstNameOrLastName(firstName, lastName);
	}
}
