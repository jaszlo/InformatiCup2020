package app.knapsack;

import java.util.HashSet;

/**
 * Item represents a part of a knapsack.
 * A Set of Items represents a solution. This is done with @Bag class
 */
public abstract class Item implements Comparable<Item>{

	private final int cost;
	
	private static int idCount = 0;
	private final int id = idCount++; //ZUM TESTEN
	
	/**
	 * 
	 * @param cost The weight this item adds to the knapsack
	 */
	public Item(int cost) {
		this.cost = cost;
	}

	/**
	 * 
	 * @return The weight of this item to the knapsack
	 */
	public int getCost() {
		return cost;
	}

	public int compareTo(Item i) {
		return Integer.compare(getCost(), i.getCost());
	}

	/**
	 * 
	 * @return the unique identifier of this Item. Every Item is assigned a different id.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return The Items information represented as a String.
	 */
	public String toString() {
		return "Id:"+getId()+", Cost:"+getCost();
	}

	public abstract int getSetValue(HashSet<? extends Item> set);
	
}
