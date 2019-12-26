package app.solver;

import app.game.Game;
import app.game.actions.Action;
import app.game.actions.ActionControl;

public class Main {

	public static String solve(Game game) {
		return  ActionControl.generatePossibleActions(game).parallelStream().filter(a -> a.getType().getPoints(a.getRounds()) <= game.getPoints())
				.max((Action a, Action b) -> a.getScore() - b.getScore()).orElse(new Action(game)).toString();
	}

}
