import java.util.*;
import java.io.*;
import javax.swing.*;          
import javax.swing.event.*;          
import javax.swing.border.*;          
import java.awt.*;
import java.awt.event.*;

/**
 * This class can be used to animate the interaction between a cat and
 * mouse.  The MDP is simulated with the sequence of states being
 * determined by a given <tt>MdpSimulator</tt> object.  The results
 * are either printed to standard output (or another chosen print
 * stream), or they are displayed using a GUI (graphical user
 * interface), or both.
 */
public class CatMouseAnimator {

    /**
     * The constructor for this class for printing results to standard
     * output.  This constructor takes as input an <tt>Mdp</tt>, which
     * must correspond to a cat and mouse world; otherwise, the
     * constructor will almost certainly crash.  Any printed results
     * get sent to standard output.
     */
    public CatMouseAnimator(Mdp mdp) {
	this(mdp, System.out);
    }

    /**
     * The constructor for this class for sending printed results to a
     * given print stream.  This constructor takes as input an
     * <tt>Mdp</tt>, which must correspond to a cat and mouse world;
     * otherwise, the constructor will almost certainly crash.  The
     * constructor also takes a second argument specifying a
     * <tt>PrintStream</tt> to which all of the results are to be
     * printed.
     */
    public CatMouseAnimator(Mdp mdp,
			    PrintStream out) {
	this.mdp = mdp;
	this.out = out;

	cmp = new CatMousePosition[mdp.numStates];

	for (int s = 0; s < mdp.numStates; s++) {
	    CatMousePosition cm = cmp[s] = new CatMousePosition(s);

	    if (cm.mx < minx) minx = cm.mx;
	    if (cm.cx < minx) minx = cm.cx;
	    if (cm.my < miny) miny = cm.my;
	    if (cm.cy < miny) miny = cm.cy;

	    if (cm.mx > maxx) maxx = cm.mx;
	    if (cm.cx > maxx) maxx = cm.cx;
	    if (cm.my > maxy) maxy = cm.my;
	    if (cm.cy > maxy) maxy = cm.cy;
	}	    

	lenx = maxx - minx + 1;
	leny = maxy - miny + 1;

	cat_legal = new boolean[lenx][leny];
	mouse_legal = new boolean[lenx][leny];

	for (int s = 0; s < mdp.numStates; s++) {
	    CatMousePosition cm = cmp[s];

	    mouse_legal[cm.mx - minx][cm.my - miny] = true;
	    cat_legal[cm.cx - minx][cm.cy - miny] = true;
	}


    }

    /**
     * This method will print the results of running the given MDP
     * (provided to the constructor) for <tt>numSteps</tt> steps,
     * following the sequence of states provided by the given
     * <tt>simulator</tt>.  The results are printed to standard output
     * (or another print stream specified when the class was
     * constructed).  The GUI is not invoked.
     */
    public void animatePrintOnly(MdpSimulator simulator, int numSteps) {
	for (int i = 0; i < numSteps; i++) {
	    print_state(simulator.nextState());
	}
    }

    /**
     * This method invokes a graphical animation of the given MDP
     * (provided to the constructor) according to the state sequence
     * provided by the given <tt>simulator</tt>.  A transcript of the
     * animation is not generated.
     */
    public void animateGuiOnly(MdpSimulator simulator) {
	animateGuiOnly(simulator, DEFAULT_GUI_TITLE);
    }

    /**
     * This method invokes a graphical animation of the given MDP
     * (provided to the constructor) according to the state sequence
     * provided by the given <tt>simulator</tt>, and a transcript of
     * the animation is simultaneously printed to standard output (or
     * another print stream specified when the class was constructed).
     */
    public void animateGuiAndPrint(MdpSimulator simulator) {
	animateGuiAndPrint(simulator, DEFAULT_GUI_TITLE);
    }

    /**
     * This method invokes a graphical animation of the given MDP
     * (provided to the constructor) according to the state sequence
     * provided by the given <tt>simulator</tt>, using the provided
     * title.  A transcript of the animation is not generated.
     */
    public void animateGuiOnly(MdpSimulator simulator, String title) {
	animateGui(simulator, false, title);
    }

