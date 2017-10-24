package com.astrazeneca.rd.AutomatedDMTA.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.astrazeneca.rd.AutomatedDMTA.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
	public List<Person> findByFirstNameAndLastName(String firstName,
			String lastName);

	@Query("select p from Person p where p.firstName = :firstName or p.lastName = :lastName")
	public List<Person> findByFirstNameOrLastName(
			@Param("firstName") String firstName,
			@Param("lastName") String lastName);
	
	public Page<Person> findAll(Pageable pageable);
}
