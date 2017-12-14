package com.astrazeneca.rd.AutomatedDMTA.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
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
	

	// non hard-coded constants, may be edited by customer in @PropertySource
	@Value("${design_File_Path}")
	private String	design_File_Path;
	@Value("${synthesis_File_Path}")
	private String	synthesis_File_Path;
	@Value("${purification_File_Path}")
	private String	purification_File_Path;
	@Value("${testing_File_Path}")
	private String	testing_File_Path;
	@Value("${lineGraph_File_Path}")
	private String	LineGraph_File_Path;
	
	// Ensures only one job is running concurrently
	private static boolean locked=false;
	
	/**
	 * Building the BACKLOG/DESIGN stage:
	 * Populate the DB with all initially planned compounds
	 * Irrespectively on whether they moved in the cycle or not.
	 * For each compound the smiles and some identifier strings are stored
	 *
	 * This method runs only once and is outside of scheduler 
	 *   
	 * @return true: At least one compound was found and added to the DB or, for
	 *         existing compounds, their stage has been updated
	 * @return false: Path or file was not found or is empty
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws MalformedURLException 
	 * 
	 * @throws ?
	 * @throws ?
	 */
	boolean buildDesign() throws MalformedURLException, UnsupportedEncodingException, IOException
	{
	
		// Copy all lines from the text file containing the compounds into a compounds array
		String[] compoundsArr = Scan.textToArray(Scan.ShareFilePathToURL(design_File_Path));
		if (compoundsArr.equals(null) || compoundsArr.length == 0)
		{ 
			System.out.println("The cmpnd list from the desing stage returned empty or null");
		}

		String	extracted_id; //AZ or SN number
		String	extracted_smiles; //SMILES
		byte[]	structureGraph; //SMILES is needed for this
		
		boolean compoundRecorded = false;
		// Iterate through the compounds array, extracting attributes for each individual compound
		for (String compoundLine : compoundsArr)
		{
			
			// Extract identifier (AZ or SN number)
			extracted_id = Scan.extractIdentifier(compoundLine);
			if (extracted_id.equals("")) //Check that identification is found
			{
				System.out.println("no identification for the compound found");
			}
			
			// Extract SMILES (first word in the line)
			extracted_smiles = Scan.extractSmiles(compoundLine);
			if (extracted_smiles.equals("")) //Check that smiles is found
			{
				System.out.println("no smiles for the compound found");
			}
			
			// Retrieve StructureGraph and turn it to byte[] for DB
			structureGraph = Scan.BufferedImageToByteArray(Scan.UrlToBufferedImage(Scan.SmilesToUrl(extracted_smiles)));

			// Update existing compounds
			if (!Scan.newCompound(StageType.DESIGN, extracted_smiles, extracted_id, structureGraph).equals(null))
			{
				compoundRecorded = true;
			} else {

				System.out.println("The new compound was not stored in database (The Scan.newCompound returned flase)");
			}			
		}
		if (compoundRecorded = false)
		{
			System.out.println("no new compound was recorded in Design");
			return false;
		}
		
		return true;
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

				
	}
	
}
