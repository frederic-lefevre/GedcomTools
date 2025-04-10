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

package org.fl.gedcomtools.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GedcomDate {

	private static final Logger gLog = Logger.getLogger(GedcomDate.class.getName());
	
	// Date escape
	private static String gregorianTag = "@#DGREGORIAN@" ;
	
	private static final String MONTHS[]
			 	= { "JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC" };
	
	private LocalDate minDate ;
	private LocalDate maxDate ;
	
	private boolean valid ;

	public GedcomDate(String dateValue) {
		
		valid = true;
		try {
			StringTokenizer st = new StringTokenizer(dateValue);
			String word;

			if (st.hasMoreTokens()) {
				word = st.nextToken();
				
				// check for date escape
				if (((word.charAt(0)) == '@')
						&& (word.charAt(word.length() - 1) == '@')) {
					// Date escape present
					if (!word.equals(gregorianTag)) {
						valid = false;
						gLog.warning("Unsupported date (not a Gregorian calendar): "
								+ dateValue);
						return;
					}
					word = st.nextToken();
				}
				
				// now Date gregorian assumed
				int nbWord = st.countTokens() ;
				String year, month, day ;
				
				if (nbWord == 0) {
					// it is "year"
					year = zeroPad(word, 4) ;
					minDate = LocalDate.parse(year + "0101", DateTimeFormatter.BASIC_ISO_DATE) ;
					maxDate = LocalDate.parse(year + "1231", DateTimeFormatter.BASIC_ISO_DATE) ;
				} else if (nbWord == 1) {
					// it is "month year"
					month =  getMonthNumber( word) ;
					year = zeroPad(st.nextToken(), 4) ;
					minDate = LocalDate.parse(year + month + "01", DateTimeFormatter.BASIC_ISO_DATE) ;
					maxDate = minDate.with(TemporalAdjusters.lastDayOfMonth()) ;
				} else if (nbWord == 2) {
					// it is "day month year"
					day =  zeroPad(word, 2) ;
					month =  getMonthNumber(st.nextToken()) ;
					year = zeroPad(st.nextToken(), 4) ;
					minDate = LocalDate.parse(year + month + day, DateTimeFormatter.BASIC_ISO_DATE) ;
					maxDate = minDate ;
				} else {
					// unsupported
					valid = false;
					gLog.warning("Unsupported date format: " + dateValue);
				}

			} else {
				// vide
				valid = false;
				gLog.warning("Unsupported date format (empty): "
						+ dateValue);
			}
		} catch (Exception e) {
			valid = false;
			gLog.log(Level.SEVERE,"Exception dans la lecture d'une date: " + dateValue, e) ;
		}

	}
	
	// get month number as a string from month "name" 
	private String getMonthNumber(String month) throws Exception {
		
		for (int i=0; i < MONTHS.length ; i++) {
			if (month.equals(MONTHS[i])) {
				return getDayMonthNumber(i+1) ;
			}
		}
		throw new Exception("Unsupported month format: " + month) ;
	}

	private String getDayMonthNumber(int md) {
		return zeroPad(Integer.toString(md), 2) ;
	}
	
	// pad a string with '0' up to length l
	private String zeroPad(String num, int l) {
		String res = num ;
		if (num.length() < l) {
			res = zeroPad("0" + num, l) ;
		}
		return res ;
	}
	
	public LocalDate getMinDate() {
		return minDate;
	}

	public LocalDate getMaxDate() {
		return maxDate;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public boolean isExact() {
		if (valid && (minDate != null) && (maxDate != null)) {
			return minDate.equals(maxDate) ;
		} else {
			return false ;
		}
	}

}
