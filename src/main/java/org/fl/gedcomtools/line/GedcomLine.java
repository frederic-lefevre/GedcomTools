package org.fl.gedcomtools.line;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GedcomLine {

	private final static String NEWLINE = System.getProperty("line.separator") ;

	// Errors
	private static final String LINE_TOO_SHORT = "Ligne Gedcom trop courte (0 ou 1 seul token): ";
	private static final String LEVEL_NOT_FOUND = "Niveau non trouvé sur la ligne gedcom: ";
	private static final String EXCEPTION_THROWN = "Exception dans GedcomLine sur la ligne gedcom: ";
	private static final String TAG_CHAIN_BUILD_ERROR = "Erreur dans la construction de tagChain pour la ligne ";
	
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
	private List<String> parsingError;

	public GedcomLine(String line, GedcomTagChain currentTagChain, Logger gedcomLog) {

		originalLine = new StringBuilder(line) ;
		originalLine.append(NEWLINE) ;

		valid = true ;
		StringTokenizer st = new StringTokenizer(line) ;
		int nbWord = st.countTokens() ;

		if (nbWord < 2) {
			addParsingError(LINE_TOO_SHORT);
			gedcomLog.warning(LINE_TOO_SHORT + line) ;
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
				addParsingError(LEVEL_NOT_FOUND);
				gedcomLog.log(Level.SEVERE, LEVEL_NOT_FOUND + line, e) ;
			} catch (Exception e) {
				addParsingError(EXCEPTION_THROWN);
				gedcomLog.log(Level.SEVERE, EXCEPTION_THROWN + line, e) ;
			}    
		}
		
		if (tag != null) {
			tagChain = GedcomTagChain.buildTagChain(currentTagChain, tag.getTagValue(), level);
			if (tagChain == null) {
				addParsingError(TAG_CHAIN_BUILD_ERROR);
				gedcomLog.severe(TAG_CHAIN_BUILD_ERROR + line);
			}
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
	
	private void addParsingError(String err) {
		if (parsingError == null) {
			parsingError = new ArrayList<>();
		}
		parsingError.add(err);
		valid = false;
	}
}
