package app.solver;

import java.util.HashSet;

import app.game.Action;
import app.game.ActionControl;
import app.game.ActionHeuristic;
import app.game.Game;
import app.knapsack.Solver;

public class Main {
	
//	public static String solve(Game game) {
//		
//		ArrayList<Virus> pathogens = new ArrayList<>();
//		
//		// Detect dangerous pathogens
//		for(Virus pathogen: game.getViruses().values()) {
//			if(pathogen.getLethality().numericRepresenation() > 0)
//				pathogens.add(pathogen);
//		}
//		
//		
//		return createEndRound();
//	}	
	
	public static String solve(Game game) {
		HashSet<Action> actions = ActionControl.generatePossibleActions(game); //Alle möglichen aktionen beschaffen
		HashSet<Action> resultActions = Solver.solve(actions, game.getPoints()); //Die möglichst besten auswählen
		if(resultActions.isEmpty())
			return new Action(game).toString();
		Action nextAction = resultActions.stream().max((Action a, Action b) -> ActionHeuristic.getValue(a) - ActionHeuristic.getValue(b)).get();
		return nextAction.toString(); //evt erst die zufälligen nehmen. aber das ist eine geschichte für einen anderen tag :)
	}
}
