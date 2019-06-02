package org.fl.gedcomtools.sosa;

import java.util.ArrayList;

import org.fl.gedcomtools.entity.Individual;

public class Sosa {

	private Individual ind ;

	// Numéros sosa de l'individu (plusieurs si il y a de l'implex) 
	private ArrayList<Long> numerosSosa ;
	
	private boolean isTerminal ;
	
	public Sosa(Individual i) {
		ind = i ;
		isTerminal = false ;
		numerosSosa = new ArrayList<Long>() ;
	}

	public void addNum(long num) {
		numerosSosa.add(new Long(num)) ;
	}
	
	public Individual getInd() {
		return ind;
	}
	
	public void setIsTerminal(boolean t) {
		isTerminal = t ;
	}
	
	public boolean isTerminal() {
		return isTerminal ;
	}
	
	public long getHighestSosaNumber() {
		
		long res = 0 ;
		
		for (Long l : numerosSosa) {
			if (l > res) {
				res = l ;
			}
		}
		
		return res ;
	}

	public ArrayList<Long> getNumerosSosa() {
		return numerosSosa;
	}
	
	public int getImplex() {
		return numerosSosa.size() ;
	}
	
	public String printSosaNumbers() {
		
		StringBuilder res = new StringBuilder() ;
		for (Long l : numerosSosa) {
			res.append(l.toString()).append(" ") ;
		}
		return res.toString() ;
	}
	
	//-----------------------------------------
	// Static methods
	
	// Numéro du sosa descendant
	public static long getEnfantNum(long num) {
		return num/2 ;
	}
	
	// Numéro du sosa pere
	public static long getPereNum(long num) {
		return num*2 ;
	}
	
	// Numéro du sosa mere
	public static long getMereNum(long num) {
		return num*2 + 1 ;
	}
	
	// Numéro du sosa conjoint
	public static long getConjointNum(long num) {
		if (num % 2 == 0) {
			return num + 1 ;
		} else {
			return num - 1 ;
		}
	}
	
	public static boolean estUnHomme(long num) {
		return (num % 2 == 0) ;
	}
	
	// Numéro de la génération correspondant au sosa
	public static int numeroGeneration(long num) {
		
		int res = 1 ;
		while (num > 0) {
			num = num/2 ;
			if (num > 0) {
				res++ ;
			}
		}
		return res ;
	}

}
