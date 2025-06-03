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

package org.fl.gedcomtools.filtre;

import org.fl.gedcomtools.entity.GedcomNote;

public class GedcomNoteFiltre extends GedcomEntityFiltre {

	public GedcomNoteFiltre(GedcomFiltreCondition fc) {
		super(fc);
	}

	public StringBuilder filtre(GedcomNote note) {
		
		return switch (filtreCondition.getAction(note)) {
			
			case SUPPRESS -> {
				gLog.finest(() -> "Note supprimée: " + note.getGedcomSource()) ;
				yield  new StringBuilder("") ;
			}
			case FILTER -> {
				gLog.finest(() -> "Note filtrée (1ere ligne gardée seulement): " + note.getGedcomSource()) ;
				yield super.anonymisationAdresseMail(note.getGedcomLines().get(0).getOriginalLine()) ;
			}
			case NO_CHANGE -> {
				gLog.finest(() -> "Note non filtrée: " + note.getGedcomSource()) ;
				yield super.filtre(note) ;	
			}
		};
	}
}
