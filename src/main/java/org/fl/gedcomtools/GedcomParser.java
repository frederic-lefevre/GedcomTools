package org.fl.gedcomtools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.fl.gedcomtools.entity.EntityReferencesMap;
import org.fl.gedcomtools.entity.Family;
import org.fl.gedcomtools.entity.GedcomEntity;
import org.fl.gedcomtools.entity.GedcomEntityReference;
import org.fl.gedcomtools.entity.GedcomNote;
import org.fl.gedcomtools.entity.GedcomSource;
import org.fl.gedcomtools.entity.Individual;
import org.fl.gedcomtools.line.GedcomId;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.line.GedcomTag;
import org.fl.gedcomtools.line.GedcomTagChain;
import org.fl.gedcomtools.line.GedcomTagValue;
import org.fl.gedcomtools.profession.RepertoireProfession;
import org.fl.gedcomtools.util.GedcomDateValue;

public class GedcomParser {

	private static GedcomParser gedcomParser = null ;
	
	private Logger		   gLog ;
	private GedcomTagChain currentTagChain ;

	// List of all the gedcom entity in the same order as input
	private List<GedcomEntity> gArray ;

	private EntityReferencesMap<GedcomEntity> entityReferencesMap ;
	private EntityReferencesMap<Individual>	  personnesReferencesMap ;
	private EntityReferencesMap<Family>	 	  famillesReferencesMap ;
	private EntityReferencesMap<GedcomSource> sourcesReferencesMap ;
	private EntityReferencesMap<GedcomNote>	  notesReferencesMap ;
	
	private RepertoireProfession 	 repertoireProfession ;

	// Last parsed entities
	private Individual 	 lastIndividual ;
	private Family 	   	 lastFamily ;
	private GedcomSource lastSource ;
	
	private GedcomParser(Logger l) {
		gLog				 = l ;
		currentTagChain 	 = new GedcomTagChain() ;
		gArray 		 		 = new ArrayList<GedcomEntity>(10000) ;
		
		entityReferencesMap	   = new EntityReferencesMap<GedcomEntity>() ;
		personnesReferencesMap = new EntityReferencesMap<Individual>() ;
		famillesReferencesMap  = new EntityReferencesMap<Family>() ;
		sourcesReferencesMap   = new EntityReferencesMap<GedcomSource>() ;
		notesReferencesMap	   = new EntityReferencesMap<GedcomNote>() ;
		
		repertoireProfession = new RepertoireProfession(gLog) ;
		
		lastIndividual = null ;
		lastFamily 	   = null ;
		lastSource	   = null ;
	}

	public static GedcomParser getGedcomParse (Logger l) {
		if (gedcomParser == null) {
			gedcomParser = new GedcomParser(l) ;
		}
		return gedcomParser ;
	}
	
	public GedcomLine parseGedcomLine(String gLine) {

		GedcomLine gedcomLine = new GedcomLine(gLine, currentTagChain, gLog);
		currentTagChain = gedcomLine.getTagChain() ;
		
		if (gedcomLine.getLevel() == 0) {
			// Début d'une entité
			GedcomEntity newEntity = createGedcomEntity(gedcomLine) ;
			gArray.add(newEntity) ;
		} else {
			// Suite de la dernière entité créée
			gArray.get(gArray.size()-1).addGedcomLine(parseGedcomLine(gedcomLine)) ;
		}
		
		return gedcomLine;
	}
	
	private GedcomEntity createGedcomEntity(GedcomLine gLine) {
		
		GedcomTag tag = gLine.getTag() ;
		if (tag == null) {
			
			gLog.severe("Tag inconnu pour la ligne : " + gLine.getOriginalLine());
			GedcomEntity newEntity = new GedcomEntity(gLine, gLog) ;
			entityReferencesMap.addNewEntity(newEntity) ;
			return newEntity ;
			
		} else if (tag.equalsValue(GedcomTagValue.INDI)) {
			
			Individual ind = new Individual(gLine, gLog) ;
			lastIndividual = ind ;
			personnesReferencesMap.addNewEntity(ind) ;
			return ind ;
			
		} else if (tag.equalsValue(GedcomTagValue.FAM)) {
			
			Family fam = new Family(gLine, gLog) ;
			lastFamily = fam ;
			famillesReferencesMap.addNewEntity(fam) ;
			return fam ;
			
		} else if (tag.equalsValue(GedcomTagValue.SOUR)) {
			
			GedcomSource source = new GedcomSource(gLine, gLog) ;
			lastSource			= source ;
			sourcesReferencesMap.addNewEntity(source) ;
			return source ;
			
		} else if (tag.equalsValue(GedcomTagValue.NOTE)) {
			
			GedcomNote note = new GedcomNote(gLine, gLog) ;
			notesReferencesMap.addNewEntity(note) ;
			return note ;
			
		} else {
			GedcomEntity newEntity = new GedcomEntity(gLine, gLog) ;
			entityReferencesMap.addNewEntity(newEntity) ;
			return newEntity ;
		}
	}

