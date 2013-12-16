import java.util.Random;

/**
 * This is the template of a class that should run policy iteration on
 * a given MDP to compute the optimal policy which is returned in the
 * public <tt>policy</tt> field.  You need to fill in the constructor.
 * You may wish to add other fields with other useful information that
 * you want this class to return (for instance, number of iterations
 * before convergence).  You also may add other constructors or
 * methods, provided that these are in addition to the one given below
 * (which is the one that will be automatically tested).  In
 * particular, your code must work properly when run with the
 * <tt>main</tt> provided in <tt>RunCatMouse.java</tt>.
 */
public class PolicyIteration {

    /** the computed optimal policy for the given MDP **/
    public int policy[];
    private int bestAction;
    /**
     * The constructor for this class.  Computes the optimal policy
     * for the given <tt>mdp</tt> with given <tt>discount</tt> factor,
     * and stores the answer in <tt>policy</tt>.
     */
    public PolicyIteration(Mdp mdp, double discount) {

	// your code here    	
    	// Stores the number of states in the Mdp 
    	int numStates = mdp.numStates, currentState, numActions = mdp.numActions;;    	
    	// Initializing the set of utilities
    	double utility[] = new double[numStates];
    	// Initializing the current policy
    	this.policy = new int[numStates];
    	// generator is used to generate random numbers for the assigning random initial policies to each of the states
    	Random generator = new Random();
    	// Initializing the initial action at each of the states to a random action 
    	for (currentState = 0; currentState < numStates; currentState++){
    		this.policy[currentState] =  Math.abs(generator.nextInt()) % numActions; 
    	}
    	// Initializing the unchanged variable 
    	boolean unchanged;
    	// Looping till there is no change in the policy
    	double currentUtility, bestUtility;
    	while (true){
    		// Initially the value of unchanged is set to be true, since there has been no change detected
    		unchanged = true;
    		// Evaluating the current utilities using the current policy
    		PolicyEvaluation policyEvaluation = new PolicyEvaluation (mdp, discount, this.policy);
    		// Updating the policy calculated using the policyEvaluation object 
    		for (currentState = 0; currentState < numStates; currentState++){
    			utility[currentState] = policyEvaluation.utility[currentState];
    		}
    		// Iterating over the set of all the states to see there is a change required for any of the states 
    		for (currentState = 0; currentState < numStates; currentState++){
    			// calculating the current expected utility 
    			currentUtility = calculateUtilityAction (currentState, mdp, this.policy[currentState], utility);
    			bestUtility = calculateBestUtility (currentState, mdp, utility);
    			if (bestUtility > currentUtility){
    				this.policy[currentState] = this.bestAction;
    				unchanged = false;
    			}
    		}
    	// Checking if there has been some change in the policy of any one of the states
    	if (unchanged)
    		break;
    	}
    }
    
    /** This function calculates the maximum utility over all the set of actions for a given state.
     *  The action corresponding to the maximum utility is updated in bestAction.
     * @param currentState
     * @param mdp
     * @return maximumUtility
     */
    private double calculateBestUtility (int currentState, Mdp mdp, double[] utility){
    	int numDestinationStates, destinationStateCount, destinationStateIndex, numActions, currentAction;
    	double maximumUtility = Double.NEGATIVE_INFINITY, currentUtility;
    	numActions = mdp.numActions;
    	// Iterating over all the actions, to get utility for each action 
    	for (currentAction = 0; currentAction < numActions; currentAction++){
    		currentUtility = 0.0;
    		numDestinationStates = mdp.nextState[currentState][currentAction].length;
    		// Iterating over all the destination states reachable for the current state for the current action  
    		for (destinationStateCount = 0; destinationStateCount < numDestinationStates; destinationStateCount++){
    			destinationStateIndex = mdp.nextState[currentState][currentAction][destinationStateCount];
    			currentUtility += mdp.transProb[currentState][currentAction][destinationStateCount] *
    				utility[destinationStateIndex];
    		}
    		// Checking if the current utility for the state is greater than the maximum utility
    		if (currentUtility > maximumUtility){
    			maximumUtility = currentUtility;
    			this.bestAction = currentAction;
    		}
    	}
    	return maximumUtility;
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
    private double calculateUtilityAction (int currentState, Mdp mdp, int action, double[] utility){
    	// Initializing the utility sum, for the current state
    	double utilitySum = 0.0;
    	// numDestinationStates is the total number of states reachable from the current state for the given action 
    	int numDestinationStates = mdp.nextState[currentState][action].length, destinationStateCount, destinationStateIndex;
    	// Iterating over all the destination states to find the value of the 
    	// total utility obtained from the current state for the given action 
    	for (destinationStateCount = 0; destinationStateCount < numDestinationStates; destinationStateCount++){
    		destinationStateIndex = mdp.nextState[currentState][action][destinationStateCount];
    		utilitySum += mdp.transProb[currentState][action][destinationStateCount] * utility[destinationStateIndex];
    	}
    	return utilitySum;
    }

}
