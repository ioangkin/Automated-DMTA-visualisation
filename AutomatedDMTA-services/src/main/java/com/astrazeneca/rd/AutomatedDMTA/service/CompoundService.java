package com.astrazeneca.rd.AutomatedDMTA.service;

import java.awt.image.BufferedImage;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.astrazeneca.rd.AutomatedDMTA.model.Compound;
import com.astrazeneca.rd.AutomatedDMTA.model.StageType;
import com.astrazeneca.rd.AutomatedDMTA.repository.CompoundRepository;

/**
 * Business service for the Compound repository
 * 
 * @author klfl423
 *
 */
@Service
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

	public List<Compound> getAllDesign(StageType design) {
		return compoundRepository.getAllDesign(design);
	}

	public List<Compound> getAllSynthesis(StageType synthesis) {
		return compoundRepository.getAllSynthesis(synthesis);
	}

	public List<Compound> getAllPurification(StageType purification) {
		return compoundRepository.getAllPuification(purification);
	}

	public List<Compound> getAllTesting(StageType testing) {
		return compoundRepository.getAllTesting(testing);
	}

	public Page<Compound> getAllCompounds(Pageable pageable) {
		return compoundRepository.findAll(pageable);
	}

	public List<Compound> getCompoundsBySampleNumber(String sampleNumber) {
		return compoundRepository.findBySampleNumber(sampleNumber);
	}

	public BufferedImage showLineGraph(Long id) {
		Compound compound = this.getCompoundById(id);
		byte[] array = compound.getLineGraph();
		return new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
	}

	public BufferedImage showStructureGraph(Long id) {
		Compound compound = this.getCompoundById(id);
		byte[] array = compound.getStructureGraph();
		return new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
	}

	/*
	 * Not needed for now as smiles is not used public List<Compound>
	 * getCompoundByAnyAttrib(String sampleNumber, String smiles) { return
	 * compoundRepository.findBySampleNumberOrSmiles(sampleNumber, Smiles); }
	 */
}