	private GedcomLine parseGedcomLine(GedcomLine gedcomLine) {
		
		if (gedcomLine.tagForLevelEquals(0, GedcomTagValue.INDI)) {
			if (lastIndividual != null) {
				parseIndividualGedcomLine(gedcomLine) ;
			} else {
				gLog.severe("lastIndividual null at line " + gedcomLine.getOriginalLine());
			}
		} else if (gedcomLine.tagForLevelEquals(0, GedcomTagValue.FAM)) {
			if (lastFamily != null) {
				parseFamilyGedcomLine(gedcomLine) ;
			} else {
				gLog.severe("lastFamily null at line " + gedcomLine.getOriginalLine());
			}
		} else if (gedcomLine.tagForLevelEquals(0, GedcomTagValue.SOUR)) {
			if (lastSource != null) {
				parseSourceGedcomLine(gedcomLine) ;
			} else {
				gLog.severe("lastSource null at line " + gedcomLine.getOriginalLine());
			}
		}
		return gedcomLine ;
	}

	private static List<GedcomTagValue> DATE_BIRT_INDI = Arrays.asList(GedcomTagValue.DATE, GedcomTagValue.BIRT, GedcomTagValue.INDI) ;
	private static List<GedcomTagValue> DATE_DEAT_INDI = Arrays.asList(GedcomTagValue.DATE, GedcomTagValue.DEAT, GedcomTagValue.INDI) ;
	
	private void parseIndividualGedcomLine(GedcomLine gedcomLine) {
		
		if (gedcomLine.getLevel() == 1) {
			
			if (gedcomLine.tagValueEquals(GedcomTagValue.NAME)) {
				lastIndividual.setIndividualName(gedcomLine.getContent());
			}  else if (gedcomLine.tagValueEquals(GedcomTagValue.FAMC)) {
				String id = GedcomId.extractId(gedcomLine.getContent()) ;				
				lastIndividual.addFamilyAsChild(famillesReferencesMap.getOrCreateEntityReference(id)) ;
			}  else if (gedcomLine.tagValueEquals(GedcomTagValue.FAMS)) {
				String id = GedcomId.extractId(gedcomLine.getContent()) ;
				lastIndividual.addFamilyAsSpouse(famillesReferencesMap.getOrCreateEntityReference(id)) ;
			}  else if (gedcomLine.tagValueEquals(GedcomTagValue.OCCU)) {
				String profession = gedcomLine.getContent() ;
				lastIndividual.addProfession(profession) ;				
				repertoireProfession.addProfessionMembre(profession, lastIndividual) ;
			}  else if (gedcomLine.tagValueEquals(GedcomTagValue.RESI)) {
				lastIndividual.addResidence() ;
			}
		} else if (gedcomLine.getLevel() == 2) {
			
			if (gedcomLine.equalsTagChain(DATE_BIRT_INDI)) {
				lastIndividual.addDateNaissance(new GedcomDateValue( gedcomLine.getContent(), gLog)) ;
			} else if (gedcomLine.equalsTagChain(DATE_DEAT_INDI)) {
				lastIndividual.addDateDeces(new GedcomDateValue( gedcomLine.getContent(), gLog)) ;
			}
		}
		
		if (gedcomLine.tagValueEquals(GedcomTagValue.SOUR)) {
			String id = GedcomId.extractId(gedcomLine.getContent()) ;
			lastIndividual.addSourceReference(sourcesReferencesMap.getOrCreateEntityReference(id));
			if (gedcomLine.getLevel() > 1) {
				gLog.warning("Source au niveau 2 dans l'individu: " +  lastIndividual.getGedcomSource()) ;
			}
		}
		
		if (gedcomLine.tagValueEquals(GedcomTagValue.NOTE)) {
			String id = GedcomId.extractId(gedcomLine.getContent());
			if (id != null) {
				lastIndividual.addNoteReference(notesReferencesMap.getOrCreateEntityReference(id));
			}
		}
	}
	
