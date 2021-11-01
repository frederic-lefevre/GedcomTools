package org.fl.gedcomtools.filtre;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.logging.Logger;

import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.line.GedcomTagChain;
import org.fl.gedcomtools.line.GedcomTagValue;
import org.junit.jupiter.api.Test;

import com.ibm.lge.fl.util.AdvancedProperties;
import com.ibm.lge.fl.util.RunningContext;

class GedcomFiltreConditionTest {
	
	private static final String TEST_DIR	   			 = "file:///ForTests/org.fl.gedcomtools/" ;
    private static final String TEST_PROP_FILE 			 = TEST_DIR 	   + "GedcomTools.properties" ;
    
	private static final Logger log = Logger.getLogger(GedcomFiltreConditionTest.class.getName());
	
	@Test
	void test() {
		
		RunningContext gedcomRunningContext;
		try {
			gedcomRunningContext = new RunningContext("GedcomProcess", null, new URI(TEST_PROP_FILE));
			
			AdvancedProperties gedcomProperties = gedcomRunningContext.getProps();
			
			GedcomFiltreCondition filtreCondition = new GedcomFiltreCondition(gedcomProperties, log) ;
			
			assertThat(filtreCondition.anonymiseEmail()).isTrue();
			assertThat(filtreCondition.keepOnlySourceTitle()).isFalse();
			assertThat(filtreCondition.suppressSourceNote()).isFalse();
			
			assertThat(filtreCondition.titleStartToSuppresSourceNote("Acte de mariage")).isTrue();
			assertThat(filtreCondition.titleStartToSuppresSourceNote("Extrait d'acte de ...")).isTrue();
			
			String LINE1 =  "2 DATE 29 JUL 1995" ;
			GedcomTagChain currentTagChain = new GedcomTagChain(Arrays.asList(GedcomTagValue.MARR, GedcomTagValue.FAM)) ;
			GedcomLine gLine = new GedcomLine(LINE1, currentTagChain, log) ;
			
			assertThat(filtreCondition.isToBeFiltered(gLine)).isFalse();
			
			String LINE2 =  "1 _UID D861250F550CC04BBEC50772414746812438" ;
			GedcomTagChain currentTagChain2 = new GedcomTagChain(Arrays.asList(GedcomTagValue.FAM)) ;
			GedcomLine gLine2 = new GedcomLine(LINE2, currentTagChain2, log) ;
			
			assertThat(filtreCondition.isToBeFiltered(gLine2)).isTrue();
			
		} catch (URISyntaxException e) {
			
			fail("Exception reading property file " + TEST_PROP_FILE, e);
		}
		
	}
}
