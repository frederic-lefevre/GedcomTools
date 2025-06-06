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

package org.fl.gedcomtools.sosa;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.gedcomtools.entity.Family;
import org.fl.gedcomtools.entity.Individual;
import org.fl.gedcomtools.io.GedcomFileWriter;

public class ArbreDeSosa {

	private static final Logger gLog = Logger.getLogger(ArbreDeSosa.class.getName());
	
	private final Individual souche;
	
	// Map : id Gedcom de l'individu vers sosa
	private final Map<Individual, Sosa> sosaMap;
	
	// Arbre de sosa : liste de génération
	private final List<GenerationSosa> arbre;
	
	// liste des Sosa "terminaux" (donc correspondant à une (ou plusieurs si implex) branche(s) descendante(s)
	private final List<Sosa> branches ;
	
	// Map : numero Sosa vers individu
	private final Map<Long,Individual> sosaNumberMap;
	
	public ArbreDeSosa(Individual s) {

		souche = s;
		sosaMap = new HashMap<>();
		sosaNumberMap = new HashMap<>();
		branches = new ArrayList<>();

		// création de l'arbre
		arbre = new ArrayList<>();

		// ajout de la première génération
		GenerationSosa nextGen = new GenerationSosa(1);
		// ajout de la souche
		Sosa sosaSouche = new Sosa(souche);
		nextGen.addPosition(new PositionSosa(1, sosaSouche));
		sosaMap.put(souche, sosaSouche);

		do {
			arbre.add(nextGen);
			nextGen = developGeneration(nextGen);
		} while (nextGen.getNombrePositionSosa() > 0);
		SosaComparator sosaComparator = new SosaComparator();
		Collections.sort(branches, sosaComparator);
	}

	private GenerationSosa developGeneration(GenerationSosa g) {
		
		GenerationSosa gRes = new GenerationSosa(g.getNumero() + 1);

		Individual pere, mere;
		Sosa sPere, sMere, sFils;
		PositionSosa fils;
		Family fam;

		for (int i = 0; i < g.getNombrePositionSosa(); i++) {

			fils = g.getSosa(i);
			sFils = fils.getSo();
			pere = null;
			mere = null;

			fam = sFils.getIndividual().getMainFamily();
			if (fam != null) {
				mere = fam.getWife();
				pere = fam.getHusband();
			}

			if (pere != null) {
				sPere = sosaMap.get(pere);
				if (sPere == null) {
					sPere = new Sosa(pere);
					sosaMap.put(pere, sPere);
				}
				gRes.addPosition(new PositionSosa(Sosa.getPereNum(fils.getPosition()), sPere));
			}
			if (mere != null) {
				sMere = sosaMap.get(mere);
				if (sMere == null) {
					sMere = new Sosa(mere);
					sosaMap.put(mere, sMere);
				}
				gRes.addPosition(new PositionSosa(Sosa.getMereNum(fils.getPosition()), sMere));
			}

			sosaNumberMap.put(fils.getPosition(), sFils.getIndividual());
			if ((pere == null) && (mere == null)) {
				sFils.setIsTerminal(true);
				if (!branches.contains(sFils)) {
					branches.add(sFils);
				}
			}
		}
		return gRes;

	}
	
	private final static String NEWLINE = System.getProperty("line.separator");
	private final static String SEPARATEUR_GENERATION = "____;____;____;____;____;____;____;____;____;____" + NEWLINE;
	private final static String ENTETE_SOSAS = "Sosa;Nom;Professions;Ascendants connus;Age moyen des ascendants;Sources;Résidences;Professions;Implex;Sosas" + NEWLINE;
		
