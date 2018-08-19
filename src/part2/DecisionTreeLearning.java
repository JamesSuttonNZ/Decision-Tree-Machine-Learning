package part2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import part2.Instance;

public class DecisionTreeLearning {
	
	public static List<String> categories = new ArrayList<String>();
	public static List<Instance> trainingInstances = new ArrayList<Instance>();
	public static List<Instance> testInstances = new ArrayList<Instance>();
	public static List<String> attributes = new ArrayList<String>();
	public static int numCategories;
	public static int numAtts;

	public static void main(String[] args){
		
		readData(args);
		
		List<String> attr = new ArrayList<String>(attributes);
		
		DecisionTree dt = new DecisionTree(buildTree(trainingInstances, attr));
		
		double correctC1 = 0;
		double correctC2 = 0;
		double c1 = 0;
		double c2 = 0;
		for(Instance i : testInstances){
			Node n = dt.getRoot();
			while(n.getLeft() != null && n.getRight() != null){
				int index = attributes.indexOf(n.getAttribute());
				boolean b = i.getAttributes().get(index);
				if(b){
					n = n.getLeft();
				}else{
					n = n.getRight();
				}
			}
			if(n.getCategory().equals(categories.get(i.getCategory()))){
				if(i.getCategory() == 0){
					correctC1++;
				}else if(i.getCategory() == 1){
					correctC2++;
				}
			}
			if(i.getCategory() == 0){
				c1++;
			}else if(i.getCategory() == 1){
				c2++;
			}
		}
		double dtAccuracy = ((correctC1+correctC2)/(c1+c2))*100;
		String category = "";
		if(c1 == c2){
			Random r = new Random();
			int index = r.nextInt(testInstances.size());
			int m = testInstances.get(index).getCategory();
			category = categories.get(m);
		}else if(c1 > c2){
			category = categories.get(0);
		}else{
			category = categories.get(1);
		}
		double bAccuracy = 0;
		if(categories.indexOf(category) == 0){
			bAccuracy = ((c1)/(c1+c2))*100;
		}else{
			bAccuracy = ((c2)/(c1+c2))*100;
		}
		System.out.println(categories.get(0)+": "+correctC1+" correct out of "+c1);
		System.out.println(categories.get(1)+": "+correctC2+" correct out of "+c2+"\n");
		System.out.println("Accuracy:");
		System.out.println("Decision Tree Accuracy: "+dtAccuracy+"%");
		System.out.println("Baseline Accuracy ("+category+"): "+bAccuracy+"%\n");
		
		System.out.println("Decision Tree constructed:");
		printTree(dt.getRoot(), "");
	}
	
	private static void printTree(Node n, String indent) {
		Node left = n.getLeft();
		Node right = n.getRight();
		if(left == null && right == null){
			System.out.println(indent+"Class "+n.getCategory()+", prob = "+n.getProbability());
		}else{
			System.out.println(indent+n.getAttribute()+" = True:");
			printTree(left,indent+"\t");
			System.out.println(indent+n.getAttribute()+" = False:");
			printTree(right,indent+"\t");
		}
	}

