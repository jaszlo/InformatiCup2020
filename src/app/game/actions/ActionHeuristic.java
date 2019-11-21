package app.game.actions;

import java.util.Arrays;
import java.util.HashSet;

import app.game.City;
import app.game.Game;
import app.game.Virus;

public class ActionHeuristic {
	// FACTOR: The factor by which the score is scaled.
	// THRESHOLD: The minimum of points an action need to reach in order to be
	// executed.

	static final int END_ROUND_FACTOR = 0;
	static final int END_ROUND_THRESHOLD = 0;

	static final int QUARANTINE_FACTOR = 100;
	static final int QUARANTINE_THRESHOLD = 350;

	static final int DEV_VACCINE_FACTOR = 1;
	static final int DEV_VACCINE_THRESHOLD = 9;
	// Only develop vaccine if global prevlance is lower than this threshold value.
	static final double DEV_VACCINE_PREVALANCE_THRESHOLD = 1.0 / 3.0;

	// Medication over vaccination therefore multiply the factor by 3.
	static final int DEV_MEDICATION_FACTOR = DEV_VACCINE_FACTOR * 3;
	static final int DEV_MEDICATION_THRESHOLD = DEV_VACCINE_THRESHOLD;

	static final int DEP_VACCINE_FACTOR = 50;
	static final int DEP_VACCINE_THRESHOLD = 40;

	static final int DEP_MEDICATION_FACTOR = DEV_VACCINE_FACTOR;
	static final int DEP_MEDICATION_THRESHOLD = DEV_VACCINE_THRESHOLD;

	private static boolean doQuarantine(Virus virus) {
		int infectivity = virus.getInfectivity().numericRepresenation();
		int lethality = virus.getLethality().numericRepresenation();
		int mobility = virus.getMobility().numericRepresenation();
		int duration = virus.getDuration().numericRepresenation();

		// Factors that contribute to a dangerous virus killing lots of people in a
		// short amount of time
		// get multiplied. High duration would lead to a long and therefore expensive
		// Quaratine. Therefore we reverse the
		// scale of the duration.
		int score = infectivity * lethality * mobility * (6 - duration);

		return score >= QUARANTINE_THRESHOLD;
	}

	private static boolean doDevVaccine(Virus virus, Game game) {
		int infectivity = virus.getInfectivity().numericRepresenation();
		int mobility = virus.getMobility().numericRepresenation();

		int score = mobility * infectivity;

		// If a virus expands fast vaccines should not be developed.
		if (score > DEV_VACCINE_THRESHOLD) {
			return false;
		}

		// Vaccine already available so no development necessary
		if (game.getVaccAvailableEvents().stream().anyMatch(event -> event.getVirus() == virus)) {
			return false;
		}

		// Vaccine already in development so no development necessary
		if (game.getVaccDevEvents().stream().anyMatch(event -> event.getVirus() == virus)) {
			return false;
		}

		// Vaccine should not be available already or in development
		return true;
	}

	private static boolean doDevMedication(Virus virus, Game game) {
		int infectivity = virus.getInfectivity().numericRepresenation();
		int mobility = virus.getMobility().numericRepresenation();

		int score = mobility * infectivity;

		// If a virus is not expanding fast, medication should not be developed.
		if (score <= DEV_MEDICATION_THRESHOLD) {
			return false;
		}

		// Medication already available so no development necessary
		if (game.getMedAvailableEvents().stream().anyMatch(event -> event.getVirus() == virus)) {
			return false;
		}

		// Medication already in development so no development necessary
		if (game.getMedDevEvents().stream().anyMatch(event -> event.getVirus() == virus)) {
			return false;
		}
		// Medication should not be available already or in development
		return true;
	}


	public static int getValue(Action action) {
		return getValue(new HashSet<Action>(Arrays.asList(action)));
	}

	public static int getValue(HashSet<Action> actions) {
		int score = 0;
		City city;

		for (Action action : actions) {
			switch (action.getType()) {
			case endRound:
				break;
			case putUnderQuarantine:
				// City under investigation
				city = action.getCity();

				// The city does not need to be quarantined without an outbreak
				if (city.getOutbreak() == null)
					break;

				// The city does not need to be quarantined if the virus is to mild
				if (!doQuarantine(city.getOutbreak().getVirus()))
					break;

				// The city does not need to be quarantined if it is already under quarantine
				if (city.getQuarantine() != null)
					break;

				// City should be quarantined
				score += QUARANTINE_FACTOR * action.getCost();
				break;
			case closeAirport:
				break;
			case closeConnection:
				break;
			case developVaccine:
				// Only develop vaccine if necessary
				if (!doDevVaccine(action.getVirus(), action.getGame()))
					break;

				// Calculate the global prevalance. If everyone is already
				// infected vaccines are useless
				double infectedPopulation = 0;
				double totalPopulation = action.getGame().getPopulation();

				for (City c: action.getGame().getCities().values()) {
					infectedPopulation += c.getPopulation() * c.getPrevalance();
				}

				double globalPrevalance = infectedPopulation / totalPopulation;

				// Only if prevalence is low enough add score
				if (globalPrevalance < DEV_VACCINE_PREVALANCE_THRESHOLD) {
					score += (DEV_VACCINE_FACTOR * action.getVirus().getLethality().numericRepresenation());
				}

				break;
				
			case deployVaccine:
				city = action.getCity();
				// Cities only need to be vaccinated once
				if(city.getVaccineDeployed().stream().anyMatch(e -> e.getVirus() == action.getVirus())) break;
				
				// TODO: Adjust formula
				score += DEP_MEDICATION_FACTOR * (1 - city.getPrevalance()) * city.getPopulation() * action.getVirus().getLethality().numericRepresenation();
				break;
			case developMedication:
				// If virus is strong enough develop medication 
				if (doDevMedication(action.getVirus(), action.getGame())) {
					score += (DEV_MEDICATION_FACTOR * action.getVirus().getLethality().numericRepresenation());
				}
				break;
			case deployMedication:
				city = action.getCity();
				// TODO: Adjust formula
				score += DEP_MEDICATION_FACTOR * city.getPrevalance() * city.getPopulation() * action.getVirus().getLethality().numericRepresenation();
				break;
			case exertInfluence:
			case callElections:
			case applyHygienicMeasures:
			case launchCampaign:
			default:
				break;
			}
			
		}
		return score;
	}

}
