/*package com.astrazeneca.rd.AutomatedDMTA.service;

import static org.junit.Assert.assertNotNull;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.apache.log4j.chainsaw.Main;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//https://commons.apache.org/proper/commons-io/javadocs/api-release/index.html?org/apache/commons/io/FilenameUtils.html
import org.apache.commons.io.FilenameUtils; //How top import it? Maven dependences?

import com.astrazeneca.rd.AutomatedDMTA.model.Compound;
import com.astrazeneca.rd.AutomatedDMTA.repository.CompoundRepository;
import com.astrazeneca.rd.AutomatedDMTA.model.StageType;
//import com.astrazeneca.rd.AutomatedDMTA.service.Scheduler;

	public class Scan {
	
		//Read file in Backlog folder
		public String readBacklog(String filePath) {
			String backlog_Expected_FileName = FilenameUtils.getName(filePath);
			File backlog = new File(filePath);
			for (final File fileEntry : backlog.listFiles()) {
				if (FilenameUtils.getName(fileEntry.getName()) == backlog_Expected_FileName ) //We have the right file
				{	
					//Go through each line of the txt file and store them as individual entries in a list
					BufferedReader in = new BufferedReader(new FileReader(backlog_Expected_FileName));
					String compoundLine; //Each individual compound's data is laid in a single line.
					List<String> compoundsList = new ArrayList<String>();
					//add all compound lines in a list. readLine reads a line of text, where a line is considered to be terminated by any one of a line feed ('\n'), a carriage return ('\r'), or a carriage return followed immediately by a linefeed.
					//TODO: Confirm with customer that lines have line-termination characters
					//TODO: Manu: Not sure how buffereader reacts with empty line, ie: returns something or null? if null, then an empty line will stop the reading
					while((compoundLine = in.readLine()) != null){
					    compoundsList.add(compoundLine);
					}
					//Convert the list of compounds into a fixed size array
					String[] compoundArr = compoundsList.toArray(new String[compoundsList.size()]);
					
					//Iterate through array, extracting individual data such as sampleNumber and SMILES
					//TODO: Expecting customer's details on how data is arranged in the txt file
					for (String compounLine : compoundArr) {
						String extracted_smiles = compounLine.substring(0, compounLine.indexOf(" ")); //create a substring with the first word in compounLine
						String extracted_sn = compounLine.substring(compounLine.indexOf(" sn") + 1); //create a substring with the first word starting with " sn" (just remove the front space)
						
					Compound c = new Compound();
					c.setSampleNumber(extracted_sn);
					c.setStage(StageType.BACKLOG);
					c.setSmiles(extracted_smiles);
					
					  For the Structure graph the SMILES string must first be encoded into URL format, see:
					Class URLEncoder: https://docs.oracle.com/javase/7/docs/api/java/net/URLEncoder.html,
					And for details:  https://stackoverflow.com/questions/14357970/java-library-for-url-encoding-if-necessary-like-a-browser
					then, embed it here: http://compounds.rd.astrazeneca.net/resources/structure/toimage/[SMILES_IN_URL_ENCODING_FORMAT]?inputFormat=SMILES&appid=pipelinepilot
				`	
					//A URL object containing concatenated the pipelinepilot AZ's URL for building a graphic representaton of compound structure and the ocmpound's smiles
					String structureGraph_file_with_smiles_Web_Path = "http://compounds.rd.astrazeneca.net/resources/structure/toimage/" + URLEncoder.encode(extracted_smiles, "UTF-8") + "?inputFormat=SMILES&appid=pipelinepilot";
					//TODO: Manu: Is it a good idea to save the URL in the db as a property for each compound
					URL structureGraphUrl = new URL(structureGraph_file_with_smiles_Web_Path);
					//TODO: Solve conflict with byte[] vs Image variable
					//Manu: Can we use image and ImageIO methods instead of byte[] and not re-invent the wheel? ;)
					Image structureGraph = ImageIO.read(structureGraphUrl);
					c.setStructureGraph(structureGraph);
						}
					}
				}
			}

		//Read file in design_File_Path
		public void readDesign(String filePath) {
			File design = new File(filePath);
			for (final File fileEntry : design.listFiles()) {
				if (FilenameUtils.getName(fileEntry.getName()) == FilenameUtils.getName(filePath) ) //We have the right file
				{	
					crashed = false; //The back-end system is working
					
					//TODO: Extract compound name and possibly other data: Expecting customer's details on what data is to be extracted
					
					//TODO: if (compound exists in database) {
					//	Compound.setStage = StageType.DESIGN;
				Else	
				}
					
					//TODO: Return data
					
					//TODO: Decide, if should wait for 5' after this 
					}
				}

		
		//Read file in synthesis_File_Path
		public void readSyntheis(String filePath) {
			File synthesis = new File(filePath);
			for (final File fileEntry : synthesis.listFiles()) {
				if (FilenameUtils.getName(fileEntry.getName()) == FilenameUtils.getName(filePath) ) //We have the right file
				{	
					crashed = false; //The back-end system is working
					
					//TODO: Extract compound name and possibly other data: Expecting customer's details on what data is to be extracted
					
					//TODO: thisCompound.setStage = StageType.SYNTHESIS;
					
					//TODO: Return data
					
					//TODO: Decide, if should wait for 5' after this 
					}
				}
			}
		
		//Read file in purification_File_Path
		public void readPurification(String filePath) {
			File purification = new File(filePath);
			for (final File fileEntry : purification.listFiles()) {
				if (FilenameUtils.getName(fileEntry.getName()) == FilenameUtils.getName(filePath) ) //We have the right file
				{
					crashed = false; //The back-end system is working
					
					//TODO: Extract compound name and possibly other data: Expecting customer's details on what data is to be extracted
					
					//TODO: thisCompound.setStage = StageType.PURIFICATION;
					
					//TODO: Return data
					
					//TODO: Decide, if should wait for 5' after this 
					}
				}
			}
		
		//Read file in testing_File_Path
		public void readTesting(String filePath) {
			File testing = new File(filePath);
			for (final File fileEntry : testing.listFiles()) {
				if (FilenameUtils.getName(fileEntry.getName()) == FilenameUtils.getName(filePath) ) //We have the right file
				{
					crashed = false; //The back-end system is working
					
					//TODO: Extract compound sampleNumbers and possibly other data: Expecting customer's details on what data is to be extracted
					
					//TODO: iterate through found compounds
						//TODO: thisCompound.setStage = StageType.TESTING;

					//TODO: Return data
					
					//TODO: Decide, if should wait for 5' after this 
					}
				}
			}
		
		//call wrttingImage and readFiles methods, ie: setGraph(writtingImage(filelocation))

	
	//Read image from file
	public byte[] readImage(File file) throws IOException
	{
	  Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Open File] " + file.getAbsolutePath());
	  InputStream is = new FileInputStream(file);
	  //TODO: Manu, do I really need to check file size?
	  long length = file.length();	  // Get the size of the file
	  // You cannot create an array using a long type. It needs to be an int type.
	  
       Before converting to an int type, check that file is not larger than Integer.MAX_VALUE.
	  if (length > Integer.MAX_VALUE)
	  {
	    // File is too large
	  }
	  
	  
	  // Create the byte array to hold the data
	  byte[] imageBytes = new byte[(int) length];
	  	  
	  // Read in the bytes
	  int offset = 0;
	  int numRead = 0;
	  while (offset < imageBytes.length && (numRead = is.read(imageBytes, offset, imageBytes.length - offset)) >= 0)
	  {
	    offset += numRead;
	  }
	  
	  // Ensure all the bytes have been read in
	  if (offset < imageBytes.length)
	  {
	    throw new IOException("Could not completely read file " + file.getName());
	  }
	  // Close the input stream and return bytes
	  is.close();
	  return imageBytes;
	}
	
	
	//Add image into the db
	ImageGraph myGraph = new ImageGraph();
	myGraph.setMimeType("image/jpg");
	file = new File("c:/dev/tempImage.png");
	try
	{
	  // Lets open an image file
		Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Call Read]");
		myGraph.setImageFile(mainCourse.readImage(file));
		Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Property set]");
	}
	catch (IOException ex)
	{
	  Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
	}
	
	fatController.create(myGraph);
	Logger.getLogger(Main.class.getName()).log(Level.INFO, "The number of objects is : " + fatController.getImageExampleCount());
	 
	//Read the image back from the JPA database
	// Get the list of images stored in the database.
	 List<ImageGraph> images = fatController.findImageExampleEntities;
			 for (int i = 0; i < images.size(); i++)
			 {
				 File outFile = new File("outGraph"+images.get(i).getId()+".png");
				 try {
					 mainCourse.writeFile(outfile, imags.get(i).getImageFile());
					 }
				 catch (IOException e) {
					 Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
					 
						 
				 }
			 }
	
	
	//Reads image form file and converts to byte[] that can be stored into DB
	private static byte[] writtingImage(String fileLocation) {
	      System.out.println("file lcation is"+fileLocation);
	     IOManager manager=new IOManager();
	        try {
	           return manager.getBytesFromFile(fileLocation);
	            
	        } catch (IOException e) {
	        }
	        return null;
	    }
}
*/