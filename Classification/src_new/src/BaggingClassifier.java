import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;


public class BaggingClassifier implements Classifier {
	
	public int bootStrapRounds = 20;
	private int bootStrapSize;
	private Classifier[] classifierSet ;
	private String description = "This algorithm is the implementation "
			+ "of the bootstrap aggregation algorithm.";
	private String author = "Ravi Tandon";			
	
	/** This is the constructor for the bagging classifier class.
	 *  This constructs a bootstrap sample 
	 * 
	 * @param dataSet
	 */
	public BaggingClassifier (DataSet dataSet){
		// setting the bootstrap size to the number of the training examples 
		this.bootStrapSize = dataSet.numTrainExs;
		// Array of the classifier, used for getting a majority vote for classification
		this.classifierSet = new Classifier [this.bootStrapRounds]; 
		// iterating over the rounds to build the bootstrap dataSet and get a classifier on it
		for (int currentRound = 0; currentRound < this.bootStrapRounds; currentRound++){
			DataSet bootStrapDataSet = buildBootStrapDataSet (dataSet);	
			//CrossValidationClassifier c = new CrossValidationClassifier (bootStrapDataSet);	
			//classifierSet[currentRound] = c.bestDecisionTree;
			classifierSet[currentRound] = new CrossValidationClassifier (bootStrapDataSet);
		}
	}
	
	/** This is the constructor for the bagging classifier class.
	 *  This constructs a bootstrap sample 
	 * 
	 * @param dataSet
	 */
	public BaggingClassifier (DataSet dataSet, int rounds){
		this.bootStrapRounds = rounds;
		// setting the bootstrap size to the number of the training examples 
		this.bootStrapSize = dataSet.numTrainExs;
		// Array of the classifier, used for getting a majority vote for classification
		this.classifierSet = new Classifier [this.bootStrapRounds]; 
		// iterating over the rounds to build the bootstrap dataSet and get a classifier on it
		for (int currentRound = 0; currentRound < this.bootStrapRounds; currentRound++){
			DataSet bootStrapDataSet = buildBootStrapDataSet (dataSet);	
			//CrossValidationClassifier c = new CrossValidationClassifier (bootStrapDataSet);	
			//classifierSet[currentRound] = c.bestDecisionTree;
			classifierSet[currentRound] = new CrossValidationClassifier (bootStrapDataSet);
		}
	}

	/** This function builds the Boot Strap Data Set. 
	 * It selects the set by selecting a random element from the original data set using a uniform distribution.
	 *    
	 * 
	 * @param dataSet - Original DataSet from which sampling is to be done.
	 * @return newDataSet - New constructed dataSet (the boot strap data set)
	 */
	private DataSet buildBootStrapDataSet (DataSet dataSet){
		DataSet newDataSet = new DataSet ();
		newDataSet.numTrainExs = this.bootStrapSize;
		newDataSet.trainEx = new int[newDataSet.numTrainExs][dataSet.numAttrs];
		newDataSet.trainLabel = new int[newDataSet.numTrainExs];
		newDataSet.numAttrs = dataSet.numAttrs;
		int dataSetSize = dataSet.numTrainExs, randomExampleIndex;
		Random random = new Random();
		for (int exampleIndex = 0; exampleIndex < this.bootStrapSize; exampleIndex++){
			// selecting a random sample from the original data set
			randomExampleIndex = random.nextInt(dataSetSize);
			// copying the randomly selected sample from the original to the new data set 
			for (int attributeIndex = 0; attributeIndex < dataSet.numAttrs; attributeIndex++)
				newDataSet.trainEx[exampleIndex][attributeIndex] = dataSet.trainEx[randomExampleIndex][attributeIndex];
			newDataSet.trainLabel[exampleIndex] = dataSet.trainLabel[randomExampleIndex];	
			
		}
		return newDataSet;
	}
	
	@Override
	public int predict(int[] ex) {
		int[] classificationCount = new int[2];
		for (int currentRound = 0; currentRound < this.bootStrapRounds; currentRound++){
			classificationCount[this.classifierSet[currentRound].predict(ex)]++;
		}
		return ((classificationCount[0] > classificationCount[1]) ? 0: 1);
	}

	@Override
	public String algorithmDescription() {
		return this.description;
	}

	@Override
	public String author() {
		return this.author;
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

	DataSet d = new BinaryDataSet(filestem);

	Classifier c = new BaggingClassifier(d);
	d.printTestPredictions(c, filestem);
    }

}
