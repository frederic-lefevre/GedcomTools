package org.fl.gedcomtools.line;

import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GedcomLine {

	private final static String NEWLINE = System.getProperty("line.separator") ;

	// Elements of the gedcom line
	private int 	  level ;    
	private String	  id ;
	private GedcomTag tag ;
	private String 	  content ;

	// Unmodified line
	private StringBuilder originalLine ;

	// Tag and parent tags ordered by descending level
	private GedcomTagChain tagChain ;
	
	private boolean valid ;

	public GedcomLine(String line, GedcomTagChain currentTagChain, Logger gedcomLog) {

		originalLine = new StringBuilder(line) ;
		originalLine.append(NEWLINE) ;

		valid = true ;
		StringTokenizer st = new StringTokenizer(line) ;
		int nbWord = st.countTokens() ;

		if (nbWord < 2) {
			valid = false ;
			gedcomLog.warning("Ligne Gedcom trop courte (0 ou 1 seul token): " + line) ;
		} else {

			try {
				level = Integer.parseInt(st.nextToken()) ;
				if (nbWord == 2) {
					tag = new GedcomTag(st.nextToken()) ;
				} else if (nbWord > 2) {
					String word2 = st.nextToken() ; 
					if (GedcomId.isId(word2)) {
						id = GedcomId.extractId(word2) ;
						tag = new GedcomTag(st.nextToken()) ;
						if (line.length() > tag.length() + word2.length() + 4) {
							content = line.substring(tag.length() + word2.length() + 4) ;
						}
					} else {
						tag = new GedcomTag(word2) ;
						if (line.length() > tag.length() + 3) {
							content = line.substring(tag.length() + 3) ; 
						}
					}
				}

			} catch (NumberFormatException e) {
				valid = false ;
				gedcomLog.severe("Niveau non trouvé sur la ligne gedcom: " + line + "\nException: " + e) ;
			} catch (Exception e) {
				valid = false ;
				gedcomLog.log(Level.SEVERE, "Exception dans GedcomLine sur la ligne gedcom: " + line, e) ;
			}    
		}
		
		tagChain = GedcomTagChain.buildTagChain(currentTagChain, tag.getTagValue(), level) ;
		if (tagChain == null) {
			valid = false ;
			gedcomLog.severe("Erreur dans la construction de tagChain pour la ligne " + line) ;
		}
	}

	public StringBuilder getOriginalLine() {
		return originalLine;
	}

	public int getLevel() {
		return level;
	}

	public String getId() {
		return id;
	}

	public GedcomTag getTag() {
		return tag;
	}

	public String getContent() {
		return content;
	}

	public GedcomTagChain getTagChain() {
		return tagChain;
	}

	public boolean isValid() {
		return valid;
	}

	public boolean equalsTagChain(List<GedcomTagValue> tChain) {
		return tagChain.equals(tChain) ;
	}
	
	public boolean tagForLevelEquals(int level, GedcomTagValue tagValue) {
		return tagChain.tagForLevelEquals(level, tagValue) ;
	}
	
	public boolean tagValueEquals(GedcomTagValue tagValue) {
		return tag.equalsValue(tagValue) ;
	}
}