	private static List<GedcomTagValue> DATE_MARR_FAM = Arrays.asList(GedcomTagValue.DATE, GedcomTagValue.MARR, GedcomTagValue.FAM) ;
	
	private void parseFamilyGedcomLine(GedcomLine gedcomLine) {
		
		// some first level tag parsing
		if (gedcomLine.getLevel() == 1) {
			if (gedcomLine.tagValueEquals(GedcomTagValue.WIFE)) {
				String id = GedcomId.extractId(gedcomLine.getContent()) ;	
				lastFamily.setWifeRef(personnesReferencesMap.getOrCreateEntityReference(id));
			} else if (gedcomLine.tagValueEquals(GedcomTagValue.HUSB)) {
				String id = GedcomId.extractId(gedcomLine.getContent()) ;	
				lastFamily.setHusbandRef(personnesReferencesMap.getOrCreateEntityReference(id));
			} else if (gedcomLine.tagValueEquals(GedcomTagValue.CHIL)) {
				String id = GedcomId.extractId(gedcomLine.getContent()) ;
				lastFamily.addChild(personnesReferencesMap.getOrCreateEntityReference(id));
			}
		} else if (gedcomLine.getLevel() == 2) {
			if (gedcomLine.getTagChain().equals(DATE_MARR_FAM)) {
				lastFamily.setDateMariage(new GedcomDateValue( gedcomLine.getContent(), gLog));
			}
		} 
		
		// parse source and note
		if (gedcomLine.tagValueEquals(GedcomTagValue.SOUR)) {
			lastFamily.addSource(sourcesReferencesMap.getOrCreateEntityReference(GedcomId.extractId(gedcomLine.getContent())));
			if (gedcomLine.getLevel() > 1) {
				gLog.warning("Source au niveau 2 dans la famille: " +  lastFamily.getGedcomSource()) ;
			}
		}
		if (gedcomLine.tagValueEquals(GedcomTagValue.NOTE)) {			
			String id = GedcomId.extractId(gedcomLine.getContent());
			if (id != null) {
				lastFamily.addNote(notesReferencesMap.getOrCreateEntityReference(id));
			}
		}		
	}
	
	private void parseSourceGedcomLine(GedcomLine gedcomLine) {
		
		int level = gedcomLine.getLevel() ;
		if (gedcomLine.tagValueEquals(GedcomTagValue.NOTE)) {
			String id = GedcomId.extractId(gedcomLine.getContent()) ;
			lastSource.addNote(notesReferencesMap.getOrCreateEntityReference(id));
		} else if (gedcomLine.tagValueEquals(GedcomTagValue.FILE) || gedcomLine.tagValueEquals(GedcomTagValue.OBJE)) {
			lastSource.addMediaFile(gedcomLine.getContent()) ;
		} else if ((gedcomLine.tagValueEquals(GedcomTagValue.TITL)) && (level == 1)) {
			String sourceTitle = gedcomLine.getContent() ;
			if (sourceTitle == null) {
				gLog.warning("La source (id=" + lastSource.getId() + ") semble avoir un titre null: \n" + lastSource.getGedcomSource() + "\n OriginalLine=" + gedcomLine.getOriginalLine()) ;
			} else {
				lastSource.setSourceTitle(sourceTitle) ;
			}
		}	
	}
	
	public Individual checkAndReturnSouche(String soucheName) {
		
		Individual souche = null ;
		for (Individual sInd : personnesReferencesMap.getEntities()) {
			if (sInd.getIndividualName().equals(soucheName)) {
				gLog.info("Souche trouvée, id: " + sInd.getId() + " ; Nom: " + soucheName) ;
				if (souche != null) {
					gLog.warning("Problème d'homonymie pour la souche : " + sInd.getId() + " et " + souche.getId()) ;
				} else {
					souche = sInd;
				}
			}
		}
		return souche ;
	}
	
