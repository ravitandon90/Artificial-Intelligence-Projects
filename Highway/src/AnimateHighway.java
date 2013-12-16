import java.util.Random;


public class AnimateHighway {
	private int numberCars = 4;
	private int maxX = 3, maxY = 7, goalX = 1, goalY = 6;
	private int[] yCoordCars = {0, 1, 3, 5};
	private int cur_state;
	
	public AnimateHighway(Mdp mdp, int policy[]) throws InterruptedException{
		System.out.println("Starting animation");
		System.out.println("Generating a new start state");
		generateRandomStartState(mdp);				
		// Generate Arbitrary Start State
		while (true){
			printState (mdp);
			if (isFinalState(mdp)){
				System.out.println("Goal State Achieved");
				System.out.println("Generating a new start state");
				generateRandomStartState(mdp);
			} else
				nextState (policy, mdp);
			Thread.sleep(1000);
		}
	}
	
	private boolean isFinalState (Mdp mdp){
		String[] coord = mdp.stateName[this.cur_state].split(":");
		int x = Integer.parseInt(coord[0]);
		int y = Integer.parseInt(coord[1]);
		return (x == this.goalX && y == this.goalY);
	}
	
	private void printState (Mdp mdp){
	    int state[][] = new int[this.numberCars][2];
		String[] coord = mdp.stateName[this.cur_state].split(":");
	    for (int carIndex = 0; carIndex < this.numberCars; carIndex++){
	     state[carIndex][0]	= Integer.parseInt(coord[2*carIndex]);
	     state[carIndex][1]	= Integer.parseInt(coord[2*carIndex + 1]);
	    }		
	    printState (state);
	}
	
	private void generateRandomStartState(Mdp mdp){
		int state[][] = new int[this.numberCars][2];
		Random random = new Random();
		for (int carIndex = 0; carIndex < this.numberCars; carIndex++){
			state[carIndex][0] = Math.abs(random.nextInt()) % this.maxX;
			state[carIndex][1] = yCoordCars[carIndex];
		}
		String stateStr = stateArrToString (state); 
		this.cur_state = mdp.state_map.get(stateStr);
	}
	
	private int[][] convertToMatrixRep (int state[][]){
		int space[][] = new int[this.maxX][this.maxY];
		int xCoord, yCoord;
		for (int carIndex = 0; carIndex < this.numberCars; carIndex++){
			xCoord = state[carIndex][0];
			yCoord = state[carIndex][1];
			if (space[xCoord][yCoord] > 0){ // Car is already Present
				space[xCoord][yCoord] = -1;
			} else {
				space[xCoord][yCoord] = carIndex + 1;
			}
		}
		return space;
	}
	
	private void printState(int state[][]){
		System.out.println("***************************************************");		
		int space[][] = convertToMatrixRep (state);
		for (int currentY = this.maxY-1; currentY >= 0; currentY--){
			System.out.print("\t\t"+currentY+":");
			for (int currentX = this.maxX-1; currentX >= 0; currentX--){
				if (space[currentX][currentY] == 0){
					System.out.print("|__|");
				}else if (space[currentX][currentY] == -1){
					System.out.print("|XX|");
				}else {
					System.out.print("|C" + String.valueOf(space[currentX][currentY]) + "|");
				}
			}
			System.out.println();
		}
		System.out.println("\t\t   0   1   2");
		System.out.println("***************************************************");
	} 
	
	
    public int nextState(int[] policy, Mdp mdp) {
		int s = this.cur_state;
		int a = policy[s];
		double[] tp = mdp.transProb[s][a];
		Random rand = new Random();
		double r = rand.nextDouble();
		int j = -1;
		while (r >= 0.0) {
		    r -= tp[++j];
		}
		this.cur_state = mdp.nextState[s][a][j];
		return s;
    }
    
	/** This function takes a state (as an array) as an input and returns the state representation as a string.
	 * 
	 * @param states
	 * @return tempString
	 */
	private String stateArrToString (int[][] states){
		String tempString = "";
		for (int carIndex = 0; carIndex < this.numberCars; carIndex++){
			tempString += String.valueOf(states[carIndex][0]) 
					+ ":" + String.valueOf(states[carIndex][1]);
			if (carIndex < this.numberCars - 1){
				tempString += ":"; 					
			}
		}
		return tempString;
	}
	
}
