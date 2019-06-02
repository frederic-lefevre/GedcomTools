package org.fl.gedcomtools.line;

import java.util.LinkedList;
import java.util.List;

public class GedcomTagChain extends LinkedList<GedcomTagValue> {

	private static final long serialVersionUID = 1L;

	public GedcomTagChain(List<GedcomTagValue> l) {
		super(l) ;
	}

	public GedcomTagChain() {
		super() ;
	}

	public static GedcomTagChain buildTagChain(GedcomTagChain currentTagChain, GedcomTagValue tagValue, int tagLev) {
		
		// make a copy of the current tag chain
		GedcomTagChain newTagChain = new GedcomTagChain(currentTagChain) ;
		
		int levelDifference = currentTagChain.size() - tagLev ;
		if (levelDifference < 0) {
			// error in the level which is too high
			
			return null ;
			
		}  else {
			
			newTagChain.removeNFirst(levelDifference) ;
			newTagChain.addFirst(tagValue) ;
			return newTagChain ;
		}
	}
	
	private void removeNFirst(int n) {
		for (int i=0; i < n; i++) {
			removeFirst() ;
		}
	}
	
	public boolean tagForLevelEquals(int level, GedcomTagValue tagValue) {
		
		if ((level < size()) && (level > -1)) {
			return get(size() - level - 1).equals(tagValue) ;
		} else {
			return false ;
		}
	}
}