	public void finalizeParsing() {
		linkSourcesAndNotesToAllIndividual();
		linkSourcesAndNotesToAllFamilies() ;
		linkNotesToAllSources() ;
		completeAndCheckSources() ;
	}
	
	private void linkSourcesAndNotesToAllIndividual() {
		
		for (Individual pers : personnesReferencesMap.getEntities()) {
			linkSourcesToIndividual(pers) ;
			linkNotesToIndividual(pers) ;
		}
	}

	private void linkSourcesToIndividual(Individual pers) {
		
		List<GedcomEntityReference<GedcomSource>> sources = pers.getSources() ;
		
		if ((sources != null) && (sources.size() > 0)) {
			for (GedcomEntityReference<GedcomSource> srcRef : sources) {
				GedcomSource source = srcRef.getEntity() ;
				if (source != null) {
					source.addIndividual(pers) ;
				} else {
					gLog.severe("Source réferencée mais non trouvée dans la personne: " + pers.getGedcomSource());
				}			
			}
		} else {
			gLog.warning("Individu sans source : " + pers.getIndividualName()) ;
		}
	}
	
	private void linkNotesToIndividual(Individual pers) {
		
		List<GedcomEntityReference<GedcomNote>> notesRef = pers.getNotes() ;
		
		if ((notesRef != null) && (notesRef.size() > 0)) {
			for (GedcomEntityReference<GedcomNote> noteRef : notesRef) {
				GedcomNote note = noteRef.getEntity() ;
				if (note != null) {
					note.addIndividual(pers) ;
				} else {
					gLog.severe("Note réferencée mais non trouvée dans la personne: " + pers.getGedcomSource());
				}
			}
		} 
	}
	
	private void linkSourcesAndNotesToAllFamilies() {
		
		for (Family fam : famillesReferencesMap.getEntities()) {
			linkSourcesToFamily(fam) ;
			linkNotesToFamily(fam) ;
		}
	}
	
	private void linkSourcesToFamily(Family fam) {
		
		List<GedcomEntityReference<GedcomSource>> sources = fam.getSources() ;
		
		if ((sources != null) && (sources.size() > 0)) {
			for (GedcomEntityReference<GedcomSource> src : sources) {
				GedcomSource source = src.getEntity() ;
				if (source != null) {
					source.addFamily(fam) ;
				} else {
					gLog.severe("Source réferencée mais non trouvée dans la famille: " + fam.getGedcomSource());
				}
			}
		} else if (fam.hasMarriageDateInfo()) {
			gLog.warning("Famille avec information sur la date de mariage mais sans source : " + fam.getGedcomSource()) ;
		}
	}
	
	private void linkNotesToFamily(Family fam) {
		
		List<GedcomEntityReference<GedcomNote>> notes = fam.getNotes() ;
		
		if ((notes != null) && (notes.size() > 0)) {
			for (GedcomEntityReference<GedcomNote> noteRef : notes) {
				GedcomNote note = noteRef.getEntity() ;
				if (note != null) {
					note.addFamily(fam) ;
				} else {
					gLog.severe("Note réferencée mais non trouvée dans la famille: " + fam.getGedcomSource());
				}
			}
		}
	}
	
	private void linkNotesToAllSources() {
		
		for (GedcomSource src : sourcesReferencesMap.getEntities()) {
			linkNotesToSource(src) ;
		}
	}

	private void linkNotesToSource(GedcomSource src) {
		
		List<GedcomEntityReference<GedcomNote>> notes = src.getNotes() ;
		
		if ((notes != null) && (notes.size() > 0)) {
			for (GedcomEntityReference<GedcomNote> noteRef : notes) {
				GedcomNote note = noteRef.getEntity() ;
				if (note != null) {
					note.addSource(src) ;
				} else {
					gLog.severe("Note réferencée mais non trouvée dans la source: " + src.getGedcomSource());
				}
			}
		}
	}
	
	private void completeAndCheckSources() {
		
		for (GedcomSource source : sourcesReferencesMap.getEntities()) {
			source.completeAndCheckSource() ;
		}
	}
	
	public EntityReferencesMap<Individual> getPersonnesMap() {
		return personnesReferencesMap ;
	}

	public Collection<GedcomEntity> getListeEntity() {
		return gArray;
	}

	public RepertoireProfession getRepertoireProfession() {
		return repertoireProfession;
	}
}
