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
import com.astrazeneca.rd.AutomatedDMTA.model.StageType;
import com.astrazeneca.rd.AutomatedDMTA.repository.CompoundRepository;
import com.astrazeneca.rd.AutomatedDMTA.service.CompoundService;

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

	// Dictionary for mapping above file paths to corresponding stages
	private Map<StageType, String> filePath = new HashMap<StageType, String>();
		
	@Autowired
	static CompoundService	service;

	/**
	 * Accept a path sting, ie: "\\pipeline04.rd.astrazeneca.net\SharedData\autodmta\run\dataset_original.smi"
	 * And convert it into URL, so that it can be collected by ImageIO.read(url) later on
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	static URL ShareFilePathToURL(String path) throws IOException, FileNotFoundException, MalformedURLException, UnsupportedEncodingException
	{
			return new URL(path);

	}
	
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
	static String[] textToArray(String filepath) throws FileNotFoundException, IOException
	{
		File file = new File(filepath);

		if (!file.exists())
		{
			System.out.println("design cmpnd list file or its folder is empty or doesn't exist");
			return null;
		}

		// Hold the text lines into a list
		List<String> lines = new ArrayList<String>();

		// TODO: Consider skip iterating through files if we know the exact filename
		for (final File fileEntry : file.listFiles())
		{			
			if (FilenameUtils.getName(fileEntry.getName()) == FilenameUtils.getName(filepath))
			{// We have the right file
				
				BufferedReader br = null;
				try
				{
					br = new BufferedReader(new FileReader(FilenameUtils.getName(filepath)));
				} catch (FileNotFoundException e1)
				{
					// File not found, connection may failed to establish
					System.out.println(e1.getMessage());
					return null;
					
				} catch (Exception e)
				{
					System.out.println(e.getMessage());
				}

				// Populate
				String inputLine;
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

				// No new compounds found
				if (lines.equals(null) || lines.isEmpty())
				{
					System.out.println("nothing found in Design stage");
				}
			}
		}
		// Convert to array and return
		return lines.toArray(new String[lines.size()]);
	}

	/**
	 * Go through a single line of compound information from the text file found
	 * in the respective stage folder and try to extract the AZ identification
	 * number. if AZ isn't found go for SN instead
	 * 
	 * TODO: Differentiate between AZ and SN numbers. When requirements are more
	 * concrete Consider using stage to chose between AZ and SN numbers
	 * 
	 * @param compoundLine
	 *            The line of text containing the compound information extracted
	 *            from the text file found in the respective stage folder
	 * 
	 * @return extracted_id string
	 */
	static String extractIdentifier(String compoundLine)
	{

		String extracted_id = null;

		try
		{
			// bblock (AZ) number is a word starting with " AZ", just remove leading space
			extracted_id = compoundLine.substring(compoundLine.indexOf(" AZ") + 1);

			
			if (extracted_id.equals("")) // AZ number wasn't found, go for sn number instead
			{
				// SN number is a word starting with " SN", just remove leading space
				extracted_id = compoundLine.substring(compoundLine.indexOf(" sn") + 1);
			}
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

		return extracted_id;
	}

	/**
	 * Go through a single line of compound information from the text file found
	 * in the respective stage folder and try to extract the compound's smile
	 * 
	 * 
	 * @param compoundLine
	 *            The line of text containing the compound information extracted
	 *            from the text file found in the respective stage folder
	 * 
	 * @return smiles string
	 */
	static String extractSmiles(String compoundLine)
	{
		String smiles = null;

		try
		{
			// Smiles is the first word of the line, followed by a space
			smiles = compoundLine.substring(0, compoundLine.indexOf(" "));

		} catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

		return smiles;
	}

	/**
	 * Creates a URL pointing to a compound structure graph in Chemistry connect (compounds.rd.astrazeneca.net)
	 * using the compound's SMILES
	 * 
	 * @param smiles
	 * 
	 * @return the URL
	 * 
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 */
	static URL SmilesToUrl(String smiles) throws IOException, MalformedURLException, UnsupportedEncodingException
	{

		// A URL for building the compound's structure image graph
		return new URL("http://compounds.rd.astrazeneca.net/resources/structure/toimage/" + URLEncoder.encode(smiles, "UTF-8") + "?inputFormat=SMILES&appid=chemistry connect");
		
		// TODO: consider saving the URL as a compound's property as well

	}
	
	/**
	 * Retrieve image form a given URL
	 * 
	 * @param URL link / path
	 * 
	 * @return image file as a BufferedImage
	 * 
	 */
	static BufferedImage UrlToBufferedImage(URL path) throws IOException, MalformedURLException, UnsupportedEncodingException
	{
		BufferedImage image = null;

		try
		{
			// Read returns a BufferedImage
			image = ImageIO.read(path);
			
		} catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	
		return image;
	}
	
	/**
	 * Create, populate, and save a new compound
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
	static Compound newCompound(StageType stage, String sn, String smiles, byte[] structureGraph) throws IOException
	{

		Compound c = new Compound(); 			// Create the new compound

		c.setStage(stage); 						// Store its stage
		c.setSampleNumber(sn); 					// Store the sample number
		c.setSmiles(smiles); 					// Store the smiles
		c.setStructureGraph(structureGraph);	// StructureGraph as a byte array

		// TODO: consider adding checks on whether an actual compound is
		// returned?

		return service.saveCompound(c);
	}
	
	/**
	 * Convert a bufferedImage file into a byte[] ready to be stored into the DB
	 * 
	 * @param image
	 * 
	 * @return byte[]: The byte array, ready to be stored in a DB
	 * @return null: No array
	 */
	static byte[] BufferedImageToByteArray(BufferedImage image)
	{
		
		byte[] imageInByte = null; //To be returned
		
		long length = image.toString().length();
		// You cannot create an array using a long type. It needs to be an int

		// Before converting to an int type, check that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE)
		{
			System.out.println("file is too large to be stored in a byte array for the database (length > Integer.MAX_VALUE), still will try...");
		}

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
		c.setStage(stage); // Store stage
		c.setLineGraph(lineGraph); // Store lineGraph image as a byte[]
		c.setResult(result); // Store the results of the bioessay

		return service.saveCompound(c);
	}

	/**
	 * Writes a file from a bufferedImage so that an image from DB can be used in web or on disk
	 * 
	 * @param bi:
	 *            The bufferedImage
	 * @param fileName:
	 *            The required file name for the disk (does this include a path?)
	 * 
	 * @return file: the image
	 * @return null: see console for error
	 */
	File BufferedImageToFile(BufferedImage bi, String fileName)
	{
		
		try
		{
			File file = new File(fileName);
			ImageIO.write(bi, "png", file);
			return file;
		} catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
		return null;
	}

	/**
	 * pair stages to file paths in filePath map dictionary
	 * 
	 * @param filePath the dictionary holding the pairs
	 * 
	 */
	private void FilePathGenerator(Map<StageType, String> filePath)
	{
		filePath.put(StageType.BACKLOG, backlog_File_Path);
		filePath.put(StageType.DESIGN, design_File_Path);
		filePath.put(StageType.SYNTHESIS, synthesis_File_Path);
		filePath.put(StageType.PURIFICATION, purification_File_Path);
		filePath.put(StageType.TESTING, testing_File_Path);
	}

	/**
	 * 	update record of compounds with new findings
	 * 
	 * @param stage where the compound is in
	 * 
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	static Compound updateCompounds(StageType stage) throws MalformedURLException, UnsupportedEncodingException, FileNotFoundException, IOException
	{

		// Get the list of stored compounds
		List<Compound> compoundsInDB = null;
		try
		{
			compoundsInDB = service.getAllCompounds();
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

		for (Compound dBCompound : compoundsInDB)
		{
			// Identifier for the stored compound
			String dBCompoundSN = dBCompound.getSampleNumber();

			// It is a known compound, update just its stage
			if (dBCompoundSN.equals(this.extracted_id))
			{
				dBCompound.setStage(stage);
			}

			// IT is a known compound but AZ number is missing
			/*
			 * Note: Could skip checking and just add AZ number to reduce
			 * complexity, but better avoid any conflicts in case the
			 * extracted_id doesn't have the expected value
			 */
			else if (!dBCompoundSN.substring(0, 1).equals("AZ"))
			{
				dBCompound.setSampleNumber(this.extracted_id);
			}
			
			else // Its a new compound, add it to the DB
			{
				// Build, populate and save the new compound
				try
				{
					dBCompound = newCompound(StageType.DESIGN, extracted_id, extracted_smiles, structureGraph);
				} catch (IOException e)
				{
					System.out.println(e.getMessage());
				}
			}

			// For compounds in or after testing stage
			this.extractedResults = null;
			// TODO: set source of result following updated requirements

			// Compound was just moved into or after Testing stage
			if (!dBCompound.getResult().equals(extractedResults))
			/*
			 * Note: Alternative comparison:
			 * (dBCompound.getStage().ordinal()>=StageType.TESTING.ordinal()),
			 * However avoid as a compound may have already been registered in
			 * Testing stage but without results and a LineGraph(?)
			 * 
			 */
			{
				// Retrieve LineGraph and convert it to a byte[] ready to be stored in a db
				try
				{
					lineGraph = BufferedImageToByteArray(UrlToBufferedImage(ShareFilePathToURL(LineGraph_File_Path)));
				} catch (FileNotFoundException e)
				{
					System.out.println("failed to retrieve lineGraph, file not found related fault details:");
					System.out.println(e.getMessage());
					
				} catch (IOException e1)
				{
					System.out.println("failed to retrieve lineGraph, Input/Output related fault details:");
					System.out.println(e1.getMessage());
				} catch (Exception e2)
				{
					System.out.println("failed to retrieve lineGraph, fault details:");
					System.out.println(e2.getMessage());
				}

				// finally, update compound
				return dBCompound = updateBioEssay(dBCompound, stage, extractedResults, lineGraph);
			}
		}
	}

	/**
	 * Read image from remote location (ie: fileShare) and stream it into a byte
	 * array call readImage to get graph, ie:
	 * StoreImage(readImage(filelocation))
	 * 
	 * Similarly, create writeImage(file) and call it to set graphs, ie:
	 * setGraph(writtingImage(filelocation))
	 * 
	 * @param file
	 * @return imageBytes byte array
	 * @return null in case that file is too large to be streamed
	 * @throws IOException
	 */
	//Note: this is a more advanced method for fetching images, using inputStream. currently I am using other methods
	//using ImageIO.read. Keeping this in case it is needed in the future.
/*
	public byte[] readImage(File file) throws IOException
	{
		Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Open File] " + file.getAbsolutePath());
		long length = file.length();
		// You cannot create an array using a long type. It needs to be an int
		// type.

		// Before converting to an int type, check that file is not larger than
		// Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE)
		{
			// File is too large
			System.out.println("File :" + file.getName() + " is to large to be streamed into a byte array");
			return null;
		}
		
		InputStream is = new FileInputStream(file);
		
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
*/

	/*
	 * Alternative method to convert image file to buyte[]. Source:
	 * https://blogs.oracle.com/adf/jpa-insert-and-retrieve-clob-and-blob-types
	 * Not working due to the call to IOManager
	 */
	/**
	 * Reads image from file and converts to byte[] that can be stored into DB
	 * 
	 * @param fileLocation
	 * @return
	 */
	/*
	 * private static byte[] writtingImage(String fileLocation) {
	 * System.out.println("file lcation is"+fileLocation); IOManager manager=new
	 * IOManager(); try { return manager.getBytesFromFile(fileLocation);
	 * 
	 * } catch (IOException e) { } return null; }
	 */
}
