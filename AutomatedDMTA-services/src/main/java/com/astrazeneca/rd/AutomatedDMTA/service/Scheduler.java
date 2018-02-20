package com.astrazeneca.rd.AutomatedDMTA.service;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.apache.log4j.chainsaw.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.astrazeneca.rd.AutomatedDMTA.model.Compound;
import com.astrazeneca.rd.AutomatedDMTA.model.StageType;

import jcifs.smb.NtlmPasswordAuthentication; //For accessing remote folders with authentication
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;


@Component("Scheduler")

//Storing the file location properties. File in: AutomatedDMTA.servicesrc/main/resources/variable.properties
@PropertySource("classpath:variable.properties")

public class Scheduler {

	// non hard-coded constants for the file paths, may be edited by customer in @PropertySource
	@Value("${design_File_Path}")			
	static private String	design_File_Path;
	@Value("${synthesis_File_Path}")		
	static private String	synthesis_File_Path;
	@Value("${purification_File_Path}")		
	static private String	purification_File_Path;
	@Value("${testing_File_Path}")			
	static private String	testing_File_Path;
	
	//For testing purposes:
	static public String getDesignFilePath()		{ return design_File_Path; }
	static public String getSynthesisFilePath()		{ return synthesis_File_Path; }
	static public String getPurificationFilePath()	{ return purification_File_Path; }
	static public String getTestingFilePath()		{ return testing_File_Path; }
	
	//Following is legacy code, can go (?)
/*	// Ensures only one job is running concurrently
	private static boolean locked=false;*/
	
	@Autowired
	static CompoundService	service;
	
