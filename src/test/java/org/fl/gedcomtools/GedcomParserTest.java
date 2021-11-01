package org.fl.gedcomtools;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.gedcomtools.entity.GedcomEntity;
import org.fl.gedcomtools.entity.GedcomSource;
import org.fl.gedcomtools.entity.Individual;
import org.fl.gedcomtools.line.GedcomLine;
import org.junit.jupiter.api.Test;

class GedcomParserTest {

	@Test
	void test() {
		
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
				"2 FILE C:\\FredericPersonnel\\FamilleEnfants\\genealogie\\DocumentsArbreGuiminel\\ImagesActes\\1750_1799\\1765_1769\\NicolasMaryJeanneMarieJustinard1766M.jpg"
				);
		
		// Get a parser
		Logger log = Logger.getLogger(GedcomParserTest.class.getName()) ;
		log.setLevel(Level.WARNING) ;
		GedcomParser gedcomParser = new GedcomParser(log) ;
		
		assertThat(gedcomParser).isNotNull() ;
		
		// Parse the gedcom
		assertThat(gLines.stream().map(gedcomParser::parseGedcomLine).allMatch(GedcomLine::isValid)).isTrue();
		assertThat(gedcomParser.finalizeParsing()).isTrue();
		
		// Verify results
		Collection<GedcomEntity> entities = gedcomParser.getListeEntity();
		assertThat(entities).isNotNull().isNotEmpty().hasSize(2).satisfiesExactly(
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
						.isEqualTo("C:\\FredericPersonnel\\FamilleEnfants\\genealogie\\DocumentsArbreGuiminel\\ImagesActes\\1750_1799\\1765_1769\\NicolasMaryJeanneMarieJustinard1766M.jpg");
					
				}));
	}

}
