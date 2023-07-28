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

package org.fl.gedcomtools.filtre;

import java.util.logging.Logger;

import org.fl.gedcomtools.Config;
import org.fl.gedcomtools.entity.GedcomEntity;
import org.fl.gedcomtools.line.GedcomLine;

public class GedcomEntityFiltre {

	protected static Logger gLog = Config.getLogger();
	
	protected GedcomFiltreCondition filtreCondition ;
	
	public GedcomEntityFiltre(GedcomFiltreCondition fc) {
		filtreCondition = fc ;
	}

	public StringBuilder filtre(GedcomEntity gedcomEntity) {

		StringBuilder gedcomSource = new StringBuilder() ;
		for (GedcomLine gLine : gedcomEntity.getGedcomLines()) {
			if (! filtreCondition.isToBeFiltered(gLine)) {
				gedcomSource.append(gLine.getOriginalLine()) ;
			}
		}
		return anonymisationAdresseMail(gedcomSource) ;
	}

	private final static String eol = System.getProperty("line.separator") ;

	// Annonimize any email addresses, if requested
	protected StringBuilder anonymisationAdresseMail(StringBuilder s) {

		if (filtreCondition.anonymiseEmail()) {
			boolean replace = false;
			for (int idx=s.length()-1; idx > -1 ; idx--) {
				if (replace) {
					if ((s.charAt(idx) != ' ') && (s.charAt(idx) != ':')) {
						s.setCharAt(idx,'*') ;
					}  else {
						replace = false ;
					}
				} else {
					if ((s.charAt(idx) == '@') && (idx > 0) && (s.charAt(idx-1) != ' ') && 
							(s.length() > idx+1) && (s.charAt(idx+1) != ' ') && 
							(! s.substring(idx+1).startsWith(eol))) {
						replace = true ;
					}
				}
			}
		}
		return s ;
	}
}
