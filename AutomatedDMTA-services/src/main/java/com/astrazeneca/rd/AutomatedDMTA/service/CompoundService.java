package com.astrazeneca.rd.AutomatedDMTA.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.astrazeneca.rd.AutomatedDMTA.model.Compound;
import com.astrazeneca.rd.AutomatedDMTA.repository.CompoundRepository;

/**
 * Business service for the Compound repository
 * @author klfl423
 *
 */
@Component
public class CompoundService {
	private static Logger logger = LoggerFactory.getLogger(CompoundService.class);
	
	@Autowired 
	private CompoundRepository compoundRepository;
	
	public Compound saveCompound(Compound compound) {
		return compoundRepository.save(compound);
	}
	
	public void deleteCompound(Long compoundId) {
		compoundRepository.delete(compoundId);
	}
	
	public Compound getCompoundById(Long compoundId) {
		return compoundRepository.findOne(compoundId);
	}

	public List<Compound> getAllCompounds() {
		return compoundRepository.findAll();
	}

	public Page<Compound> getAllCompounds(Pageable pageable) {
		return compoundRepository.findAll(pageable);
	}
	
	public List<Compound> getCompoundsByIds(String compoundId) {
		return compoundRepository.findByCompoundId(compoundId);
	}

/* Not needed for now as Smile is not used
 * 	public List<Compound> getCompoundByAnyAttrib(String compoundId, String smile) {
		return compoundRepository.findByCompoundIdOrSmile(compoundId, Smile);
	}*/
}
