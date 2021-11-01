package org.fl.gedcomtools.filtre;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.fl.gedcomtools.entity.Individual;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.line.GedcomTagValue;

public class GedcomIndividualFiltre extends GedcomEntityFiltre {

	private final static String datePattern = "dd MMMM uuuu" ;
	
	private DateTimeFormatter formatter ;
	
	public GedcomIndividualFiltre(GedcomFiltreCondition fc, Logger l) {
		super(fc, l);
		
		formatter = DateTimeFormatter.ofPattern(datePattern, Locale.FRANCE) ;
	}

	private static String EOF = System.getProperty("line.separator");
	
	private final static char   nameSeparator	  ='/' ;
	
	public StringBuilder filtre(Individual individual) {
				
		switch (filtreCondition.getAction(individual)) {
		
		case SUPPRESS :
			// à éliminer
			gLog.info("A éliminer: " + individual.getIndividualName() + "; Naissance: " + individual.getDateNaissanceMaximum().format(formatter)) ;
			return new StringBuilder("") ;
			
		case FILTER :
			// il faut filtrer
			String 	  individualName	   = individual.getIndividualName() ;
			LocalDate dateNaissanceMaximum = individual.getDateNaissanceMaximum() ;
			gLog.info("A filtrer: " + individualName + "; Naissance: " + dateNaissanceMaximum.format(formatter)) ;
			
			StringBuilder filteredGedcom = new StringBuilder() ;
			List<GedcomLine> gLines = individual.getGedcomLines() ;
			
			// keep the first line
			filteredGedcom.append(gLines.get(0).getOriginalLine()) ;
			
			// filter the other
			for (GedcomLine gLineParts : gLines) {

				if (gLineParts.tagValueEquals(GedcomTagValue.NAME)) {
					filteredGedcom.append("1 NAME x ").append(individualName.substring(individualName.indexOf(nameSeparator))).append(EOF) ;
				}  else if (gLineParts.tagValueEquals(GedcomTagValue.FAMC)) {
					filteredGedcom.append(gLineParts.getOriginalLine()) ;
				}  else if (gLineParts.tagValueEquals(GedcomTagValue.FAMS)) {
					filteredGedcom.append(gLineParts.getOriginalLine()) ;
				}
			}
			return filteredGedcom ;
				
		case NO_CHANGE :
			return super.filtre(individual) ;
			
		default :
			gLog.warning("Unknown filtre action : " + filtreCondition.getAction(individual));
			return super.filtre(individual) ;
		}
			
	}
	
}
