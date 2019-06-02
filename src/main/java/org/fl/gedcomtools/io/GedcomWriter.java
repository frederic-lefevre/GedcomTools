package org.fl.gedcomtools.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public abstract class GedcomWriter  extends GedcomIO {

	public GedcomWriter(URI gco, String cs, Logger gedcomLog) {
		super(gco, cs, gedcomLog) ;
	}

	public abstract BufferedWriter getBufferedWriter() throws IOException ;
}
