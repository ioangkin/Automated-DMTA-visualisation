package com.astrazeneca.rd.AutomatedDMTA.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
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
		
		@Autowired CompoundService service;
		
		//Convert a txt file into string array, each line in the text file is a row
		/*		TODO: Add check and return null in case that:
		 *		Path was not found
		 *		File was not found
		 *		File was empty
		 *		Array is null or empty
		*/
		String[] TextToArray (String filepath) throws FileNotFoundException, IOException{

			List<String> lines = new ArrayList<String>(); //Hold the text lines into a list

			File file = new File(filepath); //the file as an object
			
			//TODO: Consider skipping iterating through files if we know the exact filename
			for (final File fileEntry : file.listFiles()) {
				if (FilenameUtils.getName(fileEntry.getName()) == FilenameUtils.getName(filepath) ) //We have the right file
				{
					FileReader filereader = new FileReader(FilenameUtils.getName(filepath));
					BufferedReader bufferedReader = new BufferedReader(filereader);
					String line;
					//Populate 
					//For readLine a line is considered to be terminated by any one of a line feed ('\n'), a carriage return ('\r'), or a carriage return followed immediately by a linefeed.
					while((line = bufferedReader.readLine()) != null){
					    lines.add(line);
					}
					bufferedReader.close();
					
					/*
					//Path or file was not found or is empty
					if (lines.equals(null) || lines.isEmpty()) {
						lines.add(0, "false"); //No new compounds found
						}
					*/
				}
			}
			return lines.toArray(new String[lines.size()]); //Convert to array and return
		}
		
		//Retrieve a compound LineGraph from Pipeline website using a SMILES
		/*
		* Get the structure graph image from the web. Steps:
		* 1. Encode the SMILES string into URL format, see: Java URLEncode
		* 2. embed it in: http://compounds.rd.astrazeneca.net/resources/structure/toimage/[SMILES_IN_URL_ENCODING_FORMAT]?inputFormat=SMILES&appid=pipelinepilot
		*/
		BufferedImage getStructureGraph(String SMILES) throws IOException, MalformedURLException, UnsupportedEncodingException {

			URL structureGraphUrl = new URL("http://compounds.rd.astrazeneca.net/resources/structure/toimage/" + URLEncoder.encode(SMILES, "UTF-8") + "?inputFormat=SMILES&appid=pipelinepilot"); //A URL object containing the complete URL for building the compound's structure
			//TODO: consider saving the URL as a compound's property as well (c.PipeLinePilotUrl)
			return ImageIO.read(structureGraphUrl); //Read returns a BufferedImage. See ImageIO in Oracle's docs
		}
		
		byte[] imageToByteArray (BufferedImage image) {
			try{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write( image, "png", baos );
				baos.flush();
				byte[] imageInByte = baos.toByteArray();
				baos.close();
				return imageInByte;
				}
			catch(IOException e){
				System.out.println(e.getMessage());
			}
			return null; 
		}
		
		/**
		 * Create and save a new compound

		 * @return c
		 */
		Compound newCompound (StageType stage, String sn, String smiles, byte[] structureGraph) throws IOException {

		Compound c = new Compound();
		c.setStage(stage);
		c.setSampleNumber(sn);
		c.setSmiles(smiles);
		c.setStructureGraph(structureGraph);
	
		//TODO: consider adding some check on whether an actual compound is returned?
		service.saveCompound(c); 
		
		return c;
		}
		
		/**
		 * Read file for Backlog stage (happens only once, unlike the rest stages that are part of the cycle)
		 * 
		 * @param filePath: path and file containing the compound(s)
		 * 
		 * @return boolean:
		 *  true: Compounds were found and added
		 * 	false: Path or file was not found or is empty
		 * 
		 * @throws IOException 
		 * @throws FileNotFoundException 
		 */
		boolean readBacklog(String filePath) throws FileNotFoundException, IOException {

			//Store the file's text lines into an array
			String[] compoundsArr = TextToArray(filePath);
			
			//Path or file was not found or is empty
			if (compoundsArr.equals(null) || compoundsArr.length == 0) {
				 return false; 
				}
			
			//Iterate through the compounds' array, extracting individual data such as sampleNumber and SMILES
			//TODO: Expecting customer's details on how data is arranged in txt file
			for (String compoundLine : compoundsArr) {

				String extracted_sn = compoundLine.substring(compoundLine.indexOf(" sn") + 1);	//Extract Sample number (a word starting with " sn", just remove the space)
				String extracted_smiles = compoundLine.substring(0, compoundLine.indexOf(" "));	//Extract SMILES (first word of each line) 
				byte[] structureGraph = imageToByteArray(getStructureGraph(extracted_smiles));	//Retrieve StructureGraph from pipelinepilot and convert to ByteArray

				//Build and populate new compound
				newCompound(StageType.BACKLOG, extracted_sn, extracted_smiles, structureGraph);

				}

			return true;
			}

		
		/**
		* Read file for Design stage (this check happens only once, unlike the rest stages that are part of the cycle)
		* 
		* Variable: filePath: path and file containing the compound(s)
		* 
		* Return:
		* 	true: Compounds were found and added
		* 	false: Path or file was not found or is empty
		*/
		boolean readDesign(String filePath) {
			
			//Store the file's text lines into an array
			String[] compoundsArr = TextToArray(filePath); 
			
			//Path or file was not found or is empty
			if (compoundsArr.equals(null) || compoundsArr.length == 0) {
				 return false; 
				}
				
			//Iterate through the compounds' array, extracting individual data such as sampleNumber and SMILES
			//TODO: Expecting customer's details on how data is arranged in the txt file
			for (String compoundLine : compoundsArr) {
				

				//Extract Sample number (a word starting with " sn", just remove the space)
				String extracted_sn = compoundLine.substring(compoundLine.indexOf(" sn") + 1); 
				
				//Search  DB for a compound with extracted_sn and change its state into DESIGN
				//TODO: Consider updating other properties as well, ie SMILE, etc, in case that they have not been updated
				List <Compound> compoundsInDB = service.getAllCompounds();
				boolean Edited;
				for (Compound k : compoundsInDB) {
					if (extracted_sn.equals(k.getSampleNumber())){ 
						k.setStage(StageType.DESIGN); //Edit stage for existing compounds
						Edited = true;
						}
					else { //A new compound found, extract more data, build and populate the new compound

						Compound c = new Compound();

						//Extract SMILES (first word of each line) 
						String extracted_smiles = compoundLine.substring(0, compoundLine.indexOf(" "));
						c.setSmiles(extracted_smiles);

						c.setSampleNumber(extracted_sn);

						//grab StructureGraph from pipelinepilot
						BufferedImage structureGraph = getStructureGraph(extracted_smiles);
						//Convert StructureGraph to ByteArray
						byte[] imageInBytes = imageToByteArray(structureGraph);
						c.setStructureGraph(imageInBytes);

						c.setStage(StageType.DESIGN);
						
						//Save compound into the repository
						//TODO: consider adding some check on whether an actual compound is returned?
						service.saveCompound(c); 
						}

					return true; //data found and at least one compound edited or saved 
					}

				return false; //no data found? 
				}

	
		//Read file in synthesis_File_Path
		boolean readSynthesis(String filePath) {
			File synthesis = new File(filePath);
			for (final File fileEntry : synthesis.listFiles()) {
				if (FilenameUtils.getName(fileEntry.getName()) == FilenameUtils.getName(filePath) ) //We have the right file
				{	
				
					//TODO: Extract compound name and possibly other data: Expecting customer's details on what data is to be extracted
					
					//TODO: thisCompound.setStage = StageType.SYNTHESIS;
					
					//TODO: Return data
					
					//TODO: Decide, if should wait for 5' after this 
					}
				}
			}
		
		//Read file in purification_File_Path
		static void readPurification(String filePath) {
			File purification = new File(filePath);
			for (final File fileEntry : purification.listFiles()) {
				if (FilenameUtils.getName(fileEntry.getName()) == FilenameUtils.getName(filePath) ) //We have the right file
				{
					
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
	  //TODO: Manu, why checking file size?
	  long length = file.length();
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
	myGraph.setMimeType("image/png");
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
	