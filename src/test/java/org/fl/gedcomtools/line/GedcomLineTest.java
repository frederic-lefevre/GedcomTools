package org.fl.gedcomtools.line;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GedcomLineTest {

	private static String EOF = System.getProperty("line.separator") ;
	
	@Test
	void test() {
		
		Logger log = Logger.getLogger(GedcomLineTest.class.getName()) ;
		log.setLevel(Level.WARNING);
		
		String LINE1 =  "0 @I00832@ INDI" ;
		
		GedcomTagChain currentTagChain = new GedcomTagChain(Arrays.asList(GedcomTagValue.DATE, GedcomTagValue.MARR, GedcomTagValue.FAM)) ;
		
		GedcomLine gLine = new GedcomLine(LINE1, currentTagChain, log) ;
		
		assertTrue(gLine.isValid()) ;
		
		assertEquals(GedcomTagValue.INDI, gLine.getTag().getTagValue()) ;
		
		String id  = gLine.getId() ;
		int    lvl = gLine.getLevel() ;
		
		assertEquals("I00832", id) ;
		assertEquals(0, lvl) ;
		
		String line = gLine.getOriginalLine().toString() ;
		
		assertEquals(LINE1 + EOF, line) ;
		
		List<GedcomTagValue> tagChain = Arrays.asList(GedcomTagValue.INDI) ;
		
		assertTrue(gLine.equalsTagChain(tagChain)) ;
		
		assertTrue(gLine.tagForLevelEquals(0, GedcomTagValue.INDI)) ;
		
		assertFalse(gLine.tagForLevelEquals(1, GedcomTagValue.INDI)) ;
	}
	
	@Test
	void tes2t() {
		
		Logger log = Logger.getGlobal();
		
		String LINE1 =  "2 DATE 29 JUL 1995" ;
		
		GedcomTagChain currentTagChain = new GedcomTagChain(Arrays.asList(GedcomTagValue.MARR, GedcomTagValue.FAM)) ;
		
		GedcomLine gLine = new GedcomLine(LINE1, currentTagChain, log) ;
		
		assertTrue(gLine.isValid()) ;
		
		assertEquals(GedcomTagValue.DATE, gLine.getTag().getTagValue()) ;
		
		String id  = gLine.getId() ;
		int    lvl = gLine.getLevel() ;
		
		assertNull(id) ;
		assertEquals(2, lvl) ;
		
		String line = gLine.getOriginalLine().toString() ;
		
		assertEquals(LINE1 + EOF, line) ;
		
		List<GedcomTagValue> tagChain = Arrays.asList(GedcomTagValue.DATE, GedcomTagValue.MARR, GedcomTagValue.FAM) ;
		
		assertTrue(gLine.equalsTagChain(tagChain)) ;
		
		assertFalse(gLine.tagForLevelEquals(-1, GedcomTagValue.MARR)) ;
		assertTrue(gLine.tagForLevelEquals(0, GedcomTagValue.FAM)) ;
		assertTrue(gLine.tagForLevelEquals(1, GedcomTagValue.MARR)) ;
		assertFalse(gLine.tagForLevelEquals(2, GedcomTagValue.MARR)) ;
		assertTrue(gLine.tagForLevelEquals(2, GedcomTagValue.DATE)) ;
		assertFalse(gLine.tagForLevelEquals(3, GedcomTagValue.MARR)) ;
	}
}
