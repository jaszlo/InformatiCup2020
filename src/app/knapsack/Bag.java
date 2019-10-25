package app.knapsack;

import java.util.Collection;
import java.util.HashSet;

public class Bag<T extends Item> {
	
	private final int costCapacity;
	
	private HashSet<T> items;
	
	private int value;
	
	private int costSum;
	
	public Bag(int capacity, HashSet<T> items) {
		this.items = items;
		this.costCapacity = capacity;
		update();
	}
	
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
	
	public boolean addItem(T i) {
		if(i.getCost()+getCostSum() <= getCostCapacity()) {
			items.add(i);
			update();
			return true;
		}else 
			return false;
	}
	
	public void removeItem(T i) {
		items.remove(i);
		update();
	}
	
	public void removeItems(Collection<T> removal) {
		items.removeAll(removal);
		update();
	}
	
	public boolean hasSpaceFor(T item) {
		return item.getCost()+getCostSum() <= getCostCapacity();
	}
	
	public int getValue() {
		return value;
	}

	public int getCostCapacity() {
		return costCapacity;
	}

	public int getCostSum() {
		return costSum;
	}
	
	public HashSet<T> getItems(){
		return items;
	}
	
	public String toString() {
		String s = "Used capacity: "+getCostSum()+"/"+getCostCapacity()+"\n";
		s+="Value:"+getValue()+"\n";
		for(T i : items)
			s+=i+"\n";
		return s;
	}
	
}
