package app.game;

/**
 * Representation of the values --, -, o, +, ++
 */
public enum Scale {

	MM,M,N,P,PP;

	/**
	 * 
	 * @param scale The String that is to be parsed
	 * @return The correlating Scale Object. Null if scale is neither -- nor - nor o nor + nor ++
	 */
	public static Scale parse(String scale) {
		if(scale.equals("--"))
			return MM;
		if(scale.equals("-"))
			return M;
		if(scale.equals("o"))
			return N;
		if(scale.equals("+"))
			return P;
		if(scale.equals("++"))
			return PP;
		return null;
	}
	
	/**
	 * 
	 * @return The numerical representation of Scale. Values: '--'=1,'-'=2,'o'=3,'+'=4,'++'=5
	 */
	public int getNumericRepresentation() {
		switch(this) {
			case MM:
				return 1;
			case M:
				return 2;
			case N:
				return 3;
			case P:
				return 4;
			case PP:
				return 5;
		}
		return 0;
	}
	
	/**
	 * @return The Scale as String. parse(toString()).equals(this) should always be true.
	 */
	@Override
	public String toString() {
		switch(this) {
			case MM:
				return "--";
			case M:
				return "-";
			case N:
				return "o";
			case P:
				return "+";
			case PP:
				return "++";
		}
		return null;
	}
	
}
