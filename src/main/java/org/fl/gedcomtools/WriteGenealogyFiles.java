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

import javax.swing.SwingWorker;

import org.fl.gedcomtools.gui.ActionJournalTableModel;
import org.fl.gedcomtools.gui.ProgressInformation;
import org.fl.gedcomtools.gui.ProgressInformationPanel;
import org.fl.gedcomtools.io.GedcomConfigIO;

public class WriteGenealogyFiles extends SwingWorker<String, ProgressInformation> {

	// Status
	private final static String GENERATION = "En cours de génération";
	// Information prefix
	private final static String ARRET = "Arreté";
	private final static String FIN_GENERATION = "Fichiers de la généalogie générés";
	
	private final ProgressInformationPanel progressInformationPanel;
	private final ActionJournalTableModel actionJournalTableModel;
	
	public WriteGenealogyFiles(ProgressInformationPanel progressInformationPanel, ActionJournalTableModel actionJournalTableModel) {
		this.progressInformationPanel = progressInformationPanel;
		this.actionJournalTableModel = actionJournalTableModel;
	}

	@Override
	protected String doInBackground() throws Exception {

		progressInformationPanel.setProcessStatus(GENERATION);
		
		GedcomConfigIO configIO = GedcomConfigIO.getGedcomConfigIO();
		
		// Gedcom genealogy filter and write
		GedcomGenealogy gedcomGenealogy = GedcomGenealogy.getInstance();
		
		gedcomGenealogy.writeGedcomGenealogy(configIO.getGenealogyWriter());
		gedcomGenealogy.writeArbreSosa(configIO.getArbreSosaWriter());
		gedcomGenealogy.writeBranchesDescendantes(configIO.getBranchesWriter());
		gedcomGenealogy.writeRepertoireProfession(configIO.getMetiersWriter());
		
		return null;
	}
	
	@Override
	public void done() {

		progressInformationPanel.setStepInformation("");
		progressInformationPanel.setStepPrefixInformation(ARRET);
		progressInformationPanel.setProcessStatus(FIN_GENERATION);
		actionJournalTableModel.fireTableDataChanged();
	}
}
