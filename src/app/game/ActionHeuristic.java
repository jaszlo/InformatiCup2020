package app.game;

import java.util.ArrayList;
import java.util.Arrays;
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
	//Only develop vaccine if global prevlance is lower than this threshold value.
	static final double DEV_VACCINE_PREVALANCE_THRESHOLD = 1.0/3.0; 

	//Medication over vaccination therefore multiply the factor by 3.
	static final int DEV_MEDICATION_FACTOR = DEV_VACCINE_FACTOR * 3; 
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
	
	private static boolean doDevMedication (Virus v) {
		int infectivity = v.getInfectivity().numericRepresenation();
		int mobility = v.getMobility().numericRepresenation();
		
		int score = mobility * infectivity;
		//If a virus expands fast medication should be developed.
		return score > DEV_MEDICATION_THRESHOLD;
	}

	private static boolean actionMatchesCity(Action a, City c) {
		return a.getHttpResponse().contains((c.getName()));
	}

	private static boolean actionMatchesVirus(Action a, Virus v) {
		return a.getHttpResponse().contains(v.getName());
	}
	
	public static int getValue(Action action) {
		return getValue(new HashSet<Action>(Arrays.asList(action)));
	}
	
	public static int getValue(HashSet<Action> actions) {
		int score = 0;
		int requiredPoints = 0;

		for (Action a : actions) {
			switch (a.getType()) {
			case endRound:
				break;
			case putUnderQuarantine:
				// Check whether a pathogen exists that is worthy to be quarantined
				boolean virusFound = false;
				for (E_PathogenEncounter e: a.getGame().getPathEncounterEvents()) {
					if (doQuaratine(e.getVirus()))  {
						virusFound = true;
						break;
					}
				}
				
				// Skip evaluation if no pathogen is found
				if (!virusFound) {
					break;
				}
				
				//iterate over all outbreaks and check if a lethal virus needs to be put under quarantine.
				for (E_Outbreak e : a.getGame().getOutbreakEvents()) {
					// Check if the Virus is dangerous enough to be put under quarantine
					//						and matches the city with the current action.
					//						and the city isn't already quarantined
					if (doQuaratine(e.getVirus()) && actionMatchesCity(a, e.getCity()) && 
							!a.getGame().cityContains(e.getCity(), EventType.quarantine)) {
						score += QUARANTINE_FACTOR * a.getCost();
						break;
					}
				}
				break;
			case closeAirport: break;
			case closeConnection: break;
			case developVaccine: 
				//iterate over all encountered pathognes and check if vaccines needs to be developed.
				for (E_PathogenEncounter e : a.getGame().getPathEncounterEvents()) {
					
					boolean alreadyDev = false;
					
					//check if virus matches with current action and needs to be vaccinated 
					if (doDevVaccine(e.getVirus()) && actionMatchesVirus(a, e.getVirus())){
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
							double totalPopulation = 0;
							double infectedPopulation = 0;
							
							//calculate the current total population
							for (City c : a.getGame().getCities().values()) {
								totalPopulation += c.getCitizens();
								infectedPopulation += c.getCitizens() * c.getPrevalance();
							}
							/*
							//calculate the current infected population
							for (E_Outbreak q : a.getGame().getOutbreakEvents()) {
								if (actionMatchesVirus(a, q.getVirus())) {
									infectedPopulation += q.getCity().getCitizens() * q.getPrevalence();
								}
							}*/
							
							//check global prevalance and remove score if necessary.
							double globalPrevalance = infectedPopulation / totalPopulation;
							if (globalPrevalance >= DEV_VACCINE_PREVALANCE_THRESHOLD) {
								score -= DEV_VACCINE_FACTOR;
							}
						}
					}
					
					
				} break;
			case deployVaccine:
				
				boolean anyVaccinesAvailable = false;
				ArrayList<City> vaccinedCities = new ArrayList<>();
				
				for (E_VaccineAvailable e : a.getGame().getVaccAvailableEvents()) {
					if (e.getName().equals(a.getParameters()[0])) {
						anyVaccinesAvailable = true;
					}
				}
				
				for (E_VaccineDeployed q : a.getGame().getVaccDeployedEvents()) {
					vaccinedCities.add(q.getCity());
				}
				double totalCityPopulation, infectedCityPopulation;
				
				if (anyVaccinesAvailable) {
					for (City c : a.getGame().getCities().values()) {
						if (vaccinedCities.contains(c)) {
							break;
						}
						totalCityPopulation = c.getCitizens();
						infectedCityPopulation = c.getCitizens() * c.getPrevalance();			

						//if prevalance is lower than Threshold deploy vaccines.
						if ((infectedCityPopulation / totalCityPopulation) <= DEP_MEDICATION_THRESHOLD) {
							score += DEP_MEDICATION_FACTOR;
						}
					}
					
				}
				
				break;
			case developMedication:
				//iterate over all encountered pathogenes and check if medication needs to be developed.
				for (E_PathogenEncounter e : a.getGame().getPathEncounterEvents()) {
				
					//check if virus matches with current action and needs to be medicated
					if (doDevMedication(e.getVirus()) && actionMatchesVirus(a, e.getVirus())) {
						score += (DEV_MEDICATION_FACTOR * e.getVirus().getLethality().numericRepresenation());
						
						//check if a medication was already developed. If it is remove the given score.
						for (E_MedicationAvailable q : a.getGame().getMedAvailableEvents()) {
							if (q.getVirus() == e.getVirus()) {
								score -= (DEV_MEDICATION_FACTOR * e.getVirus().getLethality().numericRepresenation());
								break;
							}
						}
						//check if a vaccine is  already being developed. If it is remove the given score.
						for (E_MedicationInDevelopment q: a.getGame().getMedDevEvents()) {
							if (q.getVirus() == e.getVirus()) {
								score -= (DEV_MEDICATION_FACTOR * e.getVirus().getLethality().numericRepresenation());
								break;
							}
						}
					}
				} break;
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
