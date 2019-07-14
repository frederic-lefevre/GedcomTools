package org.fl.gedcomtools.gui;

import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.ibm.lge.fl.util.AdvancedProperties;

public class GedcomPane {

	private final static String START_BUTTON_TEXT  = "Process gedcom" ;
	
	private JPanel 		 gedcommPane ;
	
	public GedcomPane(AdvancedProperties gedcomProperties, Logger gLog) {
		
		gedcommPane = new JPanel() ;
		gedcommPane.setLayout(new BoxLayout(gedcommPane, BoxLayout.Y_AXIS));
		
		StartControl startButton = new StartControl(START_BUTTON_TEXT) ;
		gedcommPane.add(startButton.getProcCtrl()) ;

		StartProcessGedcom startProcessGedCom = new StartProcessGedcom(gedcomProperties, gLog, startButton) ;
		startButton.getStartButton().addActionListener(startProcessGedCom) ;
	}

	public JPanel getGedcommPane() { return gedcommPane; }
}
