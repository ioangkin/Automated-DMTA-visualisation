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

import org.apache.commons.io.FilenameUtils;

import com.astrazeneca.rd.AutomatedDMTA.model.Compound;
import com.astrazeneca.rd.AutomatedDMTA.model.StageType;
import com.astrazeneca.rd.AutomatedDMTA.service.Scan;


@Component("Scheduler")

public class Scheduler {
	
	// Ensures only one job is running concurrently
	private static boolean locked=false;
	
	//Is system still running?
	private static boolean crashed = true;  
	
	public void setcrashed(boolean set) {
		crashed = set;
	}
	
	//Return state of system
	public boolean getcrashed() {
		return crashed;
	}
	
	/**
	 * A Cron Expressions:Seconds, Minutes, Hours, Day-of-Month, Month, Day-of-Week, Year (optional field).
	 * The ‘/’ character can be used to specify increments to values. For example, if you put ‘0/15’ in the Minutes field,
	 * it means ‘every 15th minute of the hour, starting at minute zero’. If you used ‘3/20’ in the Minutes field,
	 * it would mean ‘every 20th minute of the hour, starting at minute three’ - or in other words it is the same as
	 * specifying ‘3,23,43’ in the Minutes field.
	 * 
	 * source: http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/tutorial-lesson-06.html
	 * additional: https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm
	 * 
	 **/

	//This is the "cycling engine", going through folders looking for new or changes in compounds
	@Scheduled(cron = "0 0/5 0 * * ?") //runs every 5'
	public void scheduleJob() {		

		
		//Scan through the folders, check for files and changes.
		//TODO: Consider returning something form each call, ie: the crashed boolean or a confirmation
		//TODO: Consider readBacklog to run only once, not in the cycle
		Scan.readBacklog(backlog_File_Path);
		//TODO: If internal tasks are repeated in following methods, consider keeping only one that gets path and stage 
		Scan.readDesign(design_File_Path);
		Scan.readSynthesis(synthesis_File_Path);
		Scan.readPurification(purification_File_Path);
		Scan.readTesting(testing_File_Path);
				
		//call wrttingImage and readFiles methods, ie: setGraph(writtingImage(filelocation))
	}
	
}
