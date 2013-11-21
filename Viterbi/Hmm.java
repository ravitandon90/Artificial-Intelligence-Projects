/** This is a template for an HMM class.  Fill in code for the
 * constructor and all of the methods.  Do not change the signature of
 * any of these, and do not add any other public fields, methods or
 * constructors (but of course it is okay to add private stuff).  All
 * public access to this class must be via the constructor and methods
 * specified here.
 */
public class Hmm {

	private int numberStates;
	private int numberOutputs;	
	private double[][] transitionProbabilities;
	private double[][] outputProbabilites;
	private int dummyStateIndex;
	
    /** Constructs an HMM from the given data.  The HMM will have
     * <tt>numStates</tt> possible states and <tt>numOutputs</tt>
     * possible outputs.  The HMM is then built from the given set of
     * state and output sequences.  In particular,
     * <tt>state[i][j]</tt> is the <tt>j</tt>-th element of the
     * <tt>i</tt>-th state sequence, and similarly for
     * <tt>output[i][j]</tt>.
     */
    public Hmm(int numStates, int numOutputs,
	       int state[][], int output[][]) {

	// your code here
    	this.numberStates = numStates + 1; // the extra 1 state is for the dummy state 
    	this.numberOutputs = numOutputs;    	
    	// The last state is the dummy state 
    	this.dummyStateIndex = this.numberStates - 1;    	
    	this.transitionProbabilities = new double[this.numberStates][this.numberStates];
    	this.outputProbabilites = new double[this.numberStates][this.numberOutputs];
    	buildProbabilities (state, output); 
 
    }
    
    private void buildProbabilities (int state[][], int output[][]){
    	int countR, countC, currentState;
    	// transition count stores the number of transitions from each state to the other states
    	int[][] transitionCount = new int[this.numberStates][this.numberStates];
    	int[][] outputProbabilitiesCount = new int[this.numberStates][this.numberOutputs];
    	int[] outputCount = new int[this.numberStates];
    	// transitionsState stores the number of transitions from each state 
    	int[] transitionsState = new int[this.numberStates];
    	int lastState;
    	for (countR = 0; countR < state.length; countR++){
    		// Every time a state is encountered we increment the transition count from the dummy state to 1
    		lastState = this.dummyStateIndex;
    		for (countC = 0; countC < state[countR].length; countC++){
    			currentState = state[countR][countC];
    			transitionCount[lastState][currentState] += 1;    			
    			outputProbabilitiesCount[currentState][output[countR][countC]] += 1;
    			transitionsState[lastState] += 1;
    			outputCount[currentState] += 1;
    			lastState = currentState;    			
    		}
    	}
    	updateTransitionProbabilities (transitionCount, transitionsState);
    	updateOutputProbabilities (outputProbabilitiesCount, outputCount);
    }
    
    private void updateOutputProbabilities (int[][] outputProbabilitesCount, int[] outputCount){
    	int countR, countC;
    	for (countR = 0; countR < this.numberStates - 1; countR++){
    		for (countC = 0; countC < this.numberOutputs; countC++){
    			this.outputProbabilites[countR][countC] = Math.log((((double)outputProbabilitesCount[countR][countC] + 1) 
    					/ (outputCount[countR] + this.numberOutputs)));    			
    		}
    	}
    }
    
    private void updateTransitionProbabilities (int [][] transitionCount, int[] transitionState){
    	int countR, countC;
    	for (countR = 0; countR < this.numberStates; countR++){
    		// Transitions to the dummy state are not counted
    		for (countC = 0; countC < this.numberStates - 1; countC++){
    			this.transitionProbabilities[countR][countC] =
    					Math.log((((double)transitionCount[countR][countC] + 1) / (transitionState[countR] + this.numberStates - 1)));
    		}
    	}
    }
    
    /** Returns the number of states in this HMM. */
    public int getNumStates() {

	// your code here
    	return this.numberStates;

    }

    /** Returns the number of output symbols for this HMM. */
    public int getNumOutputs() {
    	
	// your code here
    	return this.numberOutputs;

    }

    /** Returns the log probability assigned by this HMM to a
     * transition from the dummy start state to the given
     * <tt>state</tt>.
     */
    public double getLogStartProb(int state) {

	// your code here
    	return this.transitionProbabilities[this.dummyStateIndex][state];

    }

    /** Returns the log probability assigned by this HMM to a
     * transition from <tt>fromState</tt> to <tt>toState</tt>.
     */
    public double getLogTransProb(int fromState, int toState) {

	// your code here
    	return this.transitionProbabilities[fromState][toState];

    }

    /** Returns the log probability of <tt>state</tt> emitting
     * <tt>output</tt>.
     */
    public double getLogOutputProb(int state, int output) {

	// your code here
    	return this.outputProbabilites[state][output];

    }

}
