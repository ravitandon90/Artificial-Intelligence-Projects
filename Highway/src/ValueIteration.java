/**
 * This is the template of a class that should run value iteration on
 * a given MDP to compute the optimal policy which is returned in the
 * public <tt>policy</tt> field.  The computed optimal utility is also
 * returned in the public <tt>utility</tt> field.  You need to fill in
 * the constructor.  You may wish to add other fields with other
 * useful information that you want this class to return (for
 * instance, number of iterations before convergence).  You also may
 * add other constructors or methods, provided that these are in
 * addition to the one given below (which is the one that will be
 * automatically tested).  In particular, your code must work properly
 * when run with the <tt>main</tt> provided in <tt>RunCatMouse.java</tt>.
 */
public class ValueIteration {

    /** the computed optimal policy for the given MDP **/
    public int policy[];

    /** the computed optimal utility for the given MDP **/
    public double utility[];
    
    /**
     * The constructor for this class.  Computes the optimal policy
     * for the given <tt>mdp</tt> with given <tt>discount</tt> factor,
     * and stores the answer in <tt>policy</tt>.  Also stores the
     * optimal utility in <tt>utility</tt>.
     */
    public ValueIteration(Mdp mdp, double discount) {

	// your code here
    	int numStates = mdp.numStates, currentState;
    	// Initializing the utility array
    	this.utility = new double[numStates];
    	// Initializing the policy array
    	this.policy = new int[numStates];
    	double delta = Math.pow(10, -13) * (1 - discount) / discount, difference, maxDifference; //this.delta * (1 - discount) / discount
    	double tempUtility[] = new double[numStates];
    	while (true){
    		// setting maximum distance = 0 
    		maxDifference = 0;
    		// Updating the current utility vector using the temporary utilities calculated
    		for (currentState = 0; currentState < numStates; currentState++){
    			this.utility[currentState] = tempUtility[currentState]; 
    		}
    		// Updating the temporary utility vector using the optimal action calculation methodology
	    	for (currentState = 0; currentState < numStates; currentState++){
	    		// updating the maximum utility (over all the actions) for the current state
	    		tempUtility[currentState] = mdp.reward[currentState] + (discount) * calculateOptimalAction (currentState, mdp);
	    		// Calculating the change in the utility for the current state
	    		difference = Math.abs(this.utility[currentState] - tempUtility[currentState]);
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
     * This function calculates the optimal action for the current state, using current temporary utilities 
     * and returns the optimal utility. The optimal action is stored in the utility array (array of doubles).
     * @param state
     * @param mdp
     * @return maxUtility
     */
    private double calculateOptimalAction (int state, Mdp mdp){
    	// utilitySum --> temporary variable for calculating the total utility for each action from the current state
    	// maxUtility --> maximum utility over all the actions for the current state 
    	double utilitySum, maxUtility = Double.NEGATIVE_INFINITY;
    	// optimalAction --> stores the optimal action for the current state  
    	int optimalAction = -1;
    	int numActions = mdp.numActions, numReachableStates, destinationState, destinationStateCount;
    	// Calculating the expected utility over all the actions 
    	for (int currentAction = 0; currentAction < numActions; currentAction++){
    		// Calculating the number of reachable states for each action 
    		numReachableStates = mdp.nextState[state][currentAction].length;
    		// initializing the utility sum to 0
    		utilitySum = 0.0;
    		// iterating over all the destination states (reachable from the current state for all the allowable set of actions) 
    		for (destinationStateCount = 0; destinationStateCount < numReachableStates; destinationStateCount++){    			
    			destinationState = mdp.nextState[state][currentAction][destinationStateCount];
    			utilitySum += (mdp.transProb[state][currentAction][destinationStateCount]) *
    					(this.utility[destinationState]);
    		}
    		if (utilitySum > maxUtility){
    			maxUtility = utilitySum;
    			optimalAction = currentAction;
    		}
    	}
    	assert (optimalAction != -1);
    	this.policy[state] = optimalAction;    	
    	return maxUtility;
    }
    
}
