package part2;

import java.util.ArrayList;
import java.util.Scanner;

public class Instance {

	private int category;
	private ArrayList<Boolean> attributes = new ArrayList<Boolean>();
	
	public Instance(int category, Scanner s){
		this.category = category;
		while (s.hasNextBoolean()) attributes.add(s.nextBoolean());
	}
	
	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public ArrayList<Boolean> getAttributes() {
		return attributes;
	}

	public void setAttributes(ArrayList<Boolean> attributes) {
		this.attributes = attributes;
	}
	
}
