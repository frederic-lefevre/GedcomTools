package org.fl.gedcomtools.filtre;

import java.util.List;
import java.util.logging.Logger;

import org.fl.gedcomtools.entity.GedcomMultimediaObject;
import org.fl.gedcomtools.line.GedcomLine;

public class GedcomMultimediaObjectFiltre extends GedcomEntityFiltre {

	public GedcomMultimediaObjectFiltre(GedcomFiltreCondition fc, Logger l) {
		super(fc, l);
	}

	public StringBuilder filtre(GedcomMultimediaObject multimediaObject) {
		
		switch (filtreCondition.getAction(multimediaObject)) {
			
		case SUPPRESS:
			gLog.finest(() -> "Objet multimedia supprimé: " + multimediaObject.getGedcomSource());
			return  new StringBuilder("") ;
			
		case FILTER:
			gLog.finest(() -> "Objet multimedia filtrée: " + multimediaObject.getGedcomSource());
			
			StringBuilder filteredGedcom = new StringBuilder() ;			
			List<GedcomLine> gLines = multimediaObject.getGedcomLines();
			
			// add the first line (level 0)
			filteredGedcom.append(gLines.get(0).getOriginalLine()) ;

			// filter the other lines
			for (GedcomLine gLine : gLines) {
				if ((gLine.getLevel() > 0) && (! filtreCondition.isToBeFiltered(gLine))) {
					filteredGedcom.append(gLine.getOriginalLine()) ;		
				}
			}
			return super.anonymisationAdresseMail(filteredGedcom) ;
			
		default :
			// should not happen
			gLog.warning("Unknown filtre action : " + filtreCondition.getAction(multimediaObject));
			return super.filtre(multimediaObject) ;
		}
	}
}
