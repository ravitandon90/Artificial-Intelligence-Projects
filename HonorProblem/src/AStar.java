import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * 
 */

/**
 * @author ravitandon
 *
 */
public class AStar {
	public State[] path;
    
	public int getPathCost(){
		return (path[path.length -1].getCost());
	}
	
	public void printPath(){
		for (int c = 0; c < path.length; c++){
			path[c].print();
		}
	}
	
	public class HeuristicComparator implements Comparator <Node> {
		private Heuristic heuristic; 
		public HeuristicComparator (Heuristic heuristicArgument){
			 heuristic = heuristicArgument;
		} 
		
		public int compare(Node node1, Node node2){
			int value1 = heuristic.getValue(node1.getState()) + node1.getState().getCost();
			int value2 = heuristic.getValue(node2.getState()) + node2.getState().getCost();
			if (value1 > value2)     					
				return 1;    			    			
			else if (value2 > value1)
				return -1;    			
			else if (node1.getState().getCost() < node2.getState().getCost())
				return 1;	
			else return -1;
		}			
	}; 

	public AStar(Puzzle puzzle, Heuristic heuristic){
	   int initialCapacity = 10;			    
 	   Node rootNode =  puzzle.getInitNode();  // constructing the Root Node. 
 	   Node currentNode = null;
 	   HeuristicComparator heuristicComparator = new HeuristicComparator (heuristic);
        PriorityQueue <Node> frontierNodes = new PriorityQueue<Node> (initialCapacity, heuristicComparator);           
        frontierNodes.add (rootNode);           
        HashSet<Integer> exploredStates = new HashSet<Integer> ();
        HashSet<Integer> frontierStatesHashCode = new HashSet<Integer> ();
        frontierStatesHashCode.add (rootNode.getState().hashCode());
        while (true) {
     	   if (frontierNodes.size() == 0) {
     		   path = null;
     		   break;
     	   }
     	   currentNode = frontierNodes.poll(); // Choosing the top most node (the lowest cost element from the frontier)
     	   frontierStatesHashCode.remove(currentNode.getState().hashCode());
     	   if (currentNode.getState().isGoal()){   
     		   int solutionDepth = currentNode.getDepth();
     		   path = new State[solutionDepth + 1];
     		   while (solutionDepth >= 0){        			   
     			   path [solutionDepth] = currentNode.getState();
     			   currentNode = currentNode.getParent();
     			   solutionDepth--;
     		   }
     		   break;
     	   } else {
     		exploredStates.add(currentNode.getState().hashCode());
     		Node[] childrenNode = currentNode.expand();
     		for(int count = 0; count < childrenNode.length; count++){
     			boolean isFrontier = frontierStatesHashCode.contains(childrenNode[count].getState().hashCode());        			
     			boolean isExplored = exploredStates.contains(childrenNode[count].getState().hashCode());         			
     			if (!isExplored && !isFrontier){
     				frontierNodes.add(childrenNode[count]);
     				frontierStatesHashCode.add(childrenNode[count].getState().hashCode());
     				} else if (isFrontier) {
     					Iterator <Node> it = frontierNodes.iterator();
     					while (it.hasNext()){
     						Node nextNode = it.next();
     						if (childrenNode[count].getState().equals(nextNode.getState())){
     							if (childrenNode[count].getDepth() < nextNode.getDepth()){
     								frontierNodes.remove(nextNode);
     								frontierNodes.add(childrenNode[count]);
     								frontierStatesHashCode.add(childrenNode[count].getState().hashCode());
     							}
     							break;
     						}        					
     						} 
     					}
     				}
     			} 
     		}
	}
	
}
