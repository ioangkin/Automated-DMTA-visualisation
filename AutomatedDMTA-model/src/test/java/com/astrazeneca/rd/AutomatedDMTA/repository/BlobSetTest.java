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
		
		
		Compound c = new Compound();
		c.setCompoundId("MyCompound");
        c.setLineGraph(writtingImage("\\pipeline04.rd.astrazeneca.net\SharedData\autodmta\input_bioassay\output_10.png"));// - \\pipeline04.rd.astrazeneca.net\SharedData\autodmta\input_bioassay\output_10.png

        sessionEJB.persistPerson(p);
        
		}

}