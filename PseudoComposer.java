// PseudoComposer.java
// Composes a short piece based on a melodic motive, input fron a user
//
// by Sarah Klein

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

public class PseudoComposer {
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

class ImageFrame extends JFrame {
	private final JFileChooser chooser;

	JPanel appPanel, controlsPanel;
	ImageIcon icon, choraleIcon, preludeIcon, themeIcon, toggleIcon;
	JLabel label, choraleLabel, preludeLabel, themeLabel, toggleLabel;
	BufferedImage image, choraleActiveImage, choraleInactiveImage, preludeActiveImage, preludeInactiveImage, themeActiveImage, 
				  themeInactiveImage;
	JButton actionButton;
	int measureWidth = 460, measureHeight = 169, composeWidth = 100, composeHeight = 42;

	JOptionPane disclaimer;

	Measure entry;
	Staff composition;
	Boolean composed = false, preludeActive = false, choraleActive = false, themeActive = false, playing = false;
	Player player = new Player();
	String playerString = "";
	String instrument = "ACOUSTIC_GRAND"; 
	int tempo = 40, minNotes = 3;

	public ImageFrame (int width, int height) {
		this.setTitle("PseudoComposer");
		this.setSize(width, height);
		this.chooser = new JFileChooser();
		this.chooser.setCurrentDirectory(new File("."));
		initialize();
	}

	public void initialize () {
		this.appPanel = new JPanel(new BorderLayout());
		this.controlsPanel = new JPanel(new BorderLayout());
		getContentPane().add(this.appPanel);
		this.disclaimer = new JOptionPane();

		this.entry = new Measure();
		this.entry.drawMeasure();
		this.image = new BufferedImage(measureWidth, measureHeight, BufferedImage.TYPE_INT_ARGB);
		this.icon = new ImageIcon(this.image);
		this.label = new JLabel(this.icon);
		this.label.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

		this.actionButton = new JButton("Play theme");

		setComposeImages();
		this.preludeIcon = new ImageIcon(this.preludeInactiveImage);
		this.preludeLabel = new JLabel(this.preludeIcon);
		this.choraleIcon = new ImageIcon(this.choraleInactiveImage);
		this.choraleLabel = new JLabel(this.choraleIcon);
		this.themeIcon = new ImageIcon(this.themeInactiveImage);
		this.themeLabel = new JLabel(this.themeIcon);

		addMouseListeners();
		setUpMenu();

		this.appPanel.add(label, BorderLayout.PAGE_START);
		this.appPanel.add(this.controlsPanel, BorderLayout.CENTER);
		this.controlsPanel.add(this.choraleLabel, BorderLayout.LINE_START);
		this.controlsPanel.add(this.themeLabel, BorderLayout.CENTER);
		this.controlsPanel.add(this.preludeLabel, BorderLayout.LINE_END);
		this.appPanel.add(this.actionButton, BorderLayout.PAGE_END);
		displayMeasure(this.entry);
		drawCompOptions();
	}

	public void addMouseListeners () {
		this.label.addMouseListener( new MouseAdapter() {
 			public void mousePressed(MouseEvent event) {
 				Point p;
 				//left-click: add note
 				if (event.getModifiers() == MouseEvent.BUTTON1_MASK)
 					addNote(event.getPoint());
 				//right-click: remove note
 				else if (event.getModifiers() == MouseEvent.BUTTON3_MASK)
					deleteNote(event.getPoint());
 			}
 		} );

 		this.actionButton.addMouseListener( new MouseAdapter() {
 			public void mousePressed(MouseEvent event) {
 				if (event.getModifiers() == MouseEvent.BUTTON1_MASK) {
 					if (playing)
 						stopPlaying();
 					else if (entry.noteCount > 0) {
 						if (preludeActive) {
 							makeSuitePrelude();
 						}
 						else if (choraleActive) {
 							makeChorale();
 						}
 						else if (themeActive) {
 							playTheme();
 						}
 						actionButton.setText("Stop playing");
 					}
 				}
 			}
 		} );

		this.preludeLabel.addMouseListener( new MouseAdapter() {
 			public void mousePressed(MouseEvent event) {
 				if (event.getModifiers() == MouseEvent.BUTTON1_MASK) {
 					if (entry.noteCount > 0) {
 						toggle("prelude");
 					}
 				}
 			}
 		} );

 		this.themeLabel.addMouseListener( new MouseAdapter() {
 			public void mousePressed(MouseEvent event) {
 				if (event.getModifiers() == MouseEvent.BUTTON1_MASK) {
 					if (entry.noteCount > 0) {
 						toggle("theme");
 					}
 				}
 			}
 		} );

 		this.choraleLabel.addMouseListener( new MouseAdapter() {
 			public void mousePressed(MouseEvent event) {
 				if (event.getModifiers() == MouseEvent.BUTTON1_MASK) {
 					if (entry.noteCount > 0) {
 						toggle("chorale");
 					}
 				}
 			}
 		} );
	}

