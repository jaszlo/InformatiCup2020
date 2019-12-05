package app.knapsack;

import java.util.HashSet;

public class Optimization {

	/**
	 * Tries to optimise a solution to the knapsack problem by 
	 * iterating all possible changes to the solution and applying
	 * it if its an improvement. This is repeated until no upgrade can be found.
	 * Warning: method modifies bag and availableItems to the improvement the method found.
	 * @param bag The knapsack representing the current solution.
	 * @param availableItems All available items, that can be put into the knapsack.
	 */
	public static <T extends Item> void optimiseSolution(Bag<T> bag, HashSet<T> availableItems) {
		boolean swapped = true;
		while(swapped) {
			swapped = false;
			//get list of all possible changes to current solution
			HashSet<Swap<T>> possibleSwaps = SwapSelection.getPossibleSwaps(bag, availableItems);
			for(Swap<T> swap : possibleSwaps) {
				//apply change if its an upgrade to current solution
				int preSwapValue = bag.getValue();
				Swap.applySwap(bag, availableItems, swap);
				if(bag.getValue() > preSwapValue) {
					swapped = true;
					break;
				}else {
					Swap.reverseSwap(bag, availableItems, swap);
				}
			}
		}
	}
	
}
