package app.game;

import java.util.HashSet;

public class ActionHeuristic {

	static final int END_ROUND_FACTOR = 0;
	static final int END_ROUND_THRESHOLD = 0;
	static final int QUARANTINE_FACTOR = 100;
	static final int QUARANTINE_THRESHOLD = 40; 

	private static boolean doQuaratine(Virus v) {
		int infectivity = v.getInfectivity().numericRepresenation();
		int lethality = v.getLethality().numericRepresenation();
		int mobility = v.getMobility().numericRepresenation();
		int duration = v.getDuration().numericRepresenation();
		
		//Factors that contribute to a dangerous virus killing lots of people in a short amount of time
		//get multiplied. High duration would lead to a long and therefore expensive Quaratine. Therefore we devide by it.
		int score = (infectivity * lethality * mobility) / duration;
		
		return score >= QUARANTINE_THRESHOLD;
	}

	private static boolean actionMatchesCity(Action a, City c) {
		return a.getHttpResponse().contains((c.getName()));
	}

	public static int getValue(HashSet<Action> actions) {
		int score = 0;
		int requiredPoints = 0;

		for (Action a : actions) {
			switch (a.getType()) {
			case endRound:
				break;
			case putUnderQuarantine:
				for (E_Outbreak e : a.getGame().getOutbreakEvents()) {
					//check if the Virus is dangerous enough to be put under quarantine and matches the city with the current action.
					if (doQuaratine(e.getVirus()) && actionMatchesCity(a, e.getCity())) {
						
						score += QUARANTINE_FACTOR * a.getCost();
						
						//check if the infected city is already put under quarantine.
						for (E_Quarantine q : a.getGame().getQuarantineEvents()) {
							if (a.getHttpResponse().contains(q.getCity().getName())) {
								//if the city is already put under quarantine remove the given score.
								score -= QUARANTINE_FACTOR * a.getCost();
								break;
							}
						}
						break;
					}
				}
				break;
			case closeAirport:
			case closeConnection:
			case developVaccine:
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
