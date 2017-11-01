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

	private Compound createCompound(String id, String sm) {
		Compound c = new Compound();
		
		c.setCompoundId(id);
		c.setSmile(sm);
		
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
		Compound saved = createCompound("CompoundA", "SmileA");
		Compound fromDB = compoundRepository.findOne(saved.getId());
		if (logger.isDebugEnabled()) {
			logger.debug("FROM DB : " + fromDB);
		}
		
		assertNotNull(fromDB);
		compoundRepository.delete(fromDB);
	}
	
	@Test 
	public void updateCompound() {
		Compound saved = createCompound("CompoundB", "SmileB");		
		saved.setSmile("SmileA");
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
		Compound saved0 = createCompound("CompoundA", "SmileA");
		
		int count1 = compoundRepository.findAll().size();
		assertTrue(count0 == (count1 - 1));
		
		Compound saved1 = createCompound("CompoundE", "SmileE");
		int count2 = compoundRepository.findAll().size();
		assertTrue(count0 == (count2 - 2));
		
		compoundRepository.deleteAll();
	}
	
/*	@Test
	public void findByCompoundIdAndsmile() {
		Compound saved = createCompound("CompoundA", "SmileA");		
		
		List<Compound> fromDB = compoundRepository.findByCompoundIdAndSmile("CompoundA", "SmileA");
		assertTrue(fromDB.size() == 1);
		
		Compound c = fromDB.get(0);
		assertTrue(c.getCompoundId() == "CompoundA");
		assertTrue(c.getSmile() == "SmileA");
		compoundRepository.deleteAll();
	}*/
	
	@Test
	public void findByCompoundIdOrSmile() {
		Compound saved1 = createCompound("CompoundA", "SmileA");		
		Compound saved2 = createCompound("CompoundB", "SmileB");		
		Compound saved3 = createCompound("CompoundC", "SmileC");		
		
/*		List<Compound> fromDB = compoundRepository.findByCompoundIdOrSmile("CompoundA", "SmileA");
		assertTrue(fromDB.size() == 3);
		
		fromDB = compoundRepository.findByCompoundIdOrSmile("CompoundD", "SmileD");
		assertTrue(fromDB.size() == 1);
		
		fromDB = compoundRepository.findByCompoundIdOrSmile("CompoundA", null);
		assertTrue(fromDB.size() == 2);*/
		
		compoundRepository.deleteAll();
	}
	
	@Test
	public void findOne() {
		Compound saved = createCompound("CompoundA", "SmileA");		
		
		List<Compound> fromDB = compoundRepository.findByCompoundId("CompoundA");
		assertTrue(fromDB.size() == 1);
		
		Compound c = fromDB.get(0);
		
		Compound pFromDB = compoundRepository.findOne(c.getId());
		assertNotNull(pFromDB);
		assertTrue(c.getCompoundId() == pFromDB.getCompoundId());
		assertTrue(c.getSmile() == pFromDB.getSmile());
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

		assertEquals(page.getContent().get(0).getCompoundId(), "First6");
		assertEquals(page.getContent().get(0).getSmile(), "Last6");
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

