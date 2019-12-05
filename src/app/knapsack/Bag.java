package app.knapsack;

import java.util.Collection;
import java.util.HashSet;

/**
 * Class to represent a solution to the knapsack problem.
 * For reference: https://en.wikipedia.org/wiki/Knapsack_problem
 */
public class Bag<T extends Item> {
	
	private final int costCapacity;
	
	private HashSet<T> items;
	
	private int value;
	
	private int costSum;
	
	/**
	 * Creates a Bag 
	 * @param capacity The maximum weight all items in Bag cannot exceed.
	 * @param items Set of all items the knapsack contains
	 */
	public Bag(int capacity, HashSet<T> items) {
		this.items = items;
		this.costCapacity = capacity;
		update();
	}
	
	/**
	 * Deep copies a Bag object
	 * @param toCopy Bag to copy
	 */
	public Bag(Bag<T> toCopy) {
		this.items = new HashSet<T>(toCopy.getItems());
		this.value = toCopy.getValue();
		this.costCapacity = toCopy.getCostCapacity();
		this.costSum = toCopy.getCostSum();
	}
	
	private void update() {
		updateValue();
		updateCost();
	}
	
	private void updateValue() {
		if(items.isEmpty())
			value= 0;
		else
			value= items.iterator().next().getSetValue(items);
	}
	
	private void updateCost() {
		costSum = 0;
		for(T i : items)
			costSum+=i.getCost();
	}
	
	/**
	 * Adds a list of items to the knapsack. If the additional weight
	 * of the items exceeds the bags capacity, false is returned and the operation
	 * is cancelled. Otherwise true is returned.
	 * @param addition List of items to be added to the knapsack.
	 * @return Whether items could be added or not.
	 */
	public boolean addItems(Collection<T> addition) {
		int sum = 0;
		for(T i : addition)
			sum+=i.getCost();
		if(sum+getCostSum() <= getCostCapacity()) {
			items.addAll(addition);
			update();
			return true;
		}
		else 
			return false;
	}
	
	/**
	 * Adds a single item to the knapsack. Equivalent to calling
	 * @addItems with the collection being a single item.
	 */
	public boolean addItem(T i) {
		if(i.getCost()+getCostSum() <= getCostCapacity()) {
			items.add(i);
			update();
			return true;
		}else 
			return false;
	}
	
	/**
	 * Removes a single item from the knapsack. 
	 * Equivalent to calling removeItems with one Object in the List.
	 */
	public void removeItem(T i) {
		items.remove(i);
		update();
	}
	
	/**
	 * Removes, if possible, the specified items from the bag.
	 * @param removal Items to be removed from the bag
	 */
	public void removeItems(Collection<T> removal) {
		items.removeAll(removal);
		update();
	}
	
	/**
	 * 
	 * @param item Item to check whether there is space for it
	 * @return true if item can be added, false otherwise. An item can be added if 
	 * its weight + weight of bag does not exceed the bags capacity.
	 */
	public boolean hasSpaceFor(T item) {
		return item.getCost()+getCostSum() <= getCostCapacity();
	}
	
	/**
	 * 
	 * @return The value of the knapsack, i.e. the value of this solution.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * 
	 * @return The maximum weight of items this knapsack can store.
	 */
	public int getCostCapacity() {
		return costCapacity;
	}

	/**
	 * 
	 * @return The current weight of the items in the knapsack.
	 */
	public int getCostSum() {
		return costSum;
	}
	
	/**
	 * 
	 * @return Set of all items in the knapsack.
	 */
	public HashSet<T> getItems(){
		return items;
	}
	
	/**
	 * @return General information of the bag converted into a String.
	 */
	public String toString() {
		String s = "Used capacity: "+getCostSum()+"/"+getCostCapacity()+"\n";
		s+="Value:"+getValue()+"\n";
		for(T i : items)
			s+=i+"\n";
		return s;
	}
	
}
