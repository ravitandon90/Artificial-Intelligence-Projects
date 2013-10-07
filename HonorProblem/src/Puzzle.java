import java.io.*;
import java.util.*;

/**
 * This is the class for representing a particular rush hour puzzle.
 * Methods are provided for accessing information about a puzzle, and
 * also for reading in a list of puzzles from a data file.  In
 * addition, this class maintains a counter of the number of search nodes
 * that have been generated for this puzzle.  Methods for accessing,
 * incrementing or resetting this counter are also provided.
 * <p>
 * Every car is constrained to only move horizontally or vertically.
 * Therefore, each car has one dimension along which it is fixed, and
 * another dimension along which it can be moved.  The fixed dimension
 * is stored here as part of the puzzle.  Also stored here are the
 * sizes and orientations of the cars, the size of the puzzle grid,
 * the name of the puzzle and the initial (root) search node of the
 * puzzle.
 * <p>
 * The goal car is always assigned index 0.
 */
public class Puzzle {

    private String name;
    private Node initNode;

    private int searchCount;

    private int numJobs;
    private int jobDelayCost[][];
    private int maximumDelay[];


    /** Returns the number of cars for this puzzle. */
    public int getNumJobs() {
	return numJobs;
    }

    /** Returns the fixed position of car <tt>v</tt>. */
    public int getJobDelayCost(int v, int j) {
	return jobDelayCost[v][j];
    }
    
    /** Returns the fixed position of car <tt>v</tt>. */
    public int[] getJobDelayCostArray(int v) {
	return jobDelayCost[v];
    }
    
    /** Returns the size (length) of car <tt>v</tt>. */
    public int getMaximuDelay(int v) {
	return maximumDelay[v];
    }

    
    /** Increments the search counter by <tt>d</tt>. */
    public void incrementSearchCount(int d) {
	searchCount += d;
    }

    /**
     * Returns the current value of the search counter, which keeps a
     * count of the number of nodes generated on the current
     * search.
     */
    public int getSearchCount() {
	return searchCount;
    }

    /** Resets the search counter to 1 (for the initial node). */
    public void resetSearchCount() {
	searchCount = 1;
    }

    /** Returns the name of this puzzle. */
    public String getName() {
	return name;
    }

    /** Returns the initial (root) node of this puzzle. */
    public Node getInitNode() {
	return initNode;
    }

    /**
     * The main constructor for constructing a puzzle.  You probably
     * will never need to use this constructor directly, since
     * ordinarily puzzles will be constructed by reading them in from
     * a datafile using the <tt>readPuzzlesFromFile</tt> method.  It
     * is assumed that the goal car is always assigned index 0.
     *
     * @param name     the name of the puzzle
     * @param gridSize the size of one side of the puzzle grid
     * @param numCars  the number of cars on this puzzle
     * @param orient   the orientations of each car (<tt>true</tt> = vertical)
     * @param size     the sizes of each car
     * @param x        the x-coordinates of each car
     * @param y        the y-coordinates of each car
     */
    public Puzzle(String name,
		  int numJobs,
		  int jobDelayCost[][],
		  int maximumDelay[]) {
	this.name = name;
	this.numJobs = numJobs;
	if (numJobs <= 0) {
	    throw new IllegalArgumentException("Each problem must have a positive number of jobs");
	}
	
	this.maximumDelay = new int[numJobs];
	this.jobDelayCost = new int[numJobs][numJobs];	
	for (int v = 0; v < numJobs; v++) {
		int c;
		this.maximumDelay[v] = maximumDelay[v];		
		for (c = 0; c < this.maximumDelay[v]; c++){
			this.jobDelayCost[v][c] = jobDelayCost[v][c];
		}
		for (; c < this.numJobs; c++){
			this.jobDelayCost[v][c] = -1;
		}
	}
	int array[] = {};
	initNode = new Node(new State(this, array), 0, null, 0);

	resetSearchCount();
    }

    /**
     * A static method for reading in a list of puzzles from the data
     * file called <tt>filename</tt>.  Each puzzle is described in the
     * data file using the format described on the assignment.  The
     * set of puzzles is returned as an array of <tt>Puzzle</tt>'s.
     */
    public static Puzzle[] readPuzzlesFromFile(String filename)
    	throws FileNotFoundException, IOException {

	@SuppressWarnings("resource")
	BufferedReader in = new BufferedReader(new FileReader(filename));

	ArrayList<Puzzle> puzzles = new ArrayList<Puzzle>();
	ArrayList<JobRec> job_list = null;

	String name = null;
	String line;
	String[] words = null;

	int read_mode = 0;
	int line_count = 0;
	int numberJobs = 0;

	while ((line = in.readLine()) != null) {
	    line_count++;
	    line = line.trim( );
	    words = line.split("\\s+");
	    if (line.equals(""))
		continue;

	    if (read_mode == 0) {   // reading name
		name = line;
		job_list = new ArrayList<JobRec>();
		read_mode = 1;
	    } else if (read_mode == 1) { // reading number of jobs
		if (words.length != 1)
		    throw new RuntimeException("Expected single integer for job size at line " + line_count + " in file " + filename);
		try {
			numberJobs = Integer.parseInt(words[0]);
		} catch (NumberFormatException e) {
		    throw new NumberFormatException("Expected integer grid size at line "+line_count+ " in file "+filename);
		}
		if (numberJobs <= 0)
		    throw new RuntimeException("Expected positive grid size at line "+line_count+ " in file "+filename);

		read_mode = 2;
	    } else if (line.equals(".")) {  // end of puzzle description
		int numjobs = job_list.size();
		int maxDelay[] = new int[numjobs];
		int jobDelayCost[][] = new int[numjobs][numjobs];

		for (int v = 0; v < numjobs; v++) {
		    JobRec jobrec = (JobRec) job_list.get(v);
		    maxDelay[v] = jobrec.maximumDelay;
		    for (int j = 0; j < numjobs; j++){
		    	jobDelayCost[v][j] = jobrec.delayCost[j]; 
		    }
		}

		puzzles.add(new Puzzle(name,
				       numberJobs,
				       jobDelayCost,
				       maxDelay
				       ));
		read_mode = 0;
	    } else {
		JobRec jobrec = new JobRec();		
		try {
		    jobrec.maximumDelay = Integer.parseInt(words[0]);
		} catch (NumberFormatException e) {
		    throw new NumberFormatException("Expected integer maximumDelay at line "+line_count+ " in file "+filename);
		}

		jobrec.delayCost = new int[numberJobs];
		int c;
		for (c = 0; c < jobrec.maximumDelay; c++){
			try {
				jobrec.delayCost[c] = Integer.parseInt(words[c+1]);;
			} catch (NumberFormatException e) {
			    throw new NumberFormatException("Expected integer for delay cost " + c + " at line "+line_count+ " in file "+filename);
			}
		}
		for (; c < numberJobs; c++){
			jobrec.delayCost[c] = -1;			
		}
		job_list.add(jobrec);
	    }
	}

	if (read_mode != 0)
	    throw new RuntimeException("Puzzle description ended prematurely in file " + filename);

	return (Puzzle[]) puzzles.toArray(new Puzzle[0]);
    }

    private static class JobRec {
	public int maximumDelay;
	public int delayCost[];
    }
	
}
