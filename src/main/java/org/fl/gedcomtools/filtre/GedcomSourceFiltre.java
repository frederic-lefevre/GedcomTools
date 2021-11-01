package org.fl.gedcomtools.filtre;

import java.util.List;
import java.util.logging.Logger;

import org.fl.gedcomtools.entity.GedcomSource;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.line.GedcomTag;
import org.fl.gedcomtools.line.GedcomTagValue;

public class GedcomSourceFiltre extends GedcomEntityFiltre {

	public GedcomSourceFiltre(GedcomFiltreCondition fc, Logger l) {
		super(fc, l);

	}

	public StringBuilder filtre(GedcomSource source) {

		switch (filtreCondition.getAction(source)) {

		case SUPPRESS:
			gLog.finest(() -> "Source supprimée: " + source.getGedcomSource()) ;
			return  new StringBuilder("") ;

		case FILTER:
			gLog.finest(() -> "Source filtrée: " + source.getGedcomSource()) ;

			StringBuilder filteredGedcom = new StringBuilder() ;
			List<GedcomLine> gLines = source.getGedcomLines() ;

			// add the first line (level 0)
			filteredGedcom.append(gLines.get(0).getOriginalLine()) ;

			// filter the other lines
			GedcomTag currentLevelOneTag = null ;
			for (GedcomLine gLine : gLines) {
				if (! filtreCondition.isToBeFiltered(gLine)) {
					if (gLine.getLevel() == 1) {
						currentLevelOneTag = gLine.getTag() ;
						if (gLine.tagValueEquals(GedcomTagValue.TITL)) {
							filteredGedcom.append(gLine.getOriginalLine()) ;
						} else if ((! filtreCondition.keepOnlySourceTitle()) && (! filtreCondition.suppressSourceNote())) {
							if  (gLine.tagValueEquals(GedcomTagValue.NOTE)) {
								if (! filtreCondition.titleStartToSuppresSourceNote(source.getSourceTitle())) {
									filteredGedcom.append(gLine.getOriginalLine()) ;
								}
							}
						}
					} else if ((gLine.getLevel() == 2) && 
							(currentLevelOneTag.equalsValue(GedcomTagValue.TITL)) &&
							(gLine.tagValueEquals(GedcomTagValue.CONC))) {
						// long source title split on 2 lines (or more)
						filteredGedcom.append(gLine.getOriginalLine()) ;
					}
				} 
			}
			return super.anonymisationAdresseMail(filteredGedcom) ;

		default :
			// should not happen
			gLog.warning("Unknown filtre action : " + filtreCondition.getAction(source));
			return super.filtre(source) ;
		}


	}

}
