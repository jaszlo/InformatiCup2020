package app.game.actions;

import java.util.Arrays;
import java.util.HashSet;

import app.game.City;
import app.game.Game;
import app.game.Virus;
import app.game.events.E_Outbreak;

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
	//
	static final double DEV_VACCINE_INFECTABLE_THRESHOLD = 1.0 / 5.0;

	// Medication over vaccination therefore multiply the factor by 3.
	static final int DEV_MEDICATION_FACTOR = DEV_VACCINE_FACTOR * 3;
	static final int DEV_MEDICATION_THRESHOLD = DEV_VACCINE_THRESHOLD;
	// If a Virus usualy does not require medication but this global prevalance is
	// reached develop it anyway.
	static final double DEV_MEDICATION_PREVALANCE_THRESHOLD = DEV_VACCINE_PREVALANCE_THRESHOLD;

	static final int DEP_VACCINE_FACTOR = 50;
	static final int DEP_VACCINE_THRESHOLD = 40;

	static final int DEP_MEDICATION_FACTOR = DEV_VACCINE_FACTOR;
	static final int DEP_MEDICATION_THRESHOLD = DEV_VACCINE_THRESHOLD;

	private static boolean doQuarantine(Virus virus) {
		int infectivity = virus.getInfectivity().getNumericRepresentation();
		int lethality = virus.getLethality().getNumericRepresentation();
		int mobility = virus.getMobility().getNumericRepresentation();
		int duration = virus.getDuration().getNumericRepresentation();

		// Factors that contribute to a dangerous virus killing lots of people in a
		// short amount of time
		// get multiplied. High duration would lead to a long and therefore expensive
		// Quaratine. Therefore we reverse the
		// scale of the duration.
		int score = infectivity * lethality * mobility * (6 - duration);

		return score >= QUARANTINE_THRESHOLD;
	}

	private static boolean doDevVaccine(Virus virus, Game game) {
		int infectivity = virus.getInfectivity().getNumericRepresentation();
		int mobility = virus.getMobility().getNumericRepresentation();

		int score = mobility * infectivity;

		// If a virus expands fast vaccines should not be developed.
		return score <= DEV_VACCINE_THRESHOLD;
	}

	private static boolean doDevMedication(Virus virus, Game game) {
		int infectivity = virus.getInfectivity().getNumericRepresentation();
		int mobility = virus.getMobility().getNumericRepresentation();

		int score = mobility * infectivity;

		// Because quarantine only contains a virus within one city it is not worth the
		// points to develop a medication
		if (doQuarantine(virus)) {
			return false;
		}

		// If a virus is not expanding fast, medication should not be developed.
		// Unless it already has infected enough (see the
		// DEV_MEDICATION_PREVALANCE_THRESHOLD)
		if (score <= DEV_MEDICATION_THRESHOLD) {
			double totalPopulation = game.getPopulation();
			double infectedPopulation = game.getCities().values().stream()
					.filter(c -> c.getOutbreak() != null && c.getOutbreak().getVirus() == virus)
					.mapToDouble(c -> c.getPrevalance() * c.getPopulation()).sum();

			double globalPrevalance = infectedPopulation / totalPopulation;
			if (globalPrevalance <= DEV_MEDICATION_PREVALANCE_THRESHOLD) {
				return false;
			}
		}

		// Medication should not be available already or in development
		return true;
	}

	public static int getValue(Action action) {
		return getValue(new HashSet<Action>(Arrays.asList(action)));
	}

	public static int getValue(HashSet<Action> actions) {
		int score = 0;
		for (Action action : actions) {
			City city = action.getCity();
			Game game = action.getGame();
			
			switch (action.getType()) {
			case endRound:
				score += 1; // EndRound as default action
				break;
			case putUnderQuarantine:
				
				// If a very strong virus breaks out in 2 Cities protect the biggest one
				// This only helps in the first round
				if (city.getOutbreak() == null) {
					int strongVirusAmount = 0;
					for (E_Outbreak e : game.getOutbreakEvents()) {
						if (doQuarantine(e.getVirus())) {
							strongVirusAmount++;
						}
					}
					
					// If there are more than 2 qurantinable viruses we can not quarantine both.
					// Therefore we quarantine the biggest city and hope for the best 
					if (strongVirusAmount > 1) {
						score += QUARANTINE_FACTOR * action.getCost() * city.getPopulation();
					}
				}
				
				// The city does not need to be quarantined without an outbreak
				if (city.getOutbreak() == null)
					break;

				// The city does not need to be quarantined if the virus is to mild
				if (!doQuarantine(city.getOutbreak().getVirus()))
					break;

				// The city does not need to be quarantined if it is already under quarantine
				if (city.getQuarantine() != null)
					break;

				// Check if there is another uninfected city we want to protect from this Virus.
				if (!game.getCities().values().stream().anyMatch((City c) -> c.getOutbreak() == null)) {
					break;
				}

				// City should be quarantined
				score += QUARANTINE_FACTOR * action.getCost();
				break;
			case closeAirport:
				break;
			case closeConnection:
				break;
			case developVaccine:

				// Only develop vaccine if necessary
				if (!doDevVaccine(action.getVirus(), game)
						&& game.getCities().values().stream().filter(c -> c.getOutbreak() == null)
								.mapToDouble(c -> c.getPopulation()).sum() <= 0.1 * game.getPopulation()) {
					break;
				}

				// Calculate the global prevalance. If everyone is already
				// infected vaccines are useless
				double totalPopulation = game.getPopulation();
				double infectedPopulation = game.getCities().values().stream()
						.filter(c -> c.getOutbreak() != null && c.getOutbreak().getVirus() == action.getVirus())
						.mapToDouble(c -> c.getPrevalance() * c.getPopulation()).sum();

				double globalPrevalance = infectedPopulation / totalPopulation;

				// Only if prevalence is low enough add score
				if (globalPrevalance < DEV_VACCINE_PREVALANCE_THRESHOLD) {
					score += (DEV_VACCINE_FACTOR * action.getVirus().getLethality().getNumericRepresentation());
				}

				break;

			case deployVaccine:
				// TODO: finetune this
				if (game.getPoints() <= 25) {
					break;
				}
				city = action.getCity();
				// Cities only need to be vaccinated once
				if (city.getVaccineDeployed().stream().anyMatch(e -> e.getVirus() == action.getVirus()))
					break;

				// TODO: Adjust formula
				score += DEP_VACCINE_FACTOR * (1 - city.getPrevalance()) * city.getPopulation()
						* action.getVirus().getLethality().getNumericRepresentation();
				break;
			case developMedication:
				// If virus is strong enough develop medication
				if (doDevMedication(action.getVirus(), game)) {
					score += (DEV_MEDICATION_FACTOR * action.getVirus().getLethality().getNumericRepresentation());

				}
				break;
			case deployMedication:
				// TODO: finetune this
				if (game.getPoints() <= 30) {
					break;
				}
				city = action.getCity();
				// TODO: Adjust formula
				score += DEP_MEDICATION_FACTOR * city.getPrevalance() * city.getPopulation()
						* action.getVirus().getLethality().getNumericRepresentation();
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
