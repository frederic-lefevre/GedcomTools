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

package org.fl.gedcomtools.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.line.GedcomTagChain;
import org.fl.gedcomtools.line.GedcomTagValue;
import org.fl.util.FilterCounter;
import org.fl.util.FilterCounter.LogRecordCounter;
import org.junit.jupiter.api.Test;

class GedcomEntityTest {

	@Test
	void nullParameterShouldThrowIllegalArgumentException() {
		
		assertThatIllegalArgumentException().isThrownBy(() -> new GedcomEntity(null));
	}
	
	@Test
	void nonZeroLevelLineShouldThrowIllegalArgumentException() {
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(GedcomEntity.class.getName()));
		
		assertThatIllegalArgumentException().isThrownBy(() -> new GedcomEntity(new GedcomLine("2 DATE 29 JUL 1995", new GedcomTagChain(Arrays.asList(GedcomTagValue.MARR, GedcomTagValue.FAM)))));
		
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecords()).singleElement()
			.satisfies(logRecord -> assertThat(logRecord.getMessage()).contains("la première ligne n'est pas de niveau 0"));
	}
}
