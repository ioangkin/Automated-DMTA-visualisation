/* Note: The current class is not used anymore. It is kept only for reference and can be deleted at any time */

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
	static CompoundService	service;
	
	*//**
	 * Takes in a file path string, ie: "\\pipeline04.rd.astrazeneca.net\SharedData\autodmta\run\dataset_original.smi"
	 * and converts it into URL, so that it can be collected by ImageIO.read(url) later on
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 *//*
	static URL ShareFilePathToURL(String path) throws IOException, FileNotFoundException, MalformedURLException, UnsupportedEncodingException
	{
			return new URL(path);
			
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