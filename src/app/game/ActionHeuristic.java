package app.game;

import java.util.HashSet;

public class ActionHeuristic {

	public static int getValue(HashSet<Action> actions) {
		int score = 0;
		int requiredPoints = 0;
		for(Action a: actions) {
			switch(a.getType()) {
			case endRound: break;
			case putUnderQuarantine:
				for(E_Outbreak e: a.getGame().getOutbreakEvents()) {
					if(e.getVirus().getName().contains("Admiral") && a.getHttpResponse().contains(e.getCity().getName())) {
						score += 100 * a.getCost();
						for(E_Quarantine q: a.getGame().getQuarantineEvents()) {
							if(a.getHttpResponse().contains(q.getCity().getName())) {
								score -= 100 * a.getCost();
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
			if((requiredPoints += a.getCost()) > a.getGame().getPoints()) return Integer.MIN_VALUE;
		}
		return score; //determining proper value is left as an exercise for the reader
	}
	
}
