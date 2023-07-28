package org.fl.gedcomtools.line;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GedcomLineTest {

	private static String EOF = System.getProperty("line.separator") ;
	
	@Test
	void test() {
		
		String LINE1 =  "0 @I00832@ INDI" ;
		
		GedcomTagChain currentTagChain = new GedcomTagChain(Arrays.asList(GedcomTagValue.DATE, GedcomTagValue.MARR, GedcomTagValue.FAM)) ;
		
		GedcomLine gLine = new GedcomLine(LINE1, currentTagChain) ;
		
		assertThat(gLine.isValid()).isTrue() ;
		assertThat(gLine.getTag()).isNotNull();
		assertThat(gLine.getTag().getTagValue()).isNotNull().isEqualTo(GedcomTagValue.INDI);
		assertThat(gLine.getId()).isNotNull().isEqualTo("I00832");
		assertThat(gLine.getLevel()).isEqualTo(0);
		assertThat(gLine.getOriginalLine()).isNotNull().asString().isEqualTo(LINE1 + EOF);
		
		List<GedcomTagValue> tagChain = Arrays.asList(GedcomTagValue.INDI) ;
		
		assertThat(gLine.equalsTagChain(tagChain)).isTrue() ;		
		assertThat(gLine.tagForLevelEquals(0, GedcomTagValue.INDI)).isTrue() ;
		assertThat(gLine.tagForLevelEquals(1, GedcomTagValue.INDI)).isFalse() ;
	}
	
	@Test
	void tes2t() {
		
		String LINE1 =  "2 DATE 29 JUL 1995" ;
		
		GedcomTagChain currentTagChain = new GedcomTagChain(Arrays.asList(GedcomTagValue.MARR, GedcomTagValue.FAM)) ;
		
		GedcomLine gLine = new GedcomLine(LINE1, currentTagChain) ;
		
		assertThat(gLine).isNotNull();
		assertThat(gLine.isValid()).isTrue() ;
		assertThat(gLine.getTag()).isNotNull();
		assertThat(gLine.getTag().getTagValue()).isEqualTo(GedcomTagValue.DATE);
		
		assertThat(gLine.getId()).isNull();
		assertThat(gLine.getLevel()).isEqualTo(2);
		assertThat(gLine.getOriginalLine()).isNotNull().asString().isEqualTo(LINE1 + EOF);
		
		List<GedcomTagValue> tagChain = Arrays.asList(GedcomTagValue.DATE, GedcomTagValue.MARR, GedcomTagValue.FAM) ;
		
		assertThat(gLine.equalsTagChain(tagChain)).isTrue() ;
		
		assertThat(gLine.tagForLevelEquals(-1, GedcomTagValue.MARR)).isFalse() ;
		assertThat(gLine.tagForLevelEquals(0, GedcomTagValue.FAM)).isTrue() ;
		assertThat(gLine.tagForLevelEquals(1, GedcomTagValue.MARR)).isTrue() ;
		assertThat(gLine.tagForLevelEquals(2, GedcomTagValue.MARR)).isFalse() ;
		assertThat(gLine.tagForLevelEquals(2, GedcomTagValue.DATE)).isTrue() ;
		assertThat(gLine.tagForLevelEquals(3, GedcomTagValue.MARR)).isFalse() ;
	}
}
