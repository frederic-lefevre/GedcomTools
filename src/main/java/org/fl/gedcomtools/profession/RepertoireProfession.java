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

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.gedcomtools.entity.Individual;
import org.fl.gedcomtools.entity.IndividualAlphaComparator;
import org.fl.gedcomtools.io.GedcomWriter;

public class RepertoireProfession {

	private static final Logger mLog = Logger.getLogger(RepertoireProfession.class.getName());
	
	private final static String NEWLINE = System.getProperty("line.separator");
	
	private final Map<String,Profession> repertoire ;
	
	public RepertoireProfession() {
	
		repertoire = new HashMap<>();
	}

	public void addProfessionMembre(String p, Individual i) {
		
		Profession prof ;
		if ((p == null) || (i == null)) {
			if (i != null) {
				mLog.severe("Profession null pour " + i.getIndividualName());
			} else {
				mLog.severe("Insertion de profession avec individu null");
			}
		} else {
			if (! repertoire.containsKey(p)) {
				prof = new Profession(p) ;
				repertoire.put(p, prof) ;
			} else {
				prof = repertoire.get(p) ;
			}
			prof.addMembre(i);
		}
	}
	
	public void printRepertoireProfession(GedcomWriter gedcomWriter) {
		
		List<Profession> profs = new ArrayList<>( repertoire.values()) ;		
		ProfessionAlphaComparator pac = new ProfessionAlphaComparator() ;
		Collections.sort(profs, pac);

		try (BufferedWriter out = gedcomWriter.getBufferedWriter()) {
			
			IndividualAlphaComparator iac = new IndividualAlphaComparator() ;
			for (Profession metier : profs) {
				out.append(metier.getName()).append(" ").append(Integer.toString(metier.nombresMembres())).append(NEWLINE) ;
				out.append("    ") ;
				ArrayList<Individual> membres = new ArrayList<Individual>(metier.getMembres()) ;
				Collections.sort(membres, iac) ;
				for (Individual ind : membres) {
					out.append(ind.getIndividualName()).append(" ");
				}
				out.append(NEWLINE) ;
			}
			
		} catch (Exception e) {
			mLog.log(Level.SEVERE, "Exception dans l'ecriture du rapport des métiers: ", e) ;
		}
	}

}
