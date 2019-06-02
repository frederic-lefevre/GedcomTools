package org.fl.gedcomtools.line;

public class GedcomId {

	public static String extractId(String rId) {
		if (isId(rId)) {
			return rId.substring(1, rId.length() - 1) ;
		} else {
			return null ;
		}
	}
	
	public static boolean isId(String id) {

		return ((id != null) 			&& 
				(id.length() > 2) 		&& 
				(id.charAt(0) == '@') 	&& 
				(id.charAt(id.length() - 1) == '@')) ;
	}

}
