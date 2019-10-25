package app.game;

public enum Scale {

	DM,M,N,P,DP;

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
	
	
	public int numericRepresenation() {
		switch(this) {
			case DM:
				return -2;
			case M:
				return -1;
			case N:
				return 0;
			case P:
				return 1;
			case DP:
				return 2;
		}
		return 0;
	}
	
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
