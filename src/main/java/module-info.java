module org.fl.gedcomtools {
	exports org.fl.gedcomtools.profession;
	exports org.fl.gedcomtools.gui;
	exports org.fl.gedcomtools.util;
	exports org.fl.gedcomtools.line;
	exports org.fl.gedcomtools.entity;
	exports org.fl.gedcomtools.filtre;
	exports org.fl.gedcomtools.io;
	exports org.fl.gedcomtools.sosa;
	exports org.fl.gedcomtools.sourcetype;
	exports org.fl.gedcomtools;

	requires transitive org.fl.util;
	requires com.google.gson;
	requires transitive java.desktop;
	requires transitive java.logging;
	requires transitive javafx.graphics;
	requires transitive javafx.controls;
	requires javafx.swing;
	requires jdk.jsobject;
}