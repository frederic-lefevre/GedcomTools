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

package org.fl.gedcomtools.sourcetype;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActeEtatCivil extends GenealogySource {

	private static final Logger aLog = Logger.getLogger(ActeEtatCivil.class.getName());
	
	protected static final String PREFIX_ACTE_ETAT_CIVIL = "Acte de ";
	
	private static final String acteNaissance      			= PREFIX_ACTE_ETAT_CIVIL + "naissance de ";
	private static final String acteMariage        			= PREFIX_ACTE_ETAT_CIVIL + "mariage de ";
	private static final String acteDeces         			= PREFIX_ACTE_ETAT_CIVIL + "décès de ";
	private static final String actePubMariage     			= PREFIX_ACTE_ETAT_CIVIL + "publication de mariage de ";
	private static final String acteTestament      			= PREFIX_ACTE_ETAT_CIVIL + "testament de ";
	private static final String acteDivorce        			= PREFIX_ACTE_ETAT_CIVIL + "divorce de ";
	private static final String acteReconnaissance 			= PREFIX_ACTE_ETAT_CIVIL + "reconnaissance de ";
	private static final String acteReconnaissanceConjointe = PREFIX_ACTE_ETAT_CIVIL + "reconnaissance conjointe de ";
	private static final String acteFiancialles    			= PREFIX_ACTE_ETAT_CIVIL + "fiancialles de ";
	private static final String acteNaissanceDeces 			= PREFIX_ACTE_ETAT_CIVIL + "naissance et de décès de ";
	private static final String acteOppMariage	   			= PREFIX_ACTE_ETAT_CIVIL + "opposition au mariage de ";
	
	private static final String debutAnnee = "(";
	private static final String finAnnee   = ")";
	
	// Possible patterns for date
	private static final String datePatternFormat = "uuuu[-MM[-dd]]";
	
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(datePatternFormat);
	
	enum TypeActe { NAISSANCE, MARIAGE, DECES, PUBLICATION_MARIAGE, TESTAMENT, DIVORCE, RECONNAISSANCE, RECONNAISSANCE_CONJOINTE, FIANCAILLES, NAISSANCE_DECES, OPPOSITION_MARIAGE };
	
	private final TypeActe typeActe;
	private TemporalAccessor dateActe;

	public ActeEtatCivil(String titreSource) {
		
		super(titreSource);

		// Déterminer le type d'acte
		if (titre.startsWith(acteNaissance)) {
			typeActe = TypeActe.NAISSANCE;
		} else if (titre.startsWith(acteMariage)) {
			typeActe = TypeActe.MARIAGE;
		} else if (titre.startsWith(acteDeces)) {
			typeActe = TypeActe.DECES;
		} else if (titre.startsWith(actePubMariage)) {
			typeActe = TypeActe.PUBLICATION_MARIAGE;
		} else if (titre.startsWith(acteTestament)) {
			typeActe = TypeActe.TESTAMENT;
		} else if (titre.startsWith(acteDivorce)) {
			typeActe = TypeActe.DIVORCE;
		} else if (titre.startsWith(acteReconnaissance)) {
			typeActe = TypeActe.RECONNAISSANCE;
		} else if (titre.startsWith(acteReconnaissanceConjointe)) {
			typeActe = TypeActe.RECONNAISSANCE_CONJOINTE;
		} else if (titre.startsWith(acteFiancialles)) {
			typeActe = TypeActe.FIANCAILLES;
		} else if (titre.startsWith(acteNaissanceDeces)) {
			typeActe = TypeActe.NAISSANCE_DECES;
		} else if (titre.startsWith(acteOppMariage)) {
			typeActe = TypeActe.OPPOSITION_MARIAGE;
		} else {
			aLog.warning("Source de type acte inconnue: " + titre);
			typeActe = null;
		}

		// Déterminer l'année
		setAnnee();
	}

	public TypeActe getTypeActe() {
		return typeActe;
	}
	
	private void setAnnee() {
		int startIdx = titre.indexOf(debutAnnee);
		int endIdx = titre.indexOf(finAnnee);
		
		if ((startIdx > -1) && (endIdx > startIdx)) {
			String anString = titre.substring(startIdx + 1, endIdx);
			try {
				dateActe = dateTimeFormatter.parse(anString);
			} catch (Exception e) {
				aLog.log(Level.WARNING, "Année mal formattée dans le titre de source: " + titre, e);
			}
		} else {
			aLog.warning("Année pas trouvée dans le titre de source: " + titre);
		}
	}

	public TemporalAccessor getDateActe() {
		return dateActe;
	}
}
