package org.fl.gedcomtools.io;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public abstract class GedcomIO {

	protected Charset  gedcomCharset ;
	protected URI 	   gedcomURI ;
	protected Logger   gLog ;
	
	public GedcomIO(URI gcio, String cs, Logger gedcomLog) {
		
		gLog 	  = gedcomLog ;
		gedcomURI = gcio ;

		// Charset to process gedcom io 
		gedcomCharset = null ;
		if ((cs != null) && (cs.length() > 0)) {
			if (Charset.isSupported(cs)) {
				gedcomCharset = Charset.forName(cs) ;
			} else {
				gedcomCharset = Charset.defaultCharset() ;
				gedcomLog.severe("Unsupported charset: " + cs + ". Default JVM charset assumed: " + gedcomCharset) ;				
			}
		} else {
			gedcomCharset = Charset.defaultCharset() ;
		}

	}

}
