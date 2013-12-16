import java.util.*;

/**
 * This class provides a simulator for generating a sequence of states
 * through a given MDP which are produced by following a given fixed
 * policy.
 */
public class FixedPolicySimulator implements MdpSimulator {

    /**
     * This is a constructor for the class which takes as arguments a
     * given MDP, and a given policy.
     */
    public FixedPolicySimulator(Mdp mdp, int[] policy) {
	this.mdp = mdp;
	this.policy = policy;
	cur_state = mdp.startState;
    }

    /**
     * Computes and returns the next state in the sequence by choosing
     * the next action according to the given policy, and simulating
     * its execution from the last state visited.  The first time it
     * is called, this method returns the given MDP's start state.
     */
    public int nextState() {
	int s = cur_state;
	int a = policy[s];
	double[] tp = mdp.transProb[s][a];
	double r = rand.nextDouble();
	int j = -1;
	while (r >= 0.0) {
	    r -= tp[++j];
	}
	cur_state = mdp.nextState[s][a][j];

	return s;
    }


    // private stuff

    private Mdp mdp;
    private int[] policy;
    private int cur_state;
    private Random rand = new Random();
}

