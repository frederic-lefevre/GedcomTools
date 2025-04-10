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

package org.fl.gedcomtools.io;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public abstract class GedcomIO {

	private static final Logger gedcomLog = Logger.getLogger(GedcomIO.class.getName());

	protected Charset gedcomCharset;
	protected URI gedcomURI;
	protected Logger gLog;

	public GedcomIO(URI gcio, String cs) {

		gLog = gedcomLog;
		gedcomURI = gcio;

		// Charset to process gedcom io
		gedcomCharset = null;
		if ((cs != null) && (cs.length() > 0)) {
			if (Charset.isSupported(cs)) {
				gedcomCharset = Charset.forName(cs);
			} else {
				gedcomCharset = Charset.defaultCharset();
				gedcomLog.severe("Unsupported charset: " + cs + ". Default JVM charset assumed: " + gedcomCharset);
			}
		} else {
			gedcomCharset = Charset.defaultCharset();
		}

	}

}
