package app.game;

import java.util.HashSet;

import app.solver.Main;

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
			Action influence = new Action(ActionType.exertInfluence, game, 3,
					Main.createExertInfluence(city.getName()), city.getName());
			actions.add(influence);
			
			Action elections = new Action(ActionType.callElections, game, 3,
					Main.createCallElections(city.getName()), city.getName());
			actions.add(elections);
			
			Action hygiene = new Action(ActionType.applyHygienicMeasures, game, 3,
					Main.createApplyHygienicMeasures(city.getName()), city.getName());
			actions.add(hygiene);
			
			Action campaign = new Action(ActionType.launchCampaign, game, 3,
					Main.createLaunchCampaign(city.getName()), city.getName());
			actions.add(campaign);
		}
	}
	
	private static void addQuarantineActions(Game game, HashSet<Action> actions) {
		int maxRounds = (game.getPoints()-20)/10;
		for(City city : game.getCities().values()) {
			for(int i = 1; i <= maxRounds; i++) {
				Action a = new Action(ActionType.putUnderQuarantine, game,20+i*10,
						Main.createPutUnderQuarantine(city.getName(), i), city.getName(),i);
				actions.add(a);
			}
		}
	}
	
	
	private static void addDeployVaccActions(Game game, HashSet<Action> actions) {
		for(E_VaccineAvailable e : game.getVaccAvailableEvents()) {
			for(City city : game.getCities().values()) {
					Action a = new Action(ActionType.deployVaccine, game,5,
							Main.createDeployVaccine(e.getVirus().getName(), city.getName()), e.getVirus().getName(),city.getName());
					actions.add(a);
			}
		}
	}
	
	private static void addDeployMedActions(Game game, HashSet<Action> actions) {
		for(E_MedicationAvailable e : game.getMedAvailableEvents()) {
			for(City city : game.getCities().values()) {
					Action a = new Action(ActionType.deployMedication, game,10,
							Main.createDeployMedication(e.getVirus().getName(), city.getName()), e.getVirus().getName(),city.getName());
					actions.add(a);
			}
		}
	}
	
	private static void addVaccineDevActions(Game game, HashSet<Action> actions) {
		for(Virus virus : game.getViruses().values()) {
			Action a = new Action(ActionType.developVaccine, game, 40,
					Main.createDevelopVaccine(virus.getName()),virus.getName());
			actions.add(a);
		}
	}
	
	private static void addMedDevActions(Game game, HashSet<Action> actions) {
		for(Virus virus : game.getViruses().values()) {
			Action a = new Action(ActionType.developMedication, game, 20,
					Main.createDevelopMedication(virus.getName()),virus.getName());
			actions.add(a);
		}
	}
	
	private static void addCloseAirportActions(Game game, HashSet<Action> actions) {
		int maxRounds = (game.getPoints()-15)/5;
		for(City city : game.getCities().values()) {
			for(int i = 1; i <= maxRounds; i++) {
				Action a = new Action(ActionType.closeAirport, game,15+i*5,
						Main.createCloseAirport(city.getName(), i), city.getName(),i);
				actions.add(a);
			}
		}
	}

	private static void addCloseConnectionsActions(Game game, HashSet<Action> actions) {
		int maxRounds = (game.getPoints()-3)/3;
		for(City city : game.getCities().values()) {
			for(City to : city.getConnections()) {
				for(int i = 1; i <= maxRounds; i++) {
					Action a = new Action(ActionType.closeConnection, game,3+i*3,
					Main.createCloseConnection(city.getName(), to.getName(), i), city.getName(),to.getName(),i);
					actions.add(a);
				}
			}
		}
	}

}
