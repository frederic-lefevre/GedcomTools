/*
 * MIT License

Copyright (c) 2017, 2023 Frederic Lefevre

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

package org.fl.gedcomtools.io;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.gedcomtools.Config;
import org.fl.util.AdvancedProperties;

public class GedcomConfigIO {

	private static Logger gedcomLog = Config.getLogger();
	
	private static GedcomConfigIO gedcomConfigIO = null;

	private GedcomFileReader genealogyReader;
	private GedcomFileWriter genealogyWriter;
	private GedcomFileWriter arbreSosaWriter;
	private GedcomFileWriter branchesWriter;
	private GedcomFileWriter metiersWriter;

	private static final String SOSA_FILE_EXTENTION = ".csv";
	private static final String BRANCHE_FILE_EXTENTION = ".csv";
	private static final String METIERS_FILE_EXTENTION = ".txt";

	private GedcomConfigIO(AdvancedProperties gedcomProp) {

		String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

		// Get input and output file name
		URI gedcomInputFile = gedcomProp.getURI("gedcom.input.URI");
		URI gedcomOutputFile = gedcomProp.getURI("gedcom.output.URI");

		String arbreSosaOutputFileName = gedcomProp.getProperty("gedcom.sosa.output.baseURI") + today
				+ SOSA_FILE_EXTENTION;
		String brancheOutputFileName = gedcomProp.getProperty("gedcom.branche.output.baseURI") + today
				+ BRANCHE_FILE_EXTENTION;
		String metiersOutputFileName = gedcomProp.getProperty("gedcom.metiers.output.baseURI") + today
				+ METIERS_FILE_EXTENTION;

		URI arbreSosaOutputFile = getURIfromString(arbreSosaOutputFileName);
		URI brancheOutputFile = getURIfromString(brancheOutputFileName);
		URI metiersOutputFile = getURIfromString(metiersOutputFileName);

		// Charsets for input and output
		String inCharset = gedcomProp.getProperty("gedcom.in.charset");
		String outCharset = gedcomProp.getProperty("gedcom.out.charset");
		String sosaCharset = gedcomProp.getProperty("gedcom.sosa.out.charset");
		String brancheCharset = gedcomProp.getProperty("gedcom.branche.out.charset");
		String metiersCharset = gedcomProp.getProperty("gedcom.metiers.out.charset");

		// Gedcom reader and writer
		genealogyReader = new GedcomFileReader(gedcomInputFile, inCharset);
		genealogyWriter = new GedcomFileWriter(gedcomOutputFile, outCharset);
		arbreSosaWriter = new GedcomFileWriter(arbreSosaOutputFile, sosaCharset);
		branchesWriter = new GedcomFileWriter(brancheOutputFile, brancheCharset);
		metiersWriter = new GedcomFileWriter(metiersOutputFile, metiersCharset);
	}

	private URI getURIfromString(String uriAsString) {
		
		if ((uriAsString != null) && (uriAsString.length() > 0)) {
			try {
				return new URI(uriAsString) ;
			} catch (URISyntaxException e) {
				gedcomLog.log(Level.SEVERE, "URISyntaxException when creating URI " + uriAsString, e);
				return null ;
			} catch (Exception e) {
				gedcomLog.log(Level.SEVERE, "Exception when creating Path from URI " + uriAsString, e);
				return null ;
			}
		} else {
			gedcomLog.severe("Null or empty URI for gedcom IO configuration") ;
			return null ;
		}
	}
	
	public static GedcomConfigIO getGedcomConfigIO(AdvancedProperties gedcomProp) {
		
		if (gedcomConfigIO == null) {
			gedcomConfigIO = new GedcomConfigIO(gedcomProp) ;
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
