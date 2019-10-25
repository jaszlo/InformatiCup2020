package app.game;

import java.util.HashSet;

import app.knapsack.Item;

public class Action extends Item{

	private final ActionType type;
	
	private final Game game;
	
	private final String response;
	
	private final Object[] parameters; 
	
	public Action(ActionType type, Game game,int points, String response, Object... parameters) {
		super(points);
		this.game = game;
		this.response = response;
		this.type = type;
		this.parameters = parameters;
	}

	public ActionType getType() {
		return type;
	}

	public Object[] getParameters() {
		return parameters;
	}
	
	public String getHttpResponse() {
		return response;
	}

	public Game getGame() {
		return game;
	}

	@SuppressWarnings("unchecked")
	public int getSetValue(HashSet<? extends Item> set) {
		return ActionHeuristic.getValue((HashSet<Action>)set);
	}
	
}
