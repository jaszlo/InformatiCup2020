package app.knapsack;

import java.util.HashSet;

public abstract class Item implements Comparable<Item>{

	private final int cost;
	
	private static int idCount = 0;
	private final int id = idCount++; //ZUM TESTEN
	
	public Item(int cost) {
		this.cost = cost;
	}

	public int getCost() {
		return cost;
	}

	public int compareTo(Item i) {
		return Integer.compare(getCost(), i.getCost());
	}

	public int getId() {
		return id;
	}
	
	public String toString() {
		return "Id:"+getId()+", Cost:"+getCost();
	}

	public abstract int getSetValue(HashSet<? extends Item> set);
	
}
