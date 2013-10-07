import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * This is the template for a class that performs A* search on a given
 * rush hour puzzle with a given heuristic.  The main search
 * computation is carried out by the constructor for this class, which
 * must be filled in.  The solution (a path from the initial state to
 * a goal state) is returned as an array of <tt>State</tt>s called
 * <tt>path</tt> (where the first element <tt>path[0]</tt> is the
 * initial state).  If no solution is found, the <tt>path</tt> field
 * should be set to <tt>null</tt>.  You may also wish to return other
 * information by adding additional fields to the class.
 */
public class AStar {

    /** The solution path is stored here */
    public State[] path;    
   
    /**
     * This is the constructor that performs A* search to compute a
     * solution for the given puzzle using the given heuristic.
     */
    public AStar(Puzzle puzzle, Heuristic heuristic) {      	
    	
	// your code here    	    	
    	class HeuristicComparator implements Comparator <Node> {
    		private Heuristic heuristic; 
    		public HeuristicComparator (Heuristic heuristicArgument){
    			 heuristic = heuristicArgument;
    		} 
    		
    		public int compare(Node node1, Node node2){
    			int value1 = heuristic.getValue(node1.getState()) + node1.getDepth();
    			int value2 = heuristic.getValue(node2.getState()) + node2.getDepth();
    			if (value1 > value2)     					
    				return 1;    			    			
    			else if (value2 > value1)
    				return -1;    			
    			else if (node1.getDepth() < node2.getDepth())
    				return 1;	
    			else return -1;
    		}			
    	}; 
    	    	   
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

