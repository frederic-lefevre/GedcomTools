package org.fl.gedcomtools.sourcetype;

public class NomDeSource {

	protected final static String acteEtatCivil = "Acte de " ;
	protected final static String arbreEnLigne  = "Arbre en ligne de " ;

	protected String titre ;
		
	public NomDeSource(String titreSource) {	
		titre = titreSource ;
	}

	public String getTitre() {
		return titre;
	}
}
