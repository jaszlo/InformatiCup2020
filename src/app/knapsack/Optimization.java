package app.knapsack;

import java.util.HashSet;

public class Optimization {

	public static <T extends Item> void optimiseSolution(Bag<T> bag, HashSet<T> availableItems) {
		boolean swapped = true;
		while(swapped) {
			swapped = false;
			HashSet<Swap<T>> possibleSwaps = SwapSelection.getPossibleSwaps(bag, availableItems);
			for(Swap<T> swap : possibleSwaps) {
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
