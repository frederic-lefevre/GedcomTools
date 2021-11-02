package org.fl.gedcomtools;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.gedcomtools.entity.GedcomEntity;
import org.fl.gedcomtools.entity.GedcomMultimediaObject;
import org.fl.gedcomtools.entity.GedcomSource;
import org.fl.gedcomtools.entity.Individual;
import org.fl.gedcomtools.line.GedcomLine;
import org.junit.jupiter.api.Test;

class GedcomParserTest {

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
						.isEqualTo("C:\\FredericPersonnel\\FamilleEnfants\\genealogie\\DocumentsArbreGuiminel\\ImagesActes\\1750_1799\\1765_1769\\NicolasMaryJeanneMarieJustinard1766M.jpg");
					
				}));
	}

	@Test
	void shouldParseMultimediaObject() {
		
		// Gedcom to parse
		List<String> gLines = Arrays.asList(
				"0 @O03355@ OBJE",
				"1 FILE c:\\fredericpersonnel\\familleenfants\\genealogie\\documentsarbreguiminel\\ImagesActes/1750_1799/1760_1764/ReneDeplaix1764N.jpg",
				"2 FORM jpg",
				"2 TITL ReneDeplaix1764N",
				"1 CHAN",
				"2 DATE 31 AUG 2012",
				"3 TIME 12:24:39"
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
				assertThat(entities).isNotNull().singleElement().satisfies(entity -> { 
					assertThat(entity).isNotNull().isInstanceOfSatisfying(GedcomMultimediaObject.class, multimediaObject -> {
						assertThat(multimediaObject.getId()).isNotNull();
						assertThat(multimediaObject.getMediaFileName()).isEqualTo("c:\\fredericpersonnel\\familleenfants\\genealogie\\documentsarbreguiminel\\ImagesActes/1750_1799/1760_1764/ReneDeplaix1764N.jpg");
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
				"1 FILE c:\\fredericpersonnel\\familleenfants\\genealogie\\documentsarbreguiminel\\ImagesActes/1750_1799/1760_1764/ReneDeplaix1764N.jpg",
				"2 FORM jpg",
				"2 TITL ReneDeplaix1764N",
				"1 CHAN",
				"2 DATE 31 AUG 2012",
				"3 TIME 12:24:39"
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
					assertThat(multimediaObject.getMediaFileName()).isEqualTo("c:\\fredericpersonnel\\familleenfants\\genealogie\\documentsarbreguiminel\\ImagesActes/1750_1799/1760_1764/ReneDeplaix1764N.jpg");
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
				"2 FILE C:\\FredericPersonnel\\FamilleEnfants\\genealogie\\DocumentsArbreGuiminel\\ImagesActes\\1750_1799\\1765_1769\\dontExists.jpg"
				);
		
		// Get a parser
		Logger log = Logger.getLogger(GedcomParserTest.class.getName()) ;
		log.setLevel(Level.WARNING) ;
		GedcomParser gedcomParser = new GedcomParser(log) ;
		
		assertThat(gedcomParser).isNotNull() ;
		
		// Parse the gedcom
		assertThat(gLines.stream().map(gedcomParser::parseGedcomLine).allMatch(GedcomLine::isValid)).isFalse();
	}
	
	
}
