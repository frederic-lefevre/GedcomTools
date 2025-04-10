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

package org.fl.gedcomtools.sosa;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GenerationSosa {

	private static final Logger gLog = Logger.getLogger(GenerationSosa.class.getName());
	
	private final int numero;
	private final double minPosition;
	private final double maxPosition;
	private List<PositionSosa> generation;

	public GenerationSosa(int n) {

		numero = n;
		minPosition = Math.pow(2, n - 1);
		maxPosition = Math.pow(2, n) - 1;
		generation = new ArrayList<>();
	}

	public void addPosition(PositionSosa ps) {

		// vérifier la position
		long p = ps.getPosition();
		if ((p < minPosition) || (p > maxPosition)) {
			if (numero == 1) {
				generation.add(ps);
			} else {
				gLog.severe("Ajout d'un sosa numero " + p + " dans la génération " + numero);
			}
		} else {
			if (generation.size() == 0) {
				generation.add(ps);
			} else if (p > generation.get(generation.size() - 1).getPosition()) {
				generation.add(ps);
			} else {
				gLog.severe("Ajout d'un sosa numero inférieur au précédent");
			}
		}
	}

	public int getNumero() {
		return numero;
	}

	public PositionSosa getSosa(int idx) {
		return generation.get(idx);
	}

	public int getNombrePositionSosa() {
		return generation.size();
	}

	public List<PositionSosa> getGeneration() {
		return generation;
	}

	public long getNbMaxIndividual() {
		return (long) minPosition;
	}
}