    /**
     * This method invokes a graphical animation of the given MDP
     * (provided to the constructor) according to the state sequence
     * provided by the given <tt>simulator</tt>, with the given title;
     * a transcript of the animation is simultaneously printed to
     * standard output (or another print stream specified when the
     * class was constructed).
     */
    public void animateGuiAndPrint(MdpSimulator simulator, String title) {
	animateGui(simulator, true, title);
    }

    // private stuff

    private Mdp mdp;
    private PrintStream out;
    private CatMousePosition[] cmp;

    private int minx = Integer.MAX_VALUE;
    private int miny = Integer.MAX_VALUE;
    private int maxx = Integer.MIN_VALUE;
    private int maxy = Integer.MIN_VALUE;
    private int lenx, leny;
    private boolean cat_legal[][];
    private boolean mouse_legal[][];

    private static final String CAT_SYMB = "C";
    private static final String CHEESE_SYMB = "z";
    private static final String MOUSE_ON_CHEESE_SYMB = "M";
    private static final String MOUSE_OFF_CHEESE_SYMB = "m";

    private class CatMousePosition {
	private int mx, my, cx, cy, zx, zy;
	private boolean cheese_present;
	private CatMousePosition(int s) {
	    String[] coord = mdp.stateName[s].split(":");

	    mx = Integer.parseInt(coord[0]);
	    my = Integer.parseInt(coord[1]);
	    cx = Integer.parseInt(coord[2]);
	    cy = Integer.parseInt(coord[3]);
	    if (coord.length < 6) {
		cheese_present = false;
		zx = zy = 0;
	    } else {
		cheese_present = true;
		zx = Integer.parseInt(coord[4]);
		zy = Integer.parseInt(coord[5]);
	    }
	}
    }
	    
    private String separator = null;

    private boolean first_printed_state = true;

    private void print_state(int s) {
	CatMousePosition cm = cmp[s];
	String indent = "    ";

	if (separator == null) {
	    separator = indent + "+=";
	    for (int x = minx; x <= maxx; x++)
		separator += "==";
	    separator += "+";
	}

	if (first_printed_state) {
	    out.println(separator);
	    first_printed_state = false;
	}

	String mouse_text = (cm.mx == cm.zx && cm.my == cm.zy
			     ? MOUSE_ON_CHEESE_SYMB
			     : MOUSE_OFF_CHEESE_SYMB);

	for (int y = maxy; y >= miny; y--) {
	    out.print(indent + "| ");
	    for (int x = minx; x <= maxx; x++) {
		out.print((!cat_legal[x-minx][y-miny]
			   && !mouse_legal[x-minx][y-miny])
			  ? " "
			  : (x == cm.cx && y == cm.cy
			     ? CAT_SYMB
			     : (x == cm.mx && y == cm.my
				? mouse_text
				: (x == cm.zx && y == cm.zy
				   ? CHEESE_SYMB
				   : (cat_legal[x-minx][y-miny]
				      ? "."
				      : "_")))));
		out.print(" ");
	    }
	    out.println("|");
	}

	out.println(separator);

    }

    // gui animation stuff

    private static final Font font = new Font("SansSerif", Font.BOLD, 36);
    private static final Border grid_border =
	BorderFactory.createLineBorder(Color.black, 1);
    private static final Color color_main_bg = Color.white;
    private static final Color color_mouse_hole_bg = Color.lightGray;
    private static final Color color_blocked_bg = Color.black;
    private static final Color color_fg = Color.black;
    private static final Border empty_border =
	BorderFactory.createEmptyBorder(10,10,10,10);

    private static final int SLIDER_MAX = 10000;
    private static final int DELAY_MIN = 50;
    private static final int DELAY_MAX = 2000;

    private static final String DEFAULT_GUI_TITLE = "Cat and mouse";

