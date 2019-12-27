package app.game.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import app.io.FileHandler;

public class ConstantsSetup {
	
	public static final String CONSTANTS_PATH = "resources/constants.txt";
	
	/**
	 * Maps values of constants from a specified file.
	 * Reads from a specified file. If the file does not exist or can't be opened
	 * null is returned. Otherwise each line of the format %string %double results
	 * in an entry in the resulting Map. Empty lines or lines that start with '//' are ignored.
	 * @param resourceFilename the path to file of constants relative to resource folder.
	 * @return a map of constants that were parsed from the file
	 */
	public static HashMap<String,Double> getConstants(String resourceFilename){
		HashMap<String,Double> constants = new HashMap<String,Double>();
		File constFile = FileHandler.getFileFromResources(resourceFilename);
		ArrayList<String> constantsList = FileHandler.readFile(constFile);
		if(constantsList == null)
			return null;
		for(String constant : constantsList) {
			//ignore comments and empty lines 
			if(constant.equals("") || constant.startsWith("//"))
				continue;
			String[] split = constant.split(" ");
			constants.put(split[0], Double.parseDouble(split[1]));
		}
		return constants;
	}
}
