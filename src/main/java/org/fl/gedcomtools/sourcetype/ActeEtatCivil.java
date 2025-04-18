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

public class ActeEtatCivil extends NomDeSource {

	private static final Logger aLog = Logger.getLogger(ActeEtatCivil.class.getName());
	
	private final static String acteNaissance      			= acteEtatCivil + "naissance de " ;
	private final static String acteMariage        			= acteEtatCivil + "mariage de " ;
	private final static String acteDeces         			= acteEtatCivil + "décès de " ;
	private final static String actePubMariage     			= acteEtatCivil + "publication de mariage de " ;
	private final static String acteTestament      			= acteEtatCivil + "testament de " ;
	private final static String acteDivorce        			= acteEtatCivil + "divorce de " ;
	private final static String acteReconnaissance 			= acteEtatCivil + "reconnaissance de " ;
	private final static String acteReconnaissanceConjointe = acteEtatCivil + "reconnaissance conjointe de " ;
	private final static String acteFiancialles    			= acteEtatCivil + "fiancialles de " ;
	private final static String acteNaissanceDeces 			= acteEtatCivil + "naissance et de décès de " ;
	private final static String acteOppMariage	   			= acteEtatCivil + "opposition au mariage de " ;
	
	private final static String debutAnnee = "(" ;
	private final static String finAnnee   = ")" ;
	
	// Possible patterns for date
	private final static String datePatternFormat = "uuuu[-MM[-dd]]" ;
	
	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(datePatternFormat) ;
	
	enum TypeActe { NAISSANCE, MARIAGE, DECES, PUBLICATION_MARIAGE, TESTAMENT, DIVORCE, RECONNAISSANCE, RECONNAISSANCE_CONJOINTE, FIANCAILLES, NAISSANCE_DECES, OPPOSITION_MARIAGE } ;
	
	private TypeActe typeActe;
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
		int startIdx = titre.indexOf(debutAnnee) ;
		int endIdx   = titre.indexOf(finAnnee) ;
		
		if ((startIdx > -1) && (endIdx > startIdx)) {
			String anString = titre.substring(startIdx+1, endIdx) ;
			try {
				dateActe = dateTimeFormatter.parse(anString) ;
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
