package com.astrazeneca.rd.AutomatedDMTA.repository;

import java.awt.image.BufferedImage;
import java.util.List;

import com.astrazeneca.rd.AutomatedDMTA.model.Compound;
import com.astrazeneca.rd.AutomatedDMTA.repository.CompoundRepository;

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
		c.setSampleNumber("mySN");
        c.setGraph(writtingGraph("C:/dev/tempImage.png"));// - \\pipeline04.rd.astrazeneca.net\SharedData\autodmta\input_bioassay\output_10.png

        sessionEJB.persistCompound(c);
        
		}
	
	//Retrieving the image from Database table
	public BufferedImage showGraph(Long id){
		Compound compound = this.getCompoundById(id);
    retrieveImage(compound.getGraph());   //get picture retrieved from Table 
    
	//create byte[] from image file 
	private static byte[] writtingGraph(String fileLocation) {
		System.out.println("file lication is"+fileLocation);
	     IOManager manager=new IOManager();
	     try {
	           return manager.getBytesFromFile(fileLocation);
	            
	        } catch (IOException e) {
	        }
	        return null;
	    }
	
	//Read byte[] from database and write to a image file 
	  private static void retrieveImage(byte[] b) {
		    IOManager manager=new IOManager();

		        try {manager.putBytesInFile("c:\\webtest.jpg",b);}
		        catch (IOException e) {}
		    }
	
	}

/*	public byte[] getGraph() {
		return Graph;
	}
*/
	
	
}