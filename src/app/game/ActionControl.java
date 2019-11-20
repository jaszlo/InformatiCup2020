package app.game;

import java.util.HashMap;
import java.util.HashSet;

import app.events.E_MedicationAvailable;
import app.events.E_Outbreak;
import app.events.E_VaccineAvailable;

public class ActionControl {

	public static HashSet<Action> generatePossibleActions(Game game){ //möglicherweise hier schon filtern?
		HashSet<Action> allActions = new HashSet<Action>();
		//allActions.add(new Action(ActionType.endRound,game,0,Main.createEndRound()));
		addQuarantineActions(game,allActions);
		addCloseConnectionsActions(game, allActions);
		addCloseAirportActions(game, allActions);
		addVaccineDevActions(game, allActions);
		addDeployMedActions(game, allActions);
		addDeployVaccActions(game, allActions);
		addMedDevActions(game, allActions);
		addStatRerollActions(game, allActions);
		return allActions;
	}
	
	private static void addStatRerollActions(Game game, HashSet<Action> actions) {
		for(City city : game.getCities().values()) {
			Action influence = new Action(ActionType.exertInfluence, game, city);
			actions.add(influence);
			
			Action elections = new Action(ActionType.callElections, game, city);
			actions.add(elections);
			
			Action hygiene = new Action(ActionType.applyHygienicMeasures, game, city);
			actions.add(hygiene);
			
			Action campaign = new Action(ActionType.launchCampaign, game, city);
			actions.add(campaign);
		}
	}
	
	private static void addQuarantineActions(Game game, HashSet<Action> actions) {
		int maxRounds = (game.getPoints()-20)/10;
		for(City city : game.getCities().values()) {
			for(int i = 1; i <= maxRounds; i++) {
				Action a = new Action(ActionType.putUnderQuarantine, game, city,i);
				actions.add(a);
			}
		}
	}
	
	
	private static void addDeployVaccActions(Game game, HashSet<Action> actions) {
		for(E_VaccineAvailable e : game.getVaccAvailableEvents()) {
			for(City city : game.getCities().values()) {
				Action a = new Action(ActionType.deployVaccine, game, city, e.getVirus());
				actions.add(a);
			}
		}
	}
	
	private static void addDeployMedActions(Game game, HashSet<Action> actions) {
		HashMap<Virus, E_MedicationAvailable> medAvailable = new HashMap<>();
		
		for (E_MedicationAvailable e : game.getMedAvailableEvents()) {
			medAvailable.put(e.getVirus(), e);
		}
		
		for (E_Outbreak outbreak : game.getOutbreakEvents()) {
			if (medAvailable.get(outbreak.getVirus()) != null) {
				Action a = new Action(ActionType.deployMedication, game, outbreak.getCity(), outbreak.getVirus());
				actions.add(a);
			}
		}
	}
	
	private static void addVaccineDevActions(Game game, HashSet<Action> actions) {
		for(Virus virus : game.getViruses().values()) {
			Action a = new Action(ActionType.developVaccine, game, virus);
			actions.add(a);
		}
	}
	
	private static void addMedDevActions(Game game, HashSet<Action> actions) {
		for(Virus virus : game.getViruses().values()) {
			Action a = new Action(ActionType.developMedication, game, virus);
			actions.add(a);
		}
	}
	
	private static void addCloseAirportActions(Game game, HashSet<Action> actions) {
		int maxRounds = (game.getPoints()-15)/5;
		for(City city : game.getCities().values()) {
			for(int i = 1; i <= maxRounds; i++) {
				Action a = new Action(ActionType.closeAirport, game, city, i);
				actions.add(a);
			}
		}
	}

	private static void addCloseConnectionsActions(Game game, HashSet<Action> actions) {
		int maxRounds = (game.getPoints()-3)/3;
		for(City city : game.getCities().values()) {
			for(City to : city.getConnections()) {
				for(int i = 1; i <= maxRounds; i++) {
					Action a = new Action(game, city, to, i);
					actions.add(a);
				}
			}
		}
	}

}
