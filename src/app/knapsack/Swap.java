package app.knapsack;

import java.util.HashSet;

public class Swap<T extends Item>{
	
	private final HashSet<T> toRemove;
	private final T toAdd;
	
	public Swap(HashSet<T> toRemove, T toAdd) {
		this.toRemove = toRemove;
		this.toAdd = toAdd;
	}
	
	public HashSet<T> getItemsToRemove(){
		return toRemove;
	}
	
	public T getItemToAdd() {
		return toAdd;
	}
	
	
	public static <K extends Item> void applySwap(Bag<K> bag, HashSet<K> set, Swap<K> swap) {
		bag.removeItems(swap.getItemsToRemove());
		bag.addItem(swap.getItemToAdd());
		set.remove(swap.getItemToAdd());
		set.addAll(swap.getItemsToRemove());
	}
	
	public static <K extends Item> void reverseSwap(Bag<K> bag, HashSet<K> set, Swap<K> swap) {
		bag.removeItem(swap.getItemToAdd());
		bag.addItems(swap.getItemsToRemove());
		set.removeAll(swap.getItemsToRemove());
		set.add(swap.getItemToAdd());
	}
}