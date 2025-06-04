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

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ActionJournalPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final ActionJournalTableModel actionJournalTableModel;
	
	public ActionJournalPanel(ActionJournal actionJournal) {
		super();
		
		actionJournalTableModel = new ActionJournalTableModel(actionJournal);
		JTable actionJournalTable = new JTable(actionJournalTableModel);
		JScrollPane actionJournalScrollPane = new JScrollPane(actionJournalTable);
		actionJournalScrollPane.setPreferredSize(new Dimension(GedcomToolsGui.WINDOW_WIDTH - 100, GedcomToolsGui.WINDOW_HEIGHT - 300));
		add(actionJournalScrollPane);
	}

	public ActionJournalTableModel getActionJournalTableModel() {
		return actionJournalTableModel;
	}
}
