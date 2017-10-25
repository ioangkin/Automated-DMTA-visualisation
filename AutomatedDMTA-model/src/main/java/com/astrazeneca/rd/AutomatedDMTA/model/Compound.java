package com.astrazeneca.rd.AutomatedDMTA.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

/*	ToDo: Customer would prefer if there there were no size restrictions (same for smile), can this be removed, like this:
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

	@Column()
	@BLob
	private byte[] ic50Graph;
/*  for set/get methods see: https://blogs.oracle.com/adf/jpa-insert-and-retrieve-clob-and-blob-types */
	
	@Column()
	@BLob
	private byte[] structureGraph;
	
	@Column()
	private boolean completed;
	
	public Compound() {}
	
	//TODO: confirm smile is needed in this constructor
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

	//TODO: Manu, what is the purpose?
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Compound [");
		sb.append("ID : ").append(id).append(", Compound Id : ").append(compoundId).append(", Smile : ").append(smile).
			append("]");
		return sb.toString();
	}
}
