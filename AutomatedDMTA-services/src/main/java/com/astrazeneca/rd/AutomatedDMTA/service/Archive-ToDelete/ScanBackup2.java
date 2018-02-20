/* Note: as with original ScanBackup class, this class is not used anymore. It is kept only for reference and can be deleted at any time */


/*package com.astrazeneca.rd.AutomatedDMTA.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileInputStream;
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


import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//https://commons.apache.org/proper/commons-io/javadocs/api-release/index.html?org/apache/commons/io/FilenameUtils.html
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.chainsaw.Main;
import jcifs.smb.NtlmPasswordAuthentication; //For accessing remote folders with autenitcation
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

import com.astrazeneca.rd.AutomatedDMTA.model.Compound;
import com.astrazeneca.rd.AutomatedDMTA.model.StageType;
import com.astrazeneca.rd.AutomatedDMTA.repository.CompoundRepository;
import com.astrazeneca.rd.AutomatedDMTA.service.CompoundService;

//Useful for file location properties. File in: AutomatedDMTA.servicesrc/main/resources/variable.properties
@PropertySource("classpath:variable.properties")

public class ScanBackup2
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

	// Dictionary for mapping above file paths to corresponding stages (May not be needed)
	private Map<StageType, String> filePath = new HashMap<StageType, String>();
		
	@Autowired
	static CompoundService	service;
	
	*//**
	 * Takes in a file path sting, ie: "\\pipeline04.rd.astrazeneca.net\SharedData\autodmta\run\dataset_original.smi"
	 * and convert it into URL, so that it can be collected by ImageIO.read(url) later on
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 *//*
	static URL ShareFilePathToURL(String path) throws IOException, FileNotFoundException, MalformedURLException, UnsupportedEncodingException
	{
			return new URL(path);
			
	}
	
	*//**
	 * Convert a text file into string array, each line of the text file becomes
	 * a row in the array
	 * 
	 * @param filepath
	 * 
	 * @return array with the text lines
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 *//*
	static String[] textToArray(String filepath) throws FileNotFoundException, IOException
	{
		//Get file from a windows share folder location
		File file = getFileFromSharedFolder(filepath);

		if (!file.exists())
		{
			System.out.println("File or folder is empty or doesn't exist");
			return null;
		}

		// Hold the text lines into a list
		List<String> lines = new ArrayList<String>();

		if (FilenameUtils.getName(file.getName()) == FilenameUtils.getName(filepath))
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
		// Convert to array and return
		return lines.toArray(new String[lines.size()]);
	}

	*//**
	 * Go through each line of the compounds array as found in the text file
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
	 *//*
	static String extractIdentifier(String compoundLine)
	{

		String extracted_id = null;

		try
		{
			// block (AZ) identifier number is a word starting with " AZ", just remove leading space
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

	*//**
	 * Go through a single line of compound information from the text file found
	 * in the respective stage folder and try to extract the compound's smile
	 * 
	 * 
	 * @param compoundLine
	 *            The line of text containing the compound information extracted
	 *            from the text file found in the respective stage folder
	 * 
	 * @return smiles string
	 *//*
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

	*//**
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
	 *//*
	static URL SmilesToUrl(String smiles) throws IOException, MalformedURLException, UnsupportedEncodingException
	{

		// A URL for building the compound's structure image graph
		return new URL("http://compounds.rd.astrazeneca.net/resources/structure/toimage/" + URLEncoder.encode(smiles, "UTF-8") + "?inputFormat=SMILES&appid=chemistry connect");
		
		// TODO: consider saving the URL as a compound's property as well

	}
	
	*//**
	 * Retrieve image form a given URL
	 * 
	 * @param URL link / path
	 * 
	 * @return image file as a BufferedImage
	 * 
	 *//*
	static BufferedImage UrlToBufferedImage(URL path) throws IOException, MalformedURLException, UnsupportedEncodingException
	{
		
		return ImageIO.read(path); // Read returns a BufferedImage
		
	}
	
	*//**
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
	 *//*
	static Compound newCompound(StageType stage, String sn, String smiles, byte[] structureGraph) throws IOException
	{

		Compound c = new Compound(); 			// Create the new compound

		c.setStage(stage); 						// Store its stage
		c.setSampleNumber(sn); 					// Store the sample number
		c.setSmiles(smiles); 					// Store the smiles
		c.setStructureGraph(structureGraph);	// StructureGraph as a byte array

		return service.saveCompound(c);
	}
	
	*//**
	 * Access and retrieve a file from a Windows shared folder using authentication
	 * 
	 * @param filepath: Exact location including filename
	 * 
	 * @return the file
	 * 
	 * @throws MalformedURLException
	 * @throws SmbException
	 * @throws IOException
	 *//*
	static File getFileFromSharedFolder(String filepath) throws MalformedURLException, SmbException, IOException  {
		
		//Get file from share folder using authentication
		final String USER_NAME = "userName";
	    final String PASSWORD = "password";
	    NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, USER_NAME, PASSWORD);
		String sFileUrl = "smb://" + USER_NAME + ":" + PASSWORD + "/" + filepath; //File path in Samba URL format
        SmbFile smbFile = new SmbFile(sFileUrl, auth); //The Samba file object
        InputStream in = null;
        if (smbFile.exists()) { //Get the file from remote location
            in = smbFile.getInputStream();
        }
        //Convert Samba file into a java file object
        File file = null;
        FileOutputStream out = new FileOutputStream(file);
        IOUtils.copy(in, out);
        
		return file;
		
	}
	
	*//**
	 * Convert a bufferedImage file into a byte[] ready to be stored into the DB
	 * 
	 * @param image
	 * 
	 * @return byte[]: The byte array, ready to be stored in a DB
	 * @return null: No array
	 *//*
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

	Legacy method, take out
	*//**
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
	 *//*
	Compound updateBioEssay(Compound c, StageType stage, String result, byte[] lineGraph)
	{
		c.setStage(stage); // Store stage
		c.setLineGraph(lineGraph); // Store lineGraph image as a byte[]
		c.setResult(result); // Store the results of the bioessay

		return service.saveCompound(c);
	}

	*//**
	 * Writes a file from a bufferedImage so that an image from DB can be used in web or on disk
	 * 
	 * @param bi:
	 *            The bufferedImage
	 * @param fileName:
	 *            The required file name for the disk (does this include a path?)
	 * 
	 * @return file: the image
	 * @return null: see console for error
	 *//*
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

	*//**
	 * pair stages to file paths in filePath map dictionary
	 * 
	 * @param filePath the dictionary holding the pairs
	 * 
	 *//*
	private void FilePathGenerator(Map<StageType, String> filePath)
	{
		filePath.put(StageType.BACKLOG, backlog_File_Path);
		filePath.put(StageType.DESIGN, design_File_Path);
		filePath.put(StageType.SYNTHESIS, synthesis_File_Path);
		filePath.put(StageType.PURIFICATION, purification_File_Path);
		filePath.put(StageType.TESTING, testing_File_Path);
	}

	*//**
	 * Store the first line of text from a file into a String type variable
	 * 
	 * @param file
	 * 
	 * @return String
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 *//*
	static String fileToString(File file) throws FileNotFoundException, IOException
	{

		if (!file.exists())
		{
			System.out.println("File is empty or doesn't exist");
			return null;
		}

		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1)
		{
			System.out.println(e1.getMessage());
			System.out.println("File not found, connection may failed to establish");
			return null;
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

		//Collect the first line of text in the file
		String inputLine = br.readLine();
		while (inputLine != null)
			{
				// Ignore empty lines.
				if (inputLine.equals(""))
				{
					continue;
				}
				
				br.close();
				return inputLine;
			}

		br.close();

		// If we reach this stage then no text has been found in the file
		System.out.println("The results file is empty");
			
		return inputLine;
	}
	
	*//**
	 * 
	 * Create a byte array from an image file
	 * 
	 * Note: The file is already been fetched from the physical location ie:
	 * shared folder, with methods like: getFileFromSharedFolder
	 * 
	 * @param file
	 * @return imageBytes byte array
	 * @return null in case that file is too large to be streamed
	 * @throws IOException
	 *//*
	public static byte[] fileToByteArray(File file) throws IOException
	{
		Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Open File] " + file.getAbsolutePath());
		long length = file.length();
		// length() returns a long type and this should be converted to int as arrays are int-indexed
		// But first check that file is not larger than Integer.MAX_VALUE
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


	
	 * Alternative method to convert image file to buyte[]. Source:
	 * https://blogs.oracle.com/adf/jpa-insert-and-retrieve-clob-and-blob-types
	 * Not working due to the call to IOManager
	 
	*//**
	 * Reads image from file and converts to byte[] that can be stored into DB
	 * 
	 * @param fileLocation
	 * @return
	 *//*
	
	 * private static byte[] writtingImage(String fileLocation) {
	 * System.out.println("file location is"+fileLocation); IOManager manager=new
	 * IOManager(); try { return manager.getBytesFromFile(fileLocation);
	 * 
	 * } catch (IOException e) { } return null; }
	 
}
*/