package org.fl.gedcomtools.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GedcomFileReader extends GedcomReader {

	private Path 	   gedcomFile ;
	
	public GedcomFileReader(URI gci, String cs, Logger gedcomLog) {
		super(gci, cs, gedcomLog);
		
		gedcomFile 	 = Paths.get(gedcomURI) ;
			
		gLog.info("GedcomFile en lecture: " + gedcomFile) ;		
	}
	
	public BufferedReader getBufferedReader() throws IOException {
		return  Files.newBufferedReader(gedcomFile, gedcomCharset) ;
	}
	
}
