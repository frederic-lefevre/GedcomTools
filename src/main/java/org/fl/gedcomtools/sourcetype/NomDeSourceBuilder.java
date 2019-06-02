package org.fl.gedcomtools.sourcetype;

import java.util.logging.Logger;

public class NomDeSourceBuilder {

	public static NomDeSource getNomDeSource(String titreSource, Logger nLog) {
		
		if (titreSource.startsWith(NomDeSource.acteEtatCivil)) {
			return new ActeEtatCivil(titreSource, nLog) ;
		} else if (titreSource.startsWith(NomDeSource.arbreEnLigne)) {
			return new ArbreEnLigne(titreSource) ;
		} else {
			return new NomDeSource(titreSource) ;
		}
		
	}
	
}
