package app.knapsack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Stack;

public class SwapSelection {

	/**
	 * 
	 * @param bag The knapsack
	 * @param availableItems The items that can be added to the knapsack
	 * @return A randomly chosen swap of items i.e transition to a new solution
	 */
	public static <T extends Item> Swap<T> randomSwap(Bag<T> bag, HashSet<T> availableItems) {
		ArrayList<T> itemList = new ArrayList<T>(availableItems.size());
		//collect all possible items that dont exceed bag capacity
		for(T i : availableItems) {
			if(i.getCost() <= bag.getCostCapacity())
				itemList.add(i);
		}
		//if no items can be added, no swap can take place
		if(itemList.isEmpty())
			return null;
		//choosing random item to be added
		T random = itemList.get((int)(Math.random()*itemList.size()));
		ArrayList<T> bagItems = new ArrayList<T>(bag.getItems());
		HashSet<T> removalSet = new HashSet<T>();
		int excessCost = (bag.getCostSum()+random.getCost()) -bag.getCostCapacity();
		//randomly remove items from bag until there is enough capacity
		//to add the random item.
		while(excessCost > 0) {
			int index = (int)(Math.random()*bagItems.size());
			T randomBagItem = bagItems.remove(index);
			removalSet.add(randomBagItem);
			excessCost-=randomBagItem.getCost();
		}
		return new Swap<T>(removalSet,random);
	}
	
	/**
	 * 
	 * @param <T>
	 * @param bag The knapsack
	 * @param availableItems All items that can be added to the knapsack
	 * @return A list of all possible Item Swaps i.e all possible transition from current solution to another.
	 */
	public static <T extends Item> HashSet<Swap<T>> getPossibleSwaps(Bag<T> bag, HashSet<T> availableItems) {
		HashSet<Swap<T>> swaps = new HashSet<Swap<T>>();
		//all available items can be part of a swap
		for(T i : availableItems) {
			//skip item if it is bigger than entire bag
			if(i.getCost() > bag.getCostCapacity())
				continue;
			//minimum amount of weight to remove from bag until current item can be added
			int weightToRemove = bag.getCostSum()+i.getCost() - bag.getCostCapacity();
			if(weightToRemove > 0) {
				//generate all possible combinations of items in the knapsack that exceed the weightToRemove
				//but so that if any items is removed the sum is below weightToRemove.
				Stack<HashSet<T>> possibleRemovals = getItemsMinimallyAboveLimit(bag.getItems(),weightToRemove);
				for(HashSet<T> removal : possibleRemovals)
					swaps.add(new Swap<T>(removal, i));
			}else 
				swaps.add(new Swap<T>(new HashSet<T>(),i));
		}
		return swaps;
	}
	
	//Helper method to return all possible combinations of items in a given set whose weight is above a certain
	//limit but removing any item from the set would make its weight drop below the limit.
	private static <T extends Item> Stack<HashSet<T>> getItemsMinimallyAboveLimit(HashSet<T> items, int limit) {
		Stack<HashSet<T>> result = new Stack<HashSet<T>>();
		ArrayList<T> sorted = new ArrayList<T>(items);
		Collections.sort(sorted);
		//reverse order of sorted
		for(int i = 0; i < sorted.size(); i++) {
			T temp = sorted.get(i);
			sorted.set(i, sorted.get(sorted.size()-1-i));
			sorted.set(sorted.size()-1-i, temp);
		}
		helper(result,0,new HashSet<T>(),sorted,0,limit);
		return result;
	}
	
	//Helper method that is ONLY used by getItemsMinimallyAboveLimit
	private static <T extends Item> void helper(Stack<HashSet<T>> result, int currentCost, HashSet<T> current, ArrayList<T> sortedItems, int lb, int limit) {
		if(lb >= sortedItems.size()) {
			if(currentCost >= limit)
				result.push(current);
			return;
		}
		if(currentCost+sortedItems.get(lb).getCost() >= limit) {
			for(int i = lb; i < sortedItems.size(); i++) {
				HashSet<T> temp = new HashSet<T>(current);
				temp.add(sortedItems.get(i));
				result.push(temp);
			}
			return;
		}
		while(lb < sortedItems.size() && currentCost+sortedItems.get(lb).getCost() < limit) {
			HashSet<T> temp = new HashSet<T>(current);
			T item = sortedItems.get(lb++);
			temp.add(item);
			helper(result,currentCost+item.getCost(),temp,sortedItems,lb,limit);
		}
	}
	
}
