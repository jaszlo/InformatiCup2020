package app.game.actions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import app.game.City;
import app.game.Game;
import app.game.Pathogen;

public class ActionHeuristic {
	//setup constants here
	private static HashMap<String,Double> constants = null;
	
	static {
		updateConstants();
	}
	
	/**
	 * Sets the value of the constants the the CURRENT values in constants.txt
	 */
	public static void updateConstants() {
		constants = ConstantsSetup.getConstants("resources/constants.txt");
		if(constants == null) {
			System.out.println("Konstanten konnten nicht geladen werden.");
			System.exit(1);
		}
		System.out.println("Currently loaded constants:");
		for(Map.Entry<String,Double> entry : constants.entrySet())
			System.out.println(entry.getKey()+" "+entry.getValue());
	}

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

		if (game != null && game.ignorePathogenThisRound(pathogen)) {

			return score;
		}

		switch (action.getType()) {
		case endRound:
			score += constants.get("END_ROUND_FACTOR"); // EndRound as default action
			break;
		case putUnderQuarantine:

			// The city does not need to be quarantined if it is already under quarantine
			if (city.getQuarantine() != null) {
				break;
			}
			
			// If a very strong pathogen breaks out in 2 Cities protect the biggest one. For
			// instance seed 4
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

			// Check if only one active pathogen is only in one city in the current game.
			// If so put it under quarantine as that way it can do the least amount of
			// damage
			if (game.getPathEncounterEvents().stream().filter(e -> !game.ignorePathogenThisRound(e.getPathogen()))
					.count() == 1) {

				// In this case we want to enclose a pathogen inside a city. But if said city is
				// not infected we can break at this point
				if (city.getOutbreak() == null) {
					break;
				}

				// Safe the only pathogen as a variable as we will need it later two times.
				Pathogen onlyActivePathogen = game.getPathEncounterEvents().stream()
						.filter(e -> !game.ignorePathogenThisRound(e.getPathogen())).findAny().get().getPathogen();

				// Check that there is only one outbreak event of the onlyActivePathogen and
				// that it is in the city for the current action
				if ((game.getOutbreakEvents().stream().filter(e -> e.getPathogen() == onlyActivePathogen).count() == 1)
						&& city.getOutbreak().getPathogen() == onlyActivePathogen) {

					
					score += constants.get("QUARANTINE_FACTOR") * action.getRounds();
					break;
				}
			}

			// The city does not need to be quarantined without an outbreak
			if (!city.isInfected())
				break;

			// The city does not need to be quarantined if the pathogen is to mild
			if (!doQuarantine(city.getPathogen()))
				break;

			// Check wheather every city is infected and the quarantined pathogen can not
			// spread any further hence no quarantine is required
			if (game.getCities().values().stream().allMatch((City c) -> c.isInfected())) {
				break;
			}
			// City should be quarantine
			score += constants.get("QUARANTINE_FACTOR") * action.getCost();
			break;
		case closeAirport:
			break;
		case closeConnection:
			break;
		case developVaccine:

			// Check if there is a pathogen that was put under quarantine. If so do not do
			// anything else but repeat that process
			if (!game.getQuarantineEvents().isEmpty()) {
				break;
			}

			// Check if the Virus is even still active
			if (game.getOutbreakEvents().stream().allMatch(e -> e.getPathogen() != pathogen)) {
				break;
			}

			// If 10 % of the world are allready infected do not develop vaccines as it
			// takes 7 rounds to develop
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
			if (game.getPoints() <= 25) {
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
			if (game.getPoints() <= 30) {
				break;
			}

			// TODO: Adjust formula
			score += constants.get("DEP_MEDICATION_FACTOR") * city.getPrevalance() * city.getPopulation()
					* pathogen.getLethality().getNumericRepresentation();

			break;
		case exertInfluence:
			//make sure to always be able to emergency quarantine
			if(game.getPoints() > 30)
				score+= constants.get("INFLUENCE_FACTOR") * city.getPopulation() * (5 - city.getEconomy().getNumericRepresentation() );
			break; 
		case callElections:
			if(game.getPoints() > 30)
				score+= constants.get("ELECTIONS_FACTOR") * city.getPopulation() * (5 - city.getGovernment().getNumericRepresentation() );
			break;
		case applyHygienicMeasures:
			if(game.getPoints() > 30)
				score+= constants.get("HYGIENE_FACTOR") * city.getPopulation() * (5 - city.getHygiene().getNumericRepresentation() );
			break;
		case launchCampaign:
			if(game.getPoints() > 30)
				score+=constants.get("CAMPAIGN_FACTOR") * city.getPopulation() * (5- city.getAwareness().getNumericRepresentation() );
			break;
		default:
			break;
		}
		return score;
	}

}
