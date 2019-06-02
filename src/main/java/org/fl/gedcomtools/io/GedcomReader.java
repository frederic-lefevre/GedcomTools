package org.fl.gedcomtools.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public abstract class GedcomReader extends GedcomIO {

	public GedcomReader(URI gci, String cs, Logger gedcomLog) {
		super(gci, cs, gedcomLog) ;
	}

	public abstract BufferedReader getBufferedReader() throws IOException ;
}
