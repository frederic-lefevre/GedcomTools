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

	requires FlUtils;
	requires com.google.gson;
	requires java.desktop;
	requires java.logging;
	requires javafx.graphics;
	requires javafx.controls;
	requires javafx.swing;
	requires jdk.jsobject;
}