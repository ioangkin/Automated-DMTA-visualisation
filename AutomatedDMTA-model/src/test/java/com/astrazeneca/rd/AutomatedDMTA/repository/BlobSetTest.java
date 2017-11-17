package com.astrazeneca.rd.AutomatedDMTA.repository;

import com.astrazeneca.rd.AutomatedDMTA.model.Compound;

/**
 * @author klfl423
 * 
 * Testing the Compopund's file(blob) setters methods
 *
 */

public class BlobSetTest {

	public static void main(String[] args) {

		//This code reads the image from a file and persist to Compound table in DB
		Compound c = new Compound();
		c.setSerialNumber("mySN");
        c.setLineGraph(writtingImage("C:/dev/output_11.png"));// - \\pipeline04.rd.astrazeneca.net\SharedData\autodmta\input_bioassay\output_10.png

        sessionEJB.persistCompound(c);
        
		}
	
/*	public byte[] getlineGraph() {
		return lineGraph;
	}
*/
	
	
}