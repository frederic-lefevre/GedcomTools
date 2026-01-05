/*
 * MIT License

Copyright (c) 2017, 2026 Frederic Lefevre

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

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.gedcomtools.gui.GedcomToolsGui;
import org.fl.gedcomtools.line.GedcomTagValue;
import org.fl.util.AdvancedProperties;
import org.fl.util.RunningContext;
import org.fl.util.file.FilesUtils;

public class Config {

	private static final Logger configLogger = Logger.getLogger(Config.class.getName());
	
	private static final String NOW = "now";
	private static final String datePatternParse  = "uuuu-MM-dd";
	private static final DateTimeFormatter dateTimeParser = DateTimeFormatter.ofPattern(datePatternParse,  Locale.FRANCE).withResolverStyle(ResolverStyle.STRICT);
	
	private static final String SOSA_FILE_EXTENTION = ".csv";
	private static final String BRANCHE_FILE_EXTENTION = ".csv";
	private static final String METIERS_FILE_EXTENTION = ".txt";
	
	private static Config instance;
	
	private Path gedcomInputPath;
	private Path gedcomOutputPath;
	private Path arbreSosaOutputPath;
	private Path brancheOutputPath;
	private Path metiersOutputPath;
	private Path genealogyMediaPath;
	
	private Charset inCharset;
	private Charset outCharset;
	private Charset sosaCharset;
	private Charset brancheCharset;
	private Charset metiersCharset;
	
	private String soucheName;
	private String suppressSourceNoteWhenTitleStartsWith;
	
	private boolean anonymisationEmail;
	private boolean keepOnlySourceTitle;
	private boolean keepOnlyOneLineNote;
	
	private String[] contentsToSuppress;
	
	private LocalDate anneeLimite;
	
	private HashSet<GedcomTagValue> tagsToSuppress;
	
	private static Supplier<RunningContext> runningContextSupplier = () -> GedcomToolsGui.getRunningContext();
	
	// For test purpose
	public static void setRunningContextSupplier(Supplier<RunningContext> rcs) {
		runningContextSupplier = rcs;
		instance = null;
	}
	
	private static Config getInstance() {
		if (instance == null) {
			instance = new Config(runningContextSupplier.get());
		}
		return instance;
	}
	
	private Config() {		
	}
	
	private Config(RunningContext runningContext) {
		
		AdvancedProperties gedcomProp = runningContext.getProps();
		
		try {
		
			gedcomInputPath = FilesUtils.uriStringToAbsolutePath(gedcomProp.getProperty("gedcom.input.URI"));
			gedcomOutputPath = FilesUtils.uriStringToAbsolutePath(gedcomProp.getProperty("gedcom.output.URI"));
			
			String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
			String arbreSosaOutputUriString = gedcomProp.getProperty("gedcom.sosa.output.baseURI") + today + SOSA_FILE_EXTENTION;
			String brancheOutputUriString = gedcomProp.getProperty("gedcom.branche.output.baseURI") + today + BRANCHE_FILE_EXTENTION;
			String metiersOutputUriString = gedcomProp.getProperty("gedcom.metiers.output.baseURI") + today + METIERS_FILE_EXTENTION;
			
			arbreSosaOutputPath = FilesUtils.uriStringToAbsolutePath(arbreSosaOutputUriString);
			brancheOutputPath = FilesUtils.uriStringToAbsolutePath(brancheOutputUriString);
			metiersOutputPath = FilesUtils.uriStringToAbsolutePath(metiersOutputUriString);
			
			genealogyMediaPath = FilesUtils.uriStringToAbsolutePath(gedcomProp.getProperty("gedcom.mediaFolder.URI"));
			
			inCharset = getCharset(gedcomProp, "gedcom.in.charset");
			outCharset = getCharset(gedcomProp, "gedcom.out.charset");
			sosaCharset = getCharset(gedcomProp, "gedcom.sosa.out.charset");
			brancheCharset = getCharset(gedcomProp, "gedcom.branche.out.charset");
			metiersCharset = getCharset(gedcomProp, "gedcom.metiers.out.charset");
		
			soucheName = gedcomProp.getProperty("gedcom.souche");
			if ((soucheName == null) || (soucheName.isEmpty())) {
				configLogger.warning("Nom de souche vide ou null");
			}
			
			anonymisationEmail = gedcomProp.getBoolean("gedcom.filtre.anonymisation.email", true);
			keepOnlySourceTitle = gedcomProp.getBoolean("gedcom.filtre.keepOnly.sourceTitle", true);
			keepOnlyOneLineNote = gedcomProp.getBoolean("gedcom.filtre.keepOnly.oneLineNote", true);
			
			contentsToSuppress = gedcomProp.getArrayOfString("gedcom.filtre.suppressContents", ";");
			
			anneeLimite = getAnneeLimite(gedcomProp);
			
			suppressSourceNoteWhenTitleStartsWith = gedcomProp.getProperty("gedcom.filtre.suppress.sourceNote.whenTitleStartsWith", "*");
			
			String[] tagsToSuppressStrings = gedcomProp.getArrayOfString("gedcom.filtre.suppressTags", ";");
			tagsToSuppress = new HashSet<GedcomTagValue>();
			if (tagsToSuppressStrings != null) {
				for (String tag :  tagsToSuppressStrings) {
					try {
						tagsToSuppress.add(GedcomTagValue.valueOf(tag)) ;
					} catch (Exception e) {
						configLogger.log(Level.SEVERE, "Exception parsing gedcom.filtre.suppressTags property for value " + tag, e);
					}
	 			}
			}
			
		} catch (Exception e) {
			configLogger.log(Level.SEVERE, "Exception during application initialization", e);
		}
	}
	
	public static Path getGedcomInputPath() {
		return getInstance().gedcomInputPath;
	}
	
	public static Path getGedcomOutputPath() {
		return getInstance().gedcomOutputPath;
	}
	
	public static Path getArbreSosaOutputPath() {
		return getInstance().arbreSosaOutputPath;
	}
	
	public static Path getBrancheOutputPath() {
		return getInstance().brancheOutputPath;
	}
	
	public static Path getMetiersOutputPath() {
		return getInstance().metiersOutputPath;
	}
	
	public static Charset getInCharset() {
		return getInstance().inCharset;
	}
	
	public static Charset getOutCharset() {
		return getInstance().outCharset;
	}
	
	public static Charset getSosaCharset() {
		return getInstance().sosaCharset;
	}
	
	public static Charset getBrancheCharset() {
		return getInstance().brancheCharset;
	}
	
	public static Charset getMetiersCharset() {
		return getInstance().metiersCharset;
	}
	
	public static String getSoucheName() {
		return getInstance().soucheName;
	}
	
	public static String getSuppressSourceNoteWhenTitleStartsWith() {
		return getInstance().suppressSourceNoteWhenTitleStartsWith;
	}
	
	public static Path getGenealogyMediaPath() {
		return getInstance().genealogyMediaPath;
	}
	
	public static boolean getAnonymisationEmail() {
		return getInstance().anonymisationEmail;
	}
	
	public static boolean getKeepOnlySourceTitle() {
		return getInstance().keepOnlySourceTitle;
	}
	
	public static boolean getKeepOnlyOneLineNote() {
		return getInstance().keepOnlyOneLineNote;
	}
	
	public static String[] getContetToSuppress() {
		return getInstance().contentsToSuppress;
	}
	
	public static LocalDate getAnneeLimite() {
		return getInstance().anneeLimite;
	}
	
	public static Set<GedcomTagValue> getTagsToSuppress() {
		return getInstance().tagsToSuppress;
	}
	
	private Charset getCharset(Properties gedcomProp, String charSetProperty) {

		// Charset to process gedcom io
		String cs = gedcomProp.getProperty(charSetProperty);
		if ((cs != null) && (cs.length() > 0)) {
			if (Charset.isSupported(cs)) {
				return Charset.forName(cs);
			} else {
				configLogger.severe("Unsupported charset: " + cs + " for property " + charSetProperty + ". Default JVM charset assumed: " + Charset.defaultCharset());
				return Charset.defaultCharset();
			}
		} else {
			configLogger.severe("Undefined property charset: " + charSetProperty + ". Default JVM charset assumed: " + Charset.defaultCharset());
			return Charset.defaultCharset();
		}
	}
	
	private LocalDate getAnneeLimite(AdvancedProperties gedcomProp) {
		String dateDepartProp = "gedcom.filtre.anonymisation.dateDepart";
		String dateDepart = gedcomProp.getProperty(dateDepartProp);
		LocalDate localDateStart;
		if ((dateDepart == null) || (dateDepart.isEmpty()) || (dateDepart.equals(NOW))) {
			localDateStart = LocalDate.now();
		} else {
			try {
				localDateStart = LocalDate.parse(dateDepart, dateTimeParser);
			} catch (DateTimeParseException e) {
				configLogger.log(Level.SEVERE, "Exception parsing property " + dateDepartProp + "\nwith value=" + dateDepart, e);
				localDateStart = LocalDate.now();
			}
		}
		return localDateStart.minusYears(gedcomProp.getLong("gedcom.filtre.anonymisation.anneeLimite", 100));
	}
}
