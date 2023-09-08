/*
 * MIT License

Copyright (c) 2017, 2023 Frederic Lefevre

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

package org.fl.gedcomtools.line;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GedcomLineTest {

	private static String EOF = System.getProperty("line.separator");

	@Test
	void test() {

		String LINE1 = "0 @I00832@ INDI";

		GedcomTagChain currentTagChain = new GedcomTagChain(Arrays.asList(GedcomTagValue.DATE, GedcomTagValue.MARR, GedcomTagValue.FAM));

		GedcomLine gLine = new GedcomLine(LINE1, currentTagChain);

		assertThat(gLine.isValid()).isTrue();
		assertThat(gLine.getTag()).isNotNull();
		assertThat(gLine.getTag().getTagValue()).isNotNull().isEqualTo(GedcomTagValue.INDI);
		assertThat(gLine.getId()).isNotNull().isEqualTo("I00832");
		assertThat(gLine.getLevel()).isEqualTo(0);
		assertThat(gLine.getOriginalLine()).isNotNull().asString().isEqualTo(LINE1 + EOF);

		List<GedcomTagValue> tagChain = Arrays.asList(GedcomTagValue.INDI);

		assertThat(gLine.equalsTagChain(tagChain)).isTrue();
		assertThat(gLine.tagForLevelEquals(0, GedcomTagValue.INDI)).isTrue();
		assertThat(gLine.tagForLevelEquals(1, GedcomTagValue.INDI)).isFalse();
	}

	@Test
	void tes2t() {

		String LINE1 = "2 DATE 29 JUL 1995";

		GedcomTagChain currentTagChain = new GedcomTagChain(Arrays.asList(GedcomTagValue.MARR, GedcomTagValue.FAM));

		GedcomLine gLine = new GedcomLine(LINE1, currentTagChain);

		assertThat(gLine).isNotNull();
		assertThat(gLine.isValid()).isTrue();
		assertThat(gLine.getTag()).isNotNull();
		assertThat(gLine.getTag().getTagValue()).isEqualTo(GedcomTagValue.DATE);

		assertThat(gLine.getId()).isNull();
		assertThat(gLine.getLevel()).isEqualTo(2);
		assertThat(gLine.getOriginalLine()).isNotNull().asString().isEqualTo(LINE1 + EOF);

		List<GedcomTagValue> tagChain = Arrays.asList(GedcomTagValue.DATE, GedcomTagValue.MARR, GedcomTagValue.FAM);

		assertThat(gLine.equalsTagChain(tagChain)).isTrue();

		assertThat(gLine.tagForLevelEquals(-1, GedcomTagValue.MARR)).isFalse();
		assertThat(gLine.tagForLevelEquals(0, GedcomTagValue.FAM)).isTrue();
		assertThat(gLine.tagForLevelEquals(1, GedcomTagValue.MARR)).isTrue();
		assertThat(gLine.tagForLevelEquals(2, GedcomTagValue.MARR)).isFalse();
		assertThat(gLine.tagForLevelEquals(2, GedcomTagValue.DATE)).isTrue();
		assertThat(gLine.tagForLevelEquals(3, GedcomTagValue.MARR)).isFalse();
	}
}
