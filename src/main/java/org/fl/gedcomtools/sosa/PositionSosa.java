package org.fl.gedcomtools.sosa;

public class PositionSosa {

	private Sosa so ;
	private long position;

	public PositionSosa(long pos, Sosa s) {
		
		so = s ;
		position = pos ;
		s.addNum(pos) ;
	}

	public Sosa getSo() {
		return so;
	}

	public long getPosition() {
		return position;
	}
}
