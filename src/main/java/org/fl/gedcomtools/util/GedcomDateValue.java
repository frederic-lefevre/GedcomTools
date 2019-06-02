package org.fl.gedcomtools.util;

import java.time.LocalDate;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class GedcomDateValue {

	// Date range tags
	private final static String before  = "BEF" ;
	private final static String after   = "AFT" ;
	private final static String between = "BET" ;
	private final static String and     = "AND" ;
	
	// Date period tags
	private final static String from = "FROM" ;
	private final static String to   = "TO" ;
	
	// Date approximated  tags
	private final static String about 	 = "ABT" ;
	private final static String calculated = "CAL" ;
	private final static String estimated  = "EST" ;
	
	// Date interpreted tag
	private final static String interpreted = "INT" ;
	
	// Date text
	private final static char dateTextBegin = '(' ;
	
	// date mini et maxi (évaluées, éventuellement)
	private GedcomDate minDate ;
	private GedcomDate maxDate ;
	
	// validité
	private boolean valid ;

	private boolean exact ;
	
	public GedcomDateValue(String dateValue, Logger gLog) {

		valid = true ;
		exact = false ;
		try {
			StringTokenizer st = new StringTokenizer(dateValue);
			String word;
			int idx ;

			if (st.hasMoreTokens()) {
				word = st.nextToken();
				
				if ((word.equals(before)) || (word.equals(after))) {
					minDate = new GedcomDate(dateValue.substring(before.length()+1), gLog) ;					
					maxDate = minDate ;
				} else if (word.equals(between)) {
					
					// search the "AND"
					idx = dateValue.indexOf(and) ;
					if (idx < 0) {
						gLog.warning("Erreur dans la lecture d'une date: " + dateValue + "\n AND non trouvé ") ;
					} else {
						minDate = new GedcomDate(dateValue.substring(between.length()+1, idx - 1), gLog) ;
						maxDate = new GedcomDate(dateValue.substring(idx + and.length() +1), gLog) ;
					}
					
				} else if (word.equals(from)) {
					
					// search a "TO"
					idx = dateValue.indexOf(to) ;
					if (idx < 0) {
						// no "TO"
						minDate = new GedcomDate(dateValue.substring(to.length()+1), gLog) ;
						maxDate = minDate ;
					} else {
						minDate = new GedcomDate(dateValue.substring(from.length()+1, idx - 1), gLog) ;
						maxDate = new GedcomDate(dateValue.substring(idx + to.length() +1), gLog) ;
					}
					
				} else if (word.equals(to)) {
					
					minDate = new GedcomDate(dateValue.substring(to.length()+1), gLog) ;
					maxDate = minDate ;
					
				} else if (word.equals(about)) {
					
					minDate = new GedcomDate(dateValue.substring(about.length()+1), gLog) ;
					maxDate = minDate ;
				} else if (word.equals(estimated)) {
					
					minDate = new GedcomDate(dateValue.substring(estimated.length()+1), gLog) ;
					maxDate = minDate ;
					
				} else if (word.equals(calculated)) {
					
					minDate = new GedcomDate(dateValue.substring(calculated.length()+1), gLog) ;
					exact = true ;
					maxDate = minDate ;
					
				} else if (word.equals(interpreted)) {
					
					minDate = new GedcomDate(dateValue.substring(interpreted.length()+1, dateValue.indexOf(dateTextBegin) - 1), gLog) ;
					exact = true ;
					maxDate = minDate ;
					
				} else if (word.charAt(0) == dateTextBegin) {
					
					valid = false ;
					gLog.warning("Date non interpretable: " + dateValue) ;
				} else {
					// must be a date, only
					
					minDate = new GedcomDate(dateValue, gLog) ;
					exact = true ;
					maxDate = minDate ;
				}

			}
		} catch (Exception e) {
			valid = false;
			gLog.warning("Exception dans la lecture d'une date: " + dateValue + "\n Exception: " + e) ;
		}

	}
	
	public boolean isValid() {
		return valid && minDate.isValid() && maxDate.isValid() ;
	}
	
	public LocalDate getMinDate() {
		return minDate.getMinDate();
	}

	public LocalDate getMaxDate() {
		return maxDate.getMaxDate();
	}

	public boolean isExact() {
		
		if ((minDate != null) && (maxDate != null) && isValid() && exact) {
			if (minDate.equals(maxDate) && minDate.isExact()) {
				return true ;
			} else {
				return false ;
			}
		} else {
			return false ;
		}
	}
}
