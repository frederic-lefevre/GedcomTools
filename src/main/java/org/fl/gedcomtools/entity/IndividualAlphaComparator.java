package org.fl.gedcomtools.entity;

import java.util.Comparator;

public class IndividualAlphaComparator   implements Comparator<Individual> {

	public int compare(Individual arg0, Individual arg1) {
		return arg0.getIndividualName().compareToIgnoreCase(arg1.getIndividualName()) ;
	}

}
