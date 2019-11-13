package app.game;

public enum EventType {

	airportClosed, antiVaccinationism, bioTerrorism, connectionClosed, medicationAvailable, medicationDeployed,
	medicationInDevelopment, outbreak, pathogenEncountered, quarantine, uprising, vaccineAvailable, vaccineDeployed,
	vaccineInDevelopment;

	public boolean containedByCity() {
		return  this == airportClosed		||
				this == antiVaccinationism	||
				this == bioTerrorism		||
				this == connectionClosed	||
				this == medicationDeployed	||
				this == outbreak			||
				this == quarantine			||
				this == uprising			||
				this == vaccineDeployed;
	}
}
