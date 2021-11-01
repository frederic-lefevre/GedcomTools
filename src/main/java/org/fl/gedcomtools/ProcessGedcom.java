package org.fl.gedcomtools;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.gedcomtools.io.GedcomConfigIO;

import com.ibm.lge.fl.util.AdvancedProperties;

public class ProcessGedcom {

	public static boolean process(AdvancedProperties gedcomProperties, Logger gedcomLog) {
		
		try {
			GedcomConfigIO configIO = GedcomConfigIO.getGedcomConfigIO(gedcomProperties, gedcomLog) ;
			
			// Gedcom genealogy read and then write after filter
			GedcomGenealogy gedcomGenealogy = new GedcomGenealogy(gedcomProperties, gedcomLog) ;			
			if (gedcomGenealogy.readGedcomGenealogy( configIO.getGenealogyReader())) {	
				gedcomGenealogy.writeGedcomGenealogy(configIO.getGenealogyWriter()) ;
				gedcomGenealogy.writeArbreSosa(configIO.getArbreSosaWriter());
				gedcomGenealogy.writeBranchesDescendantes(configIO.getBranchesWriter());
				gedcomGenealogy.writeRepertoireProfession(configIO.getMetiersWriter());
				gedcomLog.info("Fin du process gedcom");
				return true;
			} else {
				gedcomLog.severe("Interruption du process gedcom");
				return false;
			}
				
		} catch (Exception e) {
			gedcomLog.log(Level.SEVERE, "Exception in ProcessGedcom", e);
			return false;
		}
	}
}
