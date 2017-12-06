package com.astrazeneca.rd.AutomatedDMTA.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Image;
import java.awt.image.BufferedImage;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//https://commons.apache.org/proper/commons-io/javadocs/api-release/index.html?org/apache/commons/io/FilenameUtils.html
import org.apache.commons.io.FilenameUtils;

import com.astrazeneca.rd.AutomatedDMTA.model.Compound;
import com.astrazeneca.rd.AutomatedDMTA.repository.CompoundRepository;
import com.astrazeneca.rd.AutomatedDMTA.service.CompoundService;
import com.astrazeneca.rd.AutomatedDMTA.model.StageType;
//import com.astrazeneca.rd.AutomatedDMTA.service.Scheduler;

	public class Scan {
		
		/*Implementing CompoundRepository interface for searching and managing compounds in DB
		But not sure is needed here, as is implemented in service and in this class I create and call service for these tasks*/
		//@Autowired private CompoundRepository compoundRepository;
		/**
		 * @return 
		 * @throws java.lang.Exception
		 */
	
		//Convert a txt file into string array, each line in the text file is a row
		public String[] TextToArray (String filepath) {
			String filename = FilenameUtils.getName(filepath); //extract the filename from the given path
			File file = new File(filepath); //the file as an object
			for (final File fileEntry : file.listFiles()) { //Why going through the files? there supposed to be only one file
				if (FilenameUtils.getName(fileEntry.getName()) == FilenameUtils.getName(filepath) ) //We have the right file
				{
					BufferedReader bufferedReader = new BufferedReader(new FileReader(FilenameUtils.getName(filepath)));
					String line;
					List<String> lines = new ArrayList<String>(); //Hold the lines of the text into a list
					//Populate 
					//For readLine a line is considered to be terminated by any one of a line feed ('\n'), a carriage return ('\r'), or a carriage return followed immediately by a linefeed.
					while((line = bufferedReader.readLine()) != null){
					    lines.add(line);
					}
					bufferedReader.close();
					
					
					return lines.toArray(new String[lines.size()]); //wrap the list into an array and return it
				}
			}
		}
		
		//Retrieve a compound LineGraph from Pipeline website using a SMILES
		/*
		* Get the structure graph image from the web. Steps:
		* 1. Encode the SMILES string into URL format, see: Java URLEncode
		* 2. embed it in: http://compounds.rd.astrazeneca.net/resources/structure/toimage/[SMILES_IN_URL_ENCODING_FORMAT]?inputFormat=SMILES&appid=pipelinepilot
		*/
		public Image getLineGraph(String SMILES) {
			//A URL object containing the complete URL for building the compound's structure
			URL structureGraphUrl = new URL("http://compounds.rd.astrazeneca.net/resources/structure/toimage/" + URLEncoder.encode(SMILES, "UTF-8") + "?inputFormat=SMILES&appid=pipelinepilot");
			//TODO: consider saving the URL as a compound's property as well (c.PipeLinePilotUrl)
			//TODO: Manu: Can we use image and ImageIO methods instead of byte[] and not re-invent the wheel? 
			return ImageIO.read(structureGraphUrl);
		}
		
		public Byte[] imageToByteArray (Image image) {
			try{
				BufferedImage originalImage = ImageIO.read(image);

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write( originalImage, "jpg", baos );
					baos.flush();
					byte[] imageInByte = baos.toByteArray();
					baos.close();

					}catch(IOException e){
						System.out.println(e.getMessage());
					}
				   }
		}
		
		//Read file for Backlog stage (this check happens only once, unlike the rest stages that are part of the cycle)
		public boolean readBacklog(String filePath) {

			//Convert the text file into a string array
			String[] compoundsArr = TextToArray(filePath);
			
			//Path or file was not found or is empty
			if (compoundsArr.equals(null) || !(compoundsArr instanceof String[]) || compoundsArr.length == 0 || compoundsArr[0].isEmpty()) {
				return false; //No new compounds found
				}
			//Iterate through the compounds' array, extracting individual data such as sampleNumber and SMILES
			//TODO: Expecting customer's details on how data is arranged in txt file
			for (String compoundLine : compoundsArr) {

				//Extract SMILES: First word of each line 
				String extracted_smiles = compoundLine.substring(0, compoundLine.indexOf(" "));
				
				//Extract Sample number: The word starting with " sn" (just remove the front space)
				String extracted_sn = compoundLine.substring(compoundLine.indexOf(" sn") + 1); 
				
				//Retrieve StructureGraph
				Image structureGraph = getLineGraph(extracted_smiles);
				
				//Convert StructureGraph to ByteArray
				byte[] imageInBytes = imageToBytes(structureGraph);
				
				//Build a new compound with the found properties
				Compound c = new Compound();
				c.setSmiles(extracted_smiles);
				c.setSampleNumber(extracted_sn);
				c.setStage(StageType.BACKLOG);

				//TODO: LineGraphAsByteArray = stream LineGraph to byte[]
				c.setStructureGraph(LineGraphAsByteArray));
				
				//Save compound into the repository
				CompoundService s = new CompoundService();
				s.saveCompound(c); //TODO: Maybe add some check on whether an actual compound is returned?
				}
			return true; //Successful 
			}

		//Read file in design_File_Path
		public boolean readDesign(String filePath) {
			
			//Convert the text file into a string array
			String[] compoundsArr = TextToArray(filePath); 
			
			//Path or file was not found or is empty
			if (compoundsArr.equals(null) || !(compoundsArr instanceof String[]) || compoundsArr.length == 0 || compoundsArr[0].isEmpty()) {
				return false; //No file or compounds found
				}
			
			//Iterate through the compounds' array, extracting individual data such as sampleNumber and SMILES
			//TODO: Expecting customer's details on how data is arranged in txt file
			for (String compoundLine : compoundsArr) {
				String extracted_smiles = compoundLine.substring(0, compoundLine.indexOf(" ")); //The SMILES is the first word of each line 
				String extracted_sn = compoundLine.substring(compoundLine.indexOf(" sn") + 1); //The Sample number is the word starting with " sn" (just remove the front space)

				/*TODO: Search  DB for a compound with extracted_sn and change its state into DESIGN, exit 
				List <Compound> compoundsInDB = service.getAllCompounds();
				//boolean compoundEdited;
				for (Compound c : compoundsInDB) {
					if (extracted_smiles.equals(c.getSampleNumber())){ //Edit stage for existing compounds
						c.setStage(StageType.DESIGN);
						compoundEdited = true;
						}
					else { //add new compounds
						c.setSmiles(extracted_smiles);
						c.setSampleNumber(extracted_sn);
						c.setStage(StageType.DESIGN);
						}
					}
				
				
				Compound c = new Compound();

				 * if (compound with extracted_sn exists in DB) {(compound with extracted_sn).setStage = StageType.DESIGN;}
				CompoundService s = new CompoundService();
				
				List<Compound> co = compoundRepository.findBySampleNumber("CompoundA")
				List<Compound> compoundsWithSN = getCompoundsBySampleNumber*/
				//Else Create a new compound with the found properties
				//Create a new compound with the found properties
				Compound c = new Compound();
				c.setSmiles(extracted_smiles);
				c.setSampleNumber(extracted_sn);
				c.setStage(StageType.BACKLOG);
				
				CompoundService s = new CompoundService();
					
				}
					
					//TODO: Return data
										}
				}

/*		public Compound find(Compound c) {

			List<Compound> DBCompound = compoundRepository.findBySampleNumber(c.getSampleNumber());
			return c
			
			assertTrue(fromDB.size() == 1);
			
			Compound k = fromDB.get(0);
			
			Compound pFromDB = compoundRepository.findOne(k.getId());
			assertNotNull(pFromDB);
			assertTrue(c.getSampleNumber() == pFromDB.getSampleNumber());
			assertTrue(c.getSmiles() == pFromDB.getSmiles());
			compoundRepository.deleteAll();
		}*/
		
		//Read file in synthesis_File_Path
		public static void readSynthesis(String filePath) {
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
		public static void readPurification(String filePath) {
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
		public static void readTesting(String filePath) {
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
	  
      //Before converting to an int type, check that file is not larger than Integer.MAX_VALUE.
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
	}
	