import java.text.*;
import java.io.*;

/**
 * This is a simple class for running your code.  You might want to
 * extend or modify this class, or write your own.  (But your code
 * should still work properly when run with this code.)
 **/
public class RunViterbi {

    /* won't print this HMM if more than this many states */
    private static final int MAX_STATES = 100;

    /* won't print an evidence table for this HMM if more than this
     * many outputs */
    private static final int MAX_OUTPUTS = 100;

    /**
     * A simple main that loads the dataset contained in the file
     * named in the first command-line argument, builds an HMM, prints
     * it out and finds and prints the most likely state sequence for
     * each of the output sequences.
     **/
    public static void main(String[] argv)
	throws FileNotFoundException, IOException {

	// get file name
	String file_name = "";
	try {
	    file_name = argv[0];
	} catch (Exception e) {
	    System.err.println("Arguments: <file_name>");
	    return;
	}

	// get data from given file
	DataSet ds = new DataSet(file_name);

	// build HMM from given data
	Hmm h = new Hmm(ds.numStates, ds.numOutputs,
			ds.trainState, ds.trainOutput);

	// print HMM, unless too big
	if (ds.numStates <= MAX_STATES) {

	    System.out.println("Start probabilities:");
	    for (int s = 0; s < ds.numStates; s++)
		System.out.println(name(ds.stateName[s]) + ": " +
				   num(Math.exp(h.getLogStartProb(s))));

	    System.out.println();
	    System.out.println("Transition probabilities:");

	    System.out.print(name("") + " ");
	    for (int s = 0; s < ds.numStates; s++)
		System.out.print(" " + name(ds.stateName[s]));

	    System.out.println();
	    for (int s = 0; s < ds.numStates; s++) {
		System.out.print(name(ds.stateName[s]) + ":");
		for (int u = 0; u < ds.numStates; u++) {
		    System.out.print(" "
				   + num(Math.exp(h.getLogTransProb(s,u))));
		}
		System.out.println();
	    }

	    if (ds.numOutputs <= MAX_OUTPUTS) {

		System.out.println();
		System.out.println("Output probabilities:");

		System.out.print(name("") + " ");
		for (int o = 0; o < ds.numOutputs; o++)
		    System.out.print(" " + name(ds.outputName[o]));

		System.out.println();
		for (int s = 0; s < ds.numStates; s++) {
		    System.out.print(name(ds.stateName[s]) + ":");
		    for (int o = 0; o < ds.numOutputs; o++) {
			System.out.print(" "
				   + num(Math.exp(h.getLogOutputProb(s,o))));
		    }
		    System.out.println();
		}
	    }
	}
	
	// create Viterbi object for computing most likely sequences
	Viterbi v = new Viterbi(h);
	//PrintWriter writer_e = new PrintWriter("error_file.txt", "UTF-8");
	//PrintWriter writer_ne = new PrintWriter("non-error_file.txt", "UTF-8");
	System.out.println();
	double[] errorFraction = new double[ds.testOutput.length];
	double errorFractionSum = 0;
	int[] errorCount = new int[ds.numStates];
	int errorCountTotal = 0;
	// compute and print most likely sequence for each test sequence
	for (int i = 0; i < ds.testOutput.length; i++) {
	    int[] state = v.mostLikelySequence(ds.testOutput[i]);
	    int errors = 0;

	    //System.out.println();
	    //System.out.println("sequence "+i+":");
	    for (int j = 0; j < state.length; j++) {
		//System.out.println ("stateName=" + ds.stateName[state[j]].trim());
		//System.out.println (ds.stateName[state[j]].trim().equalsIgnoreCase("_"));
	    /*if (ds.stateName[state[j]].trim().equalsIgnoreCase("_")){
			if (isError == false){
				writer_ne.println(str);
			} else {
				writer_e.println (str);
			}
			str ="";
			isError = false;
		}*/
	    if (state[j] != ds.testState[i][j])
	    System.out.println(ds.stateName[ds.testState[i][j]]+"\t"+
				   ds.stateName[state[j]] +"\t"+
				   ds.outputName[ds.testOutput[i][j]] + "\t" + (state[j] == ds.testState[i][j]));
	    	//str = str + ds.stateName[ds.testState[i][j]];
		if (state[j] != ds.testState[i][j]){
		    errors++;
		    errorCount [ds.testState[i][j]] += 1; 
		    errorCountTotal++;
		   // isError = true;
		}
	    }
	    System.out.println("errors: " + errors + " / " + state.length +
			       " = " + ((double) errors)/state.length);
	    errorFraction[i] = ((double) errors)/state.length;
	    errorFractionSum += errorFraction[i]; 
	}
	System.out.println ("Average Error = " + errorFractionSum / ds.testOutput.length);
	for (int count = 0; count < ds.numStates; count++){
		System.out.println ("State Name : "  +ds.stateName[count] + ", ErrorCount: " + (double) errorCount[count] / errorCountTotal);
	}
	//writer_e.close();
	//writer_ne.close();
	//System.out.println ("Standard Deviation = + " getStandardDeviation (errorFraction));
    }

    // private print formatting stuff
    private static NumberFormat nf = new DecimalFormat("#.000");

    private static String name(String s) {
	return (s + " " + " " + " " + " ").substring(0, 4);
    }

    private static String num(double d) {
	return nf.format(d);
    }

}
	    
