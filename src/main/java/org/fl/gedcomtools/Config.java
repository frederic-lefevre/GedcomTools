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

import java.net.URI;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.gedcomtools.gui.GedcomToolsGui;
import org.fl.util.AdvancedProperties;
import org.fl.util.RunningContext;
import org.fl.util.file.FilesUtils;

public class Config {

	private static final Logger configLogger = Logger.getLogger(Config.class.getName());
	
	private static Config instance = null;
	
	private static RunningContext runningContext;
	
	private final Path gedcomInputPath;
	
	private Config(String propertyFile) {
		
		try {

			runningContext = new RunningContext("org.fl.gedcomtools", URI.create(propertyFile));	
		
			AdvancedProperties gedcomProp = runningContext.getProps();
		
			gedcomInputPath = FilesUtils.uriStringToAbsolutePath(gedcomProp.getProperty("gedcom.input.URI"));
			
		} catch (Exception e) {
			String message = "Exception during application initialization";
			configLogger.log(Level.SEVERE, message, e);
			throw new IllegalArgumentException(message, e);
		}
	}
		
	public static void initConfig(String propertyFile) {
		instance = new Config(propertyFile);
	}
	
	public static void initConfig() {
		initConfig(GedcomToolsGui.getPropertyFile());
	}
	
	public static RunningContext getRunningContext() {
		if (instance == null) {
			instance = new Config(GedcomToolsGui.getPropertyFile());
		}
		return runningContext;
	}
	
	public static AdvancedProperties getProperties() {
		if (instance == null) {
			instance = new Config(GedcomToolsGui.getPropertyFile());
		}
		return runningContext.getProps();
	}
	
	public static Path getGedcomInputPath() {
		if (instance == null) {
			instance = new Config(GedcomToolsGui.getPropertyFile());
		}
		return instance.gedcomInputPath;
	}
}
