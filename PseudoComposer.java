/* 
	PseudoComposer.java
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
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
	JLabel directions,
			buttons,
			background,
			musicCard,
			measuresLabel;
	JRadioButton cantusFirmusButton,
				 counterpointInFirstSpeciesButton,
				 counterpointInSecondSpeciesButton,
				 counterpointInThirdSpeciesButton;
	JButton pseudoComposeButton,
			landingPageButton;
	ButtonGroup composeButtons;

	//style variables
	String fontName = "Helvetica";

	//for drawing measures
	BufferedImage[] measures;
	ImageIcon[] measureIcons;
	JLabel[] measureLabels;
	Graphics2D g2d;
	BufferedImage clef;	
	GridBagConstraints gridBagConstraints;

	//disclaimer that explains the application
	JOptionPane disclaimer;

	//program components
	Boolean playing = false;
	Player player = new Player();
	String playerString = "";
	String instrument = "ACOUSTIC_GRAND"; 
	int tempo = 120, 
		minNotes = 3;	//minimum number of notes needed to composer a work
	CantusFirmus cantusFirmus;

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
		sets up the menus,
		adds the mouse listeners
		sets up the images,
		draws the application components to the screen
	*/
	public void initialize () {
		// set up the appPanel's layout and create the disclaimer
		this.appPanel = new JPanel(new CardLayout());
		getContentPane().add(this.appPanel);
		this.disclaimer = new JOptionPane();
		this.background = new JLabel(new ImageIcon("background.jpg"));
		this.directions = new JLabel("Hello - I am your PseudoComposer! What would you like me to 'compose' for you?", SwingConstants.CENTER);
		directions.setFont(new Font(fontName, Font.PLAIN, 24));

		setUpMenu();

		this.composeButtons = new ButtonGroup();
		this.cantusFirmusButton = new JRadioButton("Compose a canuts firmus");
		this.cantusFirmusButton.setOpaque(false);
		this.cantusFirmusButton.setContentAreaFilled(false);
		this.counterpointInFirstSpeciesButton = new JRadioButton("Compose first species counterpoint");
		this.counterpointInFirstSpeciesButton.setOpaque(false);
		this.counterpointInFirstSpeciesButton.setContentAreaFilled(false);
		this.counterpointInSecondSpeciesButton = new JRadioButton("Compose second species");
		this.counterpointInSecondSpeciesButton.setOpaque(false);
		this.counterpointInSecondSpeciesButton.setContentAreaFilled(false);
		this.counterpointInThirdSpeciesButton = new JRadioButton("Compose third species");
		this.counterpointInThirdSpeciesButton.setOpaque(false);
		this.counterpointInThirdSpeciesButton.setContentAreaFilled(false);

		this.composeButtons.add(cantusFirmusButton);
		this.composeButtons.add(counterpointInFirstSpeciesButton);
		this.composeButtons.add(counterpointInSecondSpeciesButton);
		this.composeButtons.add(counterpointInThirdSpeciesButton);

		this.buttons = new JLabel();
		this.buttons.setLayout(new GridLayout(1,4));
		this.buttons.add(cantusFirmusButton);
		this.buttons.add(counterpointInFirstSpeciesButton);
		this.buttons.add(counterpointInSecondSpeciesButton);
		this.buttons.add(counterpointInThirdSpeciesButton);

		this.pseudoComposeButton = new JButton("PseudoCompose");
		this.pseudoComposeButton.setOpaque(false);
		this.pseudoComposeButton.setContentAreaFilled(false);

		drawLandingPage();

		this.musicCard = new JLabel();
		this.musicCard.setLayout(new BorderLayout());
		this.musicCard.setBackground(Color.WHITE);

		this.measuresLabel = new JLabel();
		this.measuresLabel.setLayout(new GridBagLayout());

		this.landingPageButton = new JButton("PseudoCompose Again");

		this.musicCard.add(measuresLabel, BorderLayout.CENTER);
		this.musicCard.add(landingPageButton, BorderLayout.SOUTH);

		appPanel.add(musicCard, "MusicCard");

		setUpActionListeners();
	}

	/*
		Sets up the action listeners for the buttons
	*/
	public void setUpActionListeners() {
		this.pseudoComposeButton.addActionListener(new ActionListener() {          
		    public void actionPerformed(ActionEvent e) {
		        if (cantusFirmusButton.isSelected()) {
		        	makeCantusFirmus(true);
		        } else if (counterpointInFirstSpeciesButton.isSelected()) {
		        	makeFirstSpeciesCounterpoint();
		        } else if (counterpointInSecondSpeciesButton.isSelected()) {
		        	makeSecondSpeciesCounterpoint();
		        } else if (counterpointInThirdSpeciesButton.isSelected()) {
		        	makeThirdSpeciesCounterpoint();
		        }
		        drawMusic();
		    }
		}); 

		this.landingPageButton.addActionListener(new ActionListener() {          
		    public void actionPerformed(ActionEvent e) {
		        goToLandingPage();
		    }
		}); 
	}

	/*
		Draws the view that displays our PseudoComposition
		To-Do: Make it draw music
		Make it create the proper number of measures
	*/
	public void drawMusic() {
		CardLayout cl = (CardLayout)(appPanel.getLayout());
		int numberOfMeasures = (this.cantusFirmus.numNotes / 4) + 1;
		this.gridBagConstraints = new GridBagConstraints();

		this.measures = new BufferedImage[numberOfMeasures];
		for (int i = 0; i < this.cantusFirmus.numNotes/4; ++i) {
			this.measures[i] = new BufferedImage(460, 169, BufferedImage.TYPE_INT_ARGB);
			this.g2d = (Graphics2D) this.measures[i].createGraphics();
			//draw the background of the measure
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0,0,460,169);

			//draw the treble clef
			if (i % 4 == 0) {
				try {
		            File imagefile = new File("clef.jpg");
		            clef = ImageIO.read(imagefile);
		        } catch (IOException e) {
		              e.printStackTrace();
		        }
		        g2d.drawImage(clef, null, 0, 12);
			}

			//draw the lines of the staff
			g2d.setColor(Color.BLACK);
			g2d.drawLine(0, 28, 460, 28);
			g2d.drawLine(0, 56, 460, 56);
			g2d.drawLine(0, 84, 460, 84);
			g2d.drawLine(0, 112, 460, 112);
			g2d.drawLine(0, 140, 460, 140);

			//draw the border around the staff
			g2d.setStroke(new BasicStroke(5.0f));
			g2d.drawLine(0, 0, 459, 0);
			//g2d.drawLine(0, 0, 0, 168);
			g2d.drawLine(0, 168, 459, 168);
			//g2d.drawLine(459, 168, 459, 0);
			g2d.setStroke(new BasicStroke(1.0f));

			this.measureIcons = new ImageIcon[numberOfMeasures];
			this.measureLabels = new JLabel[numberOfMeasures];
			this.measureIcons[i] = new ImageIcon(this.measures[i]);
			this.measureLabels[i] = new JLabel(this.measureIcons[i]);
			this.gridBagConstraints.gridx = i % 4;
			this.gridBagConstraints.gridy = i / 4;
			this.measuresLabel.add(this.measureLabels[i], this.gridBagConstraints);
		}

		cl.show(appPanel, "MusicCard");
	}

	/*
		Switches to the landing page card
	*/
	public void goToLandingPage() {
		CardLayout cl = (CardLayout)(appPanel.getLayout());
		cl.show(appPanel, "LandingPageCard");
	}

	/*
		Draws the 4 radio buttons and the compose button
	*/
	public void drawLandingPageButtons() {
		this.background.add(buttons, BorderLayout.CENTER);

		this.background.add(pseudoComposeButton, BorderLayout.SOUTH);
	}

	/*
		Draws and sets up the background and directions - also calls the method
		that draws the buttons
	*/
	public void drawLandingPage() {
		this.appPanel.add(background, "LandingPageCard");
		this.background.setLayout(new BorderLayout());
		this.background.add(directions, BorderLayout.NORTH);

		drawLandingPageButtons();
	}

	/*
		Writes a cantus firmus from scratch
		Asks user for:
			length
			[future:
				range
				mode (1-8)]
	*/
	public CantusFirmus makeCantusFirmus (boolean play) {
		int length;
		Boolean trying = true;
		this.cantusFirmus = null;

		String stringVal = JOptionPane.showInputDialog("How long should your cantus firmus be (in whole notes)?");

		while (trying) {
			try {
				length = Integer.parseInt(stringVal);
				if (length < 1)
					throw new IllegalArgumentException();
				trying = false;
				cantusFirmus = new CantusFirmus(length);
				cantusFirmus.pseudoComposeFromScratch();

				this.playerString = cantusFirmus.toString();
				if (play)
					play();
			}
			catch (NumberFormatException e) {
				stringVal = JOptionPane.showInputDialog("No, a positive number");
			}
			catch (IllegalArgumentException e) {
				stringVal = JOptionPane.showInputDialog("No, a positive number");
			}
		}

		return cantusFirmus;
	}

	/*
		Writes a 2-voice first-species counterpoint composition of a user-given length
	*/
	public void makeFirstSpeciesCounterpoint () {
		CantusFirmus cantusFirmus = makeCantusFirmus(false);
		TwoVoiceCounterpoint counterpoint = new TwoVoiceCounterpoint(cantusFirmus, 1);
		counterpoint.pseudoComposeFromScratchInFirstSpecies();
		this.playerString = counterpoint.toString();
		play();
	}

	/*
		Writes a 2-voice second-species counterpoint composition of a user-given length
	*/
	public void makeSecondSpeciesCounterpoint () {
		CantusFirmus cantusFirmus = makeCantusFirmus(false);
		TwoVoiceCounterpoint counterpoint = new TwoVoiceCounterpoint(cantusFirmus, 2);
		counterpoint.pseudoComposeFromScratchInSecondSpecies();
		this.playerString = counterpoint.toString();
		play();
	}

	/*
		Writes a 2-voice second-species counterpoint composition of a user-given length
	*/
	public void makeThirdSpeciesCounterpoint () {
		CantusFirmus cantusFirmus = makeCantusFirmus(false);
		TwoVoiceCounterpoint counterpoint = new TwoVoiceCounterpoint(cantusFirmus, 3);
		counterpoint.pseudoComposeFromScratchInThirdSpecies();
		this.playerString = counterpoint.toString();
		play();
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
		Writes the PlayerString to a txt file in the user's current directory
		Future: Save as XML format so that it can be imported into Finale, possibly with a later prompt
		with instructions or a file format *choice*
	*/
	public void save () {
		MusicXMLWriter writer = new MusicXMLWriter();
		writer.write(this.playerString);
	}

	/*
		Adds the menu items to the menu
	*/
	public void setUpMenu () {
		JMenu fileMenu = new JMenu("Options");

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
