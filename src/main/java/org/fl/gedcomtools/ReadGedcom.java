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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.gedcomtools.io.GedcomConfigIO;
import org.fl.util.AdvancedProperties;

public class ReadGedcom {

	private static final Logger gedcomLog = Logger.getLogger(ReadGedcom.class.getName());
	
	public static boolean process(AdvancedProperties gedcomProperties) {

		try {
			GedcomConfigIO configIO = GedcomConfigIO.getGedcomConfigIO(gedcomProperties);

			// Gedcom genealogy read and then write after filter
			GedcomGenealogy gedcomGenealogy = GedcomGenealogy.getNewInstance(gedcomProperties);
			if (gedcomGenealogy.readGedcomGenealogy(configIO.getGenealogyReader())) {
				
				// Do some checkings
				gedcomGenealogy.doCheckingsOnGenealogy();
				
				// Write in files
				gedcomGenealogy.writeGedcomGenealogy(configIO.getGenealogyWriter());
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
