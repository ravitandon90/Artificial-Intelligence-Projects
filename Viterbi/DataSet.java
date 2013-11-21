import java.util.*;
import java.io.*;

/**
 * This class loads and stores a dataset consisting of training and
 * testing sequences.
 **/
public class DataSet {

    /** total number of possible states */
    public int numStates;

    /** total number of possible outputs */
    public int numOutputs;

    /** an array containing the names of all of the states */
    public String stateName[];

    /** an array containing the names of all of the outputs */
    public String outputName[];

    /** the set of all training state sequences.  <tt>trainState[i]</tt> is
     * an array representing the <tt>i</tt>-th sequence of states encountered
     * during training (corresponding to <tt>trainOutput[i]</tt>).
     **/
    public int trainState[][];

    /** the set of all training output sequences.  <tt>trainOutput[i]</tt> is
     * an array representing the <tt>i</tt>-th sequence of outputs encountered
     * during training (corresponding to <tt>trainState[i]</tt>).
     **/
    public int trainOutput[][];

    /** the set of all testing state sequences.  <tt>testState[i]</tt> is
     * an array representing the <tt>i</tt>-th sequence of states encountered
     * during testing (corresponding to <tt>testOutput[i]</tt>).
     **/
    public int testState[][];

    /** the set of all testing output sequences.  <tt>testOutput[i]</tt> is
     * an array representing the <tt>i</tt>-th sequence of outputs encountered
     * during testing (corresponding to <tt>testState[i]</tt>).
     **/
    public int testOutput[][];

    private class DataItem {
	private int state;
	private int output;

	private DataItem(int state, int output) {
	    this.state = state;
	    this.output = output;
	}
    }

    /** This constructor reads in data from <tt>filename</tt> and sets
     * up all of the public fields.  See assignment instructions for
     * information on the required format of this file.
     */
    public DataSet(String filename) throws FileNotFoundException, IOException {
	BufferedReader in;
	String line;
	List<int[]> all_state_seq = new ArrayList<int[]>();
	List<int[]> all_output_seq = new ArrayList<int[]>();
	Map<String, Integer> state_map = new HashMap<String, Integer>();
	Map<String, Integer> output_map = new HashMap<String, Integer>();
	ArrayList<DataItem> item_list = new ArrayList<DataItem>();
	boolean test_mode = false;

	try {
	    in = new BufferedReader(new FileReader(filename));
	} catch (FileNotFoundException e) {
	    System.err.print("File "+filename+" not found.\n");
	    throw e;
	}

	numStates = 0;
	numOutputs = 0;

	while (true) {
	    try {
		line = in.readLine();
	    }
	    catch (IOException e) {
		System.err.println("Error reading file "+filename);
		throw e;
	    }

	    String[] words = null;

	    if (line != null) {
		line = line.trim( );

		words = line.split("\\s+");

		if (line.equals(""))
		    continue;
	    }

	    if (line == null || line.equals(".") || line.equals("..")) {
		int size = item_list.size();
		if (size > 0) {
		    int[] state_seq = new int[size];
		    int[] output_seq = new int[size];
		
		    for (int i = 0; i < size; i++) {
			DataItem d = item_list.get(i);
			state_seq[i] = d.state;
			output_seq[i] = d.output;
		    }

		    all_state_seq.add(state_seq);
		    all_output_seq.add(output_seq);
		    
		    item_list.clear();
		}	
	    } else {
		String s = words[0];

		if (!state_map.containsKey(s)) {
		    state_map.put(s, new Integer(numStates));
		    numStates++;
		}
		int cur_state = state_map.get(s).intValue();

		for (int i = 1; i < words.length; i++) {
		    String o = words[i];
		    
		    if (!output_map.containsKey(o)) {
			output_map.put(o, new Integer(numOutputs));
			numOutputs++;
		    }
		    int cur_output = output_map.get(o).intValue();

		    item_list.add(new DataItem(cur_state, cur_output));
		}
	    }

	    if (line == null) {
		if (!test_mode) {
		    System.err.println("Formatting error in file"+filename+
				       ": should contain one line with ..");
		    throw new IOException();
		}

		testState = all_state_seq.toArray(new int[0][]);
		testOutput = all_output_seq.toArray(new int[0][]);

		break;
	    }

	    if (line.equals("..")) { // switch to test sequences
		if (test_mode) {
		    System.err.println("Formatting error in file"+filename+
				       ": should not contain two lines with ..");
		    throw new IOException();
		}

		test_mode = true;

		trainState = all_state_seq.toArray(new int[0][]);
		trainOutput = all_output_seq.toArray(new int[0][]);

		all_state_seq.clear();
		all_output_seq.clear();
	    }
	}

	String s = null;

	stateName = new String[numStates];
	Iterator<String> it = state_map.keySet().iterator();
	while (it.hasNext()) {
	    s = (String) it.next();
	    stateName[state_map.get(s).intValue()] = s;
	}
	
	outputName = new String[numOutputs];
	it = output_map.keySet().iterator();
	while (it.hasNext()) {
	    s = it.next();
	    outputName[output_map.get(s).intValue()] = s;
	}

    }

}
