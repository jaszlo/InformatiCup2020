package app.game;

/**
 * Representation of the values --, -, o, +, ++
 */
public enum Scale {

	/**
	 * Representation of --
	 */
	MM,

	/**
	 * Representation of -
	 */
	M,

	/**
	 * Representation of o
	 */
	N,

	/**
	 * Representation of +
	 */
	P,

	/**
	 * Representation of ++
	 */
	PP;

	/**
	 * Parses a given string to the correlating scale.
	 * 
	 * @param scale String that will be parsed.
	 * @return The correlating scale object. If scale is neither -- nor - nor o nor
	 *         + nor ++, null is returned.
	 */
	public static Scale parse(String scale) {
		if (scale.equals("--")) {
			return MM;

		} else if (scale.equals("-")) {
			return M;

		} else if (scale.equals("o")) {
			return N;

		} else if (scale.equals("+")) {
			return P;

		} else if (scale.equals("++")) {
			return PP;

		} else {
			return null;
		}
	}

	/**
	 * Get the numerical representation of the scale. The Values are '--' = 1, '-' =
	 * 2, 'o' = 3, '+' = 4, '++' = 5.
	 * 
	 * @return The numerical representation of scale.
	 */
	public int getValue() {
		switch (this) {
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
	 * @return The scale as a string. parse(this.toString()).equals(this) should always
	 *         be true.
	 */
	@Override
	public String toString() {
		switch (this) {
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
