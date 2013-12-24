import java.io.FileNotFoundException;
import java.io.IOException;


public class AdaBoostClassifier implements Classifier {

	private double[] weights;
	private int numberRounds;
	private boolean[][] classifiedCorrectly;
	private int[] hypothesis;
	private double[] alpha;
	DecisionStumpClassifier[] dSC;
	private String author = "Ravi Tandon";
	private String description = "Implementation of Adaboost, "
			+ "using decision stump as weak learning algorithm.";
		
	public AdaBoostClassifier (DataSet dataSet){
		this.numberRounds = 50;
		double error;
		this.dSC = new DecisionStumpClassifier[dataSet.numAttrs];
		this.weights = new double[dataSet.numTrainExs];
		initWeights(dataSet.numTrainExs);
		this.classifiedCorrectly = new boolean[dataSet.numAttrs][dataSet.numTrainExs];
		this.alpha = new double[this.numberRounds];
		this.hypothesis = new int[this.numberRounds];
		for (int attributeIndex = 0; attributeIndex < dataSet.numAttrs; attributeIndex++){
			this.dSC[attributeIndex] = new DecisionStumpClassifier (dataSet, attributeIndex); 
			for (int sampleIndex = 0; sampleIndex < dataSet.numTrainExs; sampleIndex++){
				classifiedCorrectly[attributeIndex][sampleIndex] = 
						(this.dSC[attributeIndex].predict(dataSet.trainEx[sampleIndex]) == dataSet.trainLabel[sampleIndex]);
			}
		}
		for (int currentRound = 0; currentRound < this.numberRounds; currentRound++){
			error = getMinimumWeightedError(dataSet.numTrainExs, dataSet.numAttrs, currentRound);
			this.alpha[currentRound] = 0.5 * Math.log((1 - error) / (error));
			updateWeights (dataSet.numTrainExs, error, 
					this.classifiedCorrectly[this.hypothesis[currentRound]], this.alpha[currentRound]);
		}
	}
	
	private void initWeights(int numSamples){
		double initWeight = ((double) 1.0)/numSamples;
		for (int sampleIndex = 0; sampleIndex < numSamples; sampleIndex++)
			this.weights[sampleIndex] = initWeight;
	}
	
	private void updateWeights (int numSamples, double error, boolean[] classficationCorrect, double alpha){
		double sum = 0.0;
		for (int sampleIndex = 0; sampleIndex < numSamples; sampleIndex++){
			if (classficationCorrect[sampleIndex])
				this.weights[sampleIndex] *= Math.pow(Math.E, -alpha);
			else 
				this.weights[sampleIndex] *= Math.pow(Math.E, alpha);
			sum += this.weights[sampleIndex]; 
		}
		for (int sampleIndex = 0; sampleIndex < numSamples; sampleIndex++)
			this.weights[sampleIndex] /= sum;
	}
	
	private double getMinimumWeightedError(int numSamples, int numAttrs, int currentRound){
		double error = 0.0, minError = Double.POSITIVE_INFINITY;
		int chosenHypothesis = -1;
		for (int attributeIndex = 0; attributeIndex < numAttrs; attributeIndex++){
			error = 0.0;
				for (int sampleIndex = 0; sampleIndex < numSamples; sampleIndex++){
						error += (this.classifiedCorrectly[attributeIndex][sampleIndex]) ? 0 : this.weights[sampleIndex];
			}
				if (error < minError){
					minError = error;
					chosenHypothesis = attributeIndex;
				}
		}
		this.hypothesis[currentRound] = chosenHypothesis;
		return minError;
	}
	
	@Override
	public int predict(int[] ex) {
		// TODO Auto-generated method stub
		double sum = 0.0; 
		int prediction;
		for (int currentRound = 0; currentRound < this.numberRounds; currentRound++){
			if (this.dSC[this.hypothesis[currentRound]].predict(ex) == 0)
				prediction = -1;
			else 
				prediction = 1;
			sum += this.alpha[currentRound] * prediction; 
		}
		if (sum > 0)
			return 1;
		return 0;
	}

	@Override
	public String algorithmDescription() {
		// TODO Auto-generated method stub
		return description;
	}

	@Override
	public String author() {
		// TODO Auto-generated method stub
		return author;
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

	Classifier c = new AdaBoostClassifier(d);
	
	d.printTestPredictions(c, filestem);
    }
    

}