	public void addNote (Point p) {
		if (this.entry.noteCount == 0) {
			this.themeActive = true;
			drawCompOptions();
		}
		if (playing)
			stopPlaying();
		this.entry.addNote(p);
		this.playerString = this.entry.toString();
		displayMeasure(this.entry);
	}

	public void changeInstrument (String inst) {
		this.instrument = inst;
		if (playing)
			stopPlaying();
	}

	public void deleteNote (Point p) {
		this.entry.deleteNote(p);
		if (this.entry.noteCount == 0) {
			this.themeActive = this.preludeActive = this.choraleActive = false;
			drawCompOptions();
			this.actionButton.setText("Play theme");
		}
		if (playing)
			stopPlaying();
		this.playerString = this.entry.toString();
		displayMeasure(this.entry);
	}

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

	public void drawCompOptions () {
		SwingUtilities.invokeLater(new Runnable () {
			public void run() {
				if (preludeActive)
					preludeIcon.setImage(preludeActiveImage);
				else
					preludeIcon.setImage(preludeInactiveImage);
				if (choraleActive)
					choraleIcon.setImage(choraleActiveImage);
				else
					choraleIcon.setImage(choraleInactiveImage);
				if (themeActive)
					themeIcon.setImage(themeActiveImage);
				else
					themeIcon.setImage(themeInactiveImage);
				if (preludeActive || choraleActive || themeActive) {
					preludeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					choraleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					themeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
				else {
					preludeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					choraleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					themeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
				preludeLabel.repaint();
				choraleLabel.repaint();
				themeLabel.repaint();
				validate();
			}
		});
	}

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

	public void load () {
		Boolean trying = true;
		File file = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;

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
		play();
	}

	public void makeChorale () {
		if (!this.composed)
			this.composed = true;
		int noteCount = this.entry.getNoteCount();
		if (noteCount < 3)
			playCheesyToccata();
		else {
			composition = new Chorale(entry, entry.getKey(), this.instrument);
			composition.pseudoCompose();
			this.playerString = composition.toString();
			play();
		}
	}

	public void makeSuitePrelude () {
		if (!this.composed)
			this.composed = true;
		int noteCount = this.entry.getNoteCount();
		if (noteCount < this.minNotes)
			playCheesyPrelude();
		else {
			composition = new SuiteMovement(entry, entry.getKey(), this.instrument);
			composition.pseudoCompose();
			this.playerString = composition.toString();
			play();
		}
	}

	public String makeStringWithInstrumentAndTempo () {
		Boolean changed = false;
		String newString = "";
		for (int i = 0; i != this.playerString.length(); ++i) {
			if (this.playerString.charAt(i) == 'V') {
				changed = true;
				newString += "V";
				newString += this.playerString.charAt(i + 1);
				newString += " I[" + instrument + "] T" + tempo + " ";
				i += 2;
			}
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

	public void play () {
		new Thread (new Runnable () {
			public void run() {
				playing = true;
				if (player.isPlaying())
					player.stop();
				Pattern pattern = new Pattern(makeStringWithInstrumentAndTempo());
		 		player.play(pattern);
		 		if (choraleActive)
			 		actionButton.setText("Make chorale");
			 	else if (themeActive)
			 		actionButton.setText("Play theme");
			 	else
			 		actionButton.setText("Make prelude");
			 	playing = false;
			}
		}).start(); 
	}

	public void playTheme () {
		this.playerString = entry.toString();
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

	public void playCheesyToccata () {
		this.playerString = "T60 d2s d3s c#3s d3s a3s d3s c#3s d3s f3s d3s c#3s d3s d2s d3s c3s d3s";
		play();	
		String disc = "I'm going to need a few more notes than that to work with...";
		this.disclaimer.showMessageDialog(this, disc);	
	}

	public void save () {
		try {
			PrintWriter transcriber = new PrintWriter("PseudoComposition.txt", "UTF-8");
			transcriber.println(this.composition.toString());
			transcriber.close();
		}
		catch (IOException e) {
		}
	}

	public void setComposeImages () {
		BufferedImage tempLabel;
		File imagefile;
		Graphics2D g2d;

		this.choraleActiveImage = new BufferedImage(composeWidth,composeHeight, BufferedImage.TYPE_INT_ARGB);
		g2d = (Graphics2D) this.choraleActiveImage.createGraphics();

		try {
            imagefile = new File("images/choraleactive.png");
            tempLabel = ImageIO.read(imagefile);
            g2d.drawImage(tempLabel, null, 0, 0);
        } catch (IOException e) {
              e.printStackTrace();
        }

        this.choraleInactiveImage = new BufferedImage(composeWidth,composeHeight, BufferedImage.TYPE_INT_ARGB);
		g2d = (Graphics2D) this.choraleInactiveImage.createGraphics();

		try {
            imagefile = new File("images/choraleinactive.png");
            tempLabel = ImageIO.read(imagefile);
            g2d.drawImage(tempLabel, null, 0, 0);
        } catch (IOException e) {
              e.printStackTrace();
        }

		this.preludeActiveImage = new BufferedImage(composeWidth,composeHeight, BufferedImage.TYPE_INT_ARGB);
		g2d = (Graphics2D) this.preludeActiveImage.createGraphics();

		try {
            imagefile = new File("images/preludeactive.png");
            tempLabel = ImageIO.read(imagefile);
            g2d.drawImage(tempLabel, null, 0, 0);
        } catch (IOException e) {
              e.printStackTrace();
        }
        
		this.preludeInactiveImage = new BufferedImage(composeWidth,composeHeight, BufferedImage.TYPE_INT_ARGB);
		g2d = (Graphics2D) this.preludeInactiveImage.createGraphics();

		try {
            imagefile = new File("images/preludeinactive.png");
            tempLabel = ImageIO.read(imagefile);
            g2d.drawImage(tempLabel, null, 0, 0);
        } catch (IOException e) {
              e.printStackTrace();
        }

		this.themeActiveImage = new BufferedImage(composeWidth,composeHeight, BufferedImage.TYPE_INT_ARGB);
		g2d = (Graphics2D) this.themeActiveImage.createGraphics();

		try {
            imagefile = new File("images/themeactive.png");
            tempLabel = ImageIO.read(imagefile);
            g2d.drawImage(tempLabel, null, 0, 0);
        } catch (IOException e) {
              e.printStackTrace();
        }

		this.themeInactiveImage = new BufferedImage(composeWidth,composeHeight, BufferedImage.TYPE_INT_ARGB);
		g2d = (Graphics2D) this.themeInactiveImage.createGraphics();

		try {
            imagefile = new File("images/themeinactive.png");
            tempLabel = ImageIO.read(imagefile);
            g2d.drawImage(tempLabel, null, 0, 0);
        } catch (IOException e) {
              e.printStackTrace();
        }
	}

	public JMenu setUpInstrumentChoices () {
		JMenu changeInstrument = new JMenu("Change instrument");

		JMenuItem accordian = new JMenuItem("Accordian");
		accordian.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("ACCORDIAN");
			}
		});
		changeInstrument.add(accordian);

		JMenuItem bass = new JMenuItem("Acoustic Bass");
		bass.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("ACOUSTIC_BASS");
			}
		});
		changeInstrument.add(bass);

