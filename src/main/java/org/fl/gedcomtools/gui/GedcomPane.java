package org.fl.gedcomtools.gui;

import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.fl.util.base.AdvancedProperties;

public class GedcomPane extends JPanel {

	private static final long serialVersionUID = 1L;
	private final static String START_BUTTON_TEXT  = "Process gedcom" ;
	
	public GedcomPane(AdvancedProperties gedcomProperties, Logger gLog) {
		
		super() ;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		StartControl startButton = new StartControl(START_BUTTON_TEXT) ;
		add(startButton.getProcCtrl()) ;

		StartProcessGedcom startProcessGedCom = new StartProcessGedcom(gedcomProperties, gLog, startButton) ;
		startButton.getStartButton().addActionListener(startProcessGedCom) ;
	}

}
