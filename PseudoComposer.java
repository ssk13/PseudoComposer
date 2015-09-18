/* PseudoComposer.java
   Composes a short piece based on a melodic motive, input from a user

   by Sarah Klein
*/

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
import javax.swing.AbstractButton.*;
import javax.swing.SwingUtilities.*;
import org.jfugue.*; 

/*
	Initialize the width and height of the application,
	implement the main method (which creates the jframe, 
	sets the close behavior, and makes the frame visible)
*/
public class PseudoComposer {
	//width and height of application
	private static final int WIDTH =  485;
	private static final int HEIGHT = 375;

	public static void main (String[] args) {
		SwingUtilities.invokeLater(new Runnable () {
			public void run() {
				JFrame frame = new ImageFrame (WIDTH, HEIGHT);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}

/*
	The ImageFrame that contains the application - basically a JFrame
*/
class ImageFrame extends JFrame {

	//visual application components
	private final JFileChooser chooser;
	JPanel appPanel;
	ImageIcon icon;
	JLabel label;
	BufferedImage image;
	int measureWidth = 460, 
		measureHeight = 169;

	//disclaimer that explains the application
	JOptionPane disclaimer;

	//program components
	Measure entry;
	Chorale composition;
	Boolean playing = false;
	Player player = new Player();
	String playerString = "";
	String instrument = "ACOUSTIC_GRAND"; 
	int tempo = 120, 
		minNotes = 3;	//minimum number of notes needed to composer a work

	/*
		Constructor
		Sets the size, 
		sets the title, 
		creates a file chooser, 
		initializes the application
	*/
	public ImageFrame (int width, int height) {
		this.setTitle("PseudoComposer");					// JFrame method
		this.setSize(width, height);						// JFrame method
		this.chooser = new JFileChooser();
		this.chooser.setCurrentDirectory(new File("."));	// JFileChooser method
		initialize();
	}

	/*
		Initializes all of the components of the JFrame (the JPanels),
		creates the JOptionPane for the disclaimer
		creates the 'entry' measure in which users can enter notes,
		draws the entry,
		sets up the menus,
		adds the mouse listeners
		sets up the images,
		draws the application components to the screen
	*/
	public void initialize () {
		// set up the appPanel's layout and create the disclaimer
		this.appPanel = new JPanel(new BorderLayout());
		getContentPane().add(this.appPanel);
		this.disclaimer = new JOptionPane();

		this.entry = new Measure();
		this.entry.drawMeasure();
		this.image = new BufferedImage(measureWidth, measureHeight, BufferedImage.TYPE_INT_ARGB);
		this.icon = new ImageIcon(this.image);
		this.label = new JLabel(this.icon);
		this.label.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

		addMouseListeners();
		setUpMenu();
		this.appPanel.add(label, BorderLayout.PAGE_START);

		// paint our initial entry
		displayMeasure(this.entry);
	}

	/*
		Add the mouse listeners for the application, including the right-click on a measure,
		which adds a note, and the left-click, which deletes a note
	*/
	public void addMouseListeners () {
		this.label.addMouseListener( new MouseAdapter() {
 			public void mousePressed(MouseEvent event) {
 				//left-click: add note
 				if (event.getModifiers() == MouseEvent.BUTTON1_MASK)
 					addNote(event.getPoint());
 				//right-click: remove note
 				else if (event.getModifiers() == MouseEvent.BUTTON3_MASK)
					deleteNote(event.getPoint());
 			}
 		} );
	}

	/*
		Stops any music that's playing, 
		adds a note to the end of the measure,
		updates the playerString,
		repaints the measure
	*/
	public void addNote (Point p) {
		if (playing)
			stopPlaying();
		this.entry.addNote(p);
		this.playerString = this.entry.toString();
		displayMeasure(this.entry);
	}

	/*
		Changes the instrument that will be used to play the PlayerString,
		stops anything that's currently playing
	*/
	public void changeInstrument (String inst) {
		this.instrument = inst;
		if (playing)
			stopPlaying();
	}

	/*
		Stops any music that's playing, 
		deletes a note if it has been right-clicked on
		updates the playerString,
		repaints the measure
		future: only stop playing the measure and rewrite the measure if something was deleted
	*/
	public void deleteNote (Point p) {
		this.entry.deleteNote(p);
		if (playing)
			stopPlaying();
		this.playerString = this.entry.toString();
		displayMeasure(this.entry);
	}

	/*
		Paints the view from a Measure onto the label of the application
	*/
	public void displayMeasure (Measure measure) {
		this.image = measure.getView();

		SwingUtilities.invokeLater(new Runnable () {
			public void run() {
				icon.setImage( image );
				label.repaint();
				validate();
			}
		});
	}

	/*
		Prompts the user for a tempo in BPM - requires a positive integer
	*/
	private int getTempo () {
		Boolean trying = true;
		int val = 0;
		
		String stringVal = JOptionPane.showInputDialog("What tempo would you like? Your current tempo is " + this.tempo + "BPM");

		while (trying) {
			try {
				val = Integer.parseInt(stringVal);
				if (val < 1)
					throw new IllegalArgumentException();
				trying = false;
			}
			catch (NumberFormatException e) {
				stringVal = JOptionPane.showInputDialog("No, a positive number");
			}
			catch (IllegalArgumentException e) {
				stringVal = JOptionPane.showInputDialog("No, a positive number");
			}
		}
		return val;
	}

	/*
		Accesses a .txt file from the user's local machine, which should contain a valid PlayerString for the player
		Future: allow MusicXML to be converted into a JFugue PlayerString
	*/
	public void load () {
		Boolean trying = true;
		File file = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;

		// try to get a file from the user
		while (trying) {
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
				file = chooser.getSelectedFile();
			try {
      			fis = new FileInputStream(file);
      			bis = new BufferedInputStream(fis);
      			BufferedReader br = new BufferedReader(new InputStreamReader(bis));
  				this.playerString = br.readLine();
      			fis.close();
      			bis.close();
      			br.close();
      			trying = false;
			}
		 	catch (FileNotFoundException exception) {
      			JOptionPane.showMessageDialog(this, exception);
   			} 
   			catch (IOException exception) {
      			JOptionPane.showMessageDialog(this, exception);
    		}
		}
		// play the string on the file that you loaded
		play();
	}

	/*
		Writes a chorale based on the entry - most of this logic is in Chorale.java,
		plays that composition
	*/
	public void makeChorale (boolean hasEntry) {
		int noteCount = this.entry.getNoteCount();
		//don't write anything if you don't have enough notes because what's the point
		if (noteCount < this.minNotes && hasEntry)
			playCheesyToccata();
		else {
			if (hasEntry) {
				composition = new Chorale(entry, entry.getKey());
				composition.pseudoCompose();
			}
			else {
				composition = new Chorale();
				composition.pseudoComposeFromScratch();
			}
			this.playerString = composition.toString();
			play();
		}
	}

	/*
		Returns a string representing the currently saved voices, notes, instrument, and tempo
	*/
	public String makeStringWithInstrumentAndTempo () {
		Boolean changed = false;
		String newString = "";
		for (int i = 0; i != this.playerString.length(); ++i) {
			//formats the voices
			if (this.playerString.charAt(i) == 'V') {
				changed = true;
				newString += "V";
				newString += this.playerString.charAt(i + 1);
				newString += " I[" + instrument + "] T" + tempo + " ";
				i += 2;
			}
			//formats the instruments
			else if (this.playerString.charAt(i) == 'I') {
				while (this.playerString.charAt(i) != '|')
					++i;
			}
			else
				newString += playerString.charAt(i);
		}
		if (!changed)
			newString = " I[" + instrument + "] T" + tempo + " " + newString;
		this.playerString = newString;
		System.out.println(newString);
		return this.playerString;
	}

	/*
		not done
		Stops anything that's currently playing
		Creates a pattern from the current PlayerString, tempo, and instrument,
		plays this string in a new Thread
	*/
	public void play () {
		new Thread (new Runnable () {
			public void run() {
				playing = true;
				if (player.isPlaying())
					player.stop();
				Pattern pattern = new Pattern(makeStringWithInstrumentAndTempo());
		 		player.play(pattern);
			 	playing = false;
			}
		}).start(); 
	}

	/*
		plays the current entry
	*/
	public void playEntry () {
		this.playerString = entry.toString();
		play();
	}

	/*
		Plays the beginning of Bach's Toccata in Dm and asks for more notes to work with
	*/
	public void playCheesyToccata () {
		this.playerString = "T60 d2s d3s c#3s d3s a3s d3s c#3s d3s f3s d3s c#3s d3s d2s d3s c3s d3s";
		play();	
		String disc = "I'm going to need a few more notes than that to work with...";
		this.disclaimer.showMessageDialog(this, disc);	
	}

	/*
		Writes the PlayerString to a txt file in the user's current directory
		Future: Save as XML format so that it can be imported into Finale, possibly with a later prompt
		with instructions or a file format *choice*
	*/
	public void save () {
		try {
			PrintWriter transcriber = new PrintWriter("PseudoComposition.txt", "UTF-8");
			transcriber.println(this.composition.toString());
			transcriber.close();
		}
		catch (IOException e) {
		}
	}

	/*
		Returns a JMenu initialized with the offered instrument choices, organized in alphabetical
		order. The default instrument choice for the app is 'PIANO' (though 'warm' sounds better
		for chorales in my opinion...)
	*/
	public JMenu setUpInstrumentChoices () {
		JMenu changeInstrumentMenu = new JMenu("Change instrument");

		JMenuItem accordian = new JMenuItem("Accordian");
		accordian.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("ACCORDIAN");
			}
		});
		changeInstrumentMenu.add(accordian);

		JMenuItem bass = new JMenuItem("Acoustic Bass");
		bass.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("ACOUSTIC_BASS");
			}
		});
		changeInstrumentMenu.add(bass);

		JMenuItem bagpipe = new JMenuItem("Bagpipe");
		bagpipe.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("BAGPIPE");
			}
		});
		changeInstrumentMenu.add(bagpipe);

		JMenuItem banjo = new JMenuItem("Banjo");
		banjo.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("BANJO");
			}
		});
		changeInstrumentMenu.add(banjo);

		JMenuItem clarinet = new JMenuItem("Clarinet");
		clarinet.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("CLARINET");
			}
		});
		changeInstrumentMenu.add(clarinet);

		JMenuItem crystal = new JMenuItem("Crystal Glasses");
		crystal.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("CRYSTAL");
			}
		});
		changeInstrumentMenu.add(crystal);

		JMenuItem flute = new JMenuItem("Flute");
		flute.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("FLUTE");
			}
		});
		changeInstrumentMenu.add(flute);

		JMenuItem horn = new JMenuItem("French Horn");
		horn.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("FRENCH_HORN");
			}
		});
		changeInstrumentMenu.add(horn);

		JMenuItem goblin = new JMenuItem("Goblins");
		goblin.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("GOBLINS");
			}
		});
		changeInstrumentMenu.add(goblin);

		JMenuItem marimba = new JMenuItem("Marimba");
		marimba.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("MARIMBA");
			}
		});
		changeInstrumentMenu.add(marimba);

		JMenuItem oboe = new JMenuItem("Oboe");
		oboe.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("OBOE");
			}
		});
		changeInstrumentMenu.add(oboe);

		JMenuItem ocarina = new JMenuItem("Ocarina");
		ocarina.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("OCARINA");
			}
		});
		changeInstrumentMenu.add(ocarina);

		JMenuItem organ = new JMenuItem("Organ");
		organ.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("CHURCH_ORGAN");
			}
		});
		changeInstrumentMenu.add(organ);

		JMenuItem piano = new JMenuItem("Piano");
		piano.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("PIANO");
			}
		});
		changeInstrumentMenu.add(piano);

		JMenuItem steel = new JMenuItem("Steel Drums");
		steel.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("STEEL_DRUMS");
			}
		});
		changeInstrumentMenu.add(steel);

		JMenuItem voice = new JMenuItem("Steel");
		voice.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("VOICE");
			}
		});
		changeInstrumentMenu.add(voice);

		JMenuItem strings = new JMenuItem("Strings");
		strings.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("ORCHESTRAL_STRINGS");
			}
		});
		changeInstrumentMenu.add(strings);

		JMenuItem warm = new JMenuItem("Warm");
		warm.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("WARM");
			}
		});
		changeInstrumentMenu.add(warm);

		return changeInstrumentMenu;
	}

	/*
		Adds the menu items to the menu
	*/
	public void setUpMenu () {
		JMenu fileMenu = new JMenu("Options");

		JMenuItem playEntryItem = new JMenuItem("Play current entry");
		playEntryItem.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				playEntry();
			}
		});
		fileMenu.add(playEntryItem);

		JMenuItem composeChoraleItem = new JMenuItem("Compose chorale");
		composeChoraleItem.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				makeChorale(true);
			}
		});
		fileMenu.add(composeChoraleItem);

		JMenuItem composeChoraleFromScratchItem = new JMenuItem("Compose chorale from scratch");
		composeChoraleFromScratchItem.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				makeChorale(false);
			}
		});
		fileMenu.add(composeChoraleFromScratchItem);

		fileMenu.add(setUpInstrumentChoices());

		JMenuItem tempoItem = new JMenuItem("Set tempo");
		tempoItem.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				tempo = getTempo();
				if (playing)
					stopPlaying();
			}
		});
		fileMenu.add(tempoItem);

		JMenuItem save = new JMenuItem("Save last algorhythmic composition");
		save.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				save();
			}
		});
		fileMenu.add(save);

		JMenuItem load = new JMenuItem("Play a saved composition");
		load.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				load();
			}
		});
		fileMenu.add(load);

		JMenu disclaimer = new JMenu("Disclaimer");

		JMenuItem readDisclaimer = new JMenuItem("Read disclaimer");
		readDisclaimer.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				showDisclaimer();
			}
		});
		disclaimer.add(readDisclaimer);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(disclaimer);
		this.setJMenuBar(menuBar);
	}

	/*
		Opens a message dialog with a disclaimer about the application and it's well, inadequacy, because otherwise I'll feel bad
		for making it...
	*/
	public void showDisclaimer () {
		String disc = 
			"While playing with my PseudoComposer, you'll notice that its compositions tend to be precise, balanced, and \n" + 
			"predictable. My mathematical music injects random chance in a number of places, but a program could never hope to \n" +
			"introduce as many branches as were contained in the brain of JS Bach. True music comes from nuance, wit, a careful \n" +
			"hand, and - yes - a bit of math.";
		this.disclaimer.showMessageDialog(this, disc);
	}

	/*
		Stops the player
	*/
	public void stopPlaying () {
		this.player.stop();
		this.playing = false;
	}
}


	/*
		Temporarily omitted - will reinclude potentially later but don't want to lose it!
		Will write a prelude based on Bach's Prelude in his 1st cello suite
		
		public void makeSuitePrelude () {
			int noteCount = this.entry.getNoteCount();
			if (noteCount < this.minNotes)
				playCheesyPrelude();
			else {
				composition = new SuiteMovement(entry, entry.getKey(), this.instrument);
				composition.pseudoCompose();
				this.playerString = composition.toString();
				play();
			}

		public void playCheesyPrelude () {
			this.playerString = "T60 g2s d3s b3s a3s b3s d3s b3s d3s g2s d3s b3s a3s b3s d3s b3s d3s " +  
								"g2s e3s c4s b3s c4s e3s c4s e3s g2s e3s c4s b3s c4s e3s c4s e3s " + 
								"g2s f#3s c4s b3s c4s f#3s c4s f#3s g2s f#3s c4s b3s c4s f#3s c4s f#3s " + 
								"g2s g3s b3s a3s b3s g3s b3s g3s g2s g3s b3s a3s b3s g3s b3s g3s " + 
								"g2s e3s b3s a3s b3s g3s f#3s g3s e3s g3s f#3s g3s b2s d3s c#3s b2s " + 
								"c#3s g3s a3s g3s a3s g3s a3s g3s c#3s g3s a3s g3s a3s g3s a3s g3s " + 
								"f#3s a3s d4s c#4s d4s a3s g3s a3s f#3s a3s g3s a3s d3s f#3s e3s d3s " + 
								"e2s b2s g3s f#3s g3s b2s g3s b2s e2s b2s g3s f#3s g3s b2s g3s b2s " +
								"e2s c#3s d3s e3s d3s c#3s b2s a2s g3s f#3s e3s d4s c#4s b3s a3s g3s " + 
								"f#3s e3s d3s d4s a3s d4s f#3s a3s d3s e3s f#3s a3s g3s f#3s e3s d3s " + 
								"g#3s d3s f3s e3s f3s d3s g#3s d3s b3s d3s f3s e3s f3s d3s g#3s d3s " +
								"c3s e3s a3s b3s c4s a3s e3s d3s c3s e3s a3s b3s c4s a3s f#3s e3s " + 
								"d#3s f#3s d#3s f#3s a3s f#3s a3s f#3s d#3s f#3s d#3s f#3s a3s f#3s a3s f#3s " + 
								"g3s f#3s e3s g3s f#3s g3s a3s f#3s g3s f#3s e3s d3s T50 c3s T40 b2s T30 a2s T20 g2s";
			play();
			String disc = "I'm going to need a few more notes than that to work with...";
			this.disclaimer.showMessageDialog(this, disc);	
		}

	}*/