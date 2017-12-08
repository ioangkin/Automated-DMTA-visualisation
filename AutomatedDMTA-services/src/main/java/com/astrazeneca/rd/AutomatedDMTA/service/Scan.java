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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

@PropertySource("classpath:variable.properties") //Useful for file location properties. File in: AutomatedDMTA.servicesrc/main/resources/variable.properties

public class Scan {
		
	//non hard-coded constants, may be edited by customer in: 'AutomatedDMTA.services/src/main/resources/variable.properties' file
	@Value("${backlog_File_Path}")
	final private String backlog_File_Path;
	@Value("${design_File_Path}")
	final private String design_File_Path;
	@Value("${synthesis_File_Path}")
	final private String synthesis_File_Path;
	@Value("${purification_File_Path}")
	final private String purification_File_Path;
	@Value("${testing_File_Path}")
	final private String testing_File_Path;
	@Value("${LineGraph_File_Path}")
	final private String LineGraph_File_Path;
	
	@Autowired CompoundService service;

	
	/**
	 * Convert a text file into string array, each line of the file is an array's row
	 * 
	 * @param filepath
	 * 
	 * @return array of the text lines
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
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
				
				String inputLine;
				
				//Populate 
				while((inputLine = bufferedReader.readLine()) != null){

		            // Ignore empty lines.
		            if(inputLine.equals("")) {
		                continue;
		            }

				    lines.add(inputLine);
				}
				bufferedReader.close();
				
				/* TODO: Optional: check that list is not empty
				if (lines.equals(null) || lines.isEmpty()) {
					lines.add(0, "false"); //No new compounds found
					}
				*/
			}
		}
		return lines.toArray(new String[lines.size()]); //Convert to array and return
	}
	/**
	 * Retrieve the LineGraph from Pipeline website using a SMILES
	 * To get the file:
	 * 	1. Encode the SMILES string into URL format, see: Java URLEncode
	 *  2. embed it in: http://compounds.rd.astrazeneca.net/resources/structure/toimage/[SMILES_IN_A_URL_ENCODING_FORMAT]?inputFormat=SMILES&appid=pipelinepilot
	 * 
	 * @param smiles
	 * 
	 * @return image file as a BufferedImage
	 * 
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 */
	BufferedImage getStructureGraph(String smiles) throws IOException, MalformedURLException, UnsupportedEncodingException {

		URL structureGraphUrl = new URL("http://compounds.rd.astrazeneca.net/resources/structure/toimage/" + URLEncoder.encode(smiles, "UTF-8") + "?inputFormat=SMILES&appid=pipelinepilot"); //A URL object containing the complete URL for building the compound's structure
		//TODO: consider saving the URL as a compound's property as well (c.PipeLinePilotUrl)
		
		return ImageIO.read(structureGraphUrl); //Read returns a BufferedImage. See ImageIO in Oracle's docs
	}
	
	/**
	 * Convert an image file into a byte array ready to be stored to the DB
	 * 
	 * @param image
	 * 
	 * @return byte[]: The byte array, ready to be stored in a DB 
	 * @return null: No array 
	 */
	
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
	 * Creates and save a new compound while in stages: BACKLOG, DESIGN, SYNTHESIS, and PURIFICATION
	 * 
	 * @param stage
	 * @param sn
	 * @param smiles
	 * @param structureGraph
	 * 
	 * @return Compound
	 * 
	 * @throws IOException
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
	 * Creates and save a new compound while in stages: TEST and COMPLETED
	 * 
	 * @param stage
	 * @param sn
	 * @param smiles
	 * @param structureGraph
	 * @param lineGraph
	 * @param results
	 * 
	 * @return Compound
	 * 
	 * @throws IOException
	 */
	Compound newCompound (StageType stage, String sn, String smiles, byte[] structureGraph, byte[] lineGraph, String result) throws IOException {

		Compound c = new Compound();
		c.setStage(stage);
		c.setSampleNumber(sn);
		c.setSmiles(smiles);
		c.setStructureGraph(structureGraph);
		c.setLineGraph(lineGraph);
		c.setResult(result);
	
		//TODO: consider adding some check on whether an actual compound is returned?
		service.saveCompound(c); 
		
		return c;
	}
	
	/**
	 * Read file and build or update compound(s) (for stages: BACKLOG, DESIGN, SYNTHESIS, PURIFICATION)
	 *
	 * @param filePath: the file containing the compound(s) data //TODO: This can be omitted given the stage
	 * @param stage: The stage the file is in
	 * 
	 * @return true: At least on compound was found and added to the DB or if it was existing its stage was updated
	 * @return false: Path or file was not found or is empty
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	boolean readFile(StageType stage) throws FileNotFoundException, IOException {
		
		//Dictionary of stages and corresponding folders/files
		Map<StageType,String> filePath = new HashMap<StageType, String>();
		filePath.put(StageType.BACKLOG, backlog_File_Path);
		filePath.put(StageType.DESIGN, design_File_Path);
		filePath.put(StageType.SYNTHESIS, synthesis_File_Path);
		filePath.put(StageType.PURIFICATION, purification_File_Path);
		filePath.put(StageType.TESTING, testing_File_Path);
		//

		/*
		//TODO: make sure to retrieve lineGraphs using the image path not the stage path with the compound's data
		@Value("${LineGraph_File_Path}")
		final private String LineGraph_File_Path;
		*/
		
		//Store the file's text lines into an array
		String[] compoundsArr = TextToArray(filePath.get(stage)); 
		
		//Path or file was not found or is empty
		if (compoundsArr.equals(null) || compoundsArr.length == 0) {
			return false; 
		}
			
		//Iterate through the compounds array, extracting compound attributes
		//TODO: Expecting customer's details on how data is arranged in the txt file
		for (String compoundLine : compoundsArr) {

			//Extract AZ number or Sample number
			//TODO: Consider using stage to chose between AZ or SM number when requirements are more concrete
			String extracted_id = compoundLine.substring(compoundLine.indexOf(" AZ") + 1); //The AZ number is a word starting with " AZ" (just remove the leading space)
			if (extracted_id.equals("")) { //AZ number wasn't found, go for sn number instead
				extracted_id = compoundLine.substring(compoundLine.indexOf(" sn") + 1); //The sample number (sn) is a word starting with " sn" (just remove the leading space)
			}
			
			//Extract SMILES (first word of each line) 
			String extracted_smiles = compoundLine.substring(0, compoundLine.indexOf(" "));
			
			//Retrieve StructureGraph from pipelinepilot and convert to ByteArray (ready for storage in DB)
			byte[] structureGraph = imageToByteArray(getStructureGraph(extracted_smiles));
			
			//Update existing compounds
			List <Compound> compoundsInDB = service.getAllCompounds();
			for (Compound dBcompound : compoundsInDB) {
				
				String dBcompoundSN = dBcompound.getSampleNumber(); //The existing compound's identifier
				
				if (dBcompoundSN.equals(extracted_id)){ //It is a known compound
					dBcompound.setStage(stage); //Update its stage
					
					//update identifier if AZ number is missing 
					if (!dBcompoundSN.substring(0, 1).equals("AZ")){
						dBcompound.setSampleNumber(extracted_id);
					}
				}
				else { //A new compound is found, add it to the DB
			
					//Build and populate the new compound
					newCompound(StageType.DESIGN, extracted_id, extracted_smiles, structureGraph);

					//TODO: add code for compounds in testing and completed (include attributes: result and lineGraph)
					}
				}
			}
		return true; //data found and at least one compound edited or saved 
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
	