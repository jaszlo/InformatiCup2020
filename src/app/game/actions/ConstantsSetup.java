package app.game.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import app.game.Game;
import app.io.FileHandler;

public class ConstantsSetup {
	
	private static final int GAMES_TO_REROLL_CONSTANTS = 100;
	
	private static final double CHANGE_CHANCE = 0.5, MAX_ADDITION = 5, MAX_PERCENTAGE_CHANCE = 1; 
	
	private static int wins = 0, games = 0;
	
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
		if(games >= GAMES_TO_REROLL_CONSTANTS) {
			HashMap<String,Double> currentConstants = getConstants("resources/constants.txt");
			HashMap<String,Double> oldConstants = getConstants("resources/lastConstants.txt");
			boolean improvement = oldConstants.get("winrate") < winrate;
			if(improvement) {
				currentConstants.put("winrate",winrate);
				ArrayList<String> lines = new ArrayList<String>(currentConstants.size());
				for(Entry<String,Double> entry : currentConstants.entrySet())
					lines.add(entry.getKey()+" "+entry.getValue());
				FileHandler.writeFile(FileHandler.getFileFromResources("resources/lastConstants.txt"),lines);
	
			}
			HashMap<String,Double> newConstants = adjustConstants(improvement? currentConstants : oldConstants,CHANGE_CHANCE, MAX_PERCENTAGE_CHANCE, MAX_ADDITION);
			newConstants.remove("winrate");
			ArrayList<String> lines = new ArrayList<String>(newConstants.size());
			for(Entry<String,Double> entry : newConstants.entrySet())
				lines.add(entry.getKey()+" "+entry.getValue());
			FileHandler.writeFile(FileHandler.getFileFromResources("resources/constants.txt"),lines);
			ActionHeuristic.updateConstants();
			games = 0;
			wins = 0;
		}
	}
	
	private static HashMap<String,Double> adjustConstants(HashMap<String,Double> map, double changeChance, double maxPercentageChange, double maxAddition){
		HashMap<String,Double> adjusted = new HashMap<String,Double>();
		boolean changeOccoured = false;
		for(Entry<String,Double> entry : map.entrySet()) {
			if(Math.random() >= changeChance) {
				changeOccoured = true;
				adjusted.put(entry.getKey(), adjustValue(entry.getValue(),maxPercentageChange,maxAddition));
			}else
				adjusted.put(entry.getKey(),entry.getValue());
		}
		if(!changeOccoured) {
			String key = adjusted.keySet().iterator().next();
			adjusted.put(key,adjustValue(adjusted.get(key),maxPercentageChange,maxAddition));
		}
		return adjusted;
	}
	
	private static double adjustValue(double value, double maxPercentageChange, double maxAddition) {
		double factor = Math.random() * maxPercentageChange * (Math.random() >= 0.5? 1:-1);
		double addition = (int) (Math.random()* (maxAddition + 1));
		value += addition;
		value *= 1+factor;
		return value;
	}
	
}
