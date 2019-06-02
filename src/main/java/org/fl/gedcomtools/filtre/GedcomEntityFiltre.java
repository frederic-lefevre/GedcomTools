package org.fl.gedcomtools.filtre;

import java.util.logging.Logger;

import org.fl.gedcomtools.entity.GedcomEntity;
import org.fl.gedcomtools.line.GedcomLine;

public class GedcomEntityFiltre {

	protected GedcomFiltreCondition filtreCondition ;
	
	protected Logger gLog ;
	
	public GedcomEntityFiltre(GedcomFiltreCondition fc, Logger l) {
		filtreCondition = fc ;
		gLog			= l ;
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
