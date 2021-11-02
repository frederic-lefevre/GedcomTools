package org.fl.gedcomtools.filtre;

import java.util.logging.Logger;

import org.fl.gedcomtools.entity.GedcomMultimediaObject;
import org.fl.gedcomtools.line.GedcomLine;

public class GedcomMultimediaObjectFiltre extends GedcomEntityFiltre {

	public GedcomMultimediaObjectFiltre(GedcomFiltreCondition fc, Logger l) {
		super(fc, l);
	}

	public StringBuilder filtre(GedcomMultimediaObject multimediaObject) {
		StringBuilder gedcomSource = new StringBuilder() ;
		for (GedcomLine gLine : multimediaObject.getGedcomLines()) {
			if (! filtreCondition.isToBeFiltered(gLine)) {
				gedcomSource.append(gLine.getOriginalLine()) ;
			}
		}
		return anonymisationAdresseMail(gedcomSource) ;
	}
}
