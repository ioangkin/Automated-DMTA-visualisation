package com.astrazeneca.rd.AutomatedDMTA.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.log4j.chainsaw.Main;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component

@PropertySource("classpath:variable.properties") //Useful for file location properties. File in: AutomatedDMTA.servicesrc/main/resources/variable.properties

public class Scheduler {
	
	// Values picked up from 'AutomatedDMTA.servicesrc/main/resources/variable.properties' file
	final private String backlog_File_location;
	final private String design_File_location;
	final private String synthesis_File_location;
	final private String purification_File_location;
	final private String testing_File_location;
	final private String LineGraph_File_location;
	
	
	final private String sampleNumber;      // Value Needed for the smile
	final private String smile;             // Value Needed for the smile_Web_Link
	final private String smile_Web_Link;    // Value Needed for the StructureGraph_file_Web_location
	final private String StructureGraph_file_Web_location;
	
	
	/**
	 * Method that runs at a specified interval to pick up and send out SDS sheets.
	 * Uses Spring Quartz jobs. Quartz job has bugs when shut down, and hence, uses 
	 * 'SchedulerShutDown.java' to shut down any quartz threads when quit. 
	 * 
	 */
	/*
	 * Cron sub-expressions: Seconds, Minutes, Hours, Day-of-Month, Month, Day-of-Week, Year (optional field).
	 * The ‘/’ character can be used to specify increments to values. For example, if you put ‘0/15’ in the Minutes field,
	 * it means ‘every 15th minute of the hour, starting at minute zero’. If you used ‘3/20’ in the Minutes field,
	 * it would mean ‘every 20th minute of the hour, starting at minute three’ - or in other words it is the same as
	 * specifying ‘3,23,43’ in the Minutes field.
	 */
	@Scheduled(cron = "0 0/5 0 * * ?") //runs every 5'
	public void scheduleJob() {
		
		//call wrttingImage and readFiles methods, ie: setGraph(writtingImage(filelocation))
		
	}
	
	/**
	 * Read files from the provided location and extract info from these as 'keyfiles'.
	 */
	
	//get metadata or compound.sampleNumber from files
	public void readFiles(String location) {
		File folder = new File(location);
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.getName()) == /*//previous name*/ )	
					}
	
		
	}
	
	//Read image form file
	public byte[] readImage(File file) throws IOException
	{
	  Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Open File] " + file.getAbsolutePath());
	  InputStream is = new FileInputStream(file);
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
	 List<ImageGraph> images = fatController.findImageExampleEntities(
			 for (int i = 0; i < images.size(); i++)
			 {
				 File outFile = new File("outGraph"+images.get(i).getId()+".png");
				 try {
					 mainCourse.writeFile(outfile, imags.get(i).getImageFile());
					 }
				 catch (IOException e) {
					 Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
					 
						 
				 }
	
	
/*	//Reads image form file and converts to byte[] that can be stored into DB
	private static byte[] writtingImage(String fileLocation) {
	      System.out.println("file lication is"+fileLocation);
	     IOManager manager=new IOManager();
	        try {
	           return manager.getBytesFromFile(fileLocation);
	            
	        } catch (IOException e) {
	        }
	        return null;
	    }*/
}
