package app.solver;

import java.util.HashSet;

import app.game.Action;
import app.game.ActionControl;
import app.game.ActionHeuristic;
import app.game.Game;
import app.knapsack.Solver;

public class Main {
	
	public static String solve(Game game) {
		HashSet<Action> actions = ActionControl.generatePossibleActions(game); //Alle möglichen aktionen beschaffen
		Runtime.getRuntime().gc();
		HashSet<Action> resultActions = Solver.solve(actions, game.getPoints()); //Die möglichst besten auswählen
		Action nextAction = resultActions.stream().max((Action a, Action b) -> ActionHeuristic.getValue(a) - ActionHeuristic.getValue(b)).orElse(new Action(game));
		return ActionHeuristic.getValue(nextAction) <= 0? new Action(game).toString(): nextAction.toString();
	}
}
