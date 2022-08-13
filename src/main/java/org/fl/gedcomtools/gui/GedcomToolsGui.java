package org.fl.gedcomtools.gui;

import java.net.URI;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.fl.util.base.AdvancedProperties;
import org.fl.util.base.RunningContext;
import org.fl.util.json.JsonUtils;
import org.fl.util.swing.ApplicationTabbedPane;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GedcomToolsGui extends Application  {

    private static final String DEFAULT_PROP_FILE = "file:///FredericPersonnel/FamilleEnfants/genealogie/sauvegarde/gedcomTools/GedcomTools.properties";

    public static void main(String[] args) {
		Application.launch();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		try {
			// access to properties and logger
			RunningContext gedcomRunningContext = new RunningContext("GedcomProcess", null, new URI(DEFAULT_PROP_FILE));
			AdvancedProperties gedcomProperties = gedcomRunningContext.getProps();
			Logger gedcomLog = gedcomRunningContext.getpLog();
			
			gedcomLog.info("Demarrage du process gedcom") ;
			gedcomLog.fine(() -> JsonUtils.jsonPrettyPrint(gedcomRunningContext.getApplicationInfo(true))) ;
			
			VBox root = new VBox();
			Scene scene = new Scene(root, 1500, 1000);
	
			GedcomPane gedcomPane = new GedcomPane(gedcomProperties, gedcomLog) ;
			
			ApplicationTabbedPane gedcomTabs = new ApplicationTabbedPane(gedcomRunningContext) ;

			gedcomTabs.add(gedcomPane, "Génération Gedcom", 0) ;
			
			gedcomTabs.setSelectedIndex(0) ;
			
			SwingNode swingNode = new SwingNode();
			SwingUtilities.invokeLater(() -> 
				swingNode.setContent(gedcomTabs));
			
			root.getChildren().add(swingNode);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Outils Gedcom");
			primaryStage.setX(50);
			primaryStage.setY(50);
			primaryStage.show();
			
		} catch (Exception e) {
			System.out.println("Exception caught in Main (see default prop file processing)") ;
			e.printStackTrace() ;
		}
		
	}
    
}
