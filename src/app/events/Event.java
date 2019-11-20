package app.events;

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
