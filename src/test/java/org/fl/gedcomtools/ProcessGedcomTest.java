/*
 * MIT License

Copyright (c) 2017, 2025 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.gedcomtools;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
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

	private static final Logger log = Logger.getLogger(ProcessGedcomTest.class.getName());
	
	private static final String TEST_DIR = "file:///ForTests/org.fl.gedcomtools/";
	private static final String TEST_PROP_FILE = "GedcomToolsForTest.properties";
	private static final String RESULT_BASE_URI = TEST_DIR + "ResultTestFiles/";
	private static final String GEDCOM_RESULT_FILE = RESULT_BASE_URI + "GuiminelFiltre.ged";
	private static final String SOSA_RESULT_BASE_URI = RESULT_BASE_URI + "sosa";
	private static final String BRANCHE_RESULT_BASE_URI = RESULT_BASE_URI + "branche";
	private static final String METIERS_RESULT_BASE_URI = RESULT_BASE_URI + "metiers";

	private static final String SOSA_FILE_EXTENTION = ".csv";
	private static final String BRANCHE_FILE_EXTENTION = ".csv";
	private static final String METIERS_FILE_EXTENTION = ".txt";

	private static final String RESULT_REFERENCE_DIR = TEST_DIR + "ResultReferenceFiles/";
	private static final String GEDCOM_RESULT_REF = RESULT_REFERENCE_DIR + "GuiminelFiltre.ged";
	private static final String SOSA_RESULT_REF = RESULT_REFERENCE_DIR + "sosa";
	private static final String BRANCHE_RESULT_REF = RESULT_REFERENCE_DIR + "branche";
	private static final String METIERS_RESULT_REF = RESULT_REFERENCE_DIR + "metiers";
	
	@Test
	void testGenealogie() {

		String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

		String arbreSosaOutputFileName = SOSA_RESULT_BASE_URI + today + SOSA_FILE_EXTENTION;
		String brancheOutputFileName = BRANCHE_RESULT_BASE_URI + today + BRANCHE_FILE_EXTENTION;
		String metiersOutputFileName = METIERS_RESULT_BASE_URI + today + METIERS_FILE_EXTENTION;

		String testPropertyUriString = ProcessGedcomTest.class.getClassLoader().getResource(TEST_PROP_FILE).toString();
		Config.initConfig(testPropertyUriString);
		RunningContext gedcomRunningContext = Config.getRunningContext();

		assertThat(deleteResults(log)).isTrue();

		AdvancedProperties gedcomProperties = gedcomRunningContext.getProps();

		boolean success = ReadGedcom.process(gedcomProperties);
		assertThat(success).isTrue();

		String arbreSosaReferenceFileName = SOSA_RESULT_REF + SOSA_FILE_EXTENTION;
		String brancheReferenceFileName = BRANCHE_RESULT_REF + BRANCHE_FILE_EXTENTION;
		String metiersReferenceFileName = METIERS_RESULT_REF + METIERS_FILE_EXTENTION;

		FileComparator fileComparator = new FileComparator(log);

		boolean goodGedcomResult = fileComparator.haveSameContent(getPathFromUriString(GEDCOM_RESULT_FILE),
				getPathFromUriString(GEDCOM_RESULT_REF));
		assertThat(goodGedcomResult).isTrue();

		boolean goodSosaResult = fileComparator.haveSameContent(getPathFromUriString(arbreSosaOutputFileName),
				getPathFromUriString(arbreSosaReferenceFileName));
		assertThat(goodSosaResult).isTrue();

		boolean goodBrancheResult = fileComparator.haveSameContent(getPathFromUriString(brancheOutputFileName),
				getPathFromUriString(brancheReferenceFileName));
		assertThat(goodBrancheResult).isTrue();

		boolean goodMetiersResult = fileComparator.haveSameContent(getPathFromUriString(metiersOutputFileName),
				getPathFromUriString(metiersReferenceFileName));
		assertThat(goodMetiersResult).isTrue();

		// If everything was ok, delete result files
		assertThat(deleteResults(log)).isTrue();

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
