package app.game.actions;

import java.util.HashMap;

import java.util.HashSet;

import app.game.City;
import app.game.Game;
import app.game.Pathogen;
import app.game.events.E_MedicationAvailable;
import app.game.events.E_Outbreak;
import app.game.events.E_VaccineAvailable;

public class ActionControl {

	public static HashSet<Action> generatePossibleActions(Game game) { // möglicherweise hier schon filtern?
		HashSet<Action> allActions = new HashSet<Action>();
		allActions.add(new Action(game));
		addQuarantineActions(game, allActions);
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
		for (City city : game.getCities().values()) {
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
		int maxRounds = (game.getPoints() - 20) / 10;
		for (City city : game.getCities().values()) {
			for (int i = 1; i <= maxRounds; i++) {
				if(city.getQuarantine() == null) {
					Action a = new Action(ActionType.putUnderQuarantine, game, city, i);
					actions.add(a);
				}
			}
		}
	}

	private static void addDeployVaccActions(Game game, HashSet<Action> actions) {
		for (E_VaccineAvailable e : game.getVaccAvailableEvents()) {
			for (City city : game.getCities().values()) {
				Action a = new Action(ActionType.deployVaccine, game, city, e.getPathogen());
				actions.add(a);
			}
		}
	}

	private static void addDeployMedActions(Game game, HashSet<Action> actions) {
		HashMap<Pathogen, E_MedicationAvailable> medAvailable = new HashMap<>();

		for (E_MedicationAvailable e : game.getMedAvailableEvents()) {
			medAvailable.put(e.getPathogen(), e);
		}

		for (E_Outbreak outbreak : game.getOutbreakEvents()) {
			if (medAvailable.get(outbreak.getPathogen()) != null) {
				Action a = new Action(ActionType.deployMedication, game, outbreak.getCity(), outbreak.getPathogen());
				actions.add(a);
			}
		}
	}

	private static void addVaccineDevActions(Game game, HashSet<Action> actions) {
		for (Pathogen pathogen : game.getPathogens()) {
			Action a = new Action(ActionType.developVaccine, game, pathogen);
			// Only add the DevVacc event if it is not already developed or being developed.
			if (!(game.getVaccAvailableEvents().stream().anyMatch(e -> e.getPathogen() == a.getPathogen())
					|| game.getVaccDevEvents().stream().anyMatch(e -> e.getPathogen() == a.getPathogen()))) {

				actions.add(a);
			}
		}
	}

	private static void addMedDevActions(Game game, HashSet<Action> actions) {
		for (Pathogen pathogen : game.getPathogens()) {
			Action a = new Action(ActionType.developMedication, game, pathogen);
			// Only add the DevMed event if it is not already developed or being developed.
			if (!(game.getMedAvailableEvents().stream().anyMatch(e -> e.getPathogen() == a.getPathogen())
					|| game.getMedDevEvents().stream().anyMatch(e -> e.getPathogen() == a.getPathogen()))) {
				actions.add(a);
			}
		}
	}

	private static void addCloseAirportActions(Game game, HashSet<Action> actions) {
		int maxRounds = (game.getPoints() - 15) / 5;
		for (City city : game.getCities().values()) {
			for (int i = 1; i <= maxRounds; i++) {
				Action a = new Action(ActionType.closeAirport, game, city, i);
				actions.add(a);
			}
		}
	}

	private static void addCloseConnectionsActions(Game game, HashSet<Action> actions) {
		int maxRounds = (game.getPoints() - 3) / 3;
		for (City city : game.getCities().values()) {
			for (City to : city.getConnections()) {
				for (int i = 1; i <= maxRounds; i++) {
					Action a = new Action(game, city, to, i);
					actions.add(a);
				}
			}
		}
	}

}
