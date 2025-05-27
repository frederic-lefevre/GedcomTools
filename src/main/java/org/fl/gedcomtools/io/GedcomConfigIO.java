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

package org.fl.gedcomtools.io;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.fl.util.AdvancedProperties;
import org.fl.util.file.FilesUtils;

public class GedcomConfigIO {
	
	private static GedcomConfigIO gedcomConfigIO = null;

	private GedcomFileReader genealogyReader;
	private GedcomFileWriter genealogyWriter;
	private GedcomFileWriter arbreSosaWriter;
	private GedcomFileWriter branchesWriter;
	private GedcomFileWriter metiersWriter;

	private static final String SOSA_FILE_EXTENTION = ".csv";
	private static final String BRANCHE_FILE_EXTENTION = ".csv";
	private static final String METIERS_FILE_EXTENTION = ".txt";

	private GedcomConfigIO(AdvancedProperties gedcomProp) throws URISyntaxException {

		String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

		// Get input and output file name
		Path gedcomInputFile = FilesUtils.uriStringToAbsolutePath(gedcomProp.getProperty("gedcom.input.URI"));
		Path gedcomOutputFile = FilesUtils.uriStringToAbsolutePath(gedcomProp.getProperty("gedcom.output.URI"));

		String arbreSosaOutputUriString = gedcomProp.getProperty("gedcom.sosa.output.baseURI") + today + SOSA_FILE_EXTENTION;
		String brancheOutputUriString = gedcomProp.getProperty("gedcom.branche.output.baseURI") + today + BRANCHE_FILE_EXTENTION;
		String metiersOutputUriString = gedcomProp.getProperty("gedcom.metiers.output.baseURI") + today + METIERS_FILE_EXTENTION;

		Path arbreSosaOutputFile = FilesUtils.uriStringToAbsolutePath(arbreSosaOutputUriString);
		Path brancheOutputFile = FilesUtils.uriStringToAbsolutePath(brancheOutputUriString);
		Path metiersOutputFile = FilesUtils.uriStringToAbsolutePath(metiersOutputUriString);

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
	
	public static GedcomConfigIO getGedcomConfigIO(AdvancedProperties gedcomProp) throws URISyntaxException {
		
		if (gedcomConfigIO == null) {
			gedcomConfigIO = new GedcomConfigIO(gedcomProp);
		}
		return gedcomConfigIO;
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
