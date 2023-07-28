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

package org.fl.gedcomtools.sosa;

import java.util.ArrayList;
import java.util.List;

import org.fl.gedcomtools.entity.Individual;

public class Sosa {

	private Individual ind;

	// Numéros sosa de l'individu (plusieurs si il y a de l'implex)
	private List<Long> numerosSosa;

	private boolean isTerminal;

	public Sosa(Individual i) {
		ind = i;
		isTerminal = false;
		numerosSosa = new ArrayList<>();
	}

	public void addNum(long num) {
		numerosSosa.add(Long.valueOf(num));
	}

	public Individual getInd() {
		return ind;
	}

	public void setIsTerminal(boolean t) {
		isTerminal = t;
	}

	public boolean isTerminal() {
		return isTerminal;
	}

	public long getHighestSosaNumber() {

		long res = 0;

		for (Long l : numerosSosa) {
			if (l > res) {
				res = l;
			}
		}

		return res;
	}

	public List<Long> getNumerosSosa() {
		return numerosSosa;
	}
	
	public int getImplex() {
		return numerosSosa.size() ;
	}
	
	public String printSosaNumbers() {
		
		StringBuilder res = new StringBuilder() ;
		for (Long l : numerosSosa) {
			res.append(l.toString()).append(" ") ;
		}
		return res.toString() ;
	}
	
	//-----------------------------------------
	// Static methods
	
	// Numéro du sosa descendant
	public static long getEnfantNum(long num) {
		return num/2 ;
	}
	
	// Numéro du sosa pere
	public static long getPereNum(long num) {
		return num*2 ;
	}
	
	// Numéro du sosa mere
	public static long getMereNum(long num) {
		return num*2 + 1 ;
	}
	
	// Numéro du sosa conjoint
	public static long getConjointNum(long num) {
		if (num % 2 == 0) {
			return num + 1 ;
		} else {
			return num - 1 ;
		}
	}
	
	public static boolean estUnHomme(long num) {
		return (num % 2 == 0) ;
	}
	
	// Numéro de la génération correspondant au sosa
	public static int numeroGeneration(long num) {
		
		int res = 1 ;
		while (num > 0) {
			num = num/2 ;
			if (num > 0) {
				res++ ;
			}
		}
		return res ;
	}

}
