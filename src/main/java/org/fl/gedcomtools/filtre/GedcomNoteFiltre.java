package org.fl.gedcomtools.filtre;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.gedcomtools.entity.GedcomNote;

public class GedcomNoteFiltre extends GedcomEntityFiltre {

	public GedcomNoteFiltre(GedcomFiltreCondition fc, Logger l) {
		super(fc, l);
		
	}

	public StringBuilder filtre(GedcomNote note) {
		
		switch  (filtreCondition.getAction(note)) {
			
			case SUPPRESS :
				if (gLog.isLoggable(Level.FINEST)) gLog.finest("Note supprimée: " + note.getGedcomSource()) ;
				return  new StringBuilder("") ;
				
			case FILTER :
				if (gLog.isLoggable(Level.FINEST)) gLog.finest("Note filtrée (1ere ligne gardée seulement): " + note.getGedcomSource()) ;
				return super.anonymisationAdresseMail(note.getGedcomLines().get(0).getOriginalLine()) ;
				
			case NO_CHANGE :
				if (gLog.isLoggable(Level.FINEST)) gLog.finest("Note non filtrée: " + note.getGedcomSource()) ;
				return super.filtre(note) ;	
				
			default :
				// should not happen
				gLog.warning("Unknown filtre action : " + filtreCondition.getAction(note));
				return super.filtre(note) ;
		}
		

	}


	
}