	/**
	 * Builds decision tree using training data
	 * @param inst
	 * @param attr
	 * @return
	 */
	private static Node buildTree(List<Instance> inst, List<String> attr) {
		if(inst.isEmpty()){
			Node n = new Node(null,null,null);
			double c1 = 0;
			double c2 = 0;
			for(Instance l : trainingInstances){
				if(l.getCategory() == 0){
					c1++;
				}
				if(l.getCategory() == 1){
					c2++;
				}
			}
			double c = Math.max(c1, c2);
			n.setProbability(c/trainingInstances.size());
			if(c1 == c2){
				Random r = new Random();
				int index = r.nextInt(trainingInstances.size());
				int m = trainingInstances.get(index).getCategory();
				n.setCategory(categories.get(m));
				return n;
			}else if(c1 > c2){
				n.setCategory(categories.get(0));
				return n;
			}else{
				n.setCategory(categories.get(1));
				return n;
			}
		}
		int count = 0;
		for(Instance j : inst){
			count += j.getCategory();
		}
		if(count == 0 || count == inst.size()){
			Node n = new Node(null,null,null);
			n.setCategory(categories.get(inst.get(0).getCategory()));
			n.setProbability(1);
			return n;
		}
		if(attr.isEmpty()){
			Node n = new Node(null,null,null);
			double c1 = 0;
			double c2 = 0;
			for(Instance k : inst){
				if(k.getCategory() == 0){
					c1++;
				}
				if(k.getCategory() == 1){
					c2++;
				}
			}
			double c = Math.max(c1, c2);
			n.setProbability(c/inst.size());
			if(c1 == c2){
				Random r = new Random();
				int index = r.nextInt(inst.size());
				int m = inst.get(index).getCategory();
				n.setCategory(categories.get(m));
				return n;
			}else if(c1 > c2){
				n.setCategory(categories.get(0));
				return n;
			}else{
				n.setCategory(categories.get(1));
				return n;
			}
		}else{
			String bestAttr = attr.get(0);
			ArrayList<Instance> bestTrueInst = new ArrayList<Instance>();
			ArrayList<Instance> bestFalseInst = new ArrayList<Instance>();
			double bestPurity = Double.MAX_VALUE;
			for(int i = 0; i < attr.size(); i++){
				ArrayList<Instance> trueInst = new ArrayList<Instance>();
				ArrayList<Instance> falseInst = new ArrayList<Instance>();
				double c1T = 0;
				double c2T = 0;
				double c1F = 0;
				double c2F = 0;
				for(Instance in : inst){
					if(in.getAttributes().get(i) == true){
						trueInst.add(in);
						if(in.getCategory() == 0){
							c1T++;
						}else if(in.getCategory() == 1){
							c2T++;
						}
					}else if(in.getAttributes().get(i) == false){
						falseInst.add(in);
						if(in.getCategory() == 0){
							c1F++;
						}else if(in.getCategory() == 1){
							c2F++;
						}
					}
				}
				double purity = trueInst.size()/inst.size()*(c1T/trueInst.size()*c2T/trueInst.size()) + falseInst.size()/inst.size()*(c1F/falseInst.size()*c2F/falseInst.size());
				if(purity < bestPurity){
					bestAttr = attr.get(i);
					bestTrueInst = trueInst;
					bestFalseInst = falseInst;
				}
			}
			attr.remove(bestAttr);
			Node left = buildTree(bestTrueInst, attr);
			Node right = buildTree(bestFalseInst, attr);
			return new Node(left,right,bestAttr);
		}
		
	}

	/**
	 * read in data from files
	 * @param args
	 */
	private static void readData(String[] args) {
		
		try {
            File file = new File(args[0]);
            System.out.println("Reading training data from file "+args[0]);
            Scanner input = new Scanner(file);

            for (Scanner s = new Scanner(input.nextLine()); s.hasNext();) categories.add(s.next());
            numCategories=categories.size();
            System.out.println(numCategories +" categories");
            
            for (Scanner s = new Scanner(input.nextLine()); s.hasNext();) attributes.add(s.next());
            numAtts = attributes.size();
            System.out.println(numAtts +" attributes");
            
            while (input.hasNext()){ 
      	      Scanner line = new Scanner(input.nextLine());
      	      trainingInstances.add(new Instance(categories.indexOf(line.next()),line));
      	    }
            System.out.println("Read "+trainingInstances.size() +" instances");
            input.close();
          }
         catch (IOException e) {
           throw new RuntimeException("Data File caused IO exception");
         }
		
		try {
            File file = new File(args[1]);
            System.out.println("Reading test data from file "+args[1]);
            Scanner input = new Scanner(file);

            input.nextLine();
            input.nextLine();
            
            while (input.hasNext()){ 
    	      Scanner line = new Scanner(input.nextLine());
    	      testInstances.add(new Instance(categories.indexOf(line.next()),line));
        	}
            System.out.println("Read "+testInstances.size() +" instances");
            input.close();
          }
         catch (IOException e) {
           throw new RuntimeException("Data File caused IO exception");
         }
	}
	
}
