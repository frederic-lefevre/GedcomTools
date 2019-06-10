package org.fl.gedcomtools.sosa;

import java.util.ArrayList;
import java.util.logging.Logger;

public class GenerationSosa {

	private final Logger			gLog ;
	private final int 				numero ;
	private final double			minPosition ;
	private final double			maxPosition ;
	private ArrayList<PositionSosa> generation;
	
	public GenerationSosa(int n, Logger gedcomLog) {
		
		gLog 		= gedcomLog ;
		numero 		= n ;
		minPosition = Math.pow(2,n-1) ;
		maxPosition = Math.pow(2,n)-1 ;
		generation 	= new ArrayList<PositionSosa>() ;
	}

	public void addPosition(PositionSosa ps) {
		
		// vérifier la position
		long p = ps.getPosition() ;
		if (( p < minPosition ) || ( p > maxPosition)) {
			if (numero == 1) {
				generation.add(ps) ;
			} else {
				gLog.severe("Ajout d'un sosa numero " + p + " dans la génération " + numero) ;
			}
		} else {
			if (generation.size() == 0) {
				generation.add(ps) ;
			} else if (p > generation.get(generation.size()-1).getPosition()) {
				generation.add(ps) ;
			} else {
				gLog.severe("Ajout d'un sosa numero inférieur au précédent") ;
			}
		}
	}
	
	public int getNumero() {
		return numero;
	}
	
	public PositionSosa getSosa(int idx) {
		return generation.get(idx) ;
	}
	
	public int getNombrePositionSosa() {
		return generation.size() ;
	}
	
	public ArrayList<PositionSosa> getGeneration() {
		return generation;
	}

	public long getNbMaxIndividual() {
		return (long)minPosition ;
	}
}
