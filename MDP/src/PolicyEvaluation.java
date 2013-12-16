/**
 * This is the template of a class that evaluates a given policy,
 * i.e., computes the utility of each state when actions are chosen
 * according to it.  The utility is returned in the public
 * <tt>utility</tt> field.  You need to fill in the constructor.  You
 * may wish to add other fields with other useful information that you
 * want this class to return (for instance, number of iterations
 * before convergence).  You also may add other constructors or
 * methods, provided that these are in addition to the one given below
 * (which is the one that will be automatically tested).  In
 * particular, your code must work properly when run with the
 * <tt>main</tt> provided in <tt>RunCatMouse.java</tt>.
 */
public class PolicyEvaluation {

    /** the computed utility of each state under the given policy */
    public double utility[];
    
    /**
     * The constructor for this class.  Computes the utility of policy
     * <tt>pi</tt> for the given <tt>mdp</tt> with given
     * <tt>discount</tt> factor, and stores the answer in
     * <tt>utility</tt>.
     */
    public PolicyEvaluation(Mdp mdp, double discount, int pi[]) {    	
	// your code here
    	// Initializing the utility vector 
    	this.utility = new double[mdp.numStates];
    	// Updating the utility vector through the standard policy evaluation method 
    	policyEvaluationStandard (pi, mdp, discount);    	
    }
    
    /** This function implements the standard policy evaluation. 
     * The function keeps updating the policy estimates until the change in utility estimates becomes very small.  
     * The function internally implements value iteration.
     * 
     * @param policy
     * @param utility
     * @param mdp
     * @return void
     */
    private void policyEvaluationStandard (int[] policy, Mdp mdp, double discount){
    	// total number of states in the mdp 
    	int numStates = mdp.numStates, currentState, action;
    	// oldUtility stores the utility estimates of the previous iteration
    	double oldUtility[] = new double[numStates];
    	double delta = Math.pow(10, -13) * (1 - discount) / discount, difference, maxDifference;
    	while (true){
    		// setting maximum difference = 0 
    		maxDifference = 0;
    		// Updating the old utility vector (utility vector from previous iteration) using the current utility calculated
    		for (currentState = 0; currentState < numStates; currentState++){
    			oldUtility[currentState] = this.utility[currentState]; 
    		}
    		for (currentState = 0; currentState < numStates; currentState++){	    		
	    			action = policy[currentState];
    			// updating the current utility (for the given action) for the current state
	    		this.utility[currentState] = mdp.reward[currentState] + (discount) * 
	    				calculateUtilityAction (currentState, mdp, action);
	    		// Calculating the change in the utility for the current state
	    		difference = Math.abs(this.utility[currentState] - oldUtility[currentState]);
	    		// Calculating if the change in the current state is the maximum change
	    		if (difference > maxDifference){
	    			maxDifference = difference;
	    		}	    		
	    	}
	    	// Checking if the maximum change in the current iteration over all the nodes 
	    	// is smaller than the minimal required change. If, the change is very small,
	    	// the loop is broken (we have arrived at the optimal policy).
	    	if (delta > maxDifference){
	      		break;
	    	}
    	}
    }
    
    /**
     * This function calculates the total utility of that the given action provides from a given state, 
     * for a given utility estimate.
     * @param currentState
     * @param mdp
     * @param action
     * @param utility
     * @return utilitySum
     */
    private double calculateUtilityAction (int currentState, Mdp mdp, int action){
    	// Initializing the utility sum, for the current state
    	double utilitySum = 0.0;
    	// numDestinationStates is the total number of states reachable from the current state for the given action 
    	int numDestinationStates = mdp.nextState[currentState][action].length, destinationStateCount, destinationStateIndex;
    	// Iterating over all the destination states to find the value of the 
    	// total utility obtained from the current state for the given action 
    	for (destinationStateCount = 0; destinationStateCount < numDestinationStates; destinationStateCount++){
    		destinationStateIndex = mdp.nextState[currentState][action][destinationStateCount];
    		utilitySum += mdp.transProb[currentState][action][destinationStateCount] * this.utility[destinationStateIndex];
    	}
    	return utilitySum;
    }
}
