package org.fl.gedcomtools.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class GedcomFileWriter extends GedcomWriter {

	private Path 	   gedcomFile ;
	
	public GedcomFileWriter(URI gedcomOut, String cs, Logger gedcomLog) {
		
		super(gedcomOut, cs, gedcomLog) ;
		
		gedcomFile 	 = Paths.get(gedcomOut) ;

		gLog.info("GedcomFile résultat: " + gedcomFile) ;		
	}

	public BufferedWriter getBufferedWriter() throws IOException {
		return  Files.newBufferedWriter(gedcomFile, gedcomCharset) ;
	}
}
