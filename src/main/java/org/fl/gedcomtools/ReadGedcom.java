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

package org.fl.gedcomtools;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import org.fl.gedcomtools.gui.ProgressInformation;
import org.fl.gedcomtools.gui.ProgressInformationPanel;
import org.fl.gedcomtools.io.GedcomConfigIO;

public class ReadGedcom extends SwingWorker<GedcomGenealogy, ProgressInformation> {

	private static final Logger gedcomLog = Logger.getLogger(ReadGedcom.class.getName());

	// Status
	private static final String LECTURE_GEDCOM = "Lecture du GEDCOM source";
	private static final String FIN_LECTURE = "GEDCOM chargé et vérifié";
	
	// Information prefix
	private static final String ARRET = "Arrêté";
	private static final String EN_EXAMEN = "Nombre d'entités lues: ";
		
	private final ProgressInformationPanel progressInformationPanel;
	
	public ReadGedcom(ProgressInformationPanel progressInformationPanel) {
		this.progressInformationPanel = progressInformationPanel;
	}
	
	@Override
	protected GedcomGenealogy doInBackground() throws Exception {

		try {
			GedcomConfigIO configIO = GedcomConfigIO.getGedcomConfigIO();

			progressInformationPanel.setStepPrefixInformation(EN_EXAMEN);
			progressInformationPanel.setStepInformation("");
			progressInformationPanel.setProcessStatus(LECTURE_GEDCOM);
			
			// Gedcom genealogy read and then write after filter
			GedcomGenealogy gedcomGenealogy = GedcomGenealogy.getNewInstance();
			
			if (gedcomGenealogy.readGedcomGenealogy(configIO.getGenealogyReader())) {
				
				// Do some checkings
				gedcomGenealogy.doCheckingsOnGenealogy();
				
				return gedcomGenealogy;
			} else {
				gedcomLog.severe("Interruption du process gedcom");
				return null;
			}

		} catch (Exception e) {
			gedcomLog.log(Level.SEVERE, "Exception in ProcessGedcom", e);
			return null;
		}
	}
	
    @Override
    public void process(List<ProgressInformation> lp) {

    	ProgressInformation latestResult = lp.get(lp.size() - 1);
    	progressInformationPanel.setStepInformation(latestResult.getInformation());
    }
    
    @Override
    public void done() {
    	progressInformationPanel.setStepInformation("");
    	progressInformationPanel.setStepPrefixInformation(ARRET);
    	progressInformationPanel.setProcessStatus(FIN_LECTURE);
    }
}
