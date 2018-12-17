/*package com.astrazeneca.rd.AutomatedDMTA.service;

import com.astrazeneca.rd.AutomatedDMTA.service.Scheduler_old;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

*//**
 * The test class for the Scheduler_old.
 *
 * @author  klfl423
 * @see Assert methods: http://junit.sourceforge.net/javadoc/org/junit/Assert.html
 *
 *//*
public class SchedulerTest
{
	Scheduler_old aScheduler = new Scheduler_old();

	//Stages's file paths
	String design_FilePath			=	"C:\\dev\\workspace\\AutomatedDMTA\\AutomatedDMTA-services\\src\\test\\resources\\Cycle\\Bioassay\\";
	String synthesis_FilePath		=	"C:\\dev\\workspace\\AutomatedDMTA\\AutomatedDMTA-services\\src\\test\\resources\\Cycle\\Synthesis\\";
	String purification_FilePath	=	"C:\\dev\\workspace\\AutomatedDMTA\\AutomatedDMTA-services\\src\\test\\resources\\Cycle\\Purification\\";
	String testing_FilePath 		=	"C:\\dev\\workspace\\AutomatedDMTA\\AutomatedDMTA-services\\src\\test\\resources\\Cycle\\Testing\\";

	//The data files in each respective stage
	File designFile = createDesignDataSetFile();
	File synthesisFile;
	File purtificationFile;
	File testingFile;
	
	//The array holding the DESIGN' compounds
	String[] dataSetLines = 
	{
		"CC#CCn1c2c(nc1N3CCCN[C@@H](C3)C(C)C)n(c(=O)n(c2=O)Cc4nc(c5ccccc5n4)C)Cc6ccccc6 AZ14125558_ERM321025625",
		"CC#CCn1c2c(nc1N3CCCN[C@@H](C3)C(C)C)n(c(=O)n(c2=O)Cc4nc(c5ccccc5n4)C)Cc6ccccc6 AZ14125558_ERM321025625",
		"CC#CCn1c2c(nc1N3CC4CNCC(C3)S4(=O)=O)n(c(=O)n(c2=O)Cc5nc(c6ccccc6n5)C)Cc7ccccc7 AZ14125558_ERM321057299",
		"CC#CCn1c2c(nc1N3C[C@H]4[C@@H]3CNCC4)n(c(=O)n(c2=O)Cc5nc(c6ccccc6n5)C)Cc7ccccc7 AZ14125558_ERM321011162",
		"CC#CCn1c2c(nc1N3C[C@@H]4C[C@H]3CN4)n(c(=O)n(c2=O)Cc5nc(c6ccccc6n5)C)Cc7ccccc7 AZ14125558_ERM320987057"
	};

	*//**
	 * Create a data set file (DESIGN stage) on disk
	 * 
	 * @author klfl423
	 * @throws IOException, Exception
	 * @return file
	 *//*
	@BeforeClass
	public File createDesignDataSetFile()
	{

		try ( BufferedWriter bw = 
				new BufferedWriter (new FileWriter ("testDataset.smi")) ) 
		{			
			for (String line : dataSetLines) {
				bw.write (line + "\n");
			}
			
			bw.close ();
			
		} catch (IOException e) {
			System.out.println("Error writting testDataSet.smi on disk: " + e.getMessage());
		    System.err.format("%s: File permission problems (IOError) when CREATING testDataset.smi%n");
		    System.err.println(e);
		}
	
		return null;
	}
	
	*//**
	 * Test the file paths as they come from the variable.properties file
	 * 
	 * @author klfl423
	 *//*
	@Test
	public void testFilePaths()
	{
		assertEquals("Unexpected DESIGN file Path", design_FilePath, Scheduler_old.getDesignFilePath());
		assertEquals("Unexpected SYNTHESIS file Path", synthesis_FilePath, Scheduler_old.getSynthesisFilePath());
		assertEquals("Unexpected PURIFICATION file Path", purification_FilePath, Scheduler_old.getPurificationFilePath());
		assertEquals("Unexpected TESTING file Path", testing_FilePath, Scheduler_old.getTestingFilePath());
	}
	
	*//**
	 * Compare extracted data from dataset.smi (DESIGN data set)
	 * 
	 * @author klfl423
	 *//*
	@Test
	public void testExtractedDataFromDataSet()
	{
		try
		{
			assertArrayEquals("the compound ID is not as expected in dataset.smi (DESIGN stage) ", dataSetLines, aScheduler.textToArray(designFile));
		} catch (IOException e)
		{
		    System.err.format("%s: File permission problems (IOException) when ACCESSING " + " file%n", designFile);
		    System.err.println(e);
		}
	}
	
	*//**
	 * Test the compound's extracted AZ identifier from the data set at DESIGN stage
	 * 
	 * @author klfl423
	 *//*
	@Test
	public void testExtractIdentifier()
	{
		String TestingAzNumber = "AZ14125558_ERM321025625";
		//String TestingAzNumber = dataSetLines[0].substring(dataSetLines[0].indexOf(" AZ") + 1);
		assertEquals("the AZ identiier is not as expected at dataset.smi in Design stage", TestingAzNumber, aScheduler.extractIdentifier(dataSetLines[0]));
	}	
	
	*//**
	 * Test the compound's extracted SMILES from the data set at DESIGN stage
	 * 
	 * @author klfl423
	 *//*
	@Test
	public void testExtractSmiles()
	{
		String testingSmiles = "CC#CCn1c2c(nc1N3CCCN[C@@H](C3)C(C)C)n(c(=O)n(c2=O)Cc4nc(c5ccccc5n4)C)Cc6ccccc6";
		//String TestingSmiles = dataSetLines[0].substring(0, dataSetLines[0].indexOf(" ")
		assertEquals("the SMILES is not as expected at dataset.smi in Design stage", testingSmiles, aScheduler.extractSmiles(dataSetLines[0]));
	}
		
	
	*//**
	 * Cleaning after test, such as deleting any test files from disk
	 * 
	 * 
	 * @author klfl423
	 *//*
	@AfterClass
	public Boolean stopTest(){

		//Delete dataset.smi (DESIGN stage compounds data file)
		try {
		    Files.delete(designFile.toPath());
		    return true;
		} catch (NoSuchFileException e1) {
		    System.err.format("%s: no such" + " file%n", designFile);
		    System.err.println(e1);
		} catch (IOException e) {
		    System.err.format("%s: File permission problems (IOException) when DELETING " + " file%n", designFile);
		    System.err.println(e);
		}
		
		return false;
		
	}
	
}
*/