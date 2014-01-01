import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

/** This is an experiment class to test the Bagging Classifier.
 * 
 * @author ravitandon
 *
 */
public class Experiment1 {
	
	private int[] testLables;
	private int[] testLabelIndex;
	private double fraction;
	
	private DataSet buildTestDataSet(DataSet d) {
		// picking 10% of the training examples as the test examples
		this.fraction = 0.50;
		int testSetSize = (int) (this.fraction * d.numTrainExs);
		boolean[] isTest = new boolean [d.numTrainExs];
		int trainingSetSize = d.numTrainExs - testSetSize;
		this.testLables = new int[testSetSize];
		this.testLabelIndex = new int[testSetSize];
		DataSet testDataSet = new DataSet ();
		testDataSet.numAttrs = d.numAttrs;
		testDataSet.numTrainExs = trainingSetSize;
		testDataSet.numTestExs = testSetSize;
		testDataSet.trainEx = new int[trainingSetSize][d.numAttrs];
		testDataSet.trainLabel = new int[trainingSetSize];
		testDataSet.testEx = new int[testSetSize][d.numAttrs];
		Random random = new Random();
		int randomIndex;
		for(int testIndex = 0; testIndex < testSetSize; testIndex++){
			while (true){
				randomIndex = random.nextInt(d.numTrainExs);
				if (!isTest[randomIndex])
					break;
			}
			isTest[randomIndex] = true;
			for (int attributeIndex = 0; attributeIndex < d.numAttrs; attributeIndex++)
				testDataSet.testEx[testIndex][attributeIndex] = d.trainEx[randomIndex][attributeIndex];
			this.testLables[testIndex] = d.trainLabel[randomIndex];
			this.testLabelIndex[testIndex] = randomIndex;
		}
		int trainIndexCount = 0;
		for (int sampleIndex = 0; sampleIndex < d.numTrainExs; sampleIndex++){
			if (isTest[sampleIndex])
				continue;
			for (int attributeIndex = 0; attributeIndex < d.numAttrs; attributeIndex++){
				testDataSet.trainEx[trainIndexCount][attributeIndex] = d.trainEx[sampleIndex][attributeIndex];
			if (testDataSet.trainEx[trainIndexCount][attributeIndex] != 0 && 
					testDataSet.trainEx[trainIndexCount][attributeIndex] != 1){
				System.out.println ("Error");
				System.exit(-1);
			}
			}
			testDataSet.trainLabel[trainIndexCount] = d.trainLabel[sampleIndex];
			trainIndexCount++;
		}
		return testDataSet;
	}
	
	/** This function gets the error estimate for the current constructed decision tree.
	 * 
	 * @param decisionTree
	 * @param newDataSet
	 * @param oldDataSet
	 * @param fold
	 * @return
	 */
	private double getErrorEstimate (Classifier c, DataSet d, int[] trueLabels){
		double error = 0.0;  // error estimate 
		int errorCount = 0; // error counter variable
		for (int sampleIndex = 0; sampleIndex < d.numTestExs; sampleIndex++){
			// checking whether the prediction is correct or not
			if (!(c.predict(d.testEx[sampleIndex]) == trueLabels[sampleIndex]))
				errorCount++;
		}
		// getting the estimate of the error count, foldSize is the number of test examples
		error = (double)errorCount / d.numTestExs;
		return error;
	}
	
	/** A simple main for testing the bagging classifier algorithm.  This main reads a
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
	Experiment1 e = new Experiment1();
	DataSet od = new BinaryDataSet(filestem);
	DataSet nd = e.buildTestDataSet(od);
	double totalImprovement  = 0.0;
	int totalRounds = 100;
	for (int rounds = 0; rounds < totalRounds; rounds++){
	Classifier c = new DecisionTreeClassifier(nd, false);
	Classifier c1 = new DecisionTreeClassifier (nd, true);
	double e1 = e.getErrorEstimate (c, nd, e.testLables);
	double e2 = e.getErrorEstimate (c1, nd, e.testLables);
	double improvement = (-e2+e1)/(e1) *  100;
	totalImprovement +=  improvement;
	}
	System.out.println (totalImprovement / totalRounds);
	//System.out.println ("DecisionTreeClassifier, No Pruning: Error Estimate:" +  e1);
	//System.out.println ("DecisionTreeClassifier, With Pruning: Error Estimate:" +  e2);
    }
    
    private void checkLabels (DataSet d, DataSet nd){
    	int testLabelInd;
    	for (int testIndex = 0; testIndex < nd.numTestExs; testIndex++){
    		testLabelInd = this.testLabelIndex[testIndex];	
    		if (d.trainLabel[testLabelInd] != this.testLables[testIndex]){
	    				
    					System.out.println ("Error");
	    				System.exit(-1);
	    		}
    	}
    	System.out.println ("Test Passed !!");
    }
}
