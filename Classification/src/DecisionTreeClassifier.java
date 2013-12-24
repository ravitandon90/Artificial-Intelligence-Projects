import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DecisionTreeClassifier implements Classifier {

	/** This is the implementation of the decision tree, 
	 * which stores the structure of the tree 
	 * for classification of the elements. 
	 * 
	 * @author ravitandon
	 *
	 */
	private class DecisionTree  {
		public DecisionTree decisionTreeLeft;
		public DecisionTree decisionTreeRight;
		public int attribute;
		public int classLabel;
		public boolean isSignificant;
		public boolean isLeafNode;
		
		public DecisionTree (){
			this.attribute = -1;
			this.decisionTreeLeft = null;
			this.decisionTreeRight = null;
			this.classLabel = -1;
			this.isLeafNode = false;
			this.isSignificant = false;
		}
	}
	
	/** Class that stores each example. 
	 * An example class consists of the attributes and their labels.
	 * Another member of the class is the class Label.
	 * 
	 * @author ravitandon
	 *
	 */
	private class Example {
		public int[] attributeLabels;
		public int classLabel;
	}
	
	private double[] chiSquareTable = {0.004, 0.02, 0.06, 0.15, 0.46, 1.07, 1.64, 2.71, 3.84, 6.64, 10.83};
	private double[] confidenceValue = {5, 10, 20, 30, 50, 70, 80, 90, 95, 99, 99.9};
	
	/** This function checks whether the current attribute is statistically significant or not.
	 *  The function takes deviation in classification using a given attribute and then
	 *  checks to see whether the deviation is statistically significant to validate 
	 *  the null hypothesis (whether the attribute is significant or not). 
	 * 
	 * @param deviation
	 * @param confidence
	 * @return
	 */
	private boolean isAttributeSignificant (double deviation, int confidence){
		return (deviation > this.chiSquareTable[confidence]);
	}
	// Name of the author
	private String author = "Ravi Tandon";
	// Description of the classifier
	private String description = "An implementation of the "
			+ "decision tree learninng algorithm.";
	
	// Root of the decision tree that performs the classification
	public DecisionTree decisionTreeRoot;

	// flag that stores whether pruning has to be enabled or not  
	public boolean enablePruning;
	
	/** This function takes in an example and returns the classification based on the tree constructed. 
	 * 
	 */
	public int predict(int[] ex) {
		return (searchTree (ex, this.decisionTreeRoot));
	}

	public String algorithmDescription() {
		return description;
	}

	public String author() {
		return author;
	}
	
	public DecisionTreeClassifier (DataSet d, boolean enablePruning){
		this.enablePruning = enablePruning;
		// initializing the decision tree root
		this.decisionTreeRoot = new DecisionTree();
		// building the initial set of examples
		Example[] examples = buildExamples (d);
		// building the list of attributes
		List <Integer> attributes = buildAttributes (d);
		// building the decision tree 
		buildDecisionTree (examples, attributes, null, this.decisionTreeRoot);
		// pruning the decision tree
		if (this.enablePruning)
			pruneDecisionTree (this.decisionTreeRoot, null);
		//printDecisionTree (this.decisionTreeRoot);
	}
	
	/** This function builds an array of Examples given a DataSet.  
	 * 
	 * @param d - DataSet
	 * @return examples - Example[], array of objects of the example class
	 */
	private Example[] buildExamples (DataSet d){
		Example[] examples = new Example[d.numTrainExs];
		for (int exampleIndex = 0; exampleIndex < d.numTrainExs; exampleIndex++){
			examples[exampleIndex] = new Example ();
			examples[exampleIndex].attributeLabels = new int[d.numAttrs];
			for (int attributeIndex = 0; attributeIndex < d.numAttrs; attributeIndex++){
				examples[exampleIndex].attributeLabels[attributeIndex] = d.trainEx[exampleIndex][attributeIndex];
				if (d.trainEx[exampleIndex][attributeIndex] != 0 && d.trainEx[exampleIndex][attributeIndex] != 1){					
					System.out.println (d.trainEx[exampleIndex][attributeIndex]);
					System.out.println ("Error");
					System.exit (-1);
				}
			}
			examples[exampleIndex].classLabel = d.trainLabel[exampleIndex];
		}
		return examples;
	}
	
	/** This function builds the a list of all the attributes.
	 * 
	 * @param d - DataSet
	 * @return listAttributes - list of all the attributes 
	 */
	private List <Integer> buildAttributes (DataSet d){
		List <Integer> listAttributes = new ArrayList <Integer> ();
		for (int count = 0; count < d.numAttrs; count++){
			listAttributes.add(count);
		}
		return listAttributes;
	}
	
	/** This method finds the class which has a majority 
	 * of the members within the examples array.
	 * 
	 * @param examples - set of training samples, 
	 * examples maps each element to its labeled class, 
	 * the elements may not have their true indices here  
	 * @return most_frequent_class - the class label which appears 
	 * most frequently in the set of examples 
	 */
	private int pluralityValue (Example[] examples){
		int count[] = new int[2];
		int numberExamples = examples.length;
		for (int i = 0; i < numberExamples; i++)
		    count[examples[i].classLabel]++;
		return (count[1] > count[0] ? 1 : 0);
	}
	
	/** This function builds the decision tree based on the training examples.  
	 * 
	 * @param examples
	 * @param attributes
	 * @param trainingLabels - training labels of all the examples 
	 * @param parent_examples - training labels of the parent examples
	 */
	private void buildDecisionTree (Example[] examples, List <Integer> attributes, Example[] parent_examples, DecisionTree node){
		// checking if the number of examples is zero
		if (examples.length == 0){
			if (parent_examples == null){
				System.out.println ("parent_examples when the set of examples are empty on the current node");
				System.exit(-1);
			}
			// if the examples are zero for the current node, the plurality value over the parent examples is taken
			node.classLabel = pluralityValue (parent_examples);
			node.isLeafNode = true; // marking the node as a leaf node
		}
		else if (checkClassification (examples)){ // checking if all the examples are labeled in the same class 
			node.classLabel = examples[0].classLabel; // for a homogeneous set of examples, the class label is set as it is
			node.isLeafNode = true; // marking the node as a leaf node
		}
		else if (attributes.size() == 0){ // if the attribute list is empty OR 
			node.classLabel = pluralityValue (examples); // the plurality value over the set of examples is used
			node.isLeafNode = true;
		} else {
			
			// finding the best attribute to split the node at the current node
			int attributeIndex = findBestAttribute (examples, attributes);
			if (this.enablePruning){
				// if pruning is enabled, then the class labels for each node are labeled while constructing the tree
				// in case this node is pruned, then the majority of the example is taken as the prediction of the label 
				// at this node, the plurality value over the set of examples is used
				node.classLabel = pluralityValue (examples); 
				double deviation = calculateDeviation (examples, attributeIndex);
				int confidenceIndex = 8;
				// checking whether the current node is significant, root node is assumed to be significant  	
				node.isSignificant = isAttributeSignificant (deviation, confidenceIndex) || (node == this.decisionTreeRoot);
			}
			// setting attribute in the current node
			node.attribute = attributeIndex;
			// Removing the attribute from the list of attributes
			attributes.remove(attributes.indexOf(attributeIndex));
			// initializing the right and left children of the current node
			node.decisionTreeLeft = new DecisionTree ();
			node.decisionTreeRight = new DecisionTree ();
			// iterating over the values to separate out the classes
			// extracting samples where attribute has value 0
			Example[][] newExamplesV = extractExamples (examples, attributeIndex);
			// building decision tree on the left children of the current node
			buildDecisionTree (newExamplesV[0],  attributes, examples, node.decisionTreeLeft);
			// building decision tree on the samples where attribute has value 1
			buildDecisionTree (newExamplesV[1],  attributes, examples, node.decisionTreeRight);	
		}
	}
	/** This function calculates the deviation at the given node for the given set of examples and the attribute.
	 * 
	 * @param example
	 * @param attributeIndex
	 * @return deviation
	 */
	private double calculateDeviation (Example[] example, int attributeIndex) {
		double deviation = 0.0, expectedLabelCount; // deviation variable
		int numberExamples = example.length; // total number of examples (p+n)
		int[] labelTypeCount = new int[2]; // (stores n, p)
		int[][] attributeSetCount = new int[2][2]; // (stores n0,p0, n1, p1)
		// iterating over the set of examples to get the count of all the label types over the different examples
		for(int sampleIndex = 0; sampleIndex < numberExamples; sampleIndex++){
			// counting the label type (stores n, p)
			labelTypeCount[example[sampleIndex].classLabel]++;
			// counting the attribute label type (stores n0,p0, n1, p1)
			try{
				attributeSetCount[example[sampleIndex].attributeLabels[attributeIndex]][example[sampleIndex].classLabel]++;
			} catch (Exception e){
				System.out.println(example[sampleIndex].attributeLabels[attributeIndex]);
				System.out.println(example[sampleIndex].classLabel);
				System.exit (-1);
			}
		}
		for (int attributeLabelType = 0; attributeLabelType < 2; attributeLabelType++){
			for (int labelType = 0; labelType < 2; labelType++){
				// expectedLabelCount is p^,n^
				expectedLabelCount = labelTypeCount[labelType] * 
						((double) (attributeSetCount[attributeLabelType][0] +
								attributeSetCount[attributeLabelType][1])
								/ (numberExamples));
				deviation += ((double)Math.pow((attributeSetCount[attributeLabelType][labelType] - expectedLabelCount), 2))
						/ (expectedLabelCount);
			}
		}
		return deviation;
	}

	/** This function prunes the current decision tree. 
	 * The algorithm searches for nodes which have both the children as leaf nodes (lastClassificationNode). 
	 * The algorithm then checks whether the information gain is significant. 
	 * This check is done at the time of constructing the decision tree. If the attribute is not significant 
	 * then the node is marked as a leaf node and the class label marked earlier is used.
	 * The algorithm is implemented as a depth first search.
	 * 
	 * @param node
	 * @param parentNode
	 */
	private void pruneDecisionTree (DecisionTree node, DecisionTree parentNode){
		if (node.isLeafNode) // a leaf node cannot be pruned 
			return;
		// checking whether the current node is the last classification node, only such nodes can be pruned 
		if (isLastClassificationNode (node)){
			if (!(node.isSignificant)){ // pruning if the current node is insignificant
				node.isLeafNode = true; // marking the current node as the leaf node
				// while constructing the tree each of the node's apparent class labels are marked - extra computation
				// saves space and re-computation while pruning
			} else { // if the node is significant node
				pruneDecisionTree (node.decisionTreeLeft, node);
				pruneDecisionTree (node.decisionTreeRight, node);
			}
		} else { // if the node is not the last classification node 
			pruneDecisionTree (node.decisionTreeLeft, node);
			pruneDecisionTree (node.decisionTreeRight, node);
			// checking again since the node could now become a 
			// "last classification node" after its children have been pruned
			if (isLastClassificationNode (node) && !(node.isSignificant))
				node.isLeafNode = true;
		}
	}
	/** This function checks whether the current node is a last classification node.  
	 *  A "last classification node" is a parent of children, both of which are leaf nodes.
	 * 
	 * @param node
	 * @return boolean - whether both the children are leaf nodes or not
	 */
	private boolean isLastClassificationNode (DecisionTree node){
		return ((node.decisionTreeLeft.isLeafNode) && (node.decisionTreeRight.isLeafNode));
	}
	
	private Example[][] extractExamples (Example[] examples, int attribute){
		int[] countLabel = new int[2], labelCount = new int[2]; 
		int label;
		Example[][] newExample = new Example[2][];
		for (int count = 0; count < examples.length; count++){
			countLabel[examples[count].attributeLabels[attribute]]++;
		}
		newExample[0] = new Example[countLabel[0]];
		newExample[1] = new Example[countLabel[1]];
		for (int count = 0; count < examples.length; count++){
			label = examples[count].attributeLabels[attribute];
			newExample[label][labelCount[label]] = examples[count];
			labelCount[label]++;
		}
		return newExample;
	}
	
	/** This function checks whether the labels of all the examples are similar or not. 
	 * This is the terminating condition for the decision tree.	
	 * 
	 * @param examples
	 * @return
	 */
	private boolean checkClassification (Example[] examples){
		int length = examples.length;
		if (length == 1)
			return true;
		for (int count = 1; count < length; count++){
			if (!(examples[count-1].classLabel == examples[count].classLabel)){
				return false;
			}
		}
		return true;
	}

	/** This function calculates the index of the attribute that has the highest entropy gain at the current node.
	 * 
	 * @param examples - set of examples remaining at the current node
	 * @param trainingLabels
	 * @param attributes
	 * @return bestAttributeIndex - the index of the attribute that gives the maximum entropy gain
	 */
	private int findBestAttribute (Example[] examples, List <Integer> attributes){
		int attributeIndex, bestAttributeIndex = -1;
		double maximumEntropyGain = Double.NEGATIVE_INFINITY, entropyGain;
		// iterating over all the attributes to calculate the entropy gain for each attribute 
		Iterator <Integer> attributeIterator = attributes.iterator();
		while (attributeIterator.hasNext()) {
			attributeIndex = attributeIterator.next();
			entropyGain = importanceCalculator (attributeIndex, examples);
			if (entropyGain > maximumEntropyGain){
				maximumEntropyGain = entropyGain;
				bestAttributeIndex = attributeIndex;
			} 
		}
		if (bestAttributeIndex == -1){
			System.out.println (attributes.size());
			System.out.println("Error Attribute -1");
			System.exit (-1);
		}
		return bestAttributeIndex;
	}
	
	/** This function calculates the importance of a given attribute, over a set of examples. 
	 * The importance of an attribute is interpreted as the entropy gain.  
	 *  
	 * @param attribute
	 * @param examples
	 * @param trainingLabels
	 * @return
	 */
	private double importanceCalculator (int attribute, Example[] examples){
		double initialEntropy, finalEntropy, entropyGain = 0.0; 
		initialEntropy = calculateInitialEntropy (examples);
		finalEntropy = calculateNewEntropy (attribute, examples);
		entropyGain = initialEntropy - finalEntropy;
		return entropyGain;
	}
	
	/** This function calculates the initial entropy for a set of initial training labels.
	 * 
	 * @param trainingLabels
	 * @return
	 */
	private double calculateInitialEntropy (Example[] examples){
		int count = 0, numberExamples = examples.length;
		double fraction;
		for (int sampleIndex = 0; sampleIndex < numberExamples; sampleIndex++){
			if (examples[sampleIndex].classLabel == 0)
				count++;
		}
		fraction = (double) (count) / numberExamples;
		return (entropyCalculator (fraction));
	}
	
	/** This function calculates the entropy for a set of sample examples, 
	 * classified by the attribute.
	 * 
	 * @param attribute
	 * @param examples
	 * @return entropy
	 */
	private double calculateNewEntropy (int attribute, Example[] examples){
		// total number of examples at the present node
		int numberExamples = examples.length;
		
		// count stores the number of examples, for each label and attribute pair
		int[][] count = new int[2][2]; // number of attribute values, number of label values
		// iterating over all the examples to count the number of examples with each pair of label and attribute value
		for (int sampleIndex = 0; sampleIndex < numberExamples; sampleIndex++){
		try {
			//System.out.println (examples[sampleIndex].attributeLabels[attribute]);
			count[examples[sampleIndex].attributeLabels[attribute]][examples[sampleIndex].classLabel]++;
		} catch (Exception e){
			System.out.println ("Attribute: " +  attribute + "value:" + examples[sampleIndex].attributeLabels[attribute]);
			System.out.println("Error: "+ e);
			System.exit(-1);
		}
		}
		// weight stores the number of examples for each value of the attribute
		int[] weight = new int[2];
		weight[0] = count[0][0] + count[0][1];
		weight[1] = count[1][0] + count[1][1];
		// fraction is the fraction of examples which have attribute labeled 0
		double[] fraction = new double[2];
		fraction[0] = weight[0] == 0 ? 0 : (double) (count[0][0])  / (weight[0]); 
		fraction[1] = weight[1] == 0 ? 0 : (double) (count[1][0])  / (weight[1]);
		
		double newEntropy;
		// entropy calculator calculates the entropy for classification by the given node 
		newEntropy =  ((double) (weight[0]) / numberExamples) * entropyCalculator (fraction[0]) + 
				((double) (weight[1]) / numberExamples) * entropyCalculator (fraction[1]);
		if (newEntropy != newEntropy){
			System.out.println("Nan");
			System.exit (-1);
		}
		return newEntropy;
	}
	
	/** Entropy calculator implements the standard entropy function.
	 *  Entropy = B (fraction), here.
	 * @param fraction
	 * @return entropy
	 */
	private double entropyCalculator (double fraction){
		if (fraction == 1 || fraction == 0)
			return 0;
		return (-1) * (
					(fraction * Math.log(fraction)/Math.log(2)) + 
					((1 - fraction) * Math.log(1 - fraction)/Math.log(2))
				);
	}
	
	/** This function takes in a specific example and classifies it according 
	 *  using the decision tree created. 
	 * 
	 * @param example
	 * @return
	 */
	private int searchTree (int[] example, DecisionTree node){
		if (!node.isLeafNode){ // checking if the current node is a leaf node, for leaf nodes the  
			if (example[node.attribute] == 0) // if the attribute of the current node 
				return (searchTree (example, node.decisionTreeLeft));
			return (searchTree (example, node.decisionTreeRight));
		} 
		// return the class label of the node if the node is a leaf node
		return node.classLabel;
	}
	
    /** A simple main for testing this algorithm.  This main reads a
     * filestem from the command line, runs the learning algorithm on
     * this dataset, and prints the test predictions to filestem.testout.
     */
    public static void main(String argv[])
	throws FileNotFoundException, IOException {

	if (argv.length < 1) {
	    System.err.println("argument: filestem");
	    return;
	}

	String filestem = argv[0];

	DataSet d = new DataSet(filestem);

	Classifier c = new DecisionTreeClassifier(d, true);
	d.printTestPredictions(c, filestem);
    }
    
    private void printDecisionTree (DecisionTree node){
    	if (node != null){
    		if (node.isLeafNode)
    			System.out.println ("Label:" + node.classLabel);
    		else { 
    			System.out.println ("Attribute:" + node.attribute);
    			printDecisionTree (node.decisionTreeLeft);
    			printDecisionTree (node.decisionTreeRight);
    		}
    	}
    }
}
