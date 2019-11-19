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

	private static boolean doQuarantine(Virus v) {
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

	private static boolean doDevVaccine(Virus v, Game g) {
		int infectivity = v.getInfectivity().numericRepresenation();
		int mobility = v.getMobility().numericRepresenation();

		
		int score = mobility * infectivity;

		// If a virus expands fast vaccines should not be developed.
		if(score > DEV_VACCINE_THRESHOLD) {
			return false;
		}
		
		// Vaccine already available so no development necessary
		if(g.getVaccAvailableEvents().stream().anyMatch(event -> event.getVirus() == v)) {
			return false;
		}
		
		// Vaccine already in development so no development necessary
		if(g.getVaccDevEvents().stream().anyMatch(event -> event.getVirus() == v)) {
			return false;
		}
		
		// Vaccine should not be available already or in development
		return true;
	}
	
	private static boolean doDevMedication (Virus v, Game g) {
		int infectivity = v.getInfectivity().numericRepresenation();
		int mobility = v.getMobility().numericRepresenation();
		
		int score = mobility * infectivity;
		
		// If a virus is not expanding fast, medication should not be developed.
		if(score <= DEV_MEDICATION_THRESHOLD) {
			return false;
		}	
		
		// Medication already available so no development necessary
		if(g.getMedAvailableEvents().stream().anyMatch(event -> event.getVirus() == v)) {
			return false;
		}
		
		// Medication already in development so no development necessary
		if(g.getMedDevEvents().stream().anyMatch(event -> event.getVirus() == v)) {
			return false;
		}
		// Medication should not be available already or in development
		return true;
	}

	private static boolean actionMatchesVirus(Action a, Virus v) {
		return a.getVirus() == v;
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
				for(Event e: a.getGame().getEventsByCity(a.getCity())) {
					if(e.getType() == EventType.outbreak && doQuarantine(((E_Outbreak) e).getVirus()) && a.getGame().cityContains(a.getCity(), EventType.quarantine)) {
						score += QUARANTINE_FACTOR * a.getCost();
					}
				}
				break;
			case closeAirport: break;
			case closeConnection: break;
			case developVaccine: 
				//iterate over all encountered pathognes and check if vaccines needs to be developed.
				for (E_PathogenEncounter e : a.getGame().getPathEncounterEvents()) {
					
					
					//check if virus matches with current action and needs to be vaccinated 
					if (doDevVaccine(e.getVirus(), a.getGame()) && actionMatchesVirus(a, e.getVirus())){
						score += (DEV_VACCINE_FACTOR * e.getVirus().getLethality().numericRepresenation());
					
						double infectedPopulation = 0;
						double totalPopulation = a.getGame().getPopulation();
						//calculate the current total population
						for (City c : a.getGame().getCities().values()) {
							infectedPopulation += c.getCitizens() * c.getPrevalance();
						}
						
						//check global prevalance and remove score if necessary.
						double globalPrevalance = infectedPopulation / totalPopulation;
						if (globalPrevalance >= DEV_VACCINE_PREVALANCE_THRESHOLD) {
							score -= DEV_VACCINE_FACTOR;
						}
					}
				}
				break;
			case deployVaccine:
				
				boolean anyVaccinesAvailable = false;
				ArrayList<City> vaccinedCities = new ArrayList<>();
				
				for (E_VaccineAvailable e : a.getGame().getVaccAvailableEvents()) {
					if (e.getVirus() == a.getVirus()) {
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
					if (doDevMedication(e.getVirus(), a.getGame()) && actionMatchesVirus(a, e.getVirus())) {
						score += (DEV_MEDICATION_FACTOR * e.getVirus().getLethality().numericRepresenation());
					}
				} 
				break;
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
