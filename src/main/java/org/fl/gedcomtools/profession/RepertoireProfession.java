package org.fl.gedcomtools.profession;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.gedcomtools.entity.Individual;
import org.fl.gedcomtools.entity.IndividualAlphaComparator;
import org.fl.gedcomtools.io.GedcomWriter;

public class RepertoireProfession {

	private Logger mLog ;
	private final static String NEWLINE = System.getProperty("line.separator");
	
	private Map<String,Profession> repertoire ;
	
	public RepertoireProfession(Logger l) {
	
		repertoire = new HashMap<>();
		mLog 	   = l ;
	}

	public void addProfessionMembre(String p, Individual i) {
		
		Profession prof ;
		if ((p == null) || (i == null)) {
			if (i != null) {
				mLog.severe("Profession null pour " + i.getIndividualName()) ;
			} else {
				mLog.severe("Insertion de profession avec individu null");
			}
		} else {
			if (! repertoire.containsKey(p)) {
				prof = new Profession(p) ;
				repertoire.put(p, prof) ;
			} else {
				prof = repertoire.get(p) ;
			}
			prof.addMembre(i);
		}
	}
	
	public void printRepertoireProfession(GedcomWriter gedcomWriter) {
		
		List<Profession> profs = new ArrayList<>( repertoire.values()) ;		
		ProfessionAlphaComparator pac = new ProfessionAlphaComparator() ;
		Collections.sort(profs, pac);

		try (BufferedWriter out = gedcomWriter.getBufferedWriter()) {
			
			IndividualAlphaComparator iac = new IndividualAlphaComparator() ;
			for (Profession metier : profs) {
				out.append(metier.getName()).append(" ").append(Integer.toString(metier.nombresMembres())).append(NEWLINE) ;
				out.append("    ") ;
				ArrayList<Individual> membres = new ArrayList<Individual>(metier.getMembres()) ;
				Collections.sort(membres, iac) ;
				for (Individual ind : membres) {
					out.append(ind.getIndividualName()).append(" ");
				}
				out.append(NEWLINE) ;
			}
			
		} catch (Exception e) {
			mLog.log(Level.SEVERE, "Exception dans l'ecriture du rapport des métiers: ", e) ;
		}
	}

}
