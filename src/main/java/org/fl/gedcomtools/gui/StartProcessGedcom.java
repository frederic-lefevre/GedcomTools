package org.fl.gedcomtools.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import org.fl.gedcomtools.ProcessGedcom;

import com.ibm.lge.fl.util.AdvancedProperties;

public class StartProcessGedcom implements ActionListener {

	private Logger gedcomLog ;
	private StartControl  startCtrl;
	private AdvancedProperties gedcomProperties ;
	
	public StartProcessGedcom(AdvancedProperties gp, Logger gl, StartControl sc) {
		super();
		gedcomLog 		 = gl;
		startCtrl 		 = sc;
		gedcomProperties = gp ;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		startCtrl.setTriggered(true) ;
		startCtrl.getStartButton().setBackground(new Color(27,224,211)) ;
		
		ProcessGedcom.process(gedcomProperties, gedcomLog);
	}

}
