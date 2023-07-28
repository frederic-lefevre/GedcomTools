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

package org.fl.gedcomtools.util;

// Age moyen d'un groupe d'individu
public class AgeMoyen {

	// age moyen en nombre de jours
	private long ageMoyenEnJour;

	// nombre d'individu
	private int poids;

	public AgeMoyen() {
		ageMoyenEnJour = 0;
		poids = 0;
	}

	public AgeMoyen(AgeMoyen a, AgeMoyen b) {
		poids = a.getPoids() + b.getPoids();
		if (poids == 0) {
			ageMoyenEnJour = 0;
		} else {
			ageMoyenEnJour = (a.getAge() * a.getPoids() + b.getAge() * b.getPoids()) / poids;
		}

	}

	public long getAge() {
		return ageMoyenEnJour;
	}

	public int getPoids() {
		return poids;
	}

	public void addAgeEnJour(long ageInd) {
		if (ageInd != 0) {
			poids = poids + 1;
			ageMoyenEnJour = (ageMoyenEnJour * (poids - 1) + ageInd) / poids;
		}
	}

	public void addAgeMoyen(AgeMoyen b) {
		int poidsTotal = poids + b.getPoids();
		if (poidsTotal == 0) {
			ageMoyenEnJour = 0;
		} else {
			ageMoyenEnJour = (ageMoyenEnJour * poids + b.getAge() * b.getPoids()) / poidsTotal;
		}
		poids = poidsTotal;
	}

	public String printAge() {
		long annees = (long) Math.floor(ageMoyenEnJour / 365.25);
		long resteSurAn = (long) (ageMoyenEnJour % 365.25);
		long mois = (long) (resteSurAn / 30.4);
		long jours = (long) (resteSurAn % 30.4);

		return annees + " années " + mois + " mois " + jours + " jours sur " + poids + " individus";
	}
}
