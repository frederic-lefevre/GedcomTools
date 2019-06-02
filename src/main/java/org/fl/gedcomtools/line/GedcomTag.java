package org.fl.gedcomtools.line;

public class GedcomTag {

	private String tag ;
	
	// Only a subset of tags are defined in GedcomTagValue
	private GedcomTagValue tagValue ;
	
	public GedcomTag(String t) {
		
		tag = t ;
		
		// tagValue maybe null if tag is not defined
		try {
			tagValue = GedcomTagValue.valueOf(tag) ;
		} catch (IllegalArgumentException e) {
			tagValue = GedcomTagValue.anotherTag ;
		}
	}
	
	public int length() {
		return tag.length() ;
	}
	
	public boolean equalsValue(GedcomTagValue tagval) {
		
		if (tagValue != null) {
			return tagValue.equals(tagval) ;
		} else {
			return false ;
		}
	}

	public GedcomTagValue getTagValue() {
		return tagValue;
	}
}
