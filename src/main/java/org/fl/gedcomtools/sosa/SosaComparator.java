package org.fl.gedcomtools.sosa;

import java.util.Comparator;

public class SosaComparator   implements Comparator<Sosa> {

	@Override
	public int compare(Sosa s1, Sosa s2) {
		
		long n1 = s1.getHighestSosaNumber() ;
		long n2 = s2.getHighestSosaNumber() ;
		if (n1 > n2) {
			return -1 ;
		} else if (n2 > n1) {
			return 1 ;
		} else {
			return 0 ;
		}
	}

}
