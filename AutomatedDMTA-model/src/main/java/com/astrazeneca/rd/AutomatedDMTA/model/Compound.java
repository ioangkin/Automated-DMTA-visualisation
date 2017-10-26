package com.astrazeneca.rd.AutomatedDMTA.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.persistence.Lob;

/**
 * @author Ioannis Gkinalas
 * 
 * Compound model class
 *
 */
@Entity
@Table
public class Compound {

	//Automatically generated ID for the DB (different from compound_ID)
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

/*	TODO: Customer would prefer if there were no size restrictions (ie: for compound id and smile), can this be removed, like this:
	@NotNull
	private String compoundId;*/
	@Column(length = 24)
	@Size(min = 3, max = 24)
	@NotNull
	private String compoundId;

	@Column(length = 10000000)
	@Size(min = 100000, max = 10000000)
	@NotNull
	private String smile;

	//Could not use @Blob [error: Blob cannot resolved to a type], I had to import javax.persistence.Lob
	@Column()
	@Lob
	private byte[] lineGraph;
/*  for set/get methods see: https://blogs.oracle.com/adf/jpa-insert-and-retrieve-clob-and-blob-types */
	
	@Column()
	@Lob
	private byte[] structureGraph;
	
	/*
	* TODO: Note: A compound is completed either when passed the test stage and has a lineGraph or failed in some other stage.
	* Requirements are not set yet on how to monitor failure
	*/
	@Column()
	private boolean completed = false;
	
	public Compound() {}
	
	//TODO: confirm smile is needed in this constructor, if so structure graph could also be added?
	public Compound(String compoundId, String smile) {
		this.compoundId = compoundId;
		this.smile = smile;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCompoundId() {
		return compoundId;
	}

	public void setCompoundId(String compoundId) {
		this.compoundId = compoundId;
	}

	public String getSmile() {
		return smile;
	}

	public void setSmile(String smile) {
		this.smile = smile;
	}
	
	public byte[] getlineGraph() {
		return lineGraph;
	}

	public void setlineGraphe(byte[] lineGraph) {
		this.lineGraph = lineGraph;
	}

	public byte[] getStructureGraph() {
		return structureGraph;
	}

	public void setstructureGraph(byte[] structureGraph) {
		this.structureGraph = structureGraph;
	}
	
	public boolean getCompleted() {
		return completed;
	}

	/*
	 * TODO: Could I add a check like: if stageType >= 5 {completed;}
	*/
	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}
	
	//TODO: What is the purpose?
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Compound [");
		sb.append("ID : ").append(id).append(", Compound Id : ").append(compoundId).append(", Smile : ").append(smile).
			append("]");
		return sb.toString();
	}
}
