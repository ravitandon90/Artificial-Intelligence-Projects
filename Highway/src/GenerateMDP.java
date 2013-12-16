import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class GenerateMDP {
	// These are the auxiliary cars 
	private int numberCars = 4;
	private int actorCarIndex = 0;
	private char actionArray[] = {'L', 'C', 'R'};
	private int maxY = 7, maxX = 3, numberActions = 3;
	private int yCoordCars[] = {0, 1, 3, 5};
	private int XcoordIndex = 0, YcoordIndex = 1;
	private double divergenceProbability[][] = {{}, {0.5, 0.25, 0.25}, {0.25, 0.25, 0.5}, {0.25, 0.5, 0.25}};
	private String startState = "0:1:1:1:1:3:1:5";
	public GenerateMDP(){		
		int carStates[][] = buildCarStates (this.numberCars, this.actionArray.length); 
		int collisionReward = -1000, successReward = 10, goalX = 1, goalY = this.maxY - 1;	
		String outputFileName = "HPMdp.txt";
		int totalNumberStates = (this.maxY) * (int) Math.pow(this.maxX, (this.numberCars));
		int states[][][] = new int[totalNumberStates][this.numberCars][2];
		int stateReward[] = new int[totalNumberStates];

		// Generating states
		int currentStateCount = 0, stateIndex;
		for (int currentY = 0; currentY < this.maxY; currentY++){			
			for (stateIndex = 0; stateIndex < carStates.length; stateIndex++){
				currentStateCount = carStates.length * currentY + stateIndex;
				for (int carIndex = 0; carIndex < this.numberCars; carIndex++){
					states[currentStateCount][carIndex][this.XcoordIndex] = carStates[stateIndex][carIndex];
					states[currentStateCount][carIndex][this.YcoordIndex]  = (carIndex == this.actorCarIndex) ? 
							currentY : this.yCoordCars[carIndex];
					
					// Negative Reward - Detecting A Collision
					if ((carIndex != this.actorCarIndex) &&
						(states[currentStateCount][this.actorCarIndex][this.XcoordIndex] == 
						states[currentStateCount][carIndex][this.XcoordIndex]) &&
						(states[currentStateCount][this.actorCarIndex][this.YcoordIndex] == 
						states[currentStateCount][carIndex][this.YcoordIndex]))
						stateReward[currentStateCount] = collisionReward;
				}
				// Assigning rewards to the states Positive Reward
				if ((states[currentStateCount][this.actorCarIndex][XcoordIndex] == goalX) && (currentY == goalY))
					stateReward[currentStateCount] = successReward;
			}
			writeMDPToFile (outputFileName, states, stateReward);
		}
	}
	
	/** This function writes the MDP to the output file. 
	 *  
	 * @param outputFileName
	 * @param states
	 */
	private void writeMDPToFile (String outputFileName, int[][][] states, int[] stateReward){
		PrintWriter writer;
		String tempString;
		try {
			// Opening the  output file
			writer = new PrintWriter(outputFileName, "UTF-8");
			// Printing the start state 
			writer.println(startState);
			// Printing all the state rewards to the file, only those states which have a reward greater than 0 are printed
			for (int currentStateCount = 0; currentStateCount < states.length ;
					currentStateCount++){
				tempString = stateArrToString (states[currentStateCount]);
				tempString += " " + stateReward[currentStateCount];
				if  (stateReward[currentStateCount] != 0)
					writer.println(tempString);
			}
			generateTransitions (writer);		
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** This function generates the sequence of transitions along with their probabilities and writes it to the Mdp File.
	 * 
	 * @param writer
	 * @param divergenceProbability
	 */
	private void generateTransitions (PrintWriter writer){
		String outString;
		int carStates[][] = buildCarStates (this.numberCars-1, this.actionArray.length);
		// For storing the transition probability
		double transitionProbability;
		// For storing the current and the destination states 
		int currentState[][] = new int[this.numberCars][2], destinationState[][] = new int[this.numberCars][2];
		// Generating the next plausible states
		for (int currentY = 0; currentY < this.maxY; currentY++){
			for (int currentX = 0; currentX < this.maxX; currentX++){
				// Generating the actor car's position
				currentState[this.actorCarIndex][0] = currentX;
				currentState[this.actorCarIndex][1] = currentY;				
				for (int carAction = 0; carAction < this.numberActions; carAction++){
					destinationState[this.actorCarIndex][1] = (carAction == 1) ? Math.min(currentY + 1, this.maxY-1) : currentY;
					destinationState[this.actorCarIndex][0] = (carAction == 0) ? Math.max(0, currentX-1) : 
						((carAction == 2) ? Math.min(2, currentX+1) : currentX);
					for (int carStateS = 0; carStateS < carStates.length; carStateS++){
						for (int carStateD = 0; carStateD < carStates.length; carStateD++){
							for (int carIndex = 1; carIndex < this.numberCars; carIndex++){
								currentState[carIndex][0] = carStates[carStateS][carIndex-1];
								destinationState[carIndex][0] = carStates[carStateD][carIndex-1];
								destinationState[carIndex][1] = currentState[carIndex][1] = this.yCoordCars[carIndex];				
							}
							transitionProbability = getTransitionProbability(carStates[carStateS], carStates[carStateD]);
							if (transitionProbability > 0){
								outString = stateArrToString (currentState) + " " + this.actionArray[carAction] + " " + 
											stateArrToString (destinationState) + " " + String.valueOf(transitionProbability);
								writer.println(outString);
							}
						}
					}
				}
			}
		}
	}
	
	/** This function gives the transition probability from a given state to another state
	 * 
	 * @param stateSource
	 * @param stateDestination
	 * @param divergenceProbability
	 * @return transitionProbability
	 */
	private double getTransitionProbability (int[] stateSource, int[] stateDestination){
		double transitionProbability = 1;
		int sourceX, destX;
		// Loop runs only for auxiliary cars here 
		for (int carIndex = 0; carIndex < this.numberCars -1; carIndex++){
			sourceX = stateSource[carIndex];
			destX = stateDestination[carIndex]; 
			if (Math.abs(sourceX - destX) == 2) // Checking for invalid transition
				return 0;
			if (sourceX == destX && sourceX == 1){ // Car Remains in the center
				transitionProbability *= this.divergenceProbability[carIndex + 1][1];
			} else if (sourceX == destX && sourceX == 0) { // Car Remains in the center or moves left 
				transitionProbability *= (this.divergenceProbability[carIndex + 1][1] + this.divergenceProbability[carIndex + 1][0]);
			} else if (sourceX == destX && sourceX == 2) { // Car Remains in the center or moves right
				transitionProbability *= (this.divergenceProbability[carIndex + 1][1] + this.divergenceProbability[carIndex + 1][2]);
			} else if (sourceX == destX+1) { // Car Moves Left
				transitionProbability *= this.divergenceProbability[carIndex + 1][0];
			} else if (sourceX == destX-1){ // Car Moves Right
				transitionProbability *= this.divergenceProbability[carIndex + 1][2];
			}
		}
		return transitionProbability;		
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
	
	/** This function dynamically builds the state of each car in a given order.
	 * 
	 * @param elements
	 * @param numberActions
	 * @return
	 */
	private int[][] buildCarStates (int elements, int numberActions){
		int totalStates = (int) Math.pow(numberActions, elements);
		int stateValues[][] = new int[totalStates][elements];
		for (int count = 0; count < totalStates; count++){
			for (int elementIndex = 0; elementIndex < elements; elementIndex++){
				stateValues[count][elementIndex] = count / (int)Math.pow(numberActions, elementIndex) % numberActions;
			}
		}
		return stateValues;
	}
	
	
 	
	
	/** Main Function
	 * 
	 * @param argv
	 */
	public static void main (String argv[]){
		GenerateMDP generateMdp = new GenerateMDP();
	}
}