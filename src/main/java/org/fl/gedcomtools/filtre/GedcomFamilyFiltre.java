package org.fl.gedcomtools.filtre;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.fl.gedcomtools.entity.Family;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.line.GedcomTagValue;

public class GedcomFamilyFiltre  extends GedcomEntityFiltre {

	private DateTimeFormatter formatter ;
	
	public GedcomFamilyFiltre(GedcomFiltreCondition fc, Logger l) {
		
		super(fc, l) ;
		
		formatter = DateTimeFormatter.ofPattern(datePattern, Locale.FRANCE);		
	}

	private final static String datePattern = "dd L uuuu" ;
	
	public StringBuilder filtre(Family family) {
		
		switch (filtreCondition.getAction(family)) {
		
		case SUPPRESS : 
			gLog.info("A éliminer: " + family.getId() + "; Mariage: " + family.getDateMariageMaximum().format(formatter)) ;
			return new StringBuilder("") ;
			
		case FILTER : 
			gLog.info("A filtrer: " + family.getId() + "; Mariage: " + family.getDateMariageMaximum().format(formatter)) ;
			
			StringBuilder filteredGedcom = new StringBuilder() ;
			List<GedcomLine> gLines = family.getGedcomLines() ;
			
			// keep the first line
			filteredGedcom.append(gLines.get(0).getOriginalLine()) ;
			
			// filter the other : keep only wife, husband and children
			for (GedcomLine gLineParts : gLines) {
				if ((gLineParts.tagValueEquals(GedcomTagValue.WIFE)) ||	
					(gLineParts.tagValueEquals(GedcomTagValue.HUSB))||  
					(gLineParts.tagValueEquals(GedcomTagValue.CHIL))) {
				
					filteredGedcom.append(gLineParts.getOriginalLine()) ;
				}
			}
			return filteredGedcom ;
			
		case NO_CHANGE :
			return super.filtre(family) ;
			
		default :
			// should not happen
			gLog.warning("Unknown filtre action : " + filtreCondition.getAction(family));
			return super.filtre(family) ;
		}
	}
	
}
