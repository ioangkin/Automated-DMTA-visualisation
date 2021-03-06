package com.astrazeneca.rd.AutomatedDMTA.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size; //TODO: remove as it is not used
import javax.persistence.Lob;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;

/**
 * @author klfl423
 * 
 *         Compound model Object class
 *
 */
@Entity
@Table
public class Compound {

	// Automatically generated ID for JPA's
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	// Note: Stages are comparable, see STageTypeTest.java and enum methods:
	// stage.ordinal() and compareTo()
	@Column()
	// @NotNull
	@Enumerated(EnumType.STRING)
	private StageType stage;

	@Column()
	@NotNull
	private String sampleNumber;

	@Column()
	@NotNull
	private String smiles;

	@Column()
	@Lob
	private byte[] lineGraph;
	/*
	 * for set & get methods see:
	 * https://blogs.oracle.com/adf/jpa-insert-and-retrieve-clob-and-blob-types
	 */

	// this is fetched from
	// http://compounds.rd.astrazeneca.net/resources/structure/toimage/YOUR_SMILES_HERE?inputFormat=SMILES&appid=pipelinepilot
	// using SMILES
	@Column()
	@Lob
	// @NotNull
	private byte[] structureGraph;

	/*
	 * A compound is completed either when passed to the test stage or failed
	 * somewhere in a previous stage. Requirements are not set yet on what is
	 * causing failure and this can't be monitored
	 */
	@Column()
	private boolean completed = false;

	// When stage == StageType.TESTING
	@Column()
	private String results;

	// Note: Can live without a constructor, but its a good practice to have at
	// least basic ones, depending on the default attributes
	public Compound() {
	}

	public Compound(String sampleNumber) {
		this.sampleNumber = sampleNumber;
	}

	public Compound(String sampleNumber, String smiles) {
		this.sampleNumber = sampleNumber;
		this.smiles = smiles;
	}

	public Compound(String sampleNumber, String smiles, byte[] structureGraph) {
		this.sampleNumber = sampleNumber;
		this.smiles = smiles;
		this.structureGraph = structureGraph;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSampleNumber() {
		return sampleNumber;
	}

	public void setSampleNumber(String sampleNumber) {
		this.sampleNumber = sampleNumber;
	}

	public String getSmiles() {
		return smiles;
	}

	public void setSmiles(String smiles) {
		this.smiles = smiles;
	}

	public byte[] getLineGraph() {
		return lineGraph;
	}

	// Todo: Lookup setter for Blob
	// for the folder lookup: retrieve files from window share
	public void setLineGraph(byte[] lineGraph) {
		this.lineGraph = lineGraph;
	}

	public byte[] getStructureGraph() {
		return structureGraph;
	}

	public void setStructureGraph(byte[] structureGraph) {
		this.structureGraph = structureGraph;
	}

	public boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public String getResults() {
		return results;
	}

	public void setResults(String results) {
		this.results = results;
	}

	public StageType getStage() {
		return stage;
	}

	public void setStage(StageType stage) {
		this.stage = stage;
	}

	// Note: Don't add Blobs
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Compound [");
		sb.append("ID : ").append(id).append(", Compound Id : ").append(sampleNumber).append(", Smiles : ")
				.append(smiles).append("]");
		return sb.toString();
	}
}
