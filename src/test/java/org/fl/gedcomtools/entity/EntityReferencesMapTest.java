/*
 * MIT License

Copyright (c) 2017, 2024 Frederic Lefevre

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

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class EntityReferencesMapTest {

	private final static String id1 = "I00832";
//	private final static String individu1 =  "0 " + id1 +"INDI" ;

	@Test
	void test() {

		EntityReferencesMap<Individual> entityRefMap = new EntityReferencesMap<Individual>();

		entityRefMap.put(id1, null);

		boolean b = entityRefMap.containsReferenceToEntity(id1);
		assertThat(b).isTrue();

		b = entityRefMap.containsReferenceToEntity("I00833");
		assertThat(b).isFalse();

		b = entityRefMap.containsEntity(id1);
		assertThat(b).isFalse();

		List<Individual> l = entityRefMap.getEntities();
		assertThat(l).isEmpty();

		long nb = entityRefMap.getNotNullEntityNumber();
		assertThat(nb).isZero();
	}

}
