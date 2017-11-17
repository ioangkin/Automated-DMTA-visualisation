/**
 * 
 */
package com.astrazeneca.rd.AutomatedDMTA.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.astrazeneca.rd.AutomatedDMTA.model.Compound;
import com.astrazeneca.rd.AutomatedDMTA.repository.CompoundRepository;

/**
 * @author kzhd491
 *
 */
public class AppInitializer {
	private static Logger logger = LoggerFactory.getLogger(AppInitializer.class);

	
	@Autowired 
	private CompoundRepository compoundRepository;
	
	public void init() {
		//Create test compounds...
		compoundRepository.deleteAll();		//Comment this out to keep old data
		
		Compound c1 = new Compound();
		c1.setSerialNumber("001");
//		c1.setSMILES("SMILEA001");
		compoundRepository.save(c1);
		
		Compound c2 = new Compound();
		c2.setSerialNumber("002");
//		p2.setLastName("SMILEA002");
		compoundRepository.save(c2);
		
		logger.debug("Added 2 TEST compounds...");
	}
}
