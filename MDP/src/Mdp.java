import java.util.*;
import java.util.zip.*;
import java.util.Vector;
import java.io.*;

/**
 * This class represents an MDP, and includes a constructor that can
 * read the MDP from a data file.  If you want to create your own
 * MDP's, you can do so by creating data files that are read by the
 * provided constructor (see assignment instructions), or by creating
 * subclasses of this class in which all of the needed fields getted
 * filled in.
 */
public class Mdp {

    /** total number of states */
    public int numStates;

    /** total number of actions */
    public int numActions;

    /** an array containing the names of all of the states */
    public String stateName[];

    /** an array containing the names of all of the actions */
    public String actionName[];

    /** an array containing the (immediate) reward associated with
     * every state **/
    public double reward[];

    /** a list of the possible states that can be reached from each
     * state under each action **/
    public int nextState[][][];

    /** a table of transition probabilities:
     * <tt>transProb[s][a][i]</tt> is the probability of transitioning
     * from state <tt>s</tt> under action <tt>a</tt> to state
     * <tt>nextState[s][a][i]</tt>. **/
    public double transProb[][][];

    /** the start state **/
    public int startState = -1;

    /** This constructor reads in data from <tt>filename</tt> and sets
     * up all of the public fields.  See assignment instructions for
     * information on the required format of this file.  This
     * constructor will properly read data from a gzipped file if
     * <tt>filename</tt> ends with a ".gz" suffix.
     */
    public Mdp(String filename) throws FileNotFoundException, IOException {
	LineNumberReader in;
	String line;
	Map<String, Integer> state_map = new HashMap<String, Integer>();
	Map<String, Integer> action_map = new HashMap<String, Integer>();
	Vector1d<Double> reward_vec = new Vector1d<Double>();
	Vector2d<Map<Integer, Double>> trans_prob_vec =
	    new Vector2d<Map<Integer, Double>>();

	try {
	    if (filename.endsWith(".gz"))
		in = new LineNumberReader(
                       new InputStreamReader(
                          new GZIPInputStream(
                             new FileInputStream(filename))));
	    else
		in = new LineNumberReader(new FileReader(filename));
	} catch (FileNotFoundException e) {
	    System.err.print("File "+filename+" not found.\n");
	    throw e;
	}

	while (true) {
	    try {
		line = in.readLine();
	    }
	    catch (IOException e) {
		System.err.println("Error reading file "+filename);
		throw e;
	    }

	    String[] words = null;

	    if (line == null)
		break;

	    line = line.trim( );

	    words = line.split("\\s+");

	    if (line.equals(""))
		continue;

	    if (words.length == 1) { // start state
		startState = getId(state_map, words[0]);
	    } else if (words.length == 2) { // reward function entry
		int s = getId(state_map, words[0]);
		Double r;
		try {
		    r = new Double(words[1]);
		} catch (NumberFormatException e) {
		    System.err.println("Expected number in file "+filename+
				       " at line " + in.getLineNumber()
				       + ": " + line);
		    throw e;
		}
		reward_vec.set(s, r);
	    } else if (words.length >= 4 && words.length % 2 == 0) { // transition prob. entry
		int s = getId(state_map, words[0]);
		int a = getId(action_map, words[1]);

		for (int i = 2; i < words.length; i += 2) {
		    double p;
		    try {
			p = Double.parseDouble(words[i+1]);
		    } catch (NumberFormatException e) {
			System.err.println("Expected number in file "+filename+
					   " at line " + in.getLineNumber()
					    + ": " + line);
			throw e;
		    }
		    if (p < 0.) {
			String err = "Probabilities must be nonnegative in file " +
			    filename + " at line " + in.getLineNumber()
			    + ": " + line;
			System.err.println(err);
			throw new RuntimeException(err);
		    }

		    Map<Integer, Double> m = trans_prob_vec.get(s, a);
		    if (m == null) {
			m = new TreeMap<Integer, Double>();
			trans_prob_vec.set(s, a, m);
		    }
		    Integer ts = new Integer(getId(state_map, words[i]));
		    double old_val = (m.containsKey(ts)
				      ? ((Double) m.get(ts)).doubleValue()
				      : 0.0);
		    m.put(ts, new Double(old_val + p));
		}
	    } else {
		String err = "Badly formatted data in " +
		    filename + " at line " + in.getLineNumber() + ": " + line;
		System.err.println(err);
		throw new RuntimeException(err);
	    }
	}

	if (startState < 0) {
	    String err = "No start state provided in " + filename;
	    System.err.println(err);
	    throw new RuntimeException(err);
	}	    

	stateName = mapToArray(state_map);
	numStates = stateName.length;

	actionName = mapToArray(action_map);
	numActions = actionName.length;

	reward = new double[numStates];

	for (int i = 0; i < numStates; i++) {
	    Double o = reward_vec.get(i);
	    if (o != null)
		reward[i] = o.doubleValue();
	}
	
	transProb = new double[numStates][numActions][];
	nextState = new int[numStates][numActions][];

	for (int fs = 0; fs < numStates; fs++)
	    for (int a = 0; a < numActions; a++) {
		Map<Integer, Double> m = trans_prob_vec.get(fs, a);
		if (m == null) {
		    String err = "State " + stateName[fs] + " with action " +
			actionName[a] +
			" did not have any transitions in file " +
			filename;
		    System.err.println(err);
		    throw new RuntimeException(err);
		}
		int size = m.size();
		transProb[fs][a] = new double[size];
		nextState[fs][a] = new int[size];
		Iterator it = m.keySet().iterator();
		double sum = 0.0;
		for (int i = 0; i < size; i++) {
		    Integer ts = (Integer) it.next();
		    nextState[fs][a][i] = ts.intValue();
		    sum += transProb[fs][a][i] = m.get(ts).doubleValue();
		}
		if (sum <= 0.0) {
		    String err = "State " + stateName[fs] + " with action " +
			actionName[a] +
			" must have positive transition probability in file " +
			filename;
		    System.err.println(err);
		    throw new RuntimeException(err);
		}
		for (int i = 0; i < size; i++)
		    transProb[fs][a][i] /= sum;
	    }
	in.close();
    }

    private class Vector1d<T> {
	private Vector<T> v;

	private Vector1d() {
	    v = new Vector<T>();
	}

	private void set(int i, T o) {
	    if (i >= v.size())
		v.setSize(i+1);
	    v.set(i, o);
	}

	private T get(int i) {
	    if (i >= v.size())
		return null;
	    return v.get(i);
	}
    }

    private class Vector2d<T> {
	private Vector1d<Vector1d<T>> v1;

	private Vector2d() {
	    v1 = new Vector1d<Vector1d<T>>();
	}

	private void set(int i, int j, T o) {
	    Vector1d<T> v2 = v1.get(i);
	    if (v2 == null) {
		v2 = new Vector1d<T>();
		v1.set(i, v2);
	    }
	    v2.set(j, o);
	}

	private T get(int i, int j) {
	    Vector1d<T> v2 = v1.get(i);
	    return (v2 == null ? null : v2.get(j));
	}
    }

    private int getId(Map<String, Integer> m, String s) {
	if (!m.containsKey(s)) {
	    int n = m.size();
	    m.put(s, new Integer(n));
	}
	return m.get(s).intValue();
    }

    private String[] mapToArray(Map<String, Integer> m) {
	String a[] = new String[m.size()];
	Iterator it = m.keySet().iterator();
	while (it.hasNext()) {
	    String s = (String) it.next();
	    a[m.get(s).intValue()] = s;
	}
	return a;
    }


}
