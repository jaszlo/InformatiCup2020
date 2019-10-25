package app.knapsack;

import java.util.HashSet;

public class SimulatedAnnealing {

	/**
	 * 
	 * @param initialSolution A random solution to the problem
	 * @param availableItems All items that could be used in the bag
	 * @param initialT Initial Temperature
	 * @param minT Temperature at when to stop the algorithm
	 * @param tempDecrease is between 0 and 1. The rate at which temperature drops.
	 * @param iterationsCount Number of iterations before temperature decreases
	 * @return The best solution that appeared throughout the simulation
	 */
	public static <T extends Item> Bag<T> simulate(Bag<T> initialSolution, HashSet<T> availableItems, 
			double initialT, double minT, double tempDecrease, int iterationsCount)  {
		HashSet<T> currentItems = new HashSet<T>(availableItems);
		double temperature = initialT;
		Bag<T> bestSolution = new Bag<T>(initialSolution);
		Bag<T> currentSol = new Bag<T>(initialSolution);
		while(temperature > minT) {
			for(int i = 0; i < iterationsCount; i++) {
				int oldValue = currentSol.getValue();
				//Get neighbor solution
				Swap<T> randomChange = SwapSelection.randomSwap(currentSol, currentItems);
				if(randomChange==null) //Skip when no change could be made
					continue;
				Swap.applySwap(currentSol,currentItems,randomChange);
				//compare to best solution
				if(currentSol.getValue() > bestSolution.getValue()) 
					bestSolution = new Bag<T>(currentSol);
				//Change temp
				double f = Math.pow(Math.E,(currentSol.getValue()-oldValue)/temperature);
				if(f <= Math.random()) 
					Swap.reverseSwap(currentSol, currentItems, randomChange);
			}
			//Lower temperature
			temperature*=tempDecrease;
		}
		return bestSolution;
	}
	
}
