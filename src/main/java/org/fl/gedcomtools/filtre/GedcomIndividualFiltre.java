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

package org.fl.gedcomtools.filtre;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.fl.gedcomtools.entity.Individual;
import org.fl.gedcomtools.gui.ActionJournal;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.line.GedcomTagValue;

public class GedcomIndividualFiltre extends GedcomEntityFiltre {

	private final static String datePattern = "dd MMMM uuuu";
	
	private final DateTimeFormatter formatter ;
	
	public GedcomIndividualFiltre(GedcomFiltreCondition fc, ActionJournal actionJournal) {
		super(fc, actionJournal);
		
		formatter = DateTimeFormatter.ofPattern(datePattern, Locale.FRANCE) ;
	}

	private static String EOF = System.getProperty("line.separator");
	
	private final static char   nameSeparator	  ='/' ;
	
	public StringBuilder filtre(Individual individual) {

		return switch (filtreCondition.getAction(individual)) {
		case SUPPRESS -> {
			// à éliminer
			addAction("Eliminé: " + individual.getIndividualName() + "; Naissance: "
					+ individual.getDateNaissanceMaximum().format(formatter));
			yield new StringBuilder("");
		}
		case FILTER -> {
			// il faut filtrer
			String individualName = individual.getIndividualName();
			LocalDate dateNaissanceMaximum = individual.getDateNaissanceMaximum();
			addAction("A filtrer: " + individualName + "; Naissance: " + dateNaissanceMaximum.format(formatter));

			StringBuilder filteredGedcom = new StringBuilder();
			List<GedcomLine> gLines = individual.getGedcomLines();

			// keep the first line
			filteredGedcom.append(gLines.get(0).getOriginalLine());

			// filter the other
			for (GedcomLine gLineParts : gLines) {

				if (gLineParts.tagValueEquals(GedcomTagValue.NAME)) {
					filteredGedcom.append("1 NAME x ")
							.append(individualName.substring(individualName.indexOf(nameSeparator))).append(EOF);
				} else if (gLineParts.tagValueEquals(GedcomTagValue.FAMC)) {
					filteredGedcom.append(gLineParts.getOriginalLine());
				} else if (gLineParts.tagValueEquals(GedcomTagValue.FAMS)) {
					filteredGedcom.append(gLineParts.getOriginalLine());
				}
			}
			yield filteredGedcom;
		}
		case NO_CHANGE -> {
			yield super.filtre(individual);
		}
		};
	}
}