	public void printArbreSosa(GedcomFileWriter gedcomWriter) {

		int nbTotalSosa = 0;
		try (BufferedWriter out = gedcomWriter.getBufferedWriter()) {

			out.append(ENTETE_SOSAS);
			for (GenerationSosa gen : arbre) {
				nbTotalSosa = nbTotalSosa + gen.getGeneration().size();
				out.append(SEPARATEUR_GENERATION);
				out.append(" ;      Génération ").append(Integer.toString(gen.getNumero())).append(" - ");
				out.append(Integer.toString(gen.getGeneration().size())).append("/").append(Long.toString(gen.getNbMaxIndividual()));
				out.append(NEWLINE);
				out.append(SEPARATEUR_GENERATION);

				for (PositionSosa pSosa : gen.getGeneration()) {
					Individual sosaInd = pSosa.getSo().getIndividual();
					appendCsvField(out, Long.toString(pSosa.getPosition()));
					appendCsvField(out, sosaInd.getIndividualName());
					appendCsvField(out, sosaInd.printProfession());
					appendCsvField(out, Integer.toString(sosaInd.getNbAscendants()));
					appendCsvField(out, sosaInd.getAgeMoyenAscendants().printAge());
					appendCsvField(out, Integer.toString(sosaInd.getNbSources()));
					appendCsvField(out, Integer.toString(sosaInd.getNbResidence()));
					appendCsvField(out, Integer.toString(sosaInd.getNbProfession()));
					appendCsvField(out, Integer.toString(pSosa.getSo().getImplex()));
					appendCsvField(out, pSosa.getSo().printSosaNumbers());
					out.append(NEWLINE);
				}
			}
		} catch (Exception e) {
			gLog.log(Level.SEVERE, "Exception writing arbre sosa", e);
		}
	}
	
	
	public void printBranchesDescendantes(GedcomFileWriter gedcomWriter) {

		try (BufferedWriter out = gedcomWriter.getBufferedWriter()) {

			int nombreGenerationTotal = arbre.size();
			int nombreGeneration = nombreGenerationTotal;
			while (nombreGeneration > 0) {
				out.append("Génération ").append(Integer.toString(nombreGeneration)).append(";");
				nombreGeneration = nombreGeneration - 1;
			}
			out.newLine();
			for (Sosa s : branches) {
				for (long numSosa : s.getNumerosSosa()) {
					out.append(printBranchesDunePositionSosa(numSosa, nombreGenerationTotal));
				}
			}
		} catch (Exception e) {
			gLog.log(Level.SEVERE, "Exception writing branches descendantes ", e);
		}
	}

	public boolean faitPartieDesSosas(Individual i) {
		return sosaMap.containsKey(i);
	}
	
	// Construction d'une branche descentdante à partir d'un sosa terminal 
	private StringBuilder printBranchesDunePositionSosa(long s, int nbGenerationTotal) {
		
		StringBuilder br = new StringBuilder();
		
		Individual sosaTerminal = sosaNumberMap.get(s);
		int decalage = nbGenerationTotal - Sosa.numeroGeneration(s);
		
		if (Sosa.estUnHomme(s)) {
			Individual saFemmeTerminal = getConjointSiSosaTerminal(s);
			if (saFemmeTerminal != null) {
				decaler(decalage, br);
				br.append(s).append(" ").append(sosaTerminal.getIndividualName()).append(" X ").append(saFemmeTerminal.getIndividualName()).append(";");
			} else {
				decaler(decalage, br);
				br.append(s).append(" ").append(sosaTerminal.getIndividualName()).append(";");
			}
		} else {
			Individual sonMariTerminal = getConjointSiSosaTerminal(s);
			if (sonMariTerminal == null) {
				decaler(decalage, br);
				br.append(s).append(" ").append(sosaTerminal.getIndividualName()).append(";");
			}
		}
		
		if (br.length() > 0) {
			long sosaNum = Sosa.getEnfantNum(s);
			while (sosaNum > 0) {
				
				Individual ind = sosaNumberMap.get(sosaNum);
				
				br.append(sosaNum).append(" ").append(ind.getIndividualName()).append(";");
				sosaNum = Sosa.getEnfantNum(sosaNum);
				
			}
			return br.append(NEWLINE);
		} else {
			return br;
		}		
	}
	
	// Décaler à la bonne colonne (génération) dans le fichier des branches
	private void decaler(int decalage, StringBuilder br) {
		while (decalage > 0) {
			br.append(";");
			decalage = decalage - 1;
		}
	}

	private Individual getConjointSiSosaTerminal(long num) {

		Individual res = null;
		long numConjoint = Sosa.getConjointNum(num);
		Individual conjoint = sosaNumberMap.get(numConjoint);
		if (conjoint != null) {
			Sosa conjointSosa = sosaMap.get(conjoint);
			if ((conjointSosa != null) && (conjointSosa.isTerminal())) {
				res = conjoint;
			}
		}
		return res;
	}

	private void appendCsvField(BufferedWriter out, String field) throws IOException {
		out.append("\" ").append(field).append("\"").append(";");
	}
	
}
