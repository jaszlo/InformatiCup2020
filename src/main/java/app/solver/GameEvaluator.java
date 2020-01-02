package app.solver;

import app.game.Game;
/**
 * Interface to represent the evaluate method.
 */
public interface GameEvaluator {
	/**
	 * Evaluates the given game and finds a reply.
	 * @param currentGame The game that will be evaluated.
	 * @return The evaluated reply for the GI client.
	 */
	public String evaluate (Game currentGame);
}
