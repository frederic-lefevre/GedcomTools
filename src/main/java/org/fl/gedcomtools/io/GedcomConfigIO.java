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
import java.nio.charset.Charset;
import java.nio.file.Path;

import org.fl.gedcomtools.Config;

public class GedcomConfigIO {
			
	private static GedcomConfigIO gedcomConfigIO = null;

	private GedcomFileReader genealogyReader;
	private GedcomFileWriter genealogyWriter;
	private GedcomFileWriter arbreSosaWriter;
	private GedcomFileWriter branchesWriter;
	private GedcomFileWriter metiersWriter;

	private GedcomConfigIO() throws URISyntaxException {

		// Get input and output file name
		Path gedcomInputFile = Config.getGedcomInputPath();
		Path gedcomOutputFile = Config.getGedcomOutputPath();

		Path arbreSosaOutputFile = Config.getArbreSosaOutputPath();
		Path brancheOutputFile = Config.getBrancheOutputPath();
		Path metiersOutputFile = Config.getMetiersOutputPath();

		// Charsets for input and output
		Charset inCharset = Config.getInCharset();
		Charset outCharset = Config.getOutCharset();
		Charset sosaCharset = Config.getSosaCharset();
		Charset brancheCharset = Config.getBrancheCharset();
		Charset metiersCharset = Config.getMetiersCharset();

		// Gedcom reader and writer
		genealogyReader = new GedcomFileReader(gedcomInputFile, inCharset);
		genealogyWriter = new GedcomFileWriter(gedcomOutputFile, outCharset);
		arbreSosaWriter = new GedcomFileWriter(arbreSosaOutputFile, sosaCharset);
		branchesWriter = new GedcomFileWriter(brancheOutputFile, brancheCharset);
		metiersWriter = new GedcomFileWriter(metiersOutputFile, metiersCharset);
	}
	
	public static GedcomConfigIO getGedcomConfigIO() throws URISyntaxException {
		
		if (gedcomConfigIO == null) {
			gedcomConfigIO = new GedcomConfigIO();
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
