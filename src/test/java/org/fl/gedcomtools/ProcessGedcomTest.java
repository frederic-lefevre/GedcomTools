package org.fl.gedcomtools;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.util.AdvancedProperties;
import org.fl.util.RunningContext;
import org.fl.util.file.FileComparator;
import org.fl.util.file.FilesUtils;
import org.junit.jupiter.api.Test;

class ProcessGedcomTest {

	private static final String TEST_DIR	   			 = "file:///ForTests/org.fl.gedcomtools/" ;
    private static final String TEST_PROP_FILE 			 = TEST_DIR 	   + "GedcomTools.properties" ;
    private static final String RESULT_BASE_URI 		 = TEST_DIR 	   + "ResultTestFiles/" ;
    private static final String GEDCOM_RESULT_FILE		 = RESULT_BASE_URI + "GuiminelFiltre.ged" ;
    private static final String SOSA_RESULT_BASE_URI  	 = RESULT_BASE_URI + "sosa" ;
    private static final String BRANCHE_RESULT_BASE_URI  = RESULT_BASE_URI + "branche" ;
    private static final String METIERS_RESULT_BASE_URI  = RESULT_BASE_URI + "metiers" ;

	private static final String SOSA_FILE_EXTENTION    	 = ".csv" ;
	private static final String BRANCHE_FILE_EXTENTION 	 = ".csv" ;
	private static final String METIERS_FILE_EXTENTION 	 = ".txt" ;

	private static final String RESULT_REFERENCE_DIR 	 = TEST_DIR 			+ "ResultReferenceFiles/" ;
	private static final String GEDCOM_RESULT_REF		 = RESULT_REFERENCE_DIR + "GuiminelFiltre.ged" ;
	private static final String SOSA_RESULT_REF		 	 = RESULT_REFERENCE_DIR + "sosa" ;
	private static final String BRANCHE_RESULT_REF		 = RESULT_REFERENCE_DIR + "branche" ;
	private static final String METIERS_RESULT_REF		 = RESULT_REFERENCE_DIR + "metiers" ;
	
	@Test
	void test() {
		
		String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) ;

		String arbreSosaOutputFileName = SOSA_RESULT_BASE_URI 	 + today + SOSA_FILE_EXTENTION ;
		String brancheOutputFileName   = BRANCHE_RESULT_BASE_URI + today + BRANCHE_FILE_EXTENTION ;
		String metiersOutputFileName   = METIERS_RESULT_BASE_URI + today + METIERS_FILE_EXTENTION;

		try {

			RunningContext gedcomRunningContext;
		
			gedcomRunningContext = new RunningContext("GedcomProcess", null, new URI(TEST_PROP_FILE));
			
			Logger log = gedcomRunningContext.getpLog();
		
			assertThat(deleteResults(log)).isTrue() ;
			
			AdvancedProperties gedcomProperties = gedcomRunningContext.getProps();

			boolean success = ProcessGedcom.process(gedcomProperties, log) ;
			assertThat(success).isTrue();

			String arbreSosaReferenceFileName = SOSA_RESULT_REF    + SOSA_FILE_EXTENTION ;
			String brancheReferenceFileName   = BRANCHE_RESULT_REF + BRANCHE_FILE_EXTENTION ;
			String metiersReferenceFileName   = METIERS_RESULT_REF + METIERS_FILE_EXTENTION;

			FileComparator fileComparator = new FileComparator(log) ;

			boolean goodGedcomResult  = fileComparator.haveSameContent(getPathFromUriString(GEDCOM_RESULT_FILE), 	  getPathFromUriString(GEDCOM_RESULT_REF)) ;		
			assertTrue(goodGedcomResult) ;

			boolean goodSosaResult 	  = fileComparator.haveSameContent(getPathFromUriString(arbreSosaOutputFileName), getPathFromUriString(arbreSosaReferenceFileName)) ;
			assertTrue(goodSosaResult) ;

			boolean goodBrancheResult = fileComparator.haveSameContent(getPathFromUriString(brancheOutputFileName),   getPathFromUriString(brancheReferenceFileName)) ;
			assertTrue(goodBrancheResult) ;

			boolean goodMetiersResult = fileComparator.haveSameContent(getPathFromUriString(metiersOutputFileName),   getPathFromUriString(metiersReferenceFileName)) ;
			assertTrue(goodMetiersResult) ;

			// If everything was ok, delete result files
			assertThat(deleteResults(log)).isTrue();
			
		} catch (URISyntaxException e) {
			fail("URI syntax exception") ;
			e.printStackTrace();
		}
	}
	
	private Path getPathFromUriString(String uriString) {
		return Paths.get(URI.create(uriString)) ;
	}

	private boolean deleteResults(Logger log) {
		boolean success ;
		try {
			Path resultDir = getPathFromUriString(RESULT_BASE_URI) ;
			if (Files.exists(resultDir)) {
				success = FilesUtils.deleteDirectoryTree(resultDir, true, log) ;
			} else {
				success = true ;
			}
			Files.createDirectory(resultDir) ;
		} catch (IOException e) {
			log.log(Level.SEVERE, "Error deleting previous test results", e);
			success = false ;
		}
		return success ;
	}
}
