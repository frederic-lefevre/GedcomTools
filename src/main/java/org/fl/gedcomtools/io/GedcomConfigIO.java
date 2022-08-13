package org.fl.gedcomtools.io;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.util.base.AdvancedProperties;

public class GedcomConfigIO {

	private static GedcomConfigIO gedcomConfigIO = null ;
	
	private GedcomFileReader genealogyReader ;
	private GedcomFileWriter genealogyWriter ;
	private GedcomFileWriter arbreSosaWriter ;
	private GedcomFileWriter branchesWriter ;
	private GedcomFileWriter metiersWriter ;
	
	private static final String SOSA_FILE_EXTENTION    = ".csv" ;
	private static final String BRANCHE_FILE_EXTENTION = ".csv" ;
	private static final String METIERS_FILE_EXTENTION = ".txt" ;
	
	private GedcomConfigIO(AdvancedProperties gedcomProp, Logger gedcomLog) {
		
		String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) ;
		
		// Get input and output file name
		URI gedcomInputFile  	= gedcomProp.getURI("gedcom.input.URI");
		URI gedcomOutputFile 	= gedcomProp.getURI("gedcom.output.URI");
		
		String arbreSosaOutputFileName = gedcomProp.getProperty("gedcom.sosa.output.baseURI") 	 + today + SOSA_FILE_EXTENTION ;
		String brancheOutputFileName   = gedcomProp.getProperty("gedcom.branche.output.baseURI") + today + BRANCHE_FILE_EXTENTION ;
		String metiersOutputFileName   = gedcomProp.getProperty("gedcom.metiers.output.baseURI") + today + METIERS_FILE_EXTENTION;

		URI arbreSosaOutputFile = getURIfromString(arbreSosaOutputFileName, gedcomLog) ;
		URI brancheOutputFile 	= getURIfromString(brancheOutputFileName, 	gedcomLog) ;
		URI metiersOutputFile 	= getURIfromString(metiersOutputFileName, 	gedcomLog) ;
		
		// Charsets for input and output
		String inCharset  	  = gedcomProp.getProperty("gedcom.in.charset") ;
		String outCharset	  = gedcomProp.getProperty("gedcom.out.charset") ;
		String sosaCharset 	  = gedcomProp.getProperty("gedcom.sosa.out.charset") ;
		String brancheCharset = gedcomProp.getProperty("gedcom.branche.out.charset") ;
		String metiersCharset = gedcomProp.getProperty("gedcom.metiers.out.charset") ;
					
		// Gedcom reader and writer
		genealogyReader = new GedcomFileReader(gedcomInputFile,  	inCharset,  	gedcomLog) ;
		genealogyWriter = new GedcomFileWriter(gedcomOutputFile, 	outCharset, 	gedcomLog) ;
		arbreSosaWriter = new GedcomFileWriter(arbreSosaOutputFile, sosaCharset, 	gedcomLog) ;
		branchesWriter  = new GedcomFileWriter(brancheOutputFile, 	brancheCharset, gedcomLog) ;
		metiersWriter 	= new GedcomFileWriter(metiersOutputFile, 	metiersCharset, gedcomLog) ;		
	}

	private URI getURIfromString(String uriAsString, Logger log) {
		
		if ((uriAsString != null) && (uriAsString.length() > 0)) {
			try {
				return new URI(uriAsString) ;
			} catch (URISyntaxException e) {
				log.log(Level.SEVERE, "URISyntaxException when creating URI " + uriAsString, e);
				return null ;
			} catch (Exception e) {
				log.log(Level.SEVERE, "Exception when creating Path from URI " + uriAsString, e);
				return null ;
			}
		} else {
			log.severe("Null or empty URI for gedcom IO configuration") ;
			return null ;
		}
	}
	
	public static GedcomConfigIO getGedcomConfigIO(AdvancedProperties gedcomProp, Logger gedcomLog) {
		
		if (gedcomConfigIO == null) {
			gedcomConfigIO = new GedcomConfigIO(gedcomProp, gedcomLog) ;
		}
		return gedcomConfigIO ;
	}

	public GedcomFileReader getGenealogyReader() {
		return genealogyReader;
	}

	public GedcomFileWriter getGenealogyWriter() {
		return genealogyWriter;
	}

	public GedcomFileWriter getMetiersWriter() {
		return metiersWriter;
	}

	public GedcomFileWriter getArbreSosaWriter() {
		return arbreSosaWriter;
	}

	public GedcomFileWriter getBranchesWriter() {
		return branchesWriter;
	}
}
