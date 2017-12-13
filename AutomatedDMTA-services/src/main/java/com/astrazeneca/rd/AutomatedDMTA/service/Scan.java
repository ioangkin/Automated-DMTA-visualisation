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
import org.slf4j.LoggerFactory;
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

//Useful for file location properties. File in: AutomatedDMTA.servicesrc/main/resources/variable.properties
@PropertySource("classpath:variable.properties")

public class Scan
{

	// non hard-coded constants, may be edited by customer in @PropertySource
	@Value("${backlog_File_Path}")
	private String	backlog_File_Path;
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

	@Autowired
	CompoundService	service;

	/**
	 * Convert a text file into string array, each line of the text file becomes
	 * a row of the array
	 * 
	 * @param filepath
	 * 
	 * @return array with the text lines
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	/*
	 * TODO: Add check and return null in case that: Path was not found File was
	 * not found File was empty Array is null or empty
	 */
	String[] textToArray(String filepath) throws FileNotFoundException, IOException
	{

		File file = new File(filepath); // the file as an object

		// Can't connect to fileshare or is empty
		if (!file.exists())
		{
			return null;
		}

		// Hold the text lines into a list
		List<String> lines = new ArrayList<String>();

		// TODO: Consider skipping iterating through files if we know the exact
		// filename
		for (final File fileEntry : file.listFiles())
		{
			// We have the right file
			if (FilenameUtils.getName(fileEntry.getName()) == FilenameUtils.getName(filepath)) 
			{
				BufferedReader br = null;
				try
				{
					br = new BufferedReader(new FileReader(FilenameUtils.getName(filepath)));
				}	
				catch (FileNotFoundException e1)
				{
					//File has not been found, connection may not been established
					System.out.println(e1.getMessage());
					return null;
				}
				catch (Exception e)
				{
					System.out.println(e.getMessage());
				}
				String inputLine;

				// Populate
				while ((inputLine = br.readLine()) != null)
				{

					// Ignore empty lines.
					if (inputLine.equals(""))
					{
						continue;
					}

					lines.add(inputLine);
				}

				br.close();

				//No new compounds found
				if (lines.equals(null) || lines.isEmpty())
				{ 
					System.out.println("nothing found in Design");
					System.out.println();
					//return null;
				}
				
			}
		}
		// Convert to array and return
		return lines.toArray(new String[lines.size()]);
	}

