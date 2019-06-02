package org.fl.gedcomtools;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.gedcomtools.io.GedcomConfigIO;

import com.ibm.lge.fl.util.AdvancedProperties;
import com.ibm.lge.fl.util.RunningContext;
import com.ibm.lge.fl.util.json.JsonUtils;

public class ProcessGedcom {

    // Default property file name, if the system property containing the property file url is null
    private static final String DEFAULT_PROP_FILE = "file:///FredericPersonnel/FamilleEnfants/genealogie/sauvegarde/gedcomTools/GedcomTools.properties";
        
	public static void main(String[] args) {

		process(DEFAULT_PROP_FILE) ;
	}
	
	public static void process(String propertiesUri) {
		
		try {
			// access to properties and logger
			RunningContext gedcomRunningContext = new RunningContext("GedcomProcess", null, new URI(propertiesUri));
			AdvancedProperties gedcomProperties = gedcomRunningContext.getProps();
			Logger gedcomLog = gedcomRunningContext.getpLog();
			gedcomLog.info("Demarrage du process gedcom") ;
			if (gedcomLog.isLoggable(Level.FINE)) {
				gedcomLog.fine(JsonUtils.jsonPrettyPrint(gedcomRunningContext.getApplicationInfo())) ;
			}
			
			GedcomConfigIO configIO = GedcomConfigIO.getGedcomConfigIO(gedcomProperties, gedcomLog) ;
			
			// Gedcom genealogy read and then write after filter
			GedcomGenealogy gedcomGenealogy = new GedcomGenealogy(gedcomProperties, gedcomLog) ;			
			gedcomGenealogy.readGedcomGenealogy( configIO.getGenealogyReader()) ;		
			gedcomGenealogy.writeGedcomGenealogy(configIO.getGenealogyWriter()) ;
			gedcomGenealogy.writeArbreSosa(configIO.getArbreSosaWriter());
			gedcomGenealogy.writeBranchesDescendantes(configIO.getBranchesWriter());
			gedcomGenealogy.writeRepertoireProfession(configIO.getMetiersWriter());
			
			gedcomLog.info("Fin du process gedcom");
			
		} catch (Exception e) {
			System.out.println("Exception caught in Main (see default prop file processing)") ;
			e.printStackTrace() ;
		}
	}
}
