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

import com.astrazeneca.rd.AutomatedDMTA.model.Compound;
import com.astrazeneca.rd.AutomatedDMTA.repository.CompoundRepository;

@ContextConfiguration("classpath:appContext-services-test.xml")
@Transactional
public class CompoundsServiceTest extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger logger = LoggerFactory.getLogger(CompoundsServiceTest.class);
	@Autowired CompoundService service;

	//This is used to clear the test database... Unit Tests should use the HelloRestService
	@Autowired private CompoundRepository compoundRepository;

	private void createCompound(String sn) {
		int currentCount = service.getAllCompounds().size();
		if (logger.isDebugEnabled()) {
			logger.debug("CURRENT COMPOUND COUNT : " + currentCount);
		}
		
		Compound c = new Compound();
		
		c.setSerialNumber(sn);
		service.saveCompound(c);
		
		int newCount = service.getAllCompounds().size();
		if (logger.isDebugEnabled()) {
			logger.debug("NEW COMPOUND COUNT : " + newCount);
		}
		
		assertTrue(currentCount == (newCount - 1));
	}
	
	@Test
	public void testSaveCompound() {
		createCompound("Test");
		compoundRepository.deleteAll();
	}

	@Test
	public void testFindCompound() {
		createCompound("Test");
		List <Compound> compounds = service.getAllCompounds();
		assertTrue(compounds.size() == 1);
		
		Compound c = compounds.get(0);
		assertTrue("Test".equals(c.getSerialNumber()));
		Compound c1 = service.getCompoundById(c.getId());
		assertNotNull(c1);
		
		createCompound("Test1");
		compounds = service.getAllCompounds();
		assertTrue(compounds.size() == 2);
		
		compoundRepository.deleteAll();
	}
	
	@Test
	public void testUpdateCompound() {
		createCompound("Test");
		List <Compound> compounds = service.getAllCompounds();
		assertTrue(compounds.size() == 1);

		Compound c = compounds.get(0);
		c.setSerialNumber("TestUpdated");
		service.saveCompound(c);
		
		compounds = service.getAllCompounds();
		c = compounds.get(0);
		assertTrue("TestUpdated".equals(c.getSerialNumber()));
		compoundRepository.deleteAll();
	}
	
	@Test
	public void testDeleteCompound() {
		createCompound("Test");
		List <Compound> compounds = service.getAllCompounds();
		assertTrue(compounds.size() == 1);
		
		Compound c = compounds.get(0);
		service.deleteCompound(c.getId());
		
		compounds = service.getAllCompounds();
		assertTrue(compounds.size() == 0);
	}
}