	/**
	 * Building the BACKLOG/DESIGN stage:
	 * Scan all planned compounds and populate DB irrespectively on
	 * whether any of these compnds have moved in the cycle already.
	 * For each compound the SMILES and an identifier strings are stored (mandatory fields)
	 *
	 * This method runs only once so it is outside of the scheduler
	 * 
	 * @return  True: At least one compound is found and added to the DB or, If
	 *         	for any reason a compnd was already existed their stage is updated //This shouldn't be happening, but being extra cautious
	 *         
	 * @return	False: Path or file was not found or is empty
	 * 
	 * @throws	IOException
	 * @throws	UnsupportedEncodingException
	 * @throws	MalformedURLException
	 */
	boolean buildDesign() throws MalformedURLException, UnsupportedEncodingException, IOException
	{
		boolean compoundRecorded = false; //Returned if no compound found
		
		//Collect the text file containing the compounds
		File designFile = getFileFromSharedFolder(design_File_Path + "design" + ".txt");
		
		//Collect all lines from the text file into a String array
		String[] compoundsArr = textToArray(designFile);
		
		//Check for array integrity
		if (compoundsArr.equals(null) || compoundsArr.length == 0)
		{ 
			System.out.println("The cmpnd list from the desing stage returned empty or null");
			
		} else {//Extract info about the compound from the array
			
			//identifier number
			String	extracted_id;
			
			//SMILES
			String	extracted_smiles;
			
			//SMILES is needed for this
			byte[]	structureGraph;

			// Iterate through the compounds list, collecting attributes for each individual compound
			for (String compoundLine : compoundsArr)
			{
				// Extract identifier (AZ or SN number) from the list
				extracted_id = extractIdentifier(compoundLine);
				if (extracted_id.equals("")) //Check that identification is found
				{
					System.out.println("no identification for the compound found");
				}
				
				// Extract SMILES (first word in the line) from the list
				extracted_smiles = extractSmiles(compoundLine);
				if (extracted_smiles.equals("")) //Check that smiles is found
				{
					System.out.println("no smiles for the compound found");
				}
				
				// Using the extracted SMILES, retrieve StructureGraph from Chemistry connect web site and turn it into a byte array for DB
				structureGraph = BufferedImageToByteArray(UrlToBufferedImage(SmilesToUrl(extracted_smiles)));
				if (structureGraph.equals(null) || structureGraph.length == 0) //Check structureGraph is found
				{
					System.out.println("the structure Graph from the online service was not found");
				}
				
				// Update existing compounds
				if (!newCompound(StageType.DESIGN, extracted_smiles, extracted_id, structureGraph).equals(null))
				{
					compoundRecorded = true;
					
				} else {
	
					System.out.println("The new compound was not stored in database (The newCompound() method returned false)");
				}
			}
		}
		if (!compoundRecorded)
		{
			System.out.println("no new compound was recorded in Design");
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * Following method is repeated in intervals and is scanning rest of stage folders (synthesis, purification and testing)
	 * for changes in compounds
	 * 
	 * On repeating intervals: The repetition period is defined in the cron expression, as:
	 * Seconds, Minutes, Hours, Day-of-Month, Month, Day-of-Week, Year (optional field).
	 * The ‘/’ character can be used to specify increments to values. For example, "0/15" in the Minutes field means:
	 * "every 15th minute of the hour, starting at minute zero", if the Minutes field is "3/20", it would mean:
	 * "every 20th minute of the hour, starting at minute three" - or in other words it is the same as
	 * specifying "3,23,43" in the Minutes field.
	 * source: http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/tutorial-lesson-06.html
	 * additional: https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm
	 * 
	 * @throws IOException 
	 * 
	 **/
	@Scheduled(cron = "0 0/5 0 * * ?") //runs every 5'
	public void scheduleJob() throws IOException
	{	
		
		for (final Compound c : service.getAllCompounds())
		{

			//For compounds that haven't been in Synthesis stage yet
			if (c.getStage().ordinal() < StageType.SYNTHESIS.ordinal())
			{
				//What should the filename be
				String filename = synthesis_File_Path + c.getSampleNumber() + ".txt";
				
				//Compound found in Synthesis stage
				if (FileExistsInSharedFolder(filename))
				{
					c.setStage(StageType.SYNTHESIS); //Update stage of compound
					continue; //Try next compound
				}
			}
			//For compounds that haven't been in Purification stage yet
			else if (c.getStage().ordinal() < StageType.PURIFICATION.ordinal())
			{
				//The filename to look for
				String filename = purification_File_Path + c.getSampleNumber() + ".txt";
				
				//Compound found in Purification stage
				if (FileExistsInSharedFolder(filename))
				{
					c.setStage(StageType.PURIFICATION); //Update stage of compound
					continue; //Try next compound
				}
			}
			//For compounds that haven't been in Testing stage yet
			else if (c.getStage().ordinal() < StageType.TESTING.ordinal())
			{
				//The filename to look for
				String filename = testing_File_Path + c.getSampleNumber() + ".txt";

				//Compound found in testing stage
				if ((FileExistsInSharedFolder(filename)) != false)
				{
					//Update compound's stage
					c.setStage(StageType.TESTING);
					
					//collect and set results value
					{
						//The file path for the results.txt file
						//For now this is identical to the last check (String filename), but requirments may change
						String resultsFilename = testing_File_Path + c.getSampleNumber() + ".txt";
			
						//Collect the file from the shared folder
						File resultsFile = getFileFromSharedFolder(resultsFilename);
						
						//Parse through the text file and collect the results (first line of actual text)
						String results = fileToString(resultsFile);
						
						//Store the data in the DB
						if (!results.equals(null) || results.length() != 0)
						{
							c.setResults(results);
						}
					}
					
					//collect and store results' linegraph
					{
						//The file path for the lineGraph .png image file
						String lineGraphFileName = testing_File_Path + c.getSampleNumber() + ".png";
						
						//Collect the file from the shared folder
						File lineGraphFile = getFileFromSharedFolder(lineGraphFileName);
						
						//Parse the image to a byte array, ready for DB storage
						byte[] lineGraph = fileToByteArray(lineGraphFile);
						
						//Store the image in the DB (as a byte array)
						if (!lineGraph.equals(null) || lineGraph.length != 0)
						{
							c.setLineGraph(lineGraph);
						}
					}
				}
			}
		}		
	}
	
	//Note: All methods from here on are tools used by the buildDesign() and scheduler() methods in this class to collect information about the compounds
	
	/**
	 * Convert a text file into string array, each line of the text file becomes
	 * a row in the array
	 * 
	 * @param filepath
	 * 
	 * @return array with the text lines
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private String[] textToArray(File file) throws FileNotFoundException, IOException
	{

		if (!file.exists())
		{
			System.out.println("File or folder is empty or doesn't exist");
			return null;
		}

		// Hold the text lines into a list
		List<String> lines = new ArrayList<String>();
			
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(file));
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
		
		// Convert to array and return
		return lines.toArray(new String[lines.size()]);
	}
	
	/**
	 * Access and retrieve a file from a Windows shared folder using authentication with Samba JCIFS
	 * 
	 * @param filepath: Exact location including filename
	 * 
	 * @return the file
	 * 
	 * @throws MalformedURLException
	 * @throws SmbException
	 * @throws IOException
	 */
	private File getFileFromSharedFolder(String filepath) throws MalformedURLException, SmbException, IOException  {
		
		//Authenticate at remote shared folder
		final String USER_NAME = "userName";
	    final String PASSWORD = "password";
	    NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, USER_NAME, PASSWORD);
		String sFileUrl = "smb://" + USER_NAME + ":" + PASSWORD + "/" + filepath; //File path in Samba URL format
        SmbFile smbFile = new SmbFile(sFileUrl, auth); //The Samba file object
        InputStream in = null;
        if (smbFile.exists()) { //Get the file from remote location
            in = smbFile.getInputStream();
        }
        
        //Convert Samba file into a java file object and return
        File file = null;
        FileOutputStream out = new FileOutputStream(file);
        IOUtils.copy(in, out);
        
		return file;
	}
	
	
	/**
	 * Look for a file within a shared folder/ Authentication is attempted
	 * Same as the getFileFromSharedFolder(String filepath) method, but it doesnt return the file
	 * 
	 * @param filepath: Exact location including filename
	 * 
	 * @return
	 * 			true: The file exists
	 * 			false: The file was not found or error occurred (ie connection error)
	 * 
	 * @throws MalformedURLException
	 * @throws SmbException
	 * @throws IOException
	 */
	private boolean FileExistsInSharedFolder(String filepath) throws IOException  {
		
		//Authenticate at remote shared folder
		final String USER_NAME = "userName";
	    final String PASSWORD = "password";
	    NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, USER_NAME, PASSWORD);
		String sFileUrl = "smb://" + USER_NAME + ":" + PASSWORD + "/" + filepath; //File path in Samba URL format
        SmbFile smbFile = new SmbFile(sFileUrl, auth);
        
        return smbFile.exists()? true:false;
        
/*        if (smbFile.exists()) { //File found
            return true;
        }
        
        return false;*/ 
	}
		
	
	/**
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
	 */
	private String extractIdentifier(String compoundLine)
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
	private String extractSmiles(String compoundLine)
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
	private URL SmilesToUrl(String smiles) throws IOException, MalformedURLException, UnsupportedEncodingException
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
	private BufferedImage UrlToBufferedImage(URL path) throws IOException, MalformedURLException, UnsupportedEncodingException
	{
		
		return ImageIO.read(path); // Read returns a BufferedImage
		
	}
	
	/**
	 * Convert a bufferedImage file into a byte[] ready to be stored into the DB
	 * 
	 * @param image
	 * 
	 * @return byte[]: The byte array, ready to be stored in a DB
	 * @return null: No array
	 */
	private byte[] BufferedImageToByteArray(BufferedImage image)
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
	private Compound newCompound(StageType stage, String sn, String smiles, byte[] structureGraph) throws IOException
	{

		Compound c = new Compound(); 			// Create the new compound

		c.setStage(stage); 						// Store its stage
		c.setSampleNumber(sn); 					// Store the sample number
		c.setSmiles(smiles); 					// Store the smiles
		c.setStructureGraph(structureGraph);	// StructureGraph as a byte array

		return service.saveCompound(c);
	}
	
	/**
	 * Store the first line of text from a file into a String type variable
	 * 
	 * @param file
	 * 
	 * @return String
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private String fileToString(File file) throws FileNotFoundException, IOException
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
	
	/**
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
	 */
	private static byte[] fileToByteArray(File file) throws IOException
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

	//Note following method is not used yet, as the data (image) is not retreived form the DB yet
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
}
