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

import com.astrazeneca.rd.AutomatedDMTA.model.Compound;

/**
 * @author kzhd491
 *
 */
@ContextConfiguration("classpath:appContext-test.xml")
public class CompoundRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger logger = LoggerFactory.getLogger(CompoundRepositoryTest.class);

	@Autowired
	CompoundRepository compoundRepository;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//Load data?
	}

	private Compound createCompound(String sn) {
		Compound c = new Compound();
		
		c.setSerialNumber(sn);
		
		if (logger.isDebugEnabled()) {
			logger.debug("NEW COMPOUND : " + c);
		}
		
		Compound saved = compoundRepository.save(c);
		assertNotNull(saved.getId());
		if (logger.isDebugEnabled()) {
			logger.debug("SAVED : " + saved);
		}
		return saved;
	}
	
	@Test
	public void saveCompound() {
		Compound saved = createCompound("CompoundA");
		Compound fromDB = compoundRepository.findOne(saved.getId());
		if (logger.isDebugEnabled()) {
			logger.debug("FROM DB : " + fromDB);
		}
		
		assertNotNull(fromDB);
		compoundRepository.delete(fromDB);
	}
	
	@Test 
	public void updateCompound() {
		Compound saved = createCompound("CompoundB");		
		saved.setSmiles("SmilesA");
		Compound updated = compoundRepository.save(saved);		
		if (logger.isDebugEnabled()) {
			logger.debug("UPDATED : " + updated);
		}
		assertNotNull(updated);		

		Compound fromDB = compoundRepository.findOne(updated.getId());
		if (logger.isDebugEnabled()) {
			logger.debug("FROM DB : " + fromDB);
		}
		
		assertNotNull(fromDB);		
		compoundRepository.delete(fromDB);
	}

	@Test
	public void findAll() {
		int count0 = compoundRepository.findAll().size();
		Compound saved0 = createCompound("CompoundA");
		
		int count1 = compoundRepository.findAll().size();
		assertTrue(count0 == (count1 - 1));
		
		Compound saved1 = createCompound("CompoundE");
		int count2 = compoundRepository.findAll().size();
		assertTrue(count0 == (count2 - 2));
		
		compoundRepository.deleteAll();
	}
	
/*  Not searching for multiple attribs

  	@Test
	public void findBySerialNumberAndsmiles() {
		Compound saved = createCompound("CompoundA", "SmilesA");		
		
		List<Compound> fromDB = compoundRepository.findBySerialNumberAndSmiles("CompoundA", "SmilesA");
		assertTrue(fromDB.size() == 1);
		
		Compound c = fromDB.get(0);
		assertTrue(c.getSerialNumber() == "CompoundA");
		assertTrue(c.getSmiles() == "SmilesA");
		compoundRepository.deleteAll();
	}*/
	
/*	@Test
	public void findBySerialNumberOrSmiles() {
		Compound saved1 = createCompound("CompoundA", "SmilesA");		
		Compound saved2 = createCompound("CompoundB", "SmilesB");		
		Compound saved3 = createCompound("CompoundC", "SmilesC");		
		
		List<Compound> fromDB = compoundRepository.findBySerialNumberOrSmiles("CompoundA", "SmilesA");
		assertTrue(fromDB.size() == 3);
		
		fromDB = compoundRepository.findBySerialNumberOrSmiles("CompoundD", "SmilesD");
		assertTrue(fromDB.size() == 1);
		
		fromDB = compoundRepository.findBySerialNumberOrSmiles("CompoundA", null);
		assertTrue(fromDB.size() == 2);
		
		compoundRepository.deleteAll();
	}*/
	
	@Test
	public void findOne() {
		Compound saved = createCompound("CompoundA");		
		
		List<Compound> fromDB = compoundRepository.findBySerialNumber("CompoundA");
		assertTrue(fromDB.size() == 1);
		
		Compound c = fromDB.get(0);
		
		Compound pFromDB = compoundRepository.findOne(c.getId());
		assertNotNull(pFromDB);
		assertTrue(c.getSerialNumber() == pFromDB.getSerialNumber());
/*		assertTrue(c.getSmiles() == pFromDB.getSmiles());*/
		compoundRepository.deleteAll();
	}
	
	@Test
	public void findPage() {
		List<Compound> saved = compoundRepository.save(generateCompounds(20));
		List<Compound> fromDB = compoundRepository.findAll();
		assertTrue(fromDB.size() == 20);
		
		Pageable pageable = new PageRequest(1, 5);

		Page<Compound> page = compoundRepository.findAll(pageable);
		assertEquals(page.getNumberOfElements(), 5);
		assertEquals(page.getNumber(), 1);
		assertEquals(page.getTotalElements(), 20);
		assertEquals(page.getTotalPages(), 4);

		assertEquals(page.getContent().get(0).getSerialNumber(), "First6");
		assertEquals(page.getContent().get(0).getSmiles(), "Last6");
		compoundRepository.deleteAll();
	}
	
	private List<Compound> generateCompounds(int number) {
		ArrayList<Compound> compounds = new ArrayList<Compound>(number);
		for (int i=1; i<=number; i++) {
			compounds.add(new Compound());
		}
		return compounds;
	}

}

