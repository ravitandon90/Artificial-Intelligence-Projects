/** This is a template for a Viterbi class, which can be used to
 * compute most likely sequences.  Fill in code for the constructor
 * and <tt>mostLikelySequence</tt> method.
 */
public class Viterbi {

    /** This is the constructor for this class, which takes as input a
     * given HMM with respect to which most likely sequences will be
     * computed.
     */
	// Stores the parent of a given state in the best path at a certain time
	int parentState[][]; // state, time
	// Stores the best possible value of the path to this state up to a given time 
	double bestPathValue[]; // state, time 
	Hmm hmm;
	int numberStates;
	
    public Viterbi(Hmm hmm) {

	// your code here
    	this.hmm = hmm;
    	this.numberStates = hmm.getNumStates();
    	return ;
    }

    /** Returns the most likely state sequence for the given
     * <tt>output</tt> sequence, i.e., the state sequence of highest
     * conditional probability given the output sequence, according to
     * the HMM that was provided to the constructor.  The returned
     * state sequence should have the same number of elements as the
     * given output sequence.
     */
    public int[] mostLikelySequence(int output[]) {
	// your code here
    	// Initializing the most likely sequence
    	int[] mostLikelySequence = new int[output.length];
    	// ignoring the dummy state, the parent state stores the parent of each state at different times 
    	this.parentState = new int[this.numberStates -1][output.length];
    	// the best path value stores the value of the best path up to the current state
    	this.bestPathValue = new double[this.numberStates - 1];
    	for (int time = 1; time <= output.length; time++){
    		updateColumn (output, time);
    	}
    	double maxValue = Double.NEGATIVE_INFINITY, currentValue; 
    	int currentValueIndex = 0;
    	for (int currentState = 0; currentState < this.hmm.getNumStates()-1; currentState++){
    		currentValue = this.bestPathValue[currentState];
    		if (currentValue > maxValue){ 
    			currentValueIndex = currentState;
    			maxValue = currentValue;
    		}
    	}
    	int optimalStateIndex = currentValueIndex;
    	for (int currentTimeIndex = output.length; currentTimeIndex > 0; currentTimeIndex-- ){
    		mostLikelySequence [currentTimeIndex - 1]  = optimalStateIndex;    		
    		optimalStateIndex = this.parentState[optimalStateIndex][currentTimeIndex - 1];
    	}
    	return mostLikelySequence;
    }
    
    public void updateColumn (int output[], int time){
    	int currentOutput = output[time -1], parentIndex;
    	double[] tempPathValue = new double [this.hmm.getNumStates()-1];
    	if (time == 1){
        	for (int currentState = 0; currentState < this.numberStates - 1;  currentState++){
        		this.bestPathValue[currentState] = (this.hmm.getLogStartProb(currentState)) 
        				+ (this.hmm.getLogOutputProb(currentState, currentOutput));        		
        	}    		
    	} else {
        	// Updating the column for each of the states 
    		for (int currentState = 0; currentState < this.numberStates - 1;  currentState++){
        		double maxValue = Double.NEGATIVE_INFINITY, pathValue;
        		parentIndex = 0;
        	// Calculating the maximum value to the current state        		
        		for (int prevState = 0; prevState < this.numberStates - 1; prevState++){
        			pathValue = this.bestPathValue[prevState] + 
        					this.hmm.getLogTransProb(prevState, currentState) + 
        					this.hmm.getLogOutputProb(currentState, currentOutput);
        			if (pathValue > maxValue){
        				maxValue = pathValue;
        				parentIndex = prevState;
        			}
        		}
        		tempPathValue [currentState] = maxValue;
        		this.parentState[currentState][time -1] = parentIndex;
        	}
    	// Updating the best path from the temporary path
    		for (int currentState = 0; currentState < this.numberStates -1; currentState++){
    			this.bestPathValue[currentState] = tempPathValue[currentState];
    		}
    	}
    }
    
    

}