	/**
	 * Retrieve the LineGraph from Pipeline website using a SMILES. The process:
	 * 1. Encode the SMILES string into URL format, using Java
	 * URLEncoder.encode(text, "UTF-8") method 2. embed it in:
	 * http://compounds.rd.astrazeneca.net/resources/structure/toimage/[
	 * SMILES_IN_A_URL_ENCODING_FORMAT]?inputFormat=SMILES&appid=pipelinepilot
	 * 
	 * Example URL:
	 * http://compounds.rd.astrazeneca.net/resources/structure/toimage/CC%23CC[n
	 * ](c([n]c1N(C(=O)N(C2=O)Cc([n]c(cccc3)c3c3C)[n]3)C)N(Cc(c(C3)[nH]c4)[n]4)
	 * C3)c12?inputFormat=SMILES&appid=pipelinepilot
	 * 
	 * @param smiles
	 * 
	 * @return image file as a BufferedImage
	 * 
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 */
	BufferedImage collectStructureGraph(String smiles) throws IOException, MalformedURLException, UnsupportedEncodingException
	{
		BufferedImage image = null;

		// A URL object containing the complete URL for building the compound's
		// structure
		URL structureGraphUrl = new URL("http://compounds.rd.astrazeneca.net/resources/structure/toimage/" + URLEncoder.encode(smiles, "UTF-8") + "?inputFormat=SMILES&appid=pipelinepilot");
		// TODO: consider saving the URL as a compound's property as well

		try
		{
			// Read returns a BufferedImage
			image = ImageIO.read(structureGraphUrl);
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return image;
	}
	
	/**
	 * locate the file using its path as a string and convert it into a bufferedImage
	 * 
	 * @param path of the file
	 * 
	 * @return the BufferedImage
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	//TODO: Consider using inputStream method/class?
	BufferedImage collectLineGraph(String path) throws IOException, FileNotFoundException
	{

		//Try to find the file
		File file = null;
		try
		{
			file = new File(path);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

			
		//Try to convert the file into a bufferedImage
		BufferedImage bf = null;
		try
		{
			bf = ImageIO.read(file);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
			
		return bf;
	}

	/**
	 * Convert an bufferedImage into a byte array ready to be stored into the DB
	 * 
	 * @param image
	 * 
	 * @return byte[]: The byte array, ready to be stored in a DB
	 * @return null: No array
	 */
	byte[] BufferedImageToByteArray(BufferedImage image)
	{
		byte[] imageInByte = null;

		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			baos.flush();
			imageInByte = baos.toByteArray();
			baos.close();

		} catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
		
		return imageInByte;
	}

	
	/**
	 * Creates and save a new compound while in stages: BACKLOG, DESIGN,
	 * SYNTHESIS, and PURIFICATION
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
	Compound newCompound(StageType stage, String sn, String smiles, byte[] structureGraph) throws IOException
	{

		Compound c = new Compound();		// Create the new compound
		c.setStage(stage);					// Store its stage
		c.setSampleNumber(sn);				// Store the sample number
		c.setSmiles(smiles);				// Store the smiles
		c.setStructureGraph(structureGraph);// StructureGraph as a byte array

		// TODO: consider adding checks on whether an actual compound is
		// returned?

		return service.saveCompound(c);
	}

	/**
	 * update a compound while in stages TEST and COMPLETED
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
	Compound updateBioEssay(Compound c, StageType stage, String result, byte[] lineGraph)
	{

		try
		{
			c.setStage(stage); // Store stage
			c.setLineGraph(lineGraph); // Store lineGraph image as a byte[]
			c.setResult(result); // Store the results of the bioessay
		}
		catch (Exception e)
		{

				System.out.println(e.getMessage());
				return null;
		}
		
		return service.saveCompound(c);
	}

	/**
	 * Writes a file onto disk from a bufferedImage (following convertion from a byte[] from DB) 
	 * 
	 * @param bi:		The bufferedImage
	 * @param fileName:	The required file name for the disk
	 * 
	 * @return file:	the image
	 * @return null:	see console for error
	 */
	File BufferedImageToFile(BufferedImage bi, String fileName)
	{
		try {
			File file = new File(fileName);
		    ImageIO.write(bi, "png", file);
		    return file;
		} catch (IOException e) {
		    System.out.println(e.getMessage());
		}
		return null;
	}
	
	/**
	 * for any stage before testing (ie: BACKLOG, DESIGN, SYNTHESIS,
	 * PURIFICATION), Read file and build or update compound(s)
	 *
	 * @param stage,
	 *            where the file/compound is in
	 * 
	 * @return true: At least one compound was found and added to the DB or, for
	 *         existing compounds, their stage has been updated
	 * @return false: Path or file was not found or is empty
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	//TODO: This is the actual scanning method, rename to scan?
	boolean scanCompounds(StageType stage) throws FileNotFoundException, IOException
	{

		// Dictionary of stages and corresponding folders/files
		Map<StageType, String> filePath = new HashMap<StageType, String>();
		filePath.put(StageType.BACKLOG, backlog_File_Path);
		filePath.put(StageType.DESIGN, design_File_Path);
		filePath.put(StageType.SYNTHESIS, synthesis_File_Path);
		filePath.put(StageType.PURIFICATION, purification_File_Path);
		filePath.put(StageType.TESTING, testing_File_Path);

		// TODO: for lineGraph make sure not using a stage text path

		// Store the file's text lines into an array
		String[] compoundsArr = textToArray(filePath.get(stage));
		if (compoundsArr.equals(null) || compoundsArr.length == 0)
		{ // Path or file was not found or is empty
			return false;
		}

		// Iterate through the array, extracting any compound attributes
		for (String compoundLine : compoundsArr)
		{

			//Consider moving this into own method
			// Differentiate between AZ and Sample number.
			/* TODO: Consider using stage to chose between AZ or SM number, when
			requirements are more concrete 
			*/
			// AZ number is a word starting with " AZ", just remove the leading
			// space
			String extracted_id = compoundLine.substring(compoundLine.indexOf(" AZ") + 1);
			if (extracted_id.equals("")) // AZ number wasn't found, go for sn
											// number instead
			{
				// SN number is a word starting with " SN", just remove the
				// leading space
				extracted_id = compoundLine.substring(compoundLine.indexOf(" sn") + 1);
			}			

