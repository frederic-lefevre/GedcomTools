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

package org.fl.gedcomtools.filtre;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.fl.gedcomtools.Config;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.line.GedcomTagChain;
import org.fl.gedcomtools.line.GedcomTagValue;
import org.junit.jupiter.api.Test;

class GedcomFiltreConditionTest {
	
	private static final String TEST_PROP_FILE = "GedcomToolsForTest.properties";
	
	@Test
	void test() {

		String testPropertyUriString = GedcomFiltreConditionTest.class.getClassLoader().getResource(TEST_PROP_FILE).toString();
		Config.initConfig(testPropertyUriString);

		GedcomFiltreCondition filtreCondition = new GedcomFiltreCondition();

		assertThat(filtreCondition.anonymiseEmail()).isTrue();
		assertThat(filtreCondition.keepOnlySourceTitle()).isFalse();
		assertThat(filtreCondition.suppressSourceNote()).isFalse();

		assertThat(filtreCondition.titleStartToSuppresSourceNote("Acte de mariage")).isTrue();
		assertThat(filtreCondition.titleStartToSuppresSourceNote("Extrait d'acte de ...")).isTrue();

		String LINE1 = "2 DATE 29 JUL 1995";
		GedcomTagChain currentTagChain = new GedcomTagChain(Arrays.asList(GedcomTagValue.MARR, GedcomTagValue.FAM));
		GedcomLine gLine = new GedcomLine(LINE1, currentTagChain);

		assertThat(gLine.isValid()).isTrue();
		assertThat(filtreCondition.isToBeFiltered(gLine)).isFalse();

		String LINE2 = "1 _UID D861250F550CC04BBEC50772414746812438";
		GedcomTagChain currentTagChain2 = new GedcomTagChain(Arrays.asList(GedcomTagValue.FAM));
		GedcomLine gLine2 = new GedcomLine(LINE2, currentTagChain2);

		assertThat(gLine2.isValid()).isTrue();
		assertThat(filtreCondition.isToBeFiltered(gLine2)).isTrue();
	}
}
