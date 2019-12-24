package app.game.actions;

import java.util.HashMap;
import java.util.HashSet;

import app.game.City;
import app.game.Game;
import app.game.Pathogen;

public class ActionHeuristic {
	
	
	private static HashMap<String, Double> constants = null;

	static {
		ActionHeuristic.constants = new HashMap<>();
		
		updateConstants();
	}

	/**
	 * Sets the value of the constants the the CURRENT values in constants.txt
	 */
	public static void updateConstants() {
		HashMap<String, Double> constantsOfFile = ConstantsSetup.getConstants(ConstantsSetup.CONSTANTS_PATH);
		
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

		// Because quarantine only contains a pathogen within one city it is not worth
		// the
		// points to develop a medication
		if (doQuarantine(pathogen)) {
			return false;
		}

		// If a pathogen is not expanding fast, medication should not be developed.
		// Unless it already has infected enough (see the
		// DEV_MEDICATION_PREVALANCE_THRESHOLD)
		if (score <= constants.get("DEV_MEDICATION_THRESHOLD")) {
			double totalPopulation = game.getPopulation();
			double infectedPopulation = game.getCities().values().stream().filter(c -> c.isInfected(pathogen))
					.mapToDouble(c -> c.getPrevalance() * c.getPopulation()).sum();

			double globalPrevalance = infectedPopulation / totalPopulation;
			if (globalPrevalance <= constants.get("DEV_MEDICATION_PREVALENCE_THRESHOLD")) {
				return false;
			}
		}

		// Medication should not be available already or in development
		return true;
	}

	public static int getValue(HashSet<Action> actions) {
		int sum = 0;
		for (Action a : actions)
			sum += getValue(a);
		return sum;
	}

