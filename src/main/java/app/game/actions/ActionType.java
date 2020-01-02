package app.game.actions;

/**
 * Enumeration to represent the type of the different actions that can be taken.
 */
public enum ActionType {

	endRound, putUnderQuarantine, closeAirport, closeConnection, developVaccine, deployVaccine, developMedication,
	deployMedication, exertInfluence, callElections, applyHygienicMeasures, launchCampaign;

	/**
	 * Calculates the cost that the action would take, dependent on the number of rounds.
	 * 
	 * @param rounds The number of rounds the action will be executed in.
	 * @return The cost of the action.
	 */
	public int getCosts(int rounds) {
		
		switch (this) {
		case endRound:
			return 0;

		case putUnderQuarantine:
			return 20 + rounds * 10;

		case closeAirport:
			return 15 + rounds * 5;

		case closeConnection:
			return 3 + rounds * 3;

		case developMedication:
			return 20;

		case deployMedication:
			return 10;

		case developVaccine:
			return 40;

		case deployVaccine:
			return 5;

		case exertInfluence:
			return 3;

		case launchCampaign:
			return 3;

		case applyHygienicMeasures:
			return 3;

		case callElections:
			return 3;
		}
		return 0;
	}
}
