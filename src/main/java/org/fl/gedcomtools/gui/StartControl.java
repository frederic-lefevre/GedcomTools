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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.function.BooleanSupplier;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class StartControl implements ActivableElement {

	private final JPanel procCtrl;
	private final JButton pStart;
	private final BooleanSupplier activationCondition;

	public StartControl(String bText, BooleanSupplier activationCondition) {

		String buttonText = "<html><p>" + bText + "</p></html>";
		
		this.activationCondition = activationCondition;
		
		procCtrl = new JPanel();
		procCtrl.setLayout(new BoxLayout(procCtrl, BoxLayout.X_AXIS));
		procCtrl.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10, Color.BLACK));

		pStart = new JButton(buttonText);

		Font font = new Font("Verdana", Font.BOLD, 14);
		pStart.setFont(font);
		pStart.setBackground(Color.GREEN);
		pStart.setPreferredSize(new Dimension(400, 100));

		procCtrl.add(pStart);
	}

	public JPanel getProcCtrl() {
		return procCtrl;
	}

	public JButton getStartButton() {
		return pStart;
	}

	public boolean isActive() {
		return pStart.isEnabled();
	}

	@Override
	public void deactivate() {
		pStart.setEnabled(false);
	}

	@Override
	public void activate() {
		if (activationCondition.getAsBoolean()) {
			pStart.setBackground(new Color(27, 224, 211));
			pStart.setEnabled(true);
		}
	}

}
