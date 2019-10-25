package app.solver;

import java.util.ArrayList;
import java.util.HashSet;

import app.game.Action;
import app.game.ActionControl;
import app.game.Game;
import app.game.Virus;
import app.knapsack.Item;
import app.knapsack.Solver;

public class Main {
	
//	public static String solve(Game game) {
//		
//		ArrayList<Virus> pathogens = new ArrayList<>();
//		
//		// Detect dangerous pathogens
//		for(Virus pathogen: game.getViruses().values()) {
//			if(pathogen.getLethality().numericRepresenation() > 0)
//				pathogens.add(pathogen);
//		}
//		
//		
//		return createEndRound();
//	}	
	
	public static String solve(Game game) {
		HashSet<Action> actions = ActionControl.generatePossibleActions(game); //Alle möglichen aktionen beschaffen
		HashSet<Action> resultActions = Solver.solve(actions, game.getPoints()); //Die möglichst besten auswählen
		if(resultActions.isEmpty())
			return createEndRound();
		Action nextAction = (Action) resultActions.iterator().next();
		return nextAction.getHttpResponse(); //evt erst die zufälligen nehmen. aber das ist eine geschichte für einen anderen tag :)
	}
	
	public static String createEndRound() {
		return "{\"type\": \"endRound\"}";
	}
	
	public static String createPutUnderQuarantine(String city, int rounds) {
		return String.format("{\"type\": \"putUnderQuarantine\", \"city\":\"%s\", \"rounds\": \"%d\"}", city, rounds);
	}
	
	public static String createCloseAirport(String city, int rounds){
		return String.format("{\"type\": \"closeAirport\", \"city\": \"%s\", \"rounds\": \"%d\"}", city, rounds);
	}
	
	public static String createCloseConnection(String fromCity, String toCity, int rounds) {
		return String.format("{\"type\": \"closeConnection\", \"fromCity\":\"%s\", \"toCity\": \"%s\", \"rounds\": \"%d\"}", fromCity, toCity, rounds);
	}
	
	public static String createDevelopVaccine(String pathogen) {
		return String.format("{\"type\": \"developVaccine\", \"pathogen\":\"%s\"}", pathogen);
	}
	
	public static String createDeployVaccine(String pathogen, String city) {
		return String.format("{\"type\": \"deployVaccine\", \"pathogen\":\"%s\", \"city\": \"%s\"}", pathogen, city);
	}
	
	public static String createDevelopMedication(String pathogen) {
		return String.format("{\"type\": \"developMedication\", \"pathogen\":\"%s\"}", pathogen);
	}
	
	public static String createDeployMedication(String pathogen, String city) {
		return String.format("{\"type\": \"deployMedication\", \"pathogen\":\"%s\", \"city\": \"%s\"}", pathogen, city);
	}
	
	public static String createExertInfluence(String city) {
		return String.format("{\"type\": \"exertInfluence\", \"city\": \"%s\"}", city);
	}
	
	public static String createCallElections(String city) {
		return String.format("{\"type\": \"callElections\", \"city\": \"%s\"}", city);
	}
	
	public static String createApplyHygienicMeasures(String city) {
		return String.format("{\"type\": \"applyHygienicMeasures\", \"city\":\"%s\"}", city);
	}
	
	public static String createLaunchCampaign(String city) {
		return String.format("{\"type\": \"launchCampaign\", \"city\": \"%s\"}", city);
	}
}
