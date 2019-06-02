package org.fl.gedcomtools.profession;


import java.util.Collection;
import java.util.HashSet;

import org.fl.gedcomtools.entity.Individual;

public class Profession {

	private String 				name ;
	private HashSet<Individual> membres ;

	public Profession(String n) {
		name 	= n ;
		membres = new HashSet<Individual>() ;
	}

	public String getName() {
		return name;
	}
	
	public int nombresMembres() {
		
		if (membres != null) {
			return membres.size() ;
		} else {
			return 0 ;
		}
	}
	
	public Collection<Individual> getMembres() {
		return membres ;
	}
	
	public void addMembre(Individual i) {
		membres.add(i) ;
	}
}