	public static int getValue(Action action) {
		int score = 0;
		City city = action.getCity();
		Game game = action.getGame();
		Pathogen pathogen = action.getPathogen();
		int currentPoints = game.getPoints();
		boolean doRandoms = game.getPathEncounterEvents().stream().allMatch(e -> game.ignorePathogenThisRound(e.getPathogen()));

		if (game != null && game.ignorePathogenThisRound(pathogen)) {

			return score;
		}

		switch (action.getType()) {
		case endRound:
			score += constants.get("END_ROUND_FACTOR"); // EndRound as default action
			break;
		case putUnderQuarantine:

			// Check if city is allready under quarantine.
			if (city.getQuarantine() != null) {
				break;
			}

			// If a very strong pathogen (e.g. Admiral Trips) breaks out in 2 Cities protect
			// the biggest city.
			// This for instance happens in seed 4.
			if (!city.isInfected()) {
				int strongPathogenAmount = (int) game.getOutbreakEvents().stream()
						.filter(e -> doQuarantine(e.getPathogen())).count();

				// If there are more than 2 qurantinable pathogenes we can not quarantine both.
				// Therefore we quarantine the biggest city and hope for the best
				if (strongPathogenAmount > 1) {
					score += constants.get("QUARANTINE_FACTOR") * action.getRounds() * city.getPopulation();
					break;
				}
			}

			// First check if there is only one active pathogen. This is done by the
			// checking if we ignore all but one pathogen.
			// Secondly if this one active pathogen has only contaminated one city so far.
			// If so put it under quarantine as that way it can do the least amount of
			// damage and will die out in this city.
			if (game.getPathEncounterEvents().stream().filter(e -> !game.ignorePathogenThisRound(e.getPathogen()))
					.count() == 1) {

				// In this case we want to enclose a pathogen inside a city. But if the city of
				// this action is
				// not infected we can break at this point
				if (city.getOutbreak() == null) {
					break;
				}

				// Safe the only pathogen as a variable as we will need it later two times.
				Pathogen onlyActivePathogen = game.getPathEncounterEvents().stream()
						.filter(e -> !game.ignorePathogenThisRound(e.getPathogen())).findAny().get().getPathogen();

				// Check that there is only one outbreak event for the onlyActivePathogen and
				// that it is in the city for the current action.
				// If that is the case we want to execute this action.
				if ((game.getOutbreakEvents().stream().filter(e -> e.getPathogen() == onlyActivePathogen).count() == 1)
						&& city.getOutbreak().getPathogen() == onlyActivePathogen) {

					score += constants.get("QUARANTINE_FACTOR") * action.getRounds();
					break;
				}
			}

			// In a regular situation with no outbreak event a city does not need to be
			// quarantined
			if (!city.isInfected())
				break;

			// Check if the pathogen in the city of the action is needs to be quarantined
			if (!doQuarantine(city.getPathogen()))
				break;

			// Check wheather every city is infected and the quarantined pathogen can not
			// spread any further. In this case there is no quarantine required.
			if (game.getCities().values().stream().allMatch((City c) -> c.isInfected())) {
				break;
			}
			// City should be quarantine
			score += constants.get("QUARANTINE_FACTOR") * action.getCost();
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

			// Check if the Virus is still active.
			if (game.getOutbreakEvents().stream().allMatch(e -> e.getPathogen() != pathogen)) {
				break;
			}

			// If 10 % of the world's population is already infected do not develop vaccines
			// as it
			// takes 7 rounds to develop and would be to late at that point.
			if (!doDevVaccine(pathogen, game) && game.getCities().values().stream().filter(c -> !c.isInfected())
					.mapToDouble(c -> c.getPopulation()).sum() <= 0.1 * game.getPopulation()) {
				break;
			}

			// Calculate the global prevalance. If everyone is already
			// infected vaccines are useless
			double totalPopulation = game.getPopulation();
			double infectedPopulation = game.getCities().values().stream().filter(c -> c.isInfected(pathogen))
					.mapToDouble(c -> c.getPrevalance() * c.getPopulation()).sum();

			double globalPrevalance = infectedPopulation / totalPopulation;

			// Only if prevalence is low enough add score
			if (globalPrevalance < constants.get("DEV_VACCINE_PREVALENCE_THRESHOLD")) {
				score += (constants.get("DEV_VACCINE_FACTOR") * pathogen.getLethality().getNumericRepresentation());
			}

			break;

		case deployVaccine:
			// Create a Point buffer. If in a later round a strong Pathogen breaks out we
			// can quarantine it.
			if (game.getPoints() <= constants.get("#STOP_DEPLOYING_VAC")) {
				break;
			}

			// Cities only need to be vaccinated once
			if (city.getVaccineDeployed().stream().anyMatch(e -> e.getPathogen() == pathogen))
				break;

			// TODO: Adjust formula
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

			// Check if the Virus is even still active
			if (game.getOutbreakEvents().stream().allMatch(e -> e.getPathogen() != pathogen)) {
				break;
			}

			// If pathogen is strong enough develop medication
			if (doDevMedication(pathogen, game)) {
				score += (constants.get("DEV_MEDICATION_FACTOR") * pathogen.getLethality().getNumericRepresentation());

			}
			break;

		case deployMedication:

			// Create a Point buffer. If in a later round a strong Pathogen breaks out we
			// can quarantine it.
			if (currentPoints <= constants.get("#STOP_DEPLOYING_MED")) {
				break;
			}

			// TODO: Adjust formula
			score += constants.get("DEP_MEDICATION_FACTOR") * city.getPrevalance() * city.getPopulation()
					* pathogen.getLethality().getNumericRepresentation();

			break;

		case exertInfluence:
			// make sure to always be able to emergency quarantine
			if (currentPoints >= constants.get("#START_RANDOM_EVENTS") && doRandoms)
				score += constants.get("INFLUENCE_FACTOR") * city.getPopulation()
						* (5 - city.getEconomy().getNumericRepresentation());
			break;
		case callElections:
			if (currentPoints >= constants.get("#START_RANDOM_EVENTS")  && doRandoms)
				score += constants.get("ELECTIONS_FACTOR") * city.getPopulation()
						* (5 - city.getGovernment().getNumericRepresentation());
			break;
		case applyHygienicMeasures:
			if (currentPoints >= constants.get("#START_RANDOM_EVENTS")  && doRandoms)
				score += constants.get("HYGIENE_FACTOR") * city.getPopulation()
						* (5 - city.getHygiene().getNumericRepresentation());
			break;
		case launchCampaign:
			if (currentPoints >= constants.get("#START_RANDOM_EVENTS")  && doRandoms)
				score += constants.get("CAMPAIGN_FACTOR") * city.getPopulation()
						* (5 - city.getAwareness().getNumericRepresentation());
			break;
		default:
			break;
		}
		return score;
	}

}
