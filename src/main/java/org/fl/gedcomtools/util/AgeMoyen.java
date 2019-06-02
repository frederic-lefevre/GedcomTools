package org.fl.gedcomtools.util;


// Age moyen d'un groupe d'individu
public class AgeMoyen {

	// age moyen en nombre de jours
	private long ageMoyenEnJour ;
	
	// nombre d'individu
	private int poids ;
	
	public AgeMoyen() {
		ageMoyenEnJour = 0 ;
		poids 		   = 0 ;
	}
	
	public AgeMoyen(AgeMoyen a, AgeMoyen b) {
		poids = a.getPoids() + b.getPoids() ;
		if (poids == 0) {
			ageMoyenEnJour = 0 ;
		} else {
			ageMoyenEnJour = (a.getAge()*a.getPoids() + b.getAge()*b.getPoids())/poids ;
		}
		
	}

	public long getAge() {
		return ageMoyenEnJour;
	}

	public int getPoids() {
		return poids;
	}

	public void addAgeEnJour(long ageInd) {
		if (ageInd != 0) {
			poids = poids + 1 ;
			ageMoyenEnJour = (ageMoyenEnJour*(poids - 1) + ageInd)/poids ; 
		}
	}
	
	public void addAgeMoyen(AgeMoyen b) {
		int poidsTotal = poids + b.getPoids() ;
		if (poidsTotal == 0) {
			ageMoyenEnJour = 0 ;
		} else {
			ageMoyenEnJour = (ageMoyenEnJour*poids + b.getAge()*b.getPoids())/poidsTotal ;
		}
		poids = poidsTotal ;
	}
	
	public String printAge() {
		long annees 	= (long)Math.floor(ageMoyenEnJour/365.25) ;
		long resteSurAn = (long)(ageMoyenEnJour%365.25) ;
		long mois 		= (long)(resteSurAn/30.4) ;
		long jours 		= (long)(resteSurAn%30.4) ;
		
		return annees + " années " + mois + " mois " + jours + " jours sur " + poids + " individus";
	}
}
