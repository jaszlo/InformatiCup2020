package app.solver;

import java.util.HashSet;

import app.game.Game;
import app.game.actions.Action;
import app.game.actions.ActionControl;
import app.game.actions.ActionHeuristic;
import app.knapsack.Solver;

public class Main {

	public static String solve(Game game) {
		return ActionControl.generatePossibleActions(game).parallelStream().filter(a -> a.getCost() <= game.getPoints())
				.max((Action a, Action b) -> ActionHeuristic.getValue(a) - ActionHeuristic.getValue(b))
				.orElse(new Action(game)).toString();
	}

	public static String solveKnappsack(Game game) {
		HashSet<Action> actions = ActionControl.generatePossibleActions(game); // Alle möglichen aktionen beschaffen
		Runtime.getRuntime().gc();
		HashSet<Action> resultActions = Solver.solve(actions, game.getPoints()); // Die möglichst besten auswählen
		Action nextAction = resultActions.stream()
				.max((Action a, Action b) -> ActionHeuristic.getValue(a) - ActionHeuristic.getValue(b))
				.orElse(new Action(game));
		return ActionHeuristic.getValue(nextAction) <= 0 ? new Action(game).toString() : nextAction.toString();
	}
}
