package app.knapsack;

import java.util.ArrayList;
import java.util.HashSet;

public class Solver {

	/**
	 * Finds a good approximation to the knapsack problem provided by set and capacity.
	 * @param <T>
	 * @param set All possible items that can be put into the knapsack
	 * @param capacity The maximum weight the sum of items of a solution may have
	 * @return The knapsack filled with items, resulting in the best possible value of the knapsack
	 */
	public static<T extends Item> HashSet<T> solve(HashSet<T> set, int capacity){
		Bag<T> maxSolution = null;
		HashSet<T> maxRestItems = null;
		final int RANDOM_ITERATIONS = 5, GREEDY_ITERATIONS = 5;
		//RANDOM FILL
		for(int i = 0; i < RANDOM_ITERATIONS; i++) {
			Bag<T> temp = new Bag<T>(capacity,new HashSet<T>());
			HashSet<T> tempItems = new HashSet<T>(set);
			fillBagRandom(temp, tempItems);
			Bag<T> solution = solveIteration(temp,tempItems,capacity);
			if(maxSolution == null || solution.getValue() > maxSolution.getValue()) {
				maxSolution = solution;
				maxRestItems = tempItems;
			}
		}
		//GREEDY FILL
		for(int i = 0; i < GREEDY_ITERATIONS; i++) {
			Bag<T> temp = new Bag<T>(capacity,new HashSet<T>());
			HashSet<T> tempItems = new HashSet<T>(set);
			fillBagGreedy(temp, tempItems);
			Bag<T> solution = solveIteration(temp,tempItems,capacity);
			if(maxSolution == null || solution.getValue() > maxSolution.getValue()) {
				maxSolution = solution;
				maxRestItems = tempItems;
			}
		}
		//Optimization.optimiseSolution(maxSolution, maxRestItems); //evt wieder nach jedem SA durchlauf
		return maxSolution.getItems();
	}

	private static <T extends Item> Bag<T> solveIteration(Bag<T> bag, HashSet<T> set, int capacity) {
		HashSet<T> originalSet = new HashSet<T>(set);
		Bag<T> saSol = SimulatedAnnealing.simulate(bag,set, 1, 0.001, 0.8, 10);
		set = new HashSet<T>(originalSet);
		set.removeAll(saSol.getItems());
		//Optimization.optimiseSolution(saSol,set);	 aus laufzeitgrunden nur am ende	
		return saSol;
	}
	
	/**
	 * Randomly fills a bag with items from a given set until no item can be put into
	 * the bag without exceeding its capacity.
	 * bag and availableItems are modified to the result of the method.
	 * @param <T>
	 * @param bag The bag to be filled
	 * @param availableItems The items that can be put into the bag
	 */
	public static<T extends Item> void fillBagRandom(Bag<T> bag, HashSet<T> availableItems) {
		boolean addedItem = true;
		while(addedItem) {
			addedItem = false;
			ArrayList<T> addable = new ArrayList<T>(availableItems.size());
			for(T i : availableItems) {
				if(bag.hasSpaceFor(i))
					addable.add(i);
			}
			if(!addable.isEmpty()) {
				addedItem = true;
				T i = addable.get((int)(Math.random()*addable.size()));
				availableItems.remove(i);
				bag.addItem(i);
			}
		}
	}
	
	/**
	 * Fills a bag with the greedy algorithm until no item can be put into the bag without
	 * exceeding its capacity.
	 * bag and availableItems are modified to the result of the method.
	 * @param <T>
	 * @param bag The bag to be filled
	 * @param availableItems The items that can be put into the bag
	 */
	public static <T extends Item> void fillBagGreedy(Bag<T> bag, HashSet<T> availableItems) { 
		HashSet<T> set = new HashSet<T>(availableItems);
		boolean addedItem = true;
		while(addedItem) {
			addedItem = false;
			int max = Integer.MIN_VALUE;
			T maxItem = null;
			//search for item with biggest value that fits into bag
			for(T item : set) {
				if(bag.hasSpaceFor(item)) {
					Bag<T> temp = new Bag<T>(bag);
					temp.addItem(item);
					if(temp.getValue() >= max) {
						max = temp.getValue();
						maxItem = item;
						addedItem = true;
					}
				}
			}
			//put most valuable item into bag
			if(maxItem != null) {
				set.remove(maxItem);
				bag.addItem(maxItem);
			}
		}
	}
}
