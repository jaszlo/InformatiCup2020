package app.solver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import app.game.City;
import app.game.Game;
import app.game.Pathogen;
import app.game.Scale;
import app.game.actions.Action;
import app.game.actions.ActionControl;

/**
 * Evaluator class that can evaluate a given action within the context of the
 * current game state.
 */
public class ActionHeuristic {

	private static Map<String, Double> constants = null;

	static {
		ActionHeuristic.constants = new HashMap<>();

		updateConstants();
	}

	/**
	 * Sets the value of the constants the the CURRENT values in constants.txt
	 */
	public static void updateConstants() {

		Map<String, Double> constantsOfFile = ConstantsSetup.getConstants(ConstantsSetup.CONSTANTS_PATH);

		if (constantsOfFile == null) {
			System.out.println("Konstanten konnten nicht geladen werden.");
			System.exit(1);
		}

		constantsOfFile.forEach((key, value) -> ActionHeuristic.constants.put(key, value));
	}

	/**
	 * Checks the statistics of a given pathogen and returns if it needs to be put
	 * under quarantine.
	 * 
	 * @param pathogen to check for need to be put under quarantine.
	 * @return true or false depending on the pathogen's statistics.
	 */
	private static boolean doQuarantine(Pathogen pathogen) {

		int infectivity = pathogen.getInfectivity().getNumericRepresentation();
		int lethality = pathogen.getLethality().getNumericRepresentation();
		int mobility = pathogen.getMobility().getNumericRepresentation();
		int duration = pathogen.getDuration().getNumericRepresentation();

		// Factors that contribute to a dangerous pathogen killing lots of people in a
		// short amount of time
		// get multiplied. High duration would lead to a long and therefore expensive
		// Quaratine. Therefore we reverse the
		// scale of the duration.
		int score = infectivity * lethality * mobility * (6 - duration);

		return score >= constants.get("QUARANTINE_THRESHOLD");
	}

	/**
	 * Checks the statistics of a given pathogen and returns if vaccines should be
	 * developed.
	 * 
	 * @param pathogen to check for vaccination requirements.
	 * @return true or false depending on the pathogen's statistics.
	 */
	private static boolean doDevVaccine(Pathogen pathogen, Game game) {

		int infectivity = pathogen.getInfectivity().getNumericRepresentation();
		int mobility = pathogen.getMobility().getNumericRepresentation();

		int score = mobility * infectivity;

		if (pathogen.getDuration() == Scale.MM) {
			return false;
		}

		if (doQuarantine(pathogen)) {
			return false;
		}

		// If a pathogen expands fast vaccines should not be developed.
		return score <= constants.get("DEV_VACCINE_THRESHOLD");
	}

	/**
	 * Checks the statistics of a given pathogen and returns if medication should be
	 * developed.
	 * 
	 * @param pathogen to check for medication requirements.
	 * @return true or false depending on the pathogen's statistics.
	 */
	private static boolean doDevMedication(Pathogen pathogen, Game game) {

		int infectivity = pathogen.getInfectivity().getNumericRepresentation();
		int mobility = pathogen.getMobility().getNumericRepresentation();

		int score = mobility * infectivity;

		if (pathogen.getDuration() == Scale.MM) {
			return false;
		}

		// Because quarantine only contains a pathogen within one city it is not worth
		// the
		// points to develop a medication
		if (doQuarantine(pathogen)) {
			return false;
		}

		// If a pathogen is not expanding fast, medication should not be developed.
		return score <= constants.get("DEV_MEDICATION_THRESHOLD"); 
	}

	public static double getScore(Set<Action> actions) {
		return actions.stream().mapToDouble(a -> a.getScore()).sum();
	}

