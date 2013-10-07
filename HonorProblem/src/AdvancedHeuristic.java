import java.util.HashSet;

/**
 * 
 */

/**
 * @author ravitandon
 *
 */
public class AdvancedHeuristic implements Heuristic {
	private Puzzle puzzle;
	private HashSet<Integer> hashSet;
	
	public AdvancedHeuristic (Puzzle puzzle){
		this.puzzle = puzzle;
		hashSet = new HashSet<Integer>();
	}
	
    public boolean contains (int array[], int value){ 
    	for (int j = 0; j < array.length; j++){
    		if (array[j] == value)
    			return true;
    	}
    	return false;
    }
    
    public int findMin(int[] array){
    	int min = -1, minPos = 0;
    	for (int count = 0; count < array.length; count++){
    		if (min == -1 || (!hashSet.contains(count) && min > array[count] && array[count] != -1)){
    			min = array[count];
    			minPos = count;
    		}
    	}
    	hashSet.add(minPos);
    	return min;
    }

    public int getValue(State state){
		int heuristicValue = 0, currentRound = state.getJobsArray().length;
		int delayCost[] = new int[puzzle.getNumJobs()];
		if (state.isGoal())
			return 0;
		for (int i = currentRound ; i < puzzle.getNumJobs(); i++){
			for (int j = 0; j < puzzle.getNumJobs(); j++){
				if (contains (state.getJobsArray(), j)){
					delayCost[j] = -1;
					hashSet.add(j);
				} else
					delayCost[j] = puzzle.getJobDelayCost(j, currentRound);
			}
			heuristicValue = heuristicValue + findMin(delayCost);
		}
		return heuristicValue;
	}
}
