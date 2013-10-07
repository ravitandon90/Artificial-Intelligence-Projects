import java.util.ArrayList;
import java.util.Arrays;

/**
 * This is the class for representing a single state of the rush hour
 * puzzle.  Methods are provided for constructing a state, for
 * accessing information about a state, for printing a state, and for
 * expanding a state (i.e., obtaining a list of all states immediately
 * reachable from it).
 * <p>
 * Every car is constrained to only move horizontally or vertically.
 * Therefore, each car has one dimension along which it is fixed, and
 * another dimension along which it can be moved.  This variable
 * dimension is stored here as part of the state.  A link to the
 * puzzle with which this state is associated is also stored.
 * Note that the goal car is always assigned index 0.  
 * <p>
 * To make it easier to use <tt>State</tt> objects with some of the
 * data structures provided as part of the Standard Java Platform, we
 * also have provided <tt>hashCode</tt> and <tt>equals</tt> methods.
 * You probably will not need to access these methods directly, but
 * they are likely to be used implicitly if you take advantage of the
 * Java Platform.  These methods define two <tt>State</tt> objects to
 * be equal if they refer to the same <tt>Puzzle</tt> object, and if
 * they indicate that the cars have the identical variable positions
 * in both states.  The hashcode is designed to satisfy the general
 * contract of the <tt>Object.hashCode</tt> method that it overrides,
 * with regard to the redefinition of <tt>equals</tt>.
 */
public class State {

    private Puzzle puzzle ;
    /** This is a list of all the jobs scheduled for up to the current state. The jobs are in the order of their schedule.*/
    private int jobsScheduled[];
    
    public int getCost(){    	
    	int cost = 0;
    	for (int i = 0; i < jobsScheduled.length; i++)
    		cost = cost + puzzle.getJobDelayCost(jobsScheduled[i], i);
    	return cost;
    }

    /**
     * The main constructor for constructing a state.  You probably
     * will never need to use this constructor.
     *
     *   @param puzzle the puzzle that this state is associated with
     *   @param varPos the variable position of each of the cars in this state
     */
    public State(Puzzle puzzle, int jobsScheduled[]) {
	this.puzzle = puzzle;
	this.jobsScheduled = new int[jobsScheduled.length];
	for (int v = 0; v < jobsScheduled.length; v++)
		this.jobsScheduled[v] = jobsScheduled[v];
	computeHashCode();
    }

    /** Returns true if and only if this state is a goal state. */
    public boolean isGoal() {    
	return (jobsScheduled.length == puzzle.getNumJobs());
    }

    /** Returns the variable position of car <tt>v</tt>. */
    public int[] getJobsArray() {
	return jobsScheduled;
    }

    /** Returns the puzzle associated with this state. */
    public Puzzle getPuzzle() {
	return puzzle;
    }

    /** Prints to standard output a primitive text representation of the state. */
    public void print() {
	for (int i = 0; i < jobsScheduled.length; i++)
		System.out.print(jobsScheduled[i] + ",");	
    System.out.println();
    }

    private int hashcode;
    private void computeHashCode() {
	hashcode = puzzle.hashCode();
	for (int i = 0; i < jobsScheduled.length; i++)
	    hashcode =  31 * hashcode + jobsScheduled[i];
    }

    /**
     * Returns a hash code value for this <tt>State</tt> object.
     * Although you probably will not need to use it directly, this
     * method is provided for the benefit of hashtables given in the
     * Java Platform.  See documentation on <tt>Object.hashCode</tt>,
     * which this method overrides, for the general contract that
     * <tt>hashCode</tt> methods must satisfy.
     */
    public int hashCode() {
	return hashcode;
    }
    
    /**
     * Computes all of the states immediately reachable from this
     * state and returns them as an array of states.  You probably
     * will not need to use this method directly, since ordinarily you
     * will be expanding <tt>Node</tt>s, not <tt>State</tt>s.
     */
    public boolean contains (int array[], int value){ 
    	for (int j = 0; j < array.length; j++){
    		if (array[j] == value)
    			return true;
    	}
    	return false;
    }
    public State[] expand() {
	int num_jobs = puzzle.getNumJobs();
	int numJobsDone = this.jobsScheduled.length;
	int currentRound = numJobsDone + 1;
	ArrayList<State> new_states = new ArrayList<State>();
    
	for (int v = 0; v < num_jobs; v++) {
		if (!(contains(jobsScheduled,v))){
			if (puzzle.getMaximuDelay(v) >= currentRound){
				int newJobsSchedule[] = new int[currentRound];
				int c;
				for (c = 0; c < currentRound - 1; c++ ){
					newJobsSchedule[c] = jobsScheduled[c]; 
				}
				newJobsSchedule[c] = v;
				new_states.add(new State(puzzle, newJobsSchedule));
			}
		}
	}

	puzzle.incrementSearchCount(new_states.size());

	return (State[]) new_states.toArray(new State[0]);
    }
    
    /**
     * Returns <tt>true</tt> if and only if this state is considered
     * equal to the given object.  In particular, equality is defined
     * to hold if the given object is also a <tt>State</tt> object, if
     * it is associated with the same <tt>Puzzle</tt> object, and if
     * the cars in both states are in the identical positions.  This
     * method overrides <tt>Object.equals</tt>.
     */
    public boolean equals(Object o) {
	State s;
	try {
	    s = (State) o;
	}
	catch (ClassCastException e) {
	    return false;
	}
	if (hashcode != s.hashcode || !puzzle.equals(s.puzzle))
	    return false;

	for (int i = 0; i < jobsScheduled.length; i++)
	    if (jobsScheduled[i] != s.jobsScheduled[i])
		return false;
	return true;
    }
}
