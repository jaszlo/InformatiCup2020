package app.game.actions;

import java.util.HashSet;
import java.util.Set;

import app.game.City;
import app.game.Game;
import app.game.Pathogen;
import app.game.events.E_MedicationAvailable;
import app.game.events.E_Outbreak;
import app.game.events.E_VaccineAvailable;

public class ActionControl {

	/**
	 * Generates all possible, legal actions for the given game.
	 * 
	 * @param game Game that the actions are generated for.
	 * @return All actions.
	 */
	public static Set<Action> generatePossibleActions(Game game) {
		Set<Action> allActions = new HashSet<Action>();
		allActions.add(new Action(game));
		addQuarantineActions(game, allActions);
		addCloseConnectionsActions(game, allActions);
		addCloseAirportActions(game, allActions);
		addVaccineDevActions(game, allActions);
		addDeployMedActions(game, allActions);
		addDeployVaccActions(game, allActions);
		addMedDevActions(game, allActions);
		addRerollActions(game, allActions);
		return allActions;
	}

	/**
	 * Adds all legal reroll actions to the given actions set.
	 * 
	 * @param game    Game that the actions are generated for.
	 * @param actions Actions set to add to.
	 */
	private static void addRerollActions(Game game, Set<Action> actions) {
		for (City city : game.getCities().values()) {
			if (game.getPoints() >= ActionType.exertInfluence.getCosts(1)) {
				Action influence = new Action(ActionType.exertInfluence, game, city);
				actions.add(influence);
			}

			if (game.getPoints() >= ActionType.callElections.getCosts(1)) {
				Action elections = new Action(ActionType.callElections, game, city);
				actions.add(elections);
			}

			if (game.getPoints() >= ActionType.applyHygienicMeasures.getCosts(1)) {
				Action hygiene = new Action(ActionType.applyHygienicMeasures, game, city);
				actions.add(hygiene);
			}

			if (game.getPoints() >= ActionType.launchCampaign.getCosts(1)) {
				Action campaign = new Action(ActionType.launchCampaign, game, city);
				actions.add(campaign);
			}
		}
	}

	/**
	 * Adds all legal quarantine actions to the given actions set.
	 * 
	 * @param game    Game that the actions are generated for.
	 * @param actions Actions set to add to.
	 */
	private static void addQuarantineActions(Game game, Set<Action> actions) {
		for (City city : game.getCities().values()) {
			if (city.getQuarantine() == null) {
				for (int rounds = 1; game.getPoints() >= ActionType.putUnderQuarantine.getCosts(rounds); rounds++) {
					Action a = new Action(ActionType.putUnderQuarantine, game, city, rounds);
					actions.add(a);
				}
			}
		}
	}

	/**
	 * Adds all legal deploy vaccine actions to the given actions set.
	 * 
	 * @param game    Game that the actions are generated for.
	 * @param actions Actions set to add to.
	 */
	private static void addDeployVaccActions(Game game, Set<Action> actions) {
		for (E_VaccineAvailable e : game.getVaccAvailableEvents()) {
			for (City city : game.getCities().values()) {
				// Filter out cities that have been vaccinated already
				if (city.getVaccineDeployed().stream().allMatch(vd -> vd.getPathogen() != e.getPathogen())) {
					Action a = new Action(ActionType.deployVaccine, game, city, e.getPathogen());
					actions.add(a);
				}
			}
		}
	}

	/**
	 * Adds all legal deploy medication actions to the given actions set.
	 * 
	 * @param game    Game that the actions are generated for.
	 * @param actions Actions set to add to.
	 */
	private static void addDeployMedActions(Game game, Set<Action> actions) {
		Set<Pathogen> medAvailable = new HashSet<>();

		for (E_MedicationAvailable e : game.getMedAvailableEvents()) {
			medAvailable.add(e.getPathogen());
		}

		for (E_Outbreak outbreak : game.getOutbreakEvents()) {
			if (medAvailable.contains(outbreak.getPathogen())) {
				Action a = new Action(ActionType.deployMedication, game, outbreak.getCity(), outbreak.getPathogen());
				actions.add(a);
			}
		}
	}

	/**
	 * Adds all legal develop vaccine actions to the given actions set.
	 * 
	 * @param game    Game that the actions are generated for.
	 * @param actions Actions set to add to.
	 */
	private static void addVaccineDevActions(Game game, Set<Action> actions) {
		for (Pathogen pathogen : game.getPathogens()) {
			Action a = new Action(ActionType.developVaccine, game, pathogen);
			// Only add the DevVacc event if it is not already developed or being developed.
			if (game.getVaccAvailableEvents().stream().allMatch(e -> e.getPathogen() != a.getPathogen())
					&& game.getVaccDevEvents().stream().allMatch(e -> e.getPathogen() != a.getPathogen())) {

				actions.add(a);
			}
		}
	}

	/**
	 * Adds all legal develop medication actions to the given actions set.
	 * 
	 * @param game    Game that the actions are generated for.
	 * @param actions Actions set to add to.
	 */
	private static void addMedDevActions(Game game, Set<Action> actions) {
		for (Pathogen pathogen : game.getPathogens()) {
			Action a = new Action(ActionType.developMedication, game, pathogen);
			// Only add the DevMed event if it is not already developed or being developed.
			if (game.getMedAvailableEvents().stream().allMatch(e -> e.getPathogen() != a.getPathogen())
					&& game.getMedDevEvents().stream().allMatch(e -> e.getPathogen() != a.getPathogen())) {
				actions.add(a);
			}
		}
	}

	/**
	 * Adds all legal close airport actions to the given actions set.
	 * 
	 * @param game    Game that the actions are generated for.
	 * @param actions Actions set to add to.
	 */
	private static void addCloseAirportActions(Game game, Set<Action> actions) {
		for (City city : game.getCities().values()) {
			if (city.getAirportClosed() == null) {
				for (int rounds = 1; game.getPoints() >= ActionType.closeAirport.getCosts(rounds); rounds++) {
					Action a = new Action(ActionType.closeAirport, game, city, rounds);
					actions.add(a);
				}
			}
		}
	}

	/**
	 * Adds all legal close connection actions to the given actions set.
	 * 
	 * @param game    Game that the actions are generated for.
	 * @param actions Actions set to add to.
	 */
	private static void addCloseConnectionsActions(Game game, Set<Action> actions) {
		for (City city : game.getCities().values()) {
			for (City to : city.getConnections()) {
				if (city.getConnectionClosed().stream().allMatch(e -> e.getTo() != city)) {
					for (int rounds = 1; game.getPoints() >= ActionType.closeConnection.getCosts(rounds); rounds++) {
						Action a = new Action(game, city, to, rounds);
						actions.add(a);
					}
				}
			}
		}
	}
}
