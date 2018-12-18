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
	/**
	 * The serializable class ImageGraph does not declare a static final
	 * serialVersionUID field of type long A version number associated by the
	 * serialization runtime, deserialization verifies that the sender and receiver
	 * of a serialized object have loaded classes for that object that are
	 * compatible with respect to serialization
	 */
	private static final long serialVersionUID = -7251051163217493507L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String MimeType; // Useful to store the mime type in case you want to send it back via a servlet.

	// We mark up the byte array with a long object datatype, setting the fetch type
	// to lazy.
	@Lob
	@Basic(fetch = FetchType.LAZY) // this gets ignored anyway, but it is recommended for blobs
	protected byte[] imageFile;

	public Long getId() {
		// TODO Auto-generated method stub
		return id;
	}

	public void setMimeType(String mimeType) {
		this.MimeType = mimeType;
	}

	public String getMimeType() {
		return MimeType;
	}

	public void setImageFile(byte[] image) {
		this.imageFile = image;
	}

	public byte[] getImageFile() {
		return imageFile;
	}
}
