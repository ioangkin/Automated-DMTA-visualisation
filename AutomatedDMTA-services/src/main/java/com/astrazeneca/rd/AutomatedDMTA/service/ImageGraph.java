package com.astrazeneca.rd.AutomatedDMTA.service;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

	@Entity
	public class ImageGraph implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	 
	private String MimeType;  // Useful to store the mime type in case you want to send it back via a servlet.
	
	 // We mark up the byte array with a long object datatype, setting the fetch type to lazy.
	@Lob
	@Basic(fetch=FetchType.LAZY) // this gets ignored anyway, but it is recommended for blobs
	protected  byte[]  imageFile;
	
	public Long getId() {
		// TODO Auto-generated method stub
		return id;
	}
}

  
