package org.fl.gedcomtools.gui;

import java.awt.EventQueue;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ibm.lge.fl.util.AdvancedProperties;
import com.ibm.lge.fl.util.RunningContext;
import com.ibm.lge.fl.util.json.JsonUtils;
import com.ibm.lge.fl.util.swing.ApplicationInfoPane;

public class GedcomToolsGui extends JFrame  {

	private static final long serialVersionUID = 1L;

    private static final String DEFAULT_PROP_FILE = "file:///FredericPersonnel/FamilleEnfants/genealogie/sauvegarde/gedcomTools/GedcomTools.properties";

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GedcomToolsGui window = new GedcomToolsGui(DEFAULT_PROP_FILE) ;
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
    
	private JTabbedPane			gedcomTabs ;	
	private ApplicationInfoPane appInfoPane ;
	
    public GedcomToolsGui(String propertiesUri) {
    	
		try {
			// access to properties and logger
			RunningContext gedcomRunningContext = new RunningContext("GedcomProcess", null, new URI(propertiesUri));
			AdvancedProperties gedcomProperties = gedcomRunningContext.getProps();
			Logger gedcomLog = gedcomRunningContext.getpLog();
			gedcomLog.info("Demarrage du process gedcom") ;
			if (gedcomLog.isLoggable(Level.FINE)) {
				gedcomLog.fine(JsonUtils.jsonPrettyPrint(gedcomRunningContext.getApplicationInfo(true))) ;
			}
						
			setBounds(50, 50, 1500, 1000);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setTitle("Outils Gedcom") ;
			getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));		
			
			GedcomPane gedcomPane = new GedcomPane(gedcomProperties, gedcomLog) ;
			appInfoPane		 	  = new ApplicationInfoPane(gedcomRunningContext) ;
			
			gedcomTabs = new JTabbedPane() ;

			gedcomTabs.addTab("Génération Gedcom", gedcomPane  ) ;
			gedcomTabs.addTab("Informations",      appInfoPane ) ;
			
			gedcomTabs.addChangeListener(new GedcomTabChangeListener());
			
			gedcomTabs.setSelectedIndex(0) ;
			getContentPane().add(gedcomTabs) ;
			
			pack() ;
					
		} catch (Exception e) {
			System.out.println("Exception caught in Main (see default prop file processing)") ;
			e.printStackTrace() ;
		}			
    }
    
	private class GedcomTabChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent arg0) {
			
			if (gedcomTabs.getSelectedComponent().equals(appInfoPane)) {
				appInfoPane.setInfos();
			}			
		}
	}
}
