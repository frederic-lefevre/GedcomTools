package org.fl.gedcomtools;

import static org.junit.jupiter.api.Assertions.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

class GedcomParserTest {

	@Test
	void test() {
		
		Logger log = Logger.getLogger(GedcomParserTest.class.getName()) ;
		log.setLevel(Level.WARNING) ;
		GedcomParser gedcomParser = GedcomParser.getGedcomParse(log) ;
		
		assertNotNull(gedcomParser) ;
	}

}