    private void animateGui(final MdpSimulator simulator,
			    final boolean printingOn,
			    final String title) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		GuiApp app = new GuiApp(simulator, printingOn);
		Component contents = app.createComponents();
		frame.getContentPane().add(contents, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
            }
        });
    }

    private class GuiApp {

	private MdpSimulator simulator;
	private boolean printingOn;
	private JLabel label[][];
	private javax.swing.Timer timer;
	private JSlider slider;
	private int cur_state;
	private JButton start_button;
	private JButton stop_button;
	private JButton step_button;


	private GuiApp(MdpSimulator simulator, boolean printingOn) {
	    this.simulator = simulator;
	    this.printingOn = printingOn;
	}

	private Component createComponents() {
	    JPanel grid_pane = new JPanel(new GridLayout(leny, lenx));
	    label = new JLabel[lenx][leny];
	    for(int y = leny-1; y >= 0; y--) {
		for(int x = 0; x < lenx; x++) {
		    label[x][y] = new JLabel(" ", SwingConstants.CENTER);
		    if (cat_legal[x][y] || mouse_legal[x][y]) {
			label[x][y].setBorder(grid_border);
			label[x][y].setFont(font);
			Color c = (cat_legal[x][y] ?
				   color_main_bg :
				   (mouse_legal[x][y] ?
				    color_mouse_hole_bg :
				    color_blocked_bg));
			label[x][y].setBackground(c);
			label[x][y].setForeground(color_fg);
			label[x][y].setOpaque(true);
		    }
		    grid_pane.add(label[x][y]);
		}
	    }

	    grid_pane.setBorder(empty_border);

	    JPanel main_pane = new JPanel();
	    main_pane.setLayout(new BoxLayout(main_pane, BoxLayout.PAGE_AXIS));
	    main_pane.add(grid_pane);

	    JPanel button_pane = new JPanel();

	    button_pane.setBorder(empty_border);

	    main_pane.add(button_pane);

	    step_button = new JButton("step");
	    step_button.addActionListener(new StepActionListener());

	    start_button = new JButton("start");
	    start_button.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			timer.start();
			step_button.setEnabled(false);
			stop_button.setEnabled(true);
			start_button.setEnabled(false);
		    }
		}
					   );

	    stop_button = new JButton("stop");
	    stop_button.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			timer.stop();
			step_button.setEnabled(true);
			stop_button.setEnabled(false);
			start_button.setEnabled(true);
		    }
		}
					  );

	    stop_button.setEnabled(false);

	    button_pane.add(start_button, BorderLayout.LINE_START);
	    button_pane.add(stop_button, BorderLayout.CENTER);
	    button_pane.add(step_button, BorderLayout.LINE_END);

	    slider = new JSlider(JSlider.HORIZONTAL, 0,
				 SLIDER_MAX, SLIDER_MAX/2);

	    slider.setBorder(empty_border);

	    Dictionary<Integer, JLabel> dict =
		new Hashtable<Integer, JLabel>();
	    dict.put(new Integer(0), new JLabel("slow"));
	    dict.put(new Integer(SLIDER_MAX), new JLabel("fast"));
	    slider.setLabelTable(dict);

	    slider.setPaintLabels(true);


	    slider.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
			    set_timer_delay();
			}
		    }
		}
				     );

	    main_pane.add(slider);

	    timer = new javax.swing.Timer(1000, new StepActionListener());

	    set_timer_delay();

	    cur_state = simulator.nextState();
	    show_cur_state();

	    return main_pane;
	}

	private void set_timer_delay() {
	    double v = slider.getValue() / ((double) SLIDER_MAX);
	    int d = ((int) (v*DELAY_MIN + (1. - v)*DELAY_MAX));
	    timer.setDelay(d);
	}

	private class StepActionListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
		cur_state = simulator.nextState();
		show_cur_state();
	    }
	}

	private CatMousePosition cm = null;

	private void show_cur_state() {
	    if (printingOn)
		print_state(cur_state);

	    if (cm != null) {
		label[cm.mx-minx][cm.my-miny].setText(" ");
		label[cm.cx-minx][cm.cy-miny].setText(" ");
		if (cm.cheese_present) {
		    label[cm.zx-minx][cm.zy-miny].setText(" ");
		    label[cm.zx-minx][cm.zy-miny].setBackground(color_main_bg);
		}
	    }


	    cm = cmp[cur_state];
	    if (cm.cheese_present) {
		label[cm.zx-minx][cm.zy-miny].setText(CHEESE_SYMB);
		label[cm.zx-minx][cm.zy-miny].setBackground(Color.yellow);
	    }
	    String mouse_text = (cm.mx == cm.zx && cm.my == cm.zy
				 ? MOUSE_ON_CHEESE_SYMB
				 : MOUSE_OFF_CHEESE_SYMB);
	    label[cm.mx-minx][cm.my-miny].setText(mouse_text);
	    label[cm.cx-minx][cm.cy-miny].setText(CAT_SYMB);
	}

    }

}
