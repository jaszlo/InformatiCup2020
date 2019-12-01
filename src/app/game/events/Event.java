package app.game.events;

/**
 * Superclass for all event-classes
 * that hold information about a specific ingame event.
 *
 */
public abstract class Event {
	
	private final EventType type;
	
	public Event(EventType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return getClass().getName();
	}
	
	public EventType getType() {
		return type;
	}
	
	public String getName() {
		return type.name();
	}
	
}
