/*
 * MIT License

Copyright (c) 2017, 2025 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.gedcomtools.gui;

import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import org.fl.gedcomtools.Config;
import org.fl.util.AdvancedProperties;
import org.fl.util.RunningContext;
import org.fl.util.json.JsonUtils;
import org.fl.util.swing.ApplicationTabbedPane;

import com.fasterxml.jackson.core.JsonProcessingException;

public class GedcomToolsGui extends JFrame  {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GedcomToolsGui window = new GedcomToolsGui() ;
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
    
    public GedcomToolsGui() {
    	
		try {
			// access to properties and logger
			Config.initConfig(Config.DEFAULT_PROP_FILE);
			RunningContext gedcomRunningContext = Config.getRunningContext();
			AdvancedProperties gedcomProperties = gedcomRunningContext.getProps();
			Logger gedcomLog = Config.getLogger();
			gedcomLog.info("Demarrage du process gedcom") ;
			gedcomLog.fine(() -> {
				try {
					return JsonUtils.jsonPrettyPrint(gedcomRunningContext.getApplicationInfo(true));
				} catch (JsonProcessingException e) {
					gedcomLog.log(Level.SEVERE, "Exception logging application info", e);
					return "Exception logging application info" + e.getMessage();
				}
			}) ;
						
			setBounds(50, 50, 1500, 1000);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setTitle("Outils Gedcom") ;
			getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));		
			
			GedcomPane gedcomPane = new GedcomPane(gedcomProperties) ;
			
			ApplicationTabbedPane gedcomTabs = new ApplicationTabbedPane(gedcomRunningContext) ;

			gedcomTabs.add(gedcomPane, "Génération Gedcom", 0) ;
			
			gedcomTabs.setSelectedIndex(0) ;
			getContentPane().add(gedcomTabs) ;
			
		} catch (Exception e) {
			System.out.println("Exception caught in Main (see default prop file processing)") ;
			e.printStackTrace() ;
		}			
    }
    
}
