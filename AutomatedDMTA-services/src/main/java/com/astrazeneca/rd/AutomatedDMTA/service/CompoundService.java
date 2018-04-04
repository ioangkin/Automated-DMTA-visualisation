package com.astrazeneca.rd.AutomatedDMTA.service;

import java.awt.image.BufferedImage;
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
	
	public void deleteCompound(Long id) {
		compoundRepository.delete(id);
	}
	
	public Compound getCompoundById(Long id) {
		return compoundRepository.findOne(id);
	}

	public List<Compound> getAllCompounds() {
		return compoundRepository.findAll();
	}
	
	public List<Compound> getAllDesign() {
		return compoundRepository.getAllDesign();
	}
	
	public List<Compound> getAllSynthesis() {
		return compoundRepository.getAllSynthesis();
	}
	
	public List<Compound> getAllPurification() {
		return compoundRepository.getAllPuification();
	}
	
	public List<Compound> getAllTesting() {
		return compoundRepository.getAllTesting();
	}

	public Page<Compound> getAllCompounds(Pageable pageable) {
		return compoundRepository.findAll(pageable);
	}
	
	public List<Compound> getCompoundsBySampleNumber(String sampleNumber) {
		return compoundRepository.findBySampleNumber(sampleNumber);
	}
	
	
	public BufferedImage showLineGraph(Long id){
		Compound compound = this.getCompoundById(id);
		byte[] array = compound.getLineGraph();
		return new BufferedImage(640,480,BufferedImage.TYPE_INT_RGB);
		//Manu: Above code puts lineGraph in array but does not return it. Only returns an empty bufferedImage eventually
	}
	
	public BufferedImage showStructureGraph(Long id){
		Compound compound = this.getCompoundById(id);
		byte[] array = compound.getStructureGraph();
		return new BufferedImage(256,256,BufferedImage.TYPE_INT_RGB);
	}

/* Not needed for now as smiles is not used
  	public List<Compound> getCompoundByAnyAttrib(String sampleNumber, String smiles) {
		return compoundRepository.findBySampleNumberOrSmiles(sampleNumber, Smiles);
	}
*/
}
