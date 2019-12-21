package app.game.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import app.game.Game;
import app.io.FileHandler;

public class ConstantsSetup {
	private static int wins = 0, games = 0;
	
	private static final int GAMES_TO_REROLL_CONSTANTS = 3;
	
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
	
	public static void registerOutcome(boolean win, Game game) {
		if(win)
			wins++;
		games++;
		double winrate = wins/(double)games;
		System.out.println("GameNR:" + games + " " + game.getOutcome() + " - current winRate = " + ((wins/(double)games)*100) + "%.");
//		if(games >= GAMES_TO_REROLL_CONSTANTS) {
//			HashMap<String,Double> currentConstants = getConstants("resources/constants.txt");
//			HashMap<String,Double> oldConstants = getConstants("resources/oldConstants.txt");
//			if(oldConstants.get("winrate") < winrate) {
//				currentConstants.put("winrate",winrate);
//				FileHandler.writeFile(FileHandler.getFileFromResources("resources/oldConstants.txt"),currentConstants.values());
//	
//			}
//			HashMap<String,Double> newConstants = adjustConstants(currentConstants);
//			FileHandler.writeFile(FileHandler.getFileFromResources("resources/constants.txt"),newConstants.values());
//			ActionHeuristic.updateConstants();
//			games = 0;
//			wins = 0;
//		}
	}
	
	private static HashMap<String,Double> adjustConstants(HashMap<String,Double> currentConstants){
		return currentConstants;
	}
	
}
