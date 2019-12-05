package app.game;

/**
 * 
 * Representation of the values --,-,o,+,++
 *
 */
public enum Scale {

	DM,M,N,P,DP;

	/**
	 * 
	 * @param scale The String that is to be parsed
	 * @return The correlating Scale Object. Null if scale is neither -- nor - nor o nor + nor ++
	 */
	public static Scale parse(String scale) {
		if(scale.equals("--"))
			return DM;
		if(scale.equals("-"))
			return M;
		if(scale.equals("o"))
			return N;
		if(scale.equals("+"))
			return P;
		if(scale.equals("++"))
			return DP;
		return null;
	}
	
	/**
	 * 
	 * @return The numerical representation of Scale. Values: '--'=1,'-'=2,'o'=3,'+'=4,'++'=5
	 */
	public int getNumericRepresentation() {
		switch(this) {
			case DM:
				return 1;
			case M:
				return 2;
			case N:
				return 3;
			case P:
				return 4;
			case DP:
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
			case DM:
				return "--";
			case M:
				return "-";
			case N:
				return "o";
			case P:
				return "+";
			case DP:
				return "++";
		}
		return null;
	}
	
}
