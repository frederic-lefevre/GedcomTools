/*
 * MIT License

Copyright (c) 2017, 2025 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.gedcomtools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.gedcomtools.entity.Family;
import org.fl.gedcomtools.entity.GedcomEntity;
import org.fl.gedcomtools.entity.GedcomMultimediaObject;
import org.fl.gedcomtools.entity.GedcomNote;
import org.fl.gedcomtools.entity.GedcomSource;
import org.fl.gedcomtools.entity.Individual;
import org.fl.gedcomtools.filtre.GedcomEntityFiltre;
import org.fl.gedcomtools.filtre.GedcomFamilyFiltre;
import org.fl.gedcomtools.filtre.GedcomFiltreCondition;
import org.fl.gedcomtools.filtre.GedcomIndividualFiltre;
import org.fl.gedcomtools.filtre.GedcomMultimediaObjectFiltre;
import org.fl.gedcomtools.filtre.GedcomNoteFiltre;
import org.fl.gedcomtools.filtre.GedcomSourceFiltre;
import org.fl.gedcomtools.gui.ActionJournal;
import org.fl.gedcomtools.io.GedcomFileReader;
import org.fl.gedcomtools.io.GedcomFileWriter;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.sosa.ArbreDeSosa;
import org.fl.gedcomtools.util.MediaSet;

public class GedcomGenealogy {

	private static final Logger gLog = Logger.getLogger(GedcomGenealogy.class.getName());

	private static boolean initialized = false;
	private static GedcomGenealogy gedcomGenealogy;
	
	private static GedcomFiltreCondition filtreCondition;

	private final GedcomParser gedcomParser;
	private ArbreDeSosa sosaTree;
	
	private static ActionJournal actionJournal;
	
	public static GedcomGenealogy getNewInstance() throws URISyntaxException {
		
		if (! initialized) {

			actionJournal = getActionJournal();
			
			filtreCondition = new GedcomFiltreCondition();
			
			GedcomEntity.setFiltre(new GedcomEntityFiltre(filtreCondition, actionJournal));
			Family.setFiltre(new GedcomFamilyFiltre(filtreCondition, actionJournal));
			Individual.setFiltre(new GedcomIndividualFiltre(filtreCondition, actionJournal));
			GedcomNote.setFiltre(new GedcomNoteFiltre(filtreCondition, actionJournal));
			GedcomSource.setFiltre(new GedcomSourceFiltre(filtreCondition, actionJournal));
			GedcomMultimediaObject.setFiltre(new GedcomMultimediaObjectFiltre(filtreCondition, actionJournal));	
			
			initialized = true;
		}
		gedcomGenealogy = new GedcomGenealogy();
		return gedcomGenealogy;
	}
	
	public static GedcomGenealogy getInstance() {
		
		if ((! initialized) || (gedcomGenealogy == null)) {
			String message = "Trying to get the GecomGenealogy but it has not been initialized";
			gLog.severe(message);
			throw new IllegalStateException(message);
		}
		return gedcomGenealogy;
	}
	
	private GedcomGenealogy() {
		gedcomParser = new GedcomParser();
	}

	public boolean readGedcomGenealogy(GedcomFileReader gedcomReader) {
		
		// Read genealogy
		boolean success = true;
		try (BufferedReader in = gedcomReader.getBufferedReader()) {
			
			String gLine;							
			while ((gLine = in.readLine()) != null) {
				GedcomLine line = gedcomParser.parseGedcomLine(gLine);
				if (! line.isValid()) {
					success = false;
				}
			}
			
		} catch (FileNotFoundException e) {
			gLog.log(Level.SEVERE, "Exception pendant l'ouverture du fichier gedcom: ", e);
			success = false;
		} catch (IOException e) {
			gLog.log(Level.SEVERE,"Exception pendant la lecture du fichier gedcom: ", e);
			success = false;
		} 
		
		// Process genealogy
		if (success) {
			// build the sosa tree
			Individual souche = gedcomParser.checkAndReturnSouche(Config.getSoucheName());
			if (souche != null) {
				sosaTree = new ArbreDeSosa(souche);			
				filtreCondition.setArbre(sosaTree);
				if (! gedcomParser.finalizeParsing()) {
					success = false;
				}
			} else {
				success = false;
			}
			gLog.info("Nombre d'entités:   " + gedcomParser.getListeEntity().size() +
			"\nNombre d'individus: " + gedcomParser.getPersonnesMap().getNotNullEntityNumber());
		} 
		if (! success) {
			gLog.warning("La lecture de la généalogie est en erreur");
		}
	
		return success;
	}
	
	public void doCheckingsOnGenealogy() {
		
		gedcomParser.getGedcomSource().forEach(GedcomSource::checkSource);
		
		MediaSet mediaSet = gedcomParser.getMediaList(); 
		List<Path> unreferencedMedia = mediaSet.getUnreferencedMedias(Config.getGenealogyMediaPath());
		if ((unreferencedMedia != null) && (! unreferencedMedia.isEmpty())) {
			gLog.warning("Les fichiers media suivant ne sont pas référencés dans la généalogie:\n" + Arrays.toString(unreferencedMedia.toArray()));
		}

		gedcomParser.getIndivuals().forEach(Individual::checkIndividual);
	}
	
	public void writeGedcomGenealogy(GedcomFileWriter gedcomWriter) {
		
		actionJournal.reset();
		
		// build the filtered gedcom
		try (BufferedWriter  out = gedcomWriter.getBufferedWriter()) {
							 
			for (GedcomEntity gEnt : gedcomParser.getListeEntity()) {
				out.append(gEnt.filtre());
			}
			
		} catch (Exception e) {
			gLog.log(Level.SEVERE, "Exception dans l'ecriture du Gedcom filtré: ", e);
		}
	}
	
	public void writeArbreSosa(GedcomFileWriter gedcomWriter) {
		sosaTree.printArbreSosa(gedcomWriter);
	}
	
	public void writeBranchesDescendantes(GedcomFileWriter gedcomWriter) {
		sosaTree.printBranchesDescendantes(gedcomWriter);
	}
	
	public void writeRepertoireProfession(GedcomFileWriter gedcomWriter) {
		gedcomParser.getRepertoireProfession().printRepertoireProfession(gedcomWriter);
	}

	public static ActionJournal getActionJournal() {
		if (actionJournal == null) {
			actionJournal = new ActionJournal();
		}
		return actionJournal;
	}
}
