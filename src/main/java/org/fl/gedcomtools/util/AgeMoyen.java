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

import java.util.Optional;

// Age moyen d'un groupe d'individu
public class AgeMoyen {

	// age total en nombre de jours
	private long ageTotalEnJour;

	// nombre d'individu
	private int poids;

	private AgeMoyen() {
		ageTotalEnJour = 0;
		poids = 0;
	}

	public static class Builder {
		
		private final AgeMoyen ageMoyen;
		private Builder() {
			ageMoyen = new AgeMoyen();
		}
		
		public static Builder getBuilder() {
			return new Builder();
		}
		
		public Builder add(AgeMoyen a) {
			ageMoyen.poids = ageMoyen.poids + a.poids;
			ageMoyen.ageTotalEnJour = ageMoyen.ageTotalEnJour + a.ageTotalEnJour;
			return this;
		}
		
		// Ajouter un individu au groupe, si son age est connu
		public Builder add(Optional<Long> ageIndividual) {
			if (! ageIndividual.isEmpty()) {
				ageMoyen.poids = ageMoyen.poids + 1;
				ageMoyen.ageTotalEnJour = ageMoyen.ageTotalEnJour + ageIndividual.get();
			}
			return this;
		}
		
		public AgeMoyen build() {
			return ageMoyen;
		}
	}

	public long getAgeMoyen() {
		if (poids == 0) {
			return 0;
		} else {
			return ageTotalEnJour /poids;
		}
	}

	public int getPoids() {
		return poids;
	}

	public String printAge() {
		if (poids > 0) {
			long annees = (long) Math.floor(getAgeMoyen() / 365.25);
			long resteSurAn = (long) (getAgeMoyen() % 365.25);
			long mois = (long) (resteSurAn / 30.4);
			long jours = (long) (resteSurAn % 30.4);
	
			return annees + " années " + mois + " mois " + jours + " jours sur " + poids + " individus";
		} else {
			return "";
		}
	}
}
