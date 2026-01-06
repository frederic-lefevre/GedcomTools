/*
 * MIT License

Copyright (c) 2017, 2026 Frederic Lefevre

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

package org.fl.gedcomtools.util;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class AgeMoyenTest {

	@Test
	void testZeroAgeMoyen() {

		AgeMoyen ageMoyen = AgeMoyen.Builder.getBuilder().build();

		assertThat(ageMoyen).isNotNull();
		assertThat(ageMoyen.getPoids()).isZero();
		assertThat(ageMoyen.getAgeMoyen()).isZero();
	}

	@Test
	void testZeroAgeMoyen2() {

		AgeMoyen ageMoyen = AgeMoyen.Builder.getBuilder().add(Optional.empty()).build();

		assertThat(ageMoyen).isNotNull();
		assertThat(ageMoyen.getPoids()).isZero();
		assertThat(ageMoyen.getAgeMoyen()).isZero();
	}

	@Test
	void testZeroAgeMoyen3() {

		AgeMoyen ageMoyen = AgeMoyen.Builder.getBuilder().add(AgeMoyen.Builder.getBuilder().build()).build();

		assertThat(ageMoyen).isNotNull();
		assertThat(ageMoyen.getPoids()).isZero();
		assertThat(ageMoyen.getAgeMoyen()).isZero();
	}

	@Test
	void testZeroAgeMoyen4() {

		AgeMoyen ageMoyen = AgeMoyen.Builder.getBuilder()
				.add(Optional.empty())
				.add(AgeMoyen.Builder.getBuilder().build())
				.add(Optional.empty())
				.add(AgeMoyen.Builder.getBuilder().build()).build();

		assertThat(ageMoyen).isNotNull();
		assertThat(ageMoyen.getPoids()).isZero();
		assertThat(ageMoyen.getAgeMoyen()).isZero();
	}

	@Test
	void testAgeMoyen1an() {

		long age = 1;
		AgeMoyen ageMoyen = AgeMoyen.Builder.getBuilder()
				.add(Optional.of(age))
				.build();

		assertThat(ageMoyen).isNotNull();
		assertThat(ageMoyen.getPoids()).isEqualTo(1);
		assertThat(ageMoyen.getAgeMoyen()).isEqualTo(age);
	}
	
	@Test
	void testAgeMoyenNan() {

		long age = 61;
		AgeMoyen ageMoyen = AgeMoyen.Builder.getBuilder()
				.add(Optional.of(age))
				.build();

		assertThat(ageMoyen).isNotNull();
		assertThat(ageMoyen.getPoids()).isEqualTo(1);
		assertThat(ageMoyen.getAgeMoyen()).isEqualTo(age);
	}
	
	@Test
	void test2AgeMoyen() {

		long age1 = 62;
		long age2 = 38;
		long ageMoyenAttendu = (age1 + age2) / 2;
		
		AgeMoyen ageMoyen = AgeMoyen.Builder.getBuilder()
				.add(Optional.of(age1))
				.add(Optional.of(age2))
				.build();

		assertThat(ageMoyen).isNotNull();
		assertThat(ageMoyen.getPoids()).isEqualTo(2);
		assertThat(ageMoyen.getAgeMoyen()).isEqualTo(ageMoyenAttendu);
	}
	
	@Test
	void test2AgeMoyen2() {

		long age1 = 62;
		long age2 = 38;
		long ageMoyenAttendu = (age1 + age2) / 2;
		
		AgeMoyen ageMoyen = AgeMoyen.Builder.getBuilder()
				.add(Optional.of(age1))
				.add(AgeMoyen.Builder.getBuilder().add(Optional.of(age2)).build())
				.build();

		assertThat(ageMoyen).isNotNull();
		assertThat(ageMoyen.getPoids()).isEqualTo(2);
		assertThat(ageMoyen.getAgeMoyen()).isEqualTo(ageMoyenAttendu);
	}
	
	@Test
	void test2AgeMoyen3() {

		long age1 = 62;
		long age2 = 38;
		long ageMoyenAttendu = (age1 + age2) / 2;
		
		AgeMoyen ageMoyen = AgeMoyen.Builder.getBuilder()
				.add(AgeMoyen.Builder.getBuilder().add(Optional.of(age1)).build())
				.add(AgeMoyen.Builder.getBuilder().add(Optional.of(age2)).build())
				.build();

		assertThat(ageMoyen).isNotNull();
		assertThat(ageMoyen.getPoids()).isEqualTo(2);
		assertThat(ageMoyen.getAgeMoyen()).isEqualTo(ageMoyenAttendu);
	}
	
	@Test
	void testAgeMoyenNan2() {

		long age = 34;
		int nbIndividual = 21;
		AgeMoyen.Builder ageMoyenBuilder = AgeMoyen.Builder.getBuilder();
		for (int i=0; i < nbIndividual; i++) {
			ageMoyenBuilder.add(Optional.of(age));
		}
		AgeMoyen ageMoyen = ageMoyenBuilder.build();

		assertThat(ageMoyen).isNotNull();
		assertThat(ageMoyen.getPoids()).isEqualTo(nbIndividual);
		assertThat(ageMoyen.getAgeMoyen()).isEqualTo(age);
	}
	
	@Test
	void testAgeMoyenNan3() {

		long age = 34;
		int nbIndividual = 21;
		AgeMoyen.Builder ageMoyenBuilder = AgeMoyen.Builder.getBuilder();
		for (int i=0; i < nbIndividual; i++) {
			ageMoyenBuilder.add(Optional.of(age));
		}
		AgeMoyen ageMoyen = ageMoyenBuilder.build();

		assertThat(ageMoyen).isNotNull();
		assertThat(ageMoyen.getPoids()).isEqualTo(nbIndividual);
		assertThat(ageMoyen.getAgeMoyen()).isEqualTo(age);
		
		for (int i=0; i < 100; i++) {
			ageMoyenBuilder
				.add(Optional.empty())
				.add(AgeMoyen.Builder.getBuilder().build());
		}
		
		AgeMoyen ageMoyen2 = ageMoyenBuilder.build();
		
		assertThat(ageMoyen2.getPoids()).isEqualTo(nbIndividual);
		assertThat(ageMoyen2.getAgeMoyen()).isEqualTo(age);
	}
	
	@Test
	void testAgeMoyenNan4() {

		long age1 = 34*365;
		long age2 = 38*365;
		long ageMoyenAttendu = (age1 + age2) / 2;
		int nbIndividual = 21;
		
		AgeMoyen.Builder ageMoyenBuilder = AgeMoyen.Builder.getBuilder();
		for (int i=0; i < nbIndividual; i++) {
			ageMoyenBuilder
				.add(Optional.of(age1))
				.add(Optional.empty())
				.add(Optional.of(age2))
				.add(AgeMoyen.Builder.getBuilder().build());
		}
		AgeMoyen ageMoyen = ageMoyenBuilder.build();

		assertThat(ageMoyen).isNotNull();
		assertThat(ageMoyen.getPoids()).isEqualTo(nbIndividual*2);
		assertThat(ageMoyen.getAgeMoyen()).isEqualTo(ageMoyenAttendu);
		
		for (int i=0; i < 100; i++) {
			ageMoyenBuilder
				.add(Optional.empty())
				.add(AgeMoyen.Builder.getBuilder().build());
		}
		
		AgeMoyen ageMoyen2 = ageMoyenBuilder.build();
		
		assertThat(ageMoyen2.getPoids()).isEqualTo(nbIndividual*2);
		assertThat(ageMoyen2.getAgeMoyen()).isEqualTo(ageMoyenAttendu);
	}
}
