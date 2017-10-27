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
 * @author Ioannis Gkinalas
 * 
 * Compound model class
 *
 */
@Entity
@Table
public class Compound {

	//Note: Automatically generated ID for JPA's internal DB (different from compound_ID)
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/*TODO: Manu: I should be able to compare stages, like: "if (c.getStage.compareTo(StageType.Synthesis >= 0) {Compound.getStructureGraph; //compound has past synthesis}"*/
	@Column()
	@NotNull
	@Enumerated(EnumType.STRING)
	private StageType stage;
	
	@Column()
	@NotNull
	private String compoundId;

	@Column()
	@NotNull
	private String smile;

	//TODO: Manu: Could not use @Blob [error: Blob cannot resolved to a type], I had to import javax.persistence.Lob
	@Column()
	@Lob
	private byte[] lineGraph;
/*  for set&get methods see: https://blogs.oracle.com/adf/jpa-insert-and-retrieve-clob-and-blob-types */
	
	//TODO: Manu: Is this @NotNull?
	@Column()
	@Lob
	private byte[] structureGraph;
	
	/*
	* Note: A compound is completed either when passed the test stage and has a lineGraph or failed in some other stage.
	* Requirements are not set yet on how failure is defined (and can be monitored)
	*/
	@Column()
	private boolean completed = false;
	
	
	public Compound() {}
	
	//TODO: confirm what parameters and attributes should be in here? ie: should structure graph be added?
	public Compound(String compoundId, String smile, StageType stage) {
		this.compoundId = compoundId;
		this.smile = smile;
		this.stage = stage;
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

//	TODO: Could I add a check like: if (c.getStage.compareTo(StageType.Testing >= 0)) {completed;}
	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}
	
    public StageType getStage() {
        return stage;
    }

    public void setRating(StageType stage) {
        this.stage = stage;
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
