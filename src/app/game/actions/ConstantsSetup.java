package app.game.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import app.io.FileHandler;

public class ConstantsSetup {
	
	public static final String CONSTANTS_PATH = "resources/constants.txt";
	
	
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
