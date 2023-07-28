/*
 * MIT License

Copyright (c) 2017, 2023 Frederic Lefevre

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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import org.fl.gedcomtools.ProcessGedcom;
import org.fl.util.AdvancedProperties;

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
		
		// TODO Test return. Display result
		ProcessGedcom.process(gedcomProperties);
	}

}
