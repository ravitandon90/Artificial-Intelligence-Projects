/**
 * This is a template for the class corresponding to the blocking
 * heuristic.  This heuristic returns zero for goal states, and
 * otherwise returns one plus the number of cars blocking the path of
 * the goal car to the exit.  This class is an implementation of the
 * <tt>Heuristic</tt> interface, and must be implemented by filling in
 * the constructor and the <tt>getValue</tt> method.
 */
public class BlockingHeuristic implements Heuristic {

    /**
     * This is the required constructor, which must be of the given form.
     */
    public BlockingHeuristic(Puzzle puzzle) {
	// your code here

    }
	

    /**
     * This method returns the value of the heuristic function at the
     * given state.
     */
    public int getValue(State state) {
    	if (state.isGoal())
    		return 0;
    	Puzzle puzzle = state.getPuzzle();
    	int numberCars = puzzle.getNumCars();
    	int numberOfBlockingCars = 0;
    	int fixedPositionGoalCar = puzzle.getFixedPosition(0), variablePositionGoalCar = state.getVariablePosition(0),
    			goalCarSize = puzzle.getCarSize(0);    	 
    	int fixedPositionCar, variablePositionCar, carSize; boolean isVertical; 
    	for (int count = 1; count < numberCars; count++){
    		isVertical = puzzle.getCarOrient(count);
    		fixedPositionCar = puzzle.getFixedPosition(count);
    	    variablePositionCar = state.getVariablePosition(count);
    	    carSize = puzzle.getCarSize(count);    	    
    		if (isVertical && 
    				((fixedPositionCar > (variablePositionGoalCar + (goalCarSize-1))) 
    						&& (variablePositionCar <= fixedPositionGoalCar) 
    						&& (variablePositionCar + (carSize-1) >= fixedPositionGoalCar))  ) {
    			numberOfBlockingCars = numberOfBlockingCars + 1;
    		} else if (!isVertical && (fixedPositionGoalCar == fixedPositionCar) && (variablePositionGoalCar < variablePositionCar))    			
    			numberOfBlockingCars = numberOfBlockingCars + 1;
    	}
       return numberOfBlockingCars + 1;
    }
}
