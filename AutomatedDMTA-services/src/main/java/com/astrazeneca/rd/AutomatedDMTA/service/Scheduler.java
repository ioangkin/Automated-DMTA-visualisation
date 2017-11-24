package com.astrazeneca.rd.AutomatedDMTA.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.log4j.chainsaw.Main;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//https://commons.apache.org/proper/commons-io/javadocs/api-release/index.html?org/apache/commons/io/FilenameUtils.html
import org.apache.commons.io.FilenameUtils; //How top import it? Maven dependences?

import com.astrazeneca.rd.AutomatedDMTA.model.Compound;
import com.astrazeneca.rd.AutomatedDMTA.model.StageType;

@Component("Scheduler")
@PropertySource("classpath:variable.properties") //Useful for file location properties. File in: AutomatedDMTA.servicesrc/main/resources/variable.properties

public class Scheduler {
	
	// Values picked up from 'AutomatedDMTA.servicesrc/main/resources/variable.properties' file
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
	
/*  For the following the SMILES string must first be encoded into URL format, see:
	Class URLEncoder: https://docs.oracle.com/javase/7/docs/api/java/net/URLEncoder.html,
	And for details:  https://stackoverflow.com/questions/14357970/java-library-for-url-encoding-if-necessary-like-a-browser
	then, embend it here: http://compounds.rd.astrazeneca.net/resources/structure/toimage/[SMILES_IN_URL_ENCODING_FORMAT]?inputFormat=SMILES&appid=pipelinepilot
*/
	final private String smiles_URL_Ecnoded_string;
	final private String StructureGraph_file_Web_Path;
	
	// Ensures only one job is running concurrently
	private static boolean locked=false;
	
	/**
	 * Method that runs at a specified interval to pick up and send out SDS sheets.
	 * Uses Spring Quartz jobs. Quartz job has bugs when shut down, and hence, uses 
	 * 'SchedulerShutDown.java' to shut down any quartz threads when quit. 
	 * 
	 */
	/*
	 * A Cron Expressions:Seconds, Minutes, Hours, Day-of-Month, Month, Day-of-Week, Year (optional field).
	 * The ‘/’ character can be used to specify increments to values. For example, if you put ‘0/15’ in the Minutes field,
	 * it means ‘every 15th minute of the hour, starting at minute zero’. If you used ‘3/20’ in the Minutes field,
	 * it would mean ‘every 20th minute of the hour, starting at minute three’ - or in other words it is the same as
	 * specifying ‘3,23,43’ in the Minutes field.
	 * 
	 * source: http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/tutorial-lesson-06.html
	 * additional: https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm
	 * 
	 */
	
	//This is the "cycling engine", going through folders looking for new or changes in compounds
	@Scheduled(cron = "0 0/5 0 * * ?") //runs every 5' //TODO: Manu: can I set this with a variable in the external file?
	public void scheduleJob() {
				
		//Monitor systme's health (whether is still operating)
		boolean crashed = true;  
		
		//Scan backlog folder for any new compounds
		//Call readBacklog(backlog_File_Path) to fill any new compounds to the DB
		
		//create compounds in DB
		//Add sampleNumber and SMILES strings to respective compound
		//Set compounds' Stage to backlog
		//Build SMILES URL encoded string
		//Retrieve structureGraph from web using URL ecnoded SMILES
		//Add structureGraph to DB
		
		//Read file in Backlog folder
		public void readBacklog(String filePath) {
			String backlog_Expected_FileName = FilenameUtils.getName(filePath);
			File backlog = new File(filePath);
			for (final File fileEntry : backlog.listFiles()) {
				if (FilenameUtils.getName(fileEntry.getName()) == backlog_Expected_FileName ) //We have the right file
				{	
					//Go through each line of the txt file and store them as individual entries in a list
					BufferedReader in = new BufferedReader(new FileReader(backlog_Expected_FileName));
					String entryLine; //Each individual compound's data is laid in a single line.
					List<String> compoundsList = new ArrayList<String>();
					//readLine reads a line of text, where a line is considered to be terminated by any one of a line feed ('\n'), a carriage return ('\r'), or a carriage return followed immediately by a linefeed.
					//TODO: Confirm with customer that lines have line-termination characters and that there will not be empty lines (I think BufferedReader considers epty lines as end of file?)
					while((entryLine = in.readLine()) != null){
					    compoundsList.add(entryLine);
					}
					//Convert the list to a fixed size array
					String[] compoundArr = compoundsList.toArray(new String[0]);
					
					//Go through each row, extracting individual data, such as sampleNumber and SMILES
					for (final String compounLine : compoundArr) {
						//Expecting customer's details on how data is arranged in the txt file
						String extracted_smiles = compounLine.substring(0, compounLine.indexOf(" ")); //create a substring with the first word in compounLine
						String extracted_sn = "sn" + compounLine.substring(compounLine.indexOf(" sn")); //create a substring starting with "sn" followed with anything starting at " sn" in compounLine
					}
					
					//TODO: iterate through found compounds
						//TODO: compound i = new Compound;
						//TODO: i.setSampleNumber(SampleNumber(i));
						//TODO: i.setSMILE(SMILE(i));
						//TODO: SMILES_IN_URL_ENCODING_FORMAT = smile_URL_Encoded_String(SMILE(i));
						//TODO: byte[] structureGraph = get SMILES_IN_URL_ENCODING_FORMAT.png from http://compounds.rd.astrazeneca.net/resources/structure/toimage/[SMILES_IN_URL_ENCODING_FORMAT]?inputFormat=SMILES&appid=pipelinepilot
						//TODO: i.setStructureGraph(structureGraph);
						//TODO: Compound(i).setStage(StageType.BACKLOG);
					
					//TODO: Return data
					
					//TODO: Decide, if should wait for 5' after this 
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
	  
      /* Before converting to an int type, check that file is not larger than Integer.MAX_VALUE.
	  if (length > Integer.MAX_VALUE)
	  {
	    // File is too large
	  }
	  */
	  
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
	
	
/*	//Reads image form file and converts to byte[] that can be stored into DB
	private static byte[] writtingImage(String fileLocation) {
	      System.out.println("file lcation is"+fileLocation);
	     IOManager manager=new IOManager();
	        try {
	           return manager.getBytesFromFile(fileLocation);
	            
	        } catch (IOException e) {
	        }
	        return null;
	    }*/
}
