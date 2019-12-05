package app.knapsack;

import java.util.HashSet;

/**
 * Class to represent a transition from a knapsack solution to another.
 * A Swap is the removal of X items to add one item Y to the knapsack.
 */
public class Swap<T extends Item>{
	
	private final HashSet<T> toRemove;
	private final T toAdd;
	
	/**
	 * Creates a swap object
	 * @param toRemove The items to be removed from the knapsack
	 * @param toAdd The item to be added to the knapsack
	 */
	public Swap(HashSet<T> toRemove, T toAdd) {
		this.toRemove = toRemove;
		this.toAdd = toAdd;
	}
	
	/**
	 * 
	 * @return A set of the items to be removed from the knapsack
	 */
	public HashSet<T> getItemsToRemove(){
		return toRemove;
	}
	
	/**
	 * 
	 * @return The item to be added to the knapsack
	 */
	public T getItemToAdd() {
		return toAdd;
	}
	
	/**
	 * 
	 * @param bag The knapsack where the item change is to be applied
	 * @param set The possible items that can be added to the knapsack. The removal items are added to this
	 * set while the item added to the knapsack is removed.
	 * @param swap
	 */
	public static <K extends Item> void applySwap(Bag<K> bag, HashSet<K> set, Swap<K> swap) {
		bag.removeItems(swap.getItemsToRemove());
		bag.addItem(swap.getItemToAdd());
		set.remove(swap.getItemToAdd());
		set.addAll(swap.getItemsToRemove());
	}
	
	/**
	 * Functions identically to applySwap except that the removal Items are swapped with the
	 * items to add.
	 */
	public static <K extends Item> void reverseSwap(Bag<K> bag, HashSet<K> set, Swap<K> swap) {
		bag.removeItem(swap.getItemToAdd());
		bag.addItems(swap.getItemsToRemove());
		set.removeAll(swap.getItemsToRemove());
		set.add(swap.getItemToAdd());
	}
}