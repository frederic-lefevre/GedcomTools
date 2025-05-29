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

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.gedcomtools.entity.GedcomEntity;
import org.fl.gedcomtools.entity.GedcomMultimediaObject;
import org.fl.gedcomtools.entity.GedcomSource;
import org.fl.gedcomtools.entity.Individual;
import org.fl.gedcomtools.entity.IndividualProfession;
import org.fl.gedcomtools.entity.Residence;
import org.fl.gedcomtools.gui.GedcomToolsGui;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.util.FilterCounter;
import org.fl.util.FilterCounter.LogRecordCounter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GedcomParserTest {

	@BeforeAll
	static void init() {
		Config.initConfig(GedcomToolsGui.DEFAULT_PROP_FILE);
	}
	
	@Test
	void shouldParseIndividualAndSource() {
		
		// Gedcom to parse
		List<String> gLines = Arrays.asList(
				"0 @I07491@ INDI",
				"1 NAME Jeanne /Geoffroy/",
				"2 GIVN Jeanne",
				"2 SURN Geoffroy",
				"1 SEX F",
				"1 BIRT",
				"2 DATE ABT 1710",
				"1 SOUR @S06315@",
				"0 @S06315@ SOUR",
				"1 TITL Acte de mariage de Nicolas Mary et de Jeanne Marie Justinard (1766)",
				"1 OBJE",
				"2 FORM jpeg",
				"2 TITL NicolasMaryJeanneMarieJustinard1766M",
				"2 FILE /FredericPersonnel/FamilleEnfants/genealogie/DocumentsArbreGuiminel/ImagesActes/1750_1799/1765_1769/NicolasMaryJeanneMarieJustinard1766M.jpg"
				);
		
		// Get a parser
		GedcomParser gedcomParser = new GedcomParser();
		
		assertThat(gedcomParser).isNotNull();
		
		// Parse the gedcom
		assertThat(gLines.stream().map(gedcomParser::parseGedcomLine).allMatch(GedcomLine::isValid)).isTrue();
		assertThat(gedcomParser.finalizeParsing()).isTrue();
		
		// Verify results
		Collection<GedcomEntity> entities = gedcomParser.getListeEntity();
		assertThat(entities).isNotNull().hasSize(2).satisfiesExactly(
				entity -> assertThat(entity).isNotNull().isInstanceOfSatisfying(Individual.class, individual -> {
					assertThat(individual.getIndividualName()).isEqualTo("Jeanne /Geoffroy/");
					assertThat(individual.getNotes()).isNotNull().isEmpty();
					assertThat(individual.getSources()).isNotNull().hasSize(1);
					assertThat(individual.getNbSources()).isEqualTo(1);
					assertThat(individual.getNbAscendants()).isZero();
					assertThat(individual.getAgeExactEnJours()).isZero();
					assertThat(individual.getMainFamily()).isNull();
					assertThat(individual.getNbProfession()).isZero();
					assertThat(individual.getNbResidence()).isZero();
				}), 
				entity -> assertThat(entity).isNotNull().isInstanceOfSatisfying(GedcomSource.class, source -> {
					assertThat(source.hasNoReferences()).isFalse();
					assertThat(source.getNotes()).isNotNull().isEmpty();
					assertThat(source.getSourceTitle()).isEqualTo("Acte de mariage de Nicolas Mary et de Jeanne Marie Justinard (1766)");
					assertThat(source.getIndividuals()).isNotNull().singleElement().satisfies(
							individual -> assertThat(individual.getIndividualName()).isEqualTo("Jeanne /Geoffroy/"));
					assertThat(source.getFamilies()).isNotNull().isEmpty();
					assertThat(source.getMediaFiles()).isNotNull().singleElement()
						.isEqualTo("/FredericPersonnel/FamilleEnfants/genealogie/DocumentsArbreGuiminel/ImagesActes/1750_1799/1765_1769/NicolasMaryJeanneMarieJustinard1766M.jpg");
					
				}));
	}

	// Gedcom to parse
	private static final List<String> jThorn = Arrays.asList(
			"0 @I00012@ INDI",
			"1 NAME Joseph /Thorn/",
			"2 TYPE birth",
			"2 GIVN Joseph",
			"2 SURN Thorn",
			"1 SEX M",
			"1 BIRT",
			"2 TYPE Naissance de Thorn, Joseph",
			"2 DATE 11 DEC 1846",
			"2 PLAC Bous, Remich, Grevenmacher, Luxembourg",
			"1 DEAT",
			"2 TYPE Décès de Thorn, Joseph",
			"2 DATE 9 AUG 1900",
			"2 PLAC Paris 17ème, Paris, Île-de-France, France",
			"1 OCCU Peintre en batiment",
			"2 DATE 22 JAN 1870",
			"1 RESI",
			"2 TYPE Résidence de Thorn, Joseph",
			"2 DATE 22 JAN 1870",
			"2 PLAC 42 rue Fazillau, Levallois-Perret, Hauts-de-Seine, Île-de-France, France",
			"1 SOUR @S00074@",
			"0 @S00074@ SOUR",
			"1 TITL Acte de naissance de Joseph Thorn (1846)",
			"1 OBJE",
			"2 FORM jpeg",
			"2 FILE /fredericpersonnel/familleenfants/genealogie/documentsarbreguiminel/ImagesActes/1825_1849/1845_1849/JosephThorn1846N.jpg"
			);
	
	@Test
	void shouldParseResidence() {
		
		// Get a parser
		GedcomParser gedcomParser = new GedcomParser();
		
		// Parse the gedcom
		assertThat(jThorn.stream().map(gedcomParser::parseGedcomLine).allMatch(GedcomLine::isValid)).isTrue();
		assertThat(gedcomParser.finalizeParsing()).isTrue();
		
		// Verify results
		Collection<GedcomEntity> entities = gedcomParser.getListeEntity();
		
		assertThat(entities).isNotNull().hasSize(2).anySatisfy(entity -> { 
			assertThat(entity).isNotNull().isInstanceOfSatisfying(Individual.class, individual -> {
				assertThat(individual.getNbResidence()).isEqualTo(1);
				Residence residence = individual.getResidences().getFirst();
				assertThat(residence.getPlace()).isEqualTo("42 rue Fazillau, Levallois-Perret, Hauts-de-Seine, Île-de-France, France");
				assertThat(residence.getDate().isValid()).isTrue();
				assertThat(residence.getDate().isExact()).isTrue();
			});
		});
	}
	
	@Test
	void shouldParseProfession() {
		
		// Get a parser
		GedcomParser gedcomParser = new GedcomParser();
		
		// Parse the gedcom
		assertThat(jThorn.stream().map(gedcomParser::parseGedcomLine).allMatch(GedcomLine::isValid)).isTrue();
		assertThat(gedcomParser.finalizeParsing()).isTrue();
		
		// Verify results
		Collection<GedcomEntity> entities = gedcomParser.getListeEntity();
		
		assertThat(entities).isNotNull().hasSize(2).anySatisfy(entity -> { 
			assertThat(entity).isNotNull().isInstanceOfSatisfying(Individual.class, individual -> {
				assertThat(individual.getNbProfession()).isEqualTo(1);
				IndividualProfession profession = individual.getAllProfessionOccurences().getFirst();
				assertThat(profession.getProfession()).isEqualTo("Peintre en batiment");
				assertThat(profession.getDate().isValid()).isTrue();
				assertThat(profession.getDate().isExact()).isTrue();
			});
		});
	}
	
	@Test
	void shouldParseMultimediaObject() {
		
		// Gedcom to parse
		List<String> gLines = Arrays.asList(
				"0 @O03355@ OBJE",
				"1 FILE /fredericpersonnel/familleenfants/genealogie/documentsarbreguiminel/ImagesActes/1750_1799/1760_1764/ReneDeplaix1764N.jpg",
				"2 FORM jpg",
				"2 TITL ReneDeplaix1764N",
				"1 CHAN",
				"2 DATE 31 AUG 2012",
				"3 TIME 12:24:39"
				);
		
		// Get a parser
		GedcomParser gedcomParser = new GedcomParser();
		
		assertThat(gedcomParser).isNotNull();
		
		// Parse the gedcom
		assertThat(gLines.stream().map(gedcomParser::parseGedcomLine).allMatch(GedcomLine::isValid)).isTrue();
		assertThat(gedcomParser.finalizeParsing()).isTrue();
		
		// Verify results
		Collection<GedcomEntity> entities = gedcomParser.getListeEntity();
		assertThat(entities).isNotNull().singleElement().satisfies(entity -> { 
			assertThat(entity).isNotNull().isInstanceOfSatisfying(GedcomMultimediaObject.class, multimediaObject -> {
				assertThat(multimediaObject.getId()).isNotNull();
				assertThat(multimediaObject.getMediaFileName()).isEqualTo("/fredericpersonnel/familleenfants/genealogie/documentsarbreguiminel/ImagesActes/1750_1799/1760_1764/ReneDeplaix1764N.jpg");
				assertThat(multimediaObject.getMediaFileType()).isEqualTo("jpg");
			}); 
		});
	}
	
	@Test
	void shouldParseSourceLinkedToMultimediaObject() {
		
		// Gedcom to parse
		List<String> gLines = Arrays.asList(
				"0 @I02043@ INDI",
				"1 NAME René /Deplaix/",
				"2 GIVN René",
				"2 SURN Deplaix",
				"1 SEX M",
				"1 BIRT",
				"2 TYPE Naissance de Deplaix, René",
				"2 DATE 7 OCT 1764",
				"1 SOUR @S00243@",
				"0 @S00243@ SOUR",
				"1 TITL Acte de naissance de René Deplaix (1764)",
				"1 OBJE @O03355@",
				"0 @O03355@ OBJE",
				"1 FILE /fredericpersonnel/familleenfants/genealogie/documentsarbreguiminel/ImagesActes/1750_1799/1760_1764/ReneDeplaix1764N.jpg",
				"2 FORM jpg",
				"2 TITL ReneDeplaix1764N",
				"1 CHAN",
				"2 DATE 31 AUG 2012",
				"3 TIME 12:24:39"
				);
		
		// Get a parser
		GedcomParser gedcomParser = new GedcomParser();
		
		assertThat(gedcomParser).isNotNull();
		
		// Parse the gedcom
		assertThat(gLines.stream().map(gedcomParser::parseGedcomLine).allMatch(GedcomLine::isValid)).isTrue();	
		assertThat(gedcomParser.finalizeParsing()).isTrue();
		
		// Verify results
		Collection<GedcomEntity> entities = gedcomParser.getListeEntity();
		assertThat(entities).isNotNull().hasSize(3).satisfiesExactly(
			entity -> assertThat(entity).isNotNull().isInstanceOfSatisfying(Individual.class, 
				individual -> {
					assertThat(individual.getIndividualName()).isEqualTo("René /Deplaix/");
					assertThat(individual.getNotes()).isNotNull().isEmpty();
					assertThat(individual.getSources()).isNotNull().hasSize(1);
				}),
			entity -> assertThat(entity).isNotNull().isInstanceOfSatisfying(GedcomSource.class,
				source -> {
					assertThat(source.hasNoReferences()).isFalse();
					assertThat(source.getNotes()).isNotNull().isEmpty();
					assertThat(source.getSourceTitle()).isEqualTo("Acte de naissance de René Deplaix (1764)");
					assertThat(source.getMultimedias()).isNotNull().hasSize(1);
				}),
			entity -> assertThat(entity).isNotNull().isInstanceOfSatisfying(GedcomMultimediaObject.class, 
				multimediaObject -> {
					assertThat(multimediaObject.getId()).isNotNull();
					assertThat(multimediaObject.getMediaFileName()).isEqualTo("/fredericpersonnel/familleenfants/genealogie/documentsarbreguiminel/ImagesActes/1750_1799/1760_1764/ReneDeplaix1764N.jpg");
					assertThat(multimediaObject.getMediaFileType()).isEqualTo("jpg");
				})
		);
	}
	
	@Test
	void shouldDetectNonExistantMedia() {
		
		// Gedcom to parse
		List<String> gLines = Arrays.asList(
				"0 @S06315@ SOUR",
				"1 TITL Acte de mariage de Nicolas Mary et de Jeanne Marie Justinard (1766)",
				"1 OBJE",
				"2 FORM jpeg",
				"2 TITL NicolasMaryJeanneMarieJustinard1766M",
				"2 FILE /FredericPersonnel/FamilleEnfants/genealogie/DocumentsArbreGuiminel/ImagesActes/1750_1799/1765_1769/dontExists.jpg"
				);
		
		LogRecordCounter logFilterCounter = FilterCounter.getLogRecordCounter(Logger.getLogger(GedcomLine.class.getName()));
		
		// Get a parser
		GedcomParser gedcomParser = new GedcomParser();
		
		assertThat(gedcomParser).isNotNull();
		
		// Parse the gedcom
		assertThat(gLines.stream().map(gedcomParser::parseGedcomLine).allMatch(GedcomLine::isValid)).isFalse();
		
		assertThat(logFilterCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logFilterCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
		
		logFilterCounter.stopLogCountAndFilter();
	}
	
	
}
