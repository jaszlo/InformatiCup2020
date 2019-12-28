package app.game.events;

/**
 * Enumeration to assign a value to each type of event.
 */
public enum EventType {

	airportClosed, antiVaccinationism, bioTerrorism, connectionClosed, medicationAvailable, medicationDeployed,
	medicationInDevelopment, outbreak, pathogenEncountered, quarantine, uprising, vaccineAvailable, vaccineDeployed,
	vaccineInDevelopment, campaignLaunched, electionsCalled, hygienicMeasuresApplied, influenceExerted;

	/**
	 * Returns whether this EventType is city specific, i.e in the event-list of a
	 * city.
	 * 
	 * @return Whether this EventType is city specific, i.e in the event-list of a
	 *         city.
	 */
	public boolean containedByCity() {
		return this == airportClosed || this == antiVaccinationism || this == bioTerrorism || this == connectionClosed
				|| this == medicationDeployed || this == outbreak || this == quarantine || this == uprising
				|| this == campaignLaunched || this == electionsCalled || this == hygienicMeasuresApplied
				|| this == influenceExerted || this == vaccineDeployed;
	}

	/**
	 * Returns whether the event can occur more than once in one city.
	 * 
	 * @return Whether the event can occur more than once in one city.
	 */
	public boolean isMultipleEventType() {
		return this == vaccineDeployed || this == medicationDeployed || this == connectionClosed;
	}
}
