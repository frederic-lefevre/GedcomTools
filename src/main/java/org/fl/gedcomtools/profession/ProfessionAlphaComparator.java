package org.fl.gedcomtools.profession;

import java.util.Comparator;


public class ProfessionAlphaComparator  implements Comparator<Profession> {

	public int compare(Profession arg0, Profession arg1) {
		return arg0.getName().compareToIgnoreCase(arg1.getName()) ;
	}
}
