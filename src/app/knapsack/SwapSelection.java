package app.knapsack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Stack;

public class SwapSelection {

	public static <T extends Item> Swap<T> randomSwap(Bag<T> bag, HashSet<T> availableItems) {
		ArrayList<T> itemList = new ArrayList<T>(availableItems.size());
		for(T i : availableItems) {
			if(i.getCost() <= bag.getCostCapacity())
				itemList.add(i);
		}
		if(itemList.isEmpty())
			return null;
		T random = itemList.get((int)(Math.random()*itemList.size()));
		ArrayList<T> bagItems = new ArrayList<T>(bag.getItems());
		HashSet<T> removalSet = new HashSet<T>();
		int excessCost = (bag.getCostSum()+random.getCost()) -bag.getCostCapacity();
		while(excessCost > 0) {
			int index = (int)(Math.random()*bagItems.size());
			T randomBagItem = bagItems.remove(index);
			removalSet.add(randomBagItem);
			excessCost-=randomBagItem.getCost();
		}
		return new Swap<T>(removalSet,random);
	}
	
	public static <T extends Item> HashSet<Swap<T>> getPossibleSwaps(Bag<T> bag, HashSet<T> availableItems) {
		HashSet<Swap<T>> swaps = new HashSet<Swap<T>>();
		for(T i : availableItems) {
			if(i.getCost() > bag.getCostCapacity())
				continue;
			int weightToRemove = bag.getCostSum()+i.getCost() - bag.getCostCapacity();
			if(weightToRemove > 0) {
				Stack<HashSet<T>> possibleRemovals = getItemsMinimallyAboveLimit(bag.getItems(),weightToRemove);
				for(HashSet<T> removal : possibleRemovals)
					swaps.add(new Swap<T>(removal, i));
			}else 
				swaps.add(new Swap<T>(new HashSet<T>(),i));
		}
		return swaps;
	}
	
	private static <T extends Item> Stack<HashSet<T>> getItemsMinimallyAboveLimit(HashSet<T> items, int limit) {
		Stack<HashSet<T>> result = new Stack<HashSet<T>>();
		ArrayList<T> sorted = new ArrayList<T>(items);
		Collections.sort(sorted);
		for(int i = 0; i < sorted.size(); i++) {
			T temp = sorted.get(i);
			sorted.set(i, sorted.get(sorted.size()-1-i));
			sorted.set(sorted.size()-1-i, temp);
		}
		helper(result,0,new HashSet<T>(),sorted,0,limit);
		return result;
	}
	
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
