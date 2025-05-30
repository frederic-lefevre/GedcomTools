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

package org.fl.gedcomtools.profession;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.fl.gedcomtools.entity.Individual;

public class Profession {

	private String name;
	private Set<Individual> membres;

	public Profession(String n) {
		name = n;
		membres = new HashSet<>();
	}

	public String getName() {
		return name;
	}

	public int nombresMembres() {

		if (membres != null) {
			return membres.size();
		} else {
			return 0;
		}
	}

	public Collection<Individual> getMembres() {
		return membres;
	}

	public void addMembre(Individual i) {
		membres.add(i);
	}
}
