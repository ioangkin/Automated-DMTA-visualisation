package com.astrazeneca.rd.AutomatedDMTA.service;

import java.io.File;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component

//@PropertySource("classpath:mailAuth.properties") //Check it out for file location properties
//file properties in: src/main/resources/mailAuth.properties

public class Scheduler {
	
	
	/**
	 * Method that runs at a specified interval to pick up and send out SDS sheets.
	 * Uses Spring Quartz jobs. Quartz job has bugs when shut down, and hence, uses 
	 * 'SchedulerShutDown.java' to shut down any quartz threads when quit. 
	 * 
	 */
	@Scheduled(cron = "0 30 12 * * ?") //Change the time to rn every 5' instead of at 12:30
	public void scheduleJob() {
		
		//call wrttingImage and readFiles methods, ie: setStructureGraph(writtingImage(filelocation))
		
	}
	
	/**
	 * Read files from the provided location and extract info from these as 'keyfiles'.
	 */
	
	//get metadata or compound.serialNumber from files
	public void readFiles(String location) {
		File folder = new File(location);
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.getName() == /*//previous name*/ )	
					}
	
		
	}
	
	//Reads image form file and converts to byte[] that can be stored into DB
	private static byte[] writtingImage(String fileLocation) {
	      System.out.println("file lication is"+fileLocation);
	     IOManager manager=new IOManager();
	        try {
	           return manager.getBytesFromFile(fileLocation);
	            
	        } catch (IOException e) {
	        }
	        return null;
	    }
}
