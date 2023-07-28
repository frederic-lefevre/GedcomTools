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

package org.fl.gedcomtools;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.fl.util.RunningContext;

public class Config {

	private static final String DEFAULT_PROP_FILE = "file:///FredericPersonnel/FamilleEnfants/genealogie/sauvegarde/gedcomTools/GedcomTools.properties";
	
	private static RunningContext runningContext;
	private static Logger gedcomLogger;
	private static boolean initialized = false;
	
	private Config() {
	}

	public static void initConfig() {
		
		try {
			
			runningContext = new RunningContext("GedcomProcess", null, new URI(DEFAULT_PROP_FILE));
			
		} catch (URISyntaxException e) {
			System.out.println("Exception caught in Config init (see default prop file processing)");
			e.printStackTrace();
		}
		
		initialized = true;
	}
		
	public static RunningContext getRunningContext() {
		if (!initialized) {
			initConfig();
		}
		return runningContext;
	}
	
	public static Logger getLogger() {
		if (!initialized) {
			initConfig();
		}
		return gedcomLogger;
	}
}