			// Extract SMILES (first word of each line)
			String extracted_smiles = compoundLine.substring(0, compoundLine.indexOf(" "));
			
			// Consider moving to own method
			// Retrieve StructureGraph from pipelinepilot and convert to
			// ByteArray (ready for storage in DB)
			byte[] structureGraph = BufferedImageToByteArray(collectStructureGraph(extracted_smiles));

			// Update existing compounds
			List<Compound> compoundsInDB = service.getAllCompounds();
			for (Compound dBCompound : compoundsInDB)
			{

				// Identifier for the existing compound.
				String dBCompoundSN = dBCompound.getSampleNumber();

				// For known compounds just update its stage
				if (dBCompoundSN.equals(extracted_id))
				{
					dBCompound.setStage(stage);
				}
				// If AZ number is missing, just update it.
				// Note: Could skip checking and just update to reduce complexity,
				// but better avoid any conflicts in case the extracted_id
				// doesn't have the expected value
				else if (!dBCompoundSN.substring(0, 1).equals("AZ"))
				{
						dBCompound.setSampleNumber(extracted_id);
				} else
				{ // A new compound is found, add it to the DB

					// Build and populate the new compound
					dBCompound = newCompound(StageType.DESIGN, extracted_id, extracted_smiles, structureGraph);
				}

				// For compounds in or after testing stage
				String extractedResults = null; //TODO: set source following updated requirements
				
				// Compound was moved in or after testing stage
				if (!dBCompound.getResult().equals(extractedResults))
				//Note: Not using following condition, as compound may already been registered in testing with its results saved 
				//if (dBCompound.getStage().ordinal()>=StageType.TESTING.ordinal())

				{
					// Retrieve LineGraph
					BufferedImage bf = collectLineGraph(LineGraph_File_Path);
					
					//And convert it to a byte[] ready to be stored in db
					byte[] LineGraph = BufferedImageToByteArray(bf);
					
					// Update compound
					dBCompound = updateBioEssay(dBCompound, stage, extractedResults, LineGraph);
				}
			}
		}
		return true; // data found and at least one compound edited or saved
	}

	/**
	 * Read image from remote location (ie: fileShare) and stram it into a byte array
	 * call readImage to get graph, ie: StoreImage(readImage(filelocation))
	 * 
	 * Similarly, create writeImage(file) and call it to set graphs,
	 * ie: setGraph(writtingImage(filelocation))
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public byte[] readImage(File file) throws IOException
	{
		Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Open File] " + file.getAbsolutePath());
		InputStream is = new FileInputStream(file);
		long length = file.length();
		// You cannot create an array using a long type. It needs to be an int type.


		// Before converting to an int type, check that file is not larger than
		// Integer.MAX_VALUE.
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
		
		// Close the input stream 
		is.close();
		
		// Ensure all the bytes have been read in
		if (offset < imageBytes.length)
		{
			throw new IOException("Could not completely read file " + file.getName());
		}

		return imageBytes;
	}
	
	/*	Alternative method to convert image file to buyte[].
	Source: https://blogs.oracle.com/adf/jpa-insert-and-retrieve-clob-and-blob-types
	Not working due to the call to IOManager
	 */
	/**
	 * Reads image from file and converts to byte[] that can be stored into DB
	 *  
	 * @param fileLocation
	 * @return
	 */
	/*	private static byte[] writtingImage(String fileLocation) {
      System.out.println("file lcation is"+fileLocation);
     IOManager manager=new IOManager();
        try {
           return manager.getBytesFromFile(fileLocation);
            
        } catch (IOException e) {
        }
        return null;
 }*/
}