	public static double getScore(Action action) {

		// Get all values that will be required multiple times during the evaluation
		double score = 0;
		City city = action.getCity();
		Game game = action.getGame();
		Pathogen pathogen = action.getPathogen();
		int currentPoints = game.getPoints();

		// A condition that must be met in order to do reroll events.
		boolean doRerolls = game.getPathEncounterEvents().stream()
				.allMatch(e -> game.ignorePathogenThisRound(e.getPathogen()));

		if (game.ignorePathogenThisRound(pathogen)) {
			return score;
		}

		switch (action.getType()) {
		case endRound:

			// EndRound as default action
			score += constants.get("END_ROUND_FACTOR");
			break;

		case putUnderQuarantine:
			/*
			 * If a very strong pathogen for instance Admiral Trips breaks out in two or
			 * more cities protect the biggest city.
			 */
			if (!city.isInfected()) {

				// Count outbreaks that would normaly require quarantine.
				long strongPathogenAmount = game.getOutbreakEvents().stream().filter(e -> doQuarantine(e.getPathogen()))
						.count();

				// If the counter is two or greater put the biggest city under quarantine
				if (strongPathogenAmount >= 2) {
					score += constants.get("QUARANTINE_FACTOR") * action.getRounds() * city.getPopulation();
					break;
				}
			}

			/*
			 * Check if there is only one active one pathogen and if there is only one
			 * outbreak event for this pathogen put it under quarantine enclose a pathogen
			 * inside a city.
			 */
			if (game.getPathEncounterEvents().stream().filter(e -> !game.ignorePathogenThisRound(e.getPathogen()))
					.count() == 1) {

				if (!city.isInfected()) {
					break;
				}

				// Safe the only pathogen as a variable as we will need it later two times.
				Pathogen onlyActivePathogen = game.getPathEncounterEvents().stream()
						.filter(e -> !game.ignorePathogenThisRound(e.getPathogen())).findAny().get().getPathogen();

				// Check that there is only one outbreak event for the only active pathogen and
				// that it is in the city for the current action.
				if ((game.getOutbreakEvents().stream().filter(e -> e.getPathogen() == onlyActivePathogen).count() == 1)
						&& city.isInfected(onlyActivePathogen)) {

					score += constants.get("QUARANTINE_FACTOR") * action.getRounds();
					break;
				}
			}

			// In a regular situation with no outbreak event a city does not require
			// quarantine
			if (!city.isInfected()) {
				break;
			}

			// Check if the pathogen in the city of the action needs to be quarantined
			if (!doQuarantine(city.getPathogen())) {
				break;
			}

			// Check whether every city is infected and the quarantined pathogen can not
			// spread any further. In this case there is no quarantine required.
			if (game.getCities().stream().allMatch((City c) -> c.isInfected())) {
				break;
			}

			// City should be quarantine
			score += constants.get("QUARANTINE_FACTOR") * action.getType().getCosts(action.getRounds());
			break;

		case closeAirport: // Useless action
			break;

		case closeConnection: // Useless action
			break;

		case developVaccine:
			// Check if there is a pathogen that was put under quarantine. If so do not do
			// anything else but repeat that process.
			if (!game.getQuarantineEvents().isEmpty()) {
				break;
			}

			/*
			 * If a pathogen does not qualify for vaccination because it spreads to fast
			 * there is a condition that allows for vaccines to still be usefull against it.
			 * The condition is met when less than 10% of the total population are living in
			 * an uninfected city. In this case we still want to develop vaccines. Here the
			 * opposite of that condition is checked and if not met no vaccines will be
			 * developed.
			 */
			if (!doDevVaccine(pathogen, game) && game.getCities().stream().filter(c -> !c.isInfected())
					.mapToDouble(c -> c.getPopulation()).sum() >= 0.1 * game.getPopulation()) {
				break;
			}

			// Calculate the global prevalance. If everyone is already
			// infected vaccines are not required.
			double totalPopulation = game.getPopulation();
			double infectedPopulation = game.getCities().stream().filter(c -> c.isInfected(pathogen))
					.mapToDouble(c -> c.getPrevalance() * c.getPopulation()).sum();

			double globalPrevalance = infectedPopulation / totalPopulation;

			// Only if prevalence is low enough develop vaccines.
			if (globalPrevalance < constants.get("DEV_VACCINE_PREVALENCE_THRESHOLD")) {
				score += (constants.get("DEV_VACCINE_FACTOR") * pathogen.getLethality().getNumericRepresentation());
			}

			break;

		case deployVaccine:
			// Create a point buffer. If in a later round a strong pathogen is encountered
			// it can be quarantined.
			if (game.getPoints() <= constants.get("#STOP_DEPLOYING_VAC")) {
				break;
			}

			// Calculate the healthy population of the city.
			double healthyPopulation = city.isInfected(pathogen) ? 1 - city.getPrevalance() : 1;
			score += constants.get("DEP_VACCINE_FACTOR") * healthyPopulation * city.getPopulation()
					* pathogen.getLethality().getNumericRepresentation();
			break;

		case developMedication:
			// Check if there is a pathogen that was put under quarantine. If so do not do
			// anything else but repeat that process
			if (!game.getQuarantineEvents().isEmpty()) {
				break;
			}

			// Check if the pathogen qualifies for medication. If it does develop
			// medication.
			if (doDevMedication(pathogen, game)) {
				score += (constants.get("DEV_MEDICATION_FACTOR") * pathogen.getLethality().getNumericRepresentation());
			}
			
			// Unless it already has infected enough (see the
			// DEV_MEDICATION_PREVALANCEvent)
			totalPopulation = game.getPopulation();
			infectedPopulation = game.getCities().stream().filter(c -> c.isInfected(pathogen))
					.mapToDouble(c -> c.getPrevalance() * c.getPopulation()).sum();

			globalPrevalance = infectedPopulation / totalPopulation;
			if (globalPrevalance <= constants.get("DEV_MEDICATION_PREVALENCE_THRESHOLD")) {
				break;
			}
			
			break;

		case deployMedication:
			// Create a point buffer. If in a later round a strong pathogen breaks it can be
			// quarantined.
			if (currentPoints <= constants.get("#STOP_DEPLOYING_MED")) {
				break;
			}

			score += constants.get("DEP_MEDICATION_FACTOR") * city.getPrevalance() * city.getPopulation()
					* pathogen.getLethality().getNumericRepresentation();

			break;

		case exertInfluence:
			// Make sure the current state qualifies for reroll events in general and if a
			// pointer buffer is available.
			if (currentPoints >= constants.get("#START_RANDOM_EVENTS") && doRerolls) {
				score += constants.get("INFLUENCE_FACTOR") * city.getPopulation()
						* (5 - city.getEconomy().getNumericRepresentation());
			}
			break;

		case callElections:
			// Make sure the current state qualifies for reroll events in general and if a
			// pointer buffer is available.
			if (currentPoints >= constants.get("#START_RANDOM_EVENTS") && doRerolls) {
				score += constants.get("ELECTIONS_FACTOR") * city.getPopulation()
						* (5 - city.getGovernment().getNumericRepresentation());
			}
			break;

		case applyHygienicMeasures:
			// Make sure the current state qualifies for reroll events in general and if a
			// pointer buffer is available.
			if (currentPoints >= constants.get("#START_RANDOM_EVENTS") && doRerolls) {
				score += constants.get("HYGIENE_FACTOR") * city.getPopulation()
						* (5 - city.getHygiene().getNumericRepresentation());
			}
			break;

		case launchCampaign:
			// Make sure the current state qualifies for reroll events in general and if a
			// pointer buffer is available.
			if (currentPoints >= constants.get("#START_RANDOM_EVENTS") && doRerolls) {
				score += constants.get("CAMPAIGN_FACTOR") * city.getPopulation()
						* (5 - city.getAwareness().getNumericRepresentation());
			}
			break;

		default:
			break;
		}
		return score;
	}

	/**
	 * Calculates the best possible action found by this heuristic and returns the
	 * action in a string format compatible with the GI client.
	 * 
	 * @param game The game that the best action will be calculated for.
	 * @return The best found action as a string for the GI client.
	 */
	public static String solve(Game game) {

		// Generate all possible action and stream them. Get the score for every action
		// and set it in the action. Afterwards the action with the highest score will
		// be executed.
		return ActionControl.generatePossibleActions(game).parallelStream()
				.filter(a -> a.getType().getCosts(a.getRounds()) <= game.getPoints())
				.max((Action a, Action b) -> a.getScore() == b.getScore() ? 0 : a.getScore() > b.getScore() ? 1 : -1)
				.orElse(new Action(game)).toString();
	}

}
