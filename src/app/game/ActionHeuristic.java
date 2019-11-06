package app.game;

import java.util.HashSet;

public class ActionHeuristic {

	//FACTOR: 		The factor by which the score is scaled.
	//THRESHOLD:	The minimum of points an action need to reach in order to be executed.
	
	static final int END_ROUND_FACTOR = 0;
	static final int END_ROUND_THRESHOLD = 0;

	static final int QUARANTINE_FACTOR = 100;
	static final int QUARANTINE_THRESHOLD = 350;

	static final int DEV_VACCINE_FACTOR = 50;
	static final int DEV_VACCINE_THRESHOLD = 9;
	//Only develop Vaccine if global prevlance is lower than this threshold value.
	static final double DEV_VACCINE_PREVALANCE_THRESHOLD = 1.0/3.0; 

	static final int DEV_MEDICATION_FACTOR = DEV_VACCINE_FACTOR;
	static final int DEV_MEDICATION_THRESHOLD = DEV_VACCINE_THRESHOLD;

	static final int DEP_VACCINE_FACTOR = 50;
	static final int DEP_VACCINE_THRESHOLD = 40;

	static final int DEP_MEDICATION_FACTOR = DEV_VACCINE_FACTOR;
	static final int DEP_MEDICATION_THRESHOLD = DEV_VACCINE_THRESHOLD;

	private static boolean doQuaratine(Virus v) {
		int infectivity = v.getInfectivity().numericRepresenation();
		int lethality = v.getLethality().numericRepresenation();
		int mobility = v.getMobility().numericRepresenation();
		int duration = v.getDuration().numericRepresenation();

		// Factors that contribute to a dangerous virus killing lots of people in a
		// short amount of time
		// get multiplied. High duration would lead to a long and therefore expensive
		// Quaratine. Therefore we reverse the
		// scale of the duration.
		int score = infectivity * lethality * mobility * (6 - duration);

		return score >= QUARANTINE_THRESHOLD;
	}

	private static boolean doDevVaccine(Virus v) {
		int infectivity = v.getInfectivity().numericRepresenation();
		int mobility = v.getMobility().numericRepresenation();

		// If a virus expands slowly vaccines should be developed.
		int score = mobility * infectivity;

		return score <= DEV_VACCINE_THRESHOLD;
	}

	private static boolean actionMatchesCity(Action a, City c) {
		return a.getHttpResponse().contains((c.getName()));
	}

	private static boolean actionMatchesVirus(Action a, Virus v) {
		return a.getHttpResponse().contains(v.getName());
	}

	public static int getValue(HashSet<Action> actions) {
		int score = 0;
		int requiredPoints = 0;

		for (Action a : actions) {
			switch (a.getType()) {
			case endRound:
				break;
			case putUnderQuarantine:
				//check PathogeneEncounred for doQuaratine viruses to boost performance.
				boolean virusFound = false;
				for (E_PathogenEncounter e: a.getGame().getPathEncounterEvents()) {
					if (doQuaratine(e.getVirus()))  {
						virusFound = true;
						break;
					}
				}
				
				//only iterate over outbreak events if necessary.
				if (virusFound) {
					//iterate over all outbreaks and check if a lethal virus needs to be put under quarantine.
					for (E_Outbreak e : a.getGame().getOutbreakEvents()) {
						//check if the Virus is dangerous enough to be put under quarantine and matches the city with the current action.
						if (doQuaratine(e.getVirus()) && actionMatchesCity(a, e.getCity())) {
							
							score += QUARANTINE_FACTOR * a.getCost();
							
							//check if the infected city is already put under quarantine.
							for (E_Quarantine q : a.getGame().getQuarantineEvents()) {
								if (actionMatchesCity(a, q.getCity())) {
									//if the city is already put under quarantine remove the given score.
									score -= QUARANTINE_FACTOR * a.getCost();
									break;
								}
							}
							break;
						}
					}
				}
				break;
			case closeAirport:
			case closeConnection:
			case developVaccine:
				//iterate over all encountered pathognes and check if vaccines need to be done.
				for (E_PathogenEncounter e : a.getGame().getPathEncounterEvents()) {
					
					boolean alreadyDev = false;
					
					//check if virus matches with current action and needs to be vaccinated 
					if (doDevVaccine(e.getVirus()) && actionMatchesVirus(a, e.getVirus())) {
						score += (DEV_VACCINE_FACTOR * e.getVirus().getLethality().numericRepresenation());
						
						//check if a vaccine was already developed. If it is remove the given score.
						for (E_VaccineAvailable q : a.getGame().getVaccAvailableEvents()) {
							if (q.getVirus() == e.getVirus()) {
								score -= (DEV_VACCINE_FACTOR * e.getVirus().getLethality().numericRepresenation());
								alreadyDev = true;
								break;
							}
						}
						//check if a vaccine is  already being developed. If it is remove the given score.
						for (E_VaccineInDevelopment q: a.getGame().getVaccDevEvents()) {
							if (q.getVirus() == e.getVirus()) {
								score -= (DEV_VACCINE_FACTOR * e.getVirus().getLethality().numericRepresenation());
								alreadyDev = true;
								break;
							}
						}
						//check if to many people are already infected (if score was not removed yet).
						//if too many are infected remove score.
						if (!alreadyDev) {
							int totalPopulation = 0;
							double infectedPopulation = 0;
							
							//calculate the current total population
							for (City c : a.getGame().getCities().values()) {
								totalPopulation += c.getCitizens();
							}
							//calculate the current infected population
							for (E_Outbreak q : a.getGame().getOutbreakEvents()) {
								if (actionMatchesVirus(a, q.getVirus())) {
									infectedPopulation += q.getCity().getCitizens() * q.getPrevalence();
								}
							}
							
							//check global prevalance and remove score if necessary.
							double globalPrevalance = infectedPopulation / totalPopulation;
							if (globalPrevalance >= DEV_VACCINE_PREVALANCE_THRESHOLD) {
								score -= DEV_VACCINE_FACTOR;
							}
						}
					}
					
					
				}
			case deployVaccine:
			case developMedication:
			case deployMedication:
			case exertInfluence:
			case callElections:
			case applyHygienicMeasures:
			case launchCampaign:
			default:
				break;
			}
			if ((requiredPoints += a.getCost()) > a.getGame().getPoints())
				return Integer.MIN_VALUE;
		}
		return score; // determining proper value is left as an exercise for the reader
	}

}