		JMenuItem bagpipe = new JMenuItem("Bagpipe");
		bagpipe.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("BAGPIPE");
			}
		});
		changeInstrument.add(bagpipe);

		JMenuItem banjo = new JMenuItem("Banjo");
		banjo.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("BANJO");
			}
		});
		changeInstrument.add(banjo);

		JMenuItem clarinet = new JMenuItem("Clarinet");
		clarinet.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("CLARINET");
			}
		});
		changeInstrument.add(clarinet);

		JMenuItem crystal = new JMenuItem("Crystal Glasses");
		crystal.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("CRYSTAL");
			}
		});
		changeInstrument.add(crystal);

		JMenuItem flute = new JMenuItem("Flute");
		flute.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("FLUTE");
			}
		});
		changeInstrument.add(flute);

		JMenuItem horn = new JMenuItem("French Horn");
		horn.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("FRENCH_HORN");
			}
		});
		changeInstrument.add(horn);

		JMenuItem goblin = new JMenuItem("Goblins");
		goblin.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("GOBLINS");
			}
		});
		changeInstrument.add(goblin);

		JMenuItem marimba = new JMenuItem("Marimba");
		marimba.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("MARIMBA");
			}
		});
		changeInstrument.add(marimba);

		JMenuItem oboe = new JMenuItem("Oboe");
		oboe.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("OBOE");
			}
		});
		changeInstrument.add(oboe);

		JMenuItem ocarina = new JMenuItem("Ocarina");
		ocarina.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("OCARINA");
			}
		});
		changeInstrument.add(ocarina);

		JMenuItem organ = new JMenuItem("Organ");
		organ.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("CHURCH_ORGAN");
			}
		});
		changeInstrument.add(organ);

		JMenuItem piano = new JMenuItem("Piano");
		piano.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("PIANO");
			}
		});
		changeInstrument.add(piano);

		JMenuItem steel = new JMenuItem("Steel Drums");
		steel.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("STEEL_DRUMS");
			}
		});
		changeInstrument.add(steel);

		JMenuItem voice = new JMenuItem("Steel");
		voice.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("VOICE");
			}
		});
		changeInstrument.add(voice);

		JMenuItem strings = new JMenuItem("Strings");
		strings.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("ORCHESTRAL_STRINGS");
			}
		});
		changeInstrument.add(strings);

		JMenuItem warm = new JMenuItem("Warm");
		warm.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				changeInstrument("WARM");
			}
		});
		changeInstrument.add(warm);

		return changeInstrument;
	}

	public void setUpMenu () {
		JMenu fileMenu = new JMenu("Options");

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

		JMenuItem read = new JMenuItem("Read");
		read.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				showDisclaimer();
			}
		});
		disclaimer.add(read);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(disclaimer);
		this.setJMenuBar(menuBar);
	}

	public void showDisclaimer () {
		String disc = 
			"While playing with my PseudoComposer, you'll notice that its compositions tend to be precise, balanced, and \n" + 
			"predictable. My mathematical music injects random chance in a number of places, but a program could never hope to \n" +
			"introduce as many branches as were contained in the brain of JS Bach. True music comes from nuance, wit, a careful \n" +
			"hand, and - yes - a bit of math.";
		this.disclaimer.showMessageDialog(this, disc);
	}

	public void stopPlaying () {
		this.player.stop();
		this.playing = false;
		if (choraleActive)
	 		actionButton.setText("Make chorale");
	 	else if (themeActive)
	 		actionButton.setText("Play theme");
	 	else
	 		actionButton.setText("Make prelude");
	}

	public void toggle(String pressed) {
		if (playing)
			stopPlaying();
		choraleActive = preludeActive = themeActive = false;
		if (pressed == "prelude") {
			preludeActive = true;
			actionButton.setText("Make prelude");
		}
		else if (pressed == "theme") {
			themeActive = true;
			actionButton.setText("Play theme");
		}
		else if (pressed == "chorale") {
			choraleActive = true;
			actionButton.setText("Make chorale");
		}
		drawCompOptions();			 		
	}
}