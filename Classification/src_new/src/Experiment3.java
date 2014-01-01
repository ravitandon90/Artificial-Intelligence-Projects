import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/** This is an experiment class to test the Bagging Classifier.
 * 
 * @author ravitandon
 *
 */
public class Experiment3 {
	
	private int[] testLables;
	private int[] testLabelIndex;
	public double fraction = 0.1;
	
	public DataSet buildTestDataSet(DataSet d) {
		// picking 10% of the training examples as the test examples
		int testSetSize = (int) (this.fraction * d.numTrainExs);
		boolean[] isTest = new boolean [d.numTrainExs];
		int trainingSetSize = d.numTrainExs - testSetSize;
		this.testLables = new int[testSetSize];
		this.testLabelIndex = new int[testSetSize];
		DataSet testDataSet = new DataSet ();
		testDataSet.numAttrs = d.numAttrs;
		testDataSet.numTrainExs = trainingSetSize;
		//System.out.print(trainingSetSize + "," + testSetSize);
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
	
	/** This function gets the error estimate for the current constructed decision tree.
	 * 
	 * @param decisionTree
	 * @param newDataSet
	 * @param oldDataSet
	 * @param fold
	 * @return
	 */
	private double getErrorEstimateTrain (Classifier c, DataSet d, int[] trueLabels){
		double error = 0.0;  // error estimate 
		int errorCount = 0; // error counter variable
		for (int sampleIndex = 0; sampleIndex < d.numTrainExs; sampleIndex++){
			// checking whether the prediction is correct or not
			if (!(c.predict(d.trainEx[sampleIndex]) == d.trainLabel[sampleIndex]))
				errorCount++;
		}
		// getting the estimate of the error count, foldSize is the number of test examples
		error = (double)errorCount / d.numTrainExs;
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
	Experiment3 e = new Experiment3();
	DataSet od = new BinaryDataSet(filestem);
	double fraction = 0.9; // 90% of the data is used as test data
	double errorEstimateDecisionTree, errorEstimateTreeStump, 
		errorEstimateCrossValidation, errorEstimateBagging,
		errorEstimateAdaBoost;
	try{
	PrintWriter writer = new PrintWriter("experimentalResults/learningCurveTreeComparison_census.txt", "UTF-8");
	for (fraction = 0.99; fraction > 0; fraction = fraction - 0.01){
		e.fraction = fraction;
		DataSet nd = e.buildTestDataSet(od);
		Classifier cdST = new DecisionTreeClassifier(nd, true);
		Classifier decisionStump = new DecisionStumpClassifierTreeModel (nd);
		//Classifier crossValidation = new CrossValidationClassifier (nd);
		//Classifier baggingClassifier = new BaggingClassifier (nd);
		//Classifier adaBoostClassifier = new AdaBoostClassifier (nd);
		errorEstimateDecisionTree = 1- e.getErrorEstimate (cdST, nd, e.testLables);
		errorEstimateTreeStump = 1- e.getErrorEstimate (decisionStump, nd, e.testLables);
		//errorEstimateCrossValidation =  1- e.getErrorEstimate (crossValidation, nd, e.testLables);
		//errorEstimateBagging =  1- e.getErrorEstimate (baggingClassifier, nd, e.testLables);
		//errorEstimateAdaBoost =  1- e.getErrorEstimate (adaBoostClassifier, nd, e.testLables);
		writer.println((1-fraction)*100 + "," + errorEstimateTreeStump + "," + errorEstimateDecisionTree);	
	}
	writer.close();
	} catch (Exception ex){
		System.out.print(ex);
	}
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
