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
import java.nio.file.Paths;
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
import org.fl.gedcomtools.io.GedcomFileReader;
import org.fl.gedcomtools.io.GedcomWriter;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.sosa.ArbreDeSosa;
import org.fl.util.AdvancedProperties;
import org.fl.util.file.FilesUtils;

public class GedcomGenealogy {

	private static final Logger gLog = Logger.getLogger(GedcomGenealogy.class.getName());

	private final GedcomFiltreCondition filtreCondition;

	private ArbreDeSosa sosaTree;

	private String soucheName;
	private final Path genealogyMediaPath;

	private final GedcomParser gedcomParser;

	public GedcomGenealogy(AdvancedProperties gedcomProp) throws URISyntaxException {

		gedcomParser = new GedcomParser();

		soucheName = gedcomProp.getProperty("gedcom.souche");
		if ((soucheName == null) || (soucheName.isEmpty())) {
			gLog.warning("Nom de souche vide ou null");
		}

		genealogyMediaPath = FilesUtils.uriStringToAbsolutePath(gedcomProp.getProperty("gedcom.mediaFolder.URI"));

		filtreCondition = new GedcomFiltreCondition(gedcomProp);

		GedcomEntity.setFiltre(new GedcomEntityFiltre(filtreCondition));
		Family.setFiltre(new GedcomFamilyFiltre(filtreCondition));
		Individual.setFiltre(new GedcomIndividualFiltre(filtreCondition));
		GedcomNote.setFiltre(new GedcomNoteFiltre(filtreCondition));
		GedcomSource.setFiltre(new GedcomSourceFiltre(filtreCondition));
		GedcomMultimediaObject.setFiltre(new GedcomMultimediaObjectFiltre(filtreCondition));
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
			Individual souche = gedcomParser.checkAndReturnSouche(soucheName);
			if (souche != null) {
				sosaTree = new ArbreDeSosa(souche);			
				filtreCondition.setArbre(sosaTree);
				if (! gedcomParser.finalizeParsing()) {
					success = false;
				}
			} else {
				success = false;
			}
			gLog.info("Nombre d'entités:   " + gedcomParser.getListeEntity().size());
			gLog.info("Nombre d'individus: " + gedcomParser.getPersonnesMap().getNotNullEntityNumber());
		} 
		if (! success) {
			gLog.warning("La lecture de la généalogie est en erreur");
		}
		
		List<Path> unreferencedMedia = gedcomParser.getUnreferencedMedia(genealogyMediaPath);
		if ((unreferencedMedia != null) && (! unreferencedMedia.isEmpty())) {
			gLog.warning("Les fichiers media suivant ne sont pas référencés dans la généalogie:\n" + Arrays.toString(unreferencedMedia.toArray()));
		}
	
		return success;
	}
	
	public void writeGedcomGenealogy(GedcomWriter gedcomWriter) {
		
		// build the filtered gedcom
		try (BufferedWriter  out = gedcomWriter.getBufferedWriter()) {
							 
			for (GedcomEntity gEnt : gedcomParser.getListeEntity()) {
				out.append(gEnt.filtre());
			}
			
		} catch (Exception e) {
			gLog.log(Level.SEVERE, "Exception dans l'ecriture du Gedcom filtré: ", e);
		}
	}
	
	public void writeArbreSosa(GedcomWriter gedcomWriter) {
		sosaTree.printArbreSosa(gedcomWriter);
	}
	
	public void writeBranchesDescendantes(GedcomWriter gedcomWriter) {
		sosaTree.printBranchesDescendantes(gedcomWriter);
	}
	
	public void writeRepertoireProfession(GedcomWriter gedcomWriter) {
		gedcomParser.getRepertoireProfession().printRepertoireProfession(gedcomWriter);
	}
}
