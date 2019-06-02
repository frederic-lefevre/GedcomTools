package org.fl.gedcomtools.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class EntityReferencesMapTest {

	private final static String id1 	  = "I00832" ;
//	private final static String individu1 =  "0 " + id1 +"INDI" ;
	
	@Test
	void test() {

		EntityReferencesMap<Individual> entityRefMap = new EntityReferencesMap<Individual>() ;
		
		entityRefMap.put(id1, null) ;
		
		boolean b = entityRefMap.containsReferenceToEntity(id1) ;
		assertTrue(b) ;
		
		b = entityRefMap.containsReferenceToEntity("I00833") ;
		assertFalse(b) ;
		
		b = entityRefMap.containsEntity(id1) ;
		assertFalse(b) ;
		
		List<Individual> l = entityRefMap.getEntities() ;
		assertEquals(Arrays.asList(), l) ;
		
		long nb = entityRefMap.getNotNullEntityNumber() ;
		assertEquals(0, nb) ;
	}

}
