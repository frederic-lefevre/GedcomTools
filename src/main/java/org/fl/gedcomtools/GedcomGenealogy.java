package org.fl.gedcomtools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.gedcomtools.entity.Family;
import org.fl.gedcomtools.entity.GedcomEntity;
import org.fl.gedcomtools.entity.GedcomNote;
import org.fl.gedcomtools.entity.GedcomSource;
import org.fl.gedcomtools.entity.Individual;
import org.fl.gedcomtools.filtre.GedcomEntityFiltre;
import org.fl.gedcomtools.filtre.GedcomFamilyFiltre;
import org.fl.gedcomtools.filtre.GedcomFiltreCondition;
import org.fl.gedcomtools.filtre.GedcomIndividualFiltre;
import org.fl.gedcomtools.filtre.GedcomNoteFiltre;
import org.fl.gedcomtools.filtre.GedcomSourceFiltre;
import org.fl.gedcomtools.io.GedcomFileReader;
import org.fl.gedcomtools.io.GedcomWriter;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.sosa.ArbreDeSosa;

import com.ibm.lge.fl.util.AdvancedProperties;

public class GedcomGenealogy {

	private Logger     gLog ;

	private GedcomFiltreCondition filtreCondition ;
	
	private GedcomEntityFiltre 	   entityFiltre ;
	private GedcomFamilyFiltre 	   familyFiltre ;
	private GedcomIndividualFiltre individualFiltre ;
	private GedcomNoteFiltre 	   noteFiltre ;
	private GedcomSourceFiltre 	   sourceFiltre ;

	private ArbreDeSosa sosaTree;
	
	private String 	   soucheName ;
	
	private GedcomParser gedcomParser ;
	
	public GedcomGenealogy(AdvancedProperties gedcomProp, Logger gedcomLog) {
		
		gLog 		 = gedcomLog ;
		
		gedcomParser = new GedcomParser(gLog) ;
		
		soucheName = gedcomProp.getProperty("gedcom.souche") ;
		if ((soucheName == null) || (soucheName.isEmpty())) {
			gLog.warning("Nom de souche vide ou null") ;
		}

		filtreCondition  = new GedcomFiltreCondition(gedcomProp, gLog) ;
		
		entityFiltre	 = new GedcomEntityFiltre(filtreCondition, gLog) ;
		familyFiltre 	 = new GedcomFamilyFiltre(filtreCondition, gLog) ;
		individualFiltre = new GedcomIndividualFiltre(filtreCondition, gLog) ;
		noteFiltre		 = new GedcomNoteFiltre(filtreCondition, gLog) ;
		sourceFiltre	 = new GedcomSourceFiltre(filtreCondition, gLog) ;

		GedcomEntity.setFiltre(entityFiltre) ;
		Family.setFiltre(familyFiltre) ;
		Individual.setFiltre(individualFiltre) ;
		GedcomNote.setFiltre(noteFiltre) ;
		GedcomSource.setFiltre(sourceFiltre) ;
	}

	public boolean readGedcomGenealogy(GedcomFileReader gedcomReader) {
		
		// Read genealogy
		boolean success = true ;
		try (BufferedReader in = gedcomReader.getBufferedReader()) {
			
			String gLine ;							
			while ((gLine = in.readLine()) != null) {
				GedcomLine line = gedcomParser.parseGedcomLine(gLine) ;
				if (! line.isValid()) {
					success = false;
				}
			}
			
		} catch (FileNotFoundException e) {
			gLog.log(Level.SEVERE, "Exception pendant l'ouverture du fichier gedcom: ", e) ;
			success = false ;
		} catch (IOException e) {
			gLog.log(Level.SEVERE,"Exception pendant la lecture du fichier gedcom: ", e) ;
			success = false ;
		} 
		
		// Process genealogy
		if (success) {
			// build the sosa tree
			Individual souche = gedcomParser.checkAndReturnSouche(soucheName) ;
			if (souche != null) {
				sosaTree		  = new ArbreDeSosa(souche, gLog) ;			
				filtreCondition.setArbre(sosaTree) ;
				if (! gedcomParser.finalizeParsing()) {
					success = false;
				}
			} else {
				success = false;
			}
			gLog.info("Nombre d'entités:   " + gedcomParser.getListeEntity().size()) ;
			gLog.info("Nombre d'individus: " + gedcomParser.getPersonnesMap().getNotNullEntityNumber()) ;
		} 
		if (! success) {
			gLog.warning("La lecture de la généalogie est en erreur");
		}
		return success;
	}
	
	public void writeGedcomGenealogy(GedcomWriter gedcomWriter) {
		
		// build the filtered gedcom
		try (BufferedWriter  out = gedcomWriter.getBufferedWriter()) {
							 
			for (GedcomEntity gEnt : gedcomParser.getListeEntity()) {
				out.append(gEnt.filtre()) ;
			}
			
		} catch (Exception e) {
			gLog.log(Level.SEVERE, "Exception dans l'ecriture du Gedcom filtré: ", e) ;
		}
	}
	
	public void writeArbreSosa(GedcomWriter gedcomWriter) {
		sosaTree.printArbreSosa(gedcomWriter) ;
	}
	
	public void writeBranchesDescendantes(GedcomWriter gedcomWriter) {
		sosaTree.printBranchesDescendantes(gedcomWriter) ;
	}
	
	public void writeRepertoireProfession(GedcomWriter gedcomWriter) {
		gedcomParser.getRepertoireProfession().printRepertoireProfession(gedcomWriter);
	}

}
