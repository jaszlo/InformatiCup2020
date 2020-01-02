package app.solver;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.io.FileHandler;
/**
 * Getting constants that are used in the heuristic from an external file.
 */
public class ConstantsSetup {

	public static final String CONSTANTS_PATH = "constants.txt";

	/**
	 * Maps the values of the constants from a specified file. Reads from a specified file.
	 * If the file does not exist or can not be opened, null is returned. Otherwise
	 * each line of the format %string %double results in an entry in the resulting
	 * Map. Empty lines or lines that start with '//' are ignored.
	 * 
	 * @param resourceFilename The path to file of constants relative to resource
	 *                         folder.
	 * @return A map of constants that were parsed from a file.
	 */
	public static Map<String, Double> getConstants(String resourceFilename) {

		Map<String, Double> constants = new HashMap<String, Double>();
		File constFile = FileHandler.getFileFromResources(resourceFilename);
		ArrayList<String> constantsList = FileHandler.readFile(constFile);

		if (constantsList == null) {
			return null;
		}

		for (String constant : constantsList) {

			// Ignore comments and empty lines
			if (constant.equals("") || constant.startsWith("//")) {
				continue;
			}
			
			String[] split = constant.split(" ");
			constants.put(split[0], Double.parseDouble(split[1]));
		}
		
		return constants;
	}
}
