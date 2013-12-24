
/** This is the implementation of the cross validation class.
 *  The cross validation class is a wrapper around the decision tree class.
 * 
 * @author ravitandon
 *
 */
public class CrossValidationClassifier implements Classifier {
	
	private String description = "Implementation of the Cross Validation Classifier. "
			+ "It uses decision tree classifier as the base learning algorithm.";
	private String author = "Ravi Tandon";
	private int folding = 5; // value of the folding parameter to implement the cross validation
	private int foldSize; // foldsize is the size of the number of examples that are used as test examples
	public Classifier bestClassifier; // stores the best decision tree based on k-fold cross validation
	
	/** The constructor for the cross validation class.
	 *  The class builds the constructs the best decision tree based on the 5-fold validation principle. 
	 *  
	 * @param dataSet - Original DataSet
	 */
	public CrossValidationClassifier (DataSet dataSet){
		this.foldSize = dataSet.numTrainExs / this.folding;
		double minClassificationError = Double.POSITIVE_INFINITY, classificationError;
		for (int currentFold = 0; currentFold < this.folding; currentFold++){
			DataSet newDataSet = buildDataSet (dataSet, currentFold);
			Classifier classifier = new DecisionTreeClassifier (newDataSet, true);
			classificationError = getErrorEstimate (classifier, dataSet, currentFold);
			if (classificationError < minClassificationError){
				minClassificationError = classificationError;
				this.bestClassifier = classifier;
			}
		}
	}
	
	/** This function gets the error estimate for the current constructed decision tree.
	 * 
	 * @param decisionTree
	 * @param newDataSet
	 * @param oldDataSet
	 * @param fold
	 * @return
	 */
	private double getErrorEstimate (Classifier classifier, DataSet oldDataSet, int fold){
		double error = 0.0;  // error estimate 
		int errorCount = 0; // error counter variable
		// start and the final indices of the training examples that are used as test examples
		int startIndex = fold * this.foldSize, finalIndex = startIndex + this.foldSize - 1; 
		for (int sampleIndex = startIndex; sampleIndex <= finalIndex; sampleIndex++){
			// checking whether the prediction is correct or not
			if (!(classifier.predict(oldDataSet.trainEx[sampleIndex]) == oldDataSet.trainLabel[sampleIndex]))
				errorCount++;
		}
		// getting the estimate of the error count, foldSize is the number of test examples
		error = errorCount / this.foldSize;
		return error;
	}
	
	/** This method copies the data in the current "fold" from the origin data set to the current data set.
	 *  This function builds the test and the training examples.
	 * 
	 * @param originalDataSet
	 * @param fold
	 * @param newDataSet
	 */
	public DataSet buildDataSet (DataSet originalDataSet, int fold){
		DataSet newDataSet = new DataSet ();
		int numberTrainEx = newDataSet.numTrainExs = originalDataSet.numTrainExs - this.foldSize;
		newDataSet.numAttrs = originalDataSet.numAttrs;
		newDataSet.numTestExs = this.foldSize;
		
		newDataSet.trainLabel = new int[numberTrainEx];
		newDataSet.trainEx = new int[numberTrainEx][originalDataSet.numAttrs];
		newDataSet.testEx = new int[this.foldSize][originalDataSet.numAttrs];
		
		int startIndex = fold * this.foldSize, finalIndex = startIndex + this.foldSize -1, 
				sampleIndex = 0, trainIndex = 0;
		
		while (sampleIndex < originalDataSet.numTrainExs){			
			if (sampleIndex == startIndex)
				sampleIndex = finalIndex + 1;
			 else {
				for (int attributeIndex = 0; attributeIndex < originalDataSet.numAttrs; attributeIndex++){
					newDataSet.trainEx[trainIndex][attributeIndex] = originalDataSet.trainEx[sampleIndex][attributeIndex];
					if (newDataSet.trainEx[trainIndex][attributeIndex] != 0 && newDataSet.trainEx[trainIndex][attributeIndex] != 1){					
						System.out.println (newDataSet.trainEx[trainIndex][attributeIndex]);
						System.out.println (originalDataSet.trainEx[sampleIndex][attributeIndex]);
						System.out.println ("Error ....");
						System.exit (-1);
					}
				}
				newDataSet.trainLabel[trainIndex] = originalDataSet.trainLabel[sampleIndex];
				trainIndex++; 
				sampleIndex++;
			}
		}
		return newDataSet;
	}
	
	@Override
	public int predict(int[] ex) {
		return this.bestClassifier.predict(ex);
	}

	@Override
	public String algorithmDescription() {
		return this.description;
	}

	@Override
	public String author() {
		return this.author;
	}
	
}
