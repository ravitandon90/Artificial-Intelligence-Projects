import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * This class provides a simple main for running on cat and mouse
 * data.
 */
public class RunHPMdp {

    /** This is a simple main.  When invoked, it does the following:
     * (1) loads an MDP from a file named in the command line; (2)
     * runs value iteration and policy iteration; (3) runs policy
     * evaluation on the policy returned by policy iteration; (4)
     * prints out the optimal policy and utilities returned by both
     * value iteration and policy iteration; and (5) animates the cat
     * chasing the mouse, depending on the command-line arguments.
     * (Note that this animation will almost certainly crash if run on
     * MDP's other than those for a cat chasing a mouse.)
     *
     * <p>The command-line arguments should consist of a possible list
     * of options, followed by the name of a data file containing a
     * description of the MDP.  By default, a GUI (graphical user
     * interface) based animation will be invoked.  However, this can
     * be changed with the appropriate command-line options: Using the
     * "<tt>-b</tt> option will run the GUI while simultaneously
     * printing a transcript of all states visited.  Using the <tt>-p
     * &lt;num&gt;</tt> option will not invoke the GUI at all but will
     * instead run the MDP for <tt>&lt;num&gt;</tt> steps, while printing
     * the results.  Finally, using the <tt>-n</tt> option will
     * neither invoke the GUI nor print any results.
     *
     * <p>It is okay to change this main as you wish.  However, your
     * code should still work properly when using this one.
     * @throws InterruptedException 
     */
    public static void main(String argv[])
	throws FileNotFoundException, IOException, InterruptedException {

	double discount = 0.95;

	// parse options
	Options options = null;
	try {
	    options = new Options(argv);
	} catch (Exception e) {
	    printCommandLineError();
	    return;
	}

	// build MDP
	Mdp mdp = new Mdp(options.filename);

	
	long startTime = System.currentTimeMillis();	
	// run value iteration
	ValueIteration vpi = new ValueIteration(mdp, discount);
	long endTime   = System.currentTimeMillis();
	long totalTime_vpi = endTime - startTime;
	
	startTime = System.currentTimeMillis();
	// run policy iteration
	PolicyIteration ppi = new PolicyIteration(mdp, discount);
	endTime   = System.currentTimeMillis();
	long totalTime_ppi = endTime - startTime;
	
	// evaluate returned policy
	double[] util =
	    (new PolicyEvaluation(mdp, discount, ppi.policy)).utility;

	// print results
	System.out.println("Optimal policies:");
	for(int s = 0; s < mdp.numStates; s++)
	    System.out.printf(" %-12s  %-4s  %-4s  %17.12f  %17.12f\n",
		//System.out.printf(" %-12s  %-4s  %17.12f \n",
			      mdp.stateName[s],
			      mdp.actionName[vpi.policy[s]],
			      mdp.actionName[ppi.policy[s]],
			      vpi.utility[s],
			      util[s]);
	System.out.println();
	System.out.println();
	System.out.println ("Total Time Taken: Value Iteration, Policy Iteration : " + totalTime_vpi + ","+ totalTime_ppi);

	// animate cat chasing mouse

	if (options.mode == NO_ANIMATION)
	    return;
	
	AnimateHighway animateHighway = new AnimateHighway(mdp, ppi.policy);
	
	/*CatMouseAnimator animator = new CatMouseAnimator(mdp);
	MdpSimulator simulator = new FixedPolicySimulator(mdp, ppi.policy);

	switch (options.mode) {
	case GUI_ONLY:
	    animator.animateGuiOnly(simulator);
	    break;
	case GUI_WITH_TRANS:
	    animator.animateGuiAndPrint(simulator);
	    break;
	case PRINT_ONLY:
	    animator.animatePrintOnly(simulator, options.anim_steps);
	    break;
	}*/

    }

    // private stuff for parsing command line options and printing
    // error messages

    private static final int GUI_ONLY       = 0;
    private static final int GUI_WITH_TRANS = 1;
    private static final int PRINT_ONLY     = 2;
    private static final int NO_ANIMATION   = 3;

    private static class Options {
	private String filename = null;
	private int mode = GUI_ONLY;
	private int anim_steps = 0;

	private Options(String argv[]) {
	    for (int i = 0; i < argv.length; i++) {
		if (argv[i].equals("-g")) {
		    mode = GUI_ONLY;
		} else if (argv[i].equals("-p")) {
		    mode = PRINT_ONLY;
		    anim_steps = Integer.parseInt(argv[++i]);
		} else if (argv[i].equals("-b")) {
		    mode = GUI_WITH_TRANS;
		} else if (argv[i].equals("-n")) {
		    mode = NO_ANIMATION;
		} else if (filename == null) {
		    filename = argv[i];
		} else
		    throw new RuntimeException("filename specified twice");
	    }
	    if (filename == null)
		throw new RuntimeException("no filename specified");
	}

    }

    private static void printCommandLineError() {
	System.err.println("error parsing command-line arguments.");
	System.err.println("arguments: [options] <filename>");
	System.err.println("  options:  -g         run GUI only, but do not print results (default)");
	System.err.println("            -p <num>   do not invoke GUI, but print results for <num> steps");
	System.err.println("            -b         run GUI, and also print results");
	System.err.println("            -n         do not invoke GUI and do not print results");
    }

}
