/* 
	Measure.java
   	Each measure contains a monophonic line

   	by Sarah Klein
*/

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.Random;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import org.jfugue.*;

class Measure {
	BufferedImage view;
	BufferedImage clef;
	Graphics2D g2d;
	Note[] notes;
	Note newNote;
	int noteCount = 0, place, i, beats;
	char valGetsBeat;
	Boolean drawn = false;

	/*
		Constructor
		Defaultedly adds 8 eighth notes to each measure, initializing each note to '0'
	*/
	public Measure () {
		this.notes = new Note[8];
		this.beats = 8;
		this.valGetsBeat = 'i';
		for (i = 0; i != this.beats; ++i)
			this.notes[i] = new Note(0);
	}

	/*
		Constructor
		Initializes a measure with the contents of another measure
	*/
	public Measure (Measure measure) {
		this.notes = new Note[measure.beats];
		this.noteCount = measure.noteCount;
		this.beats = measure.beats;
		this.valGetsBeat = measure.valGetsBeat;
		for (i = 0; i != measure.noteCount; ++i)
			this.notes[i] = new Note(measure.notes[i]);
	}

	/*
		Adds a note to the end of the measure, based on the y-position of the mouse event,
		not adding a note if the measure is already full
		Future: Also take into account the x-position and the notes already included,
		allowing notes to be added between existing notes
	*/
	public void addNote (Point p) {
		if (noteCount == this.beats)
			return;

		if (p.y > 147)
			newNote = new Note(26);
		else if (p.y > 133)
			newNote = new Note(28);
		else if (p.y > 119)
			newNote = new Note(29);
		else if (p.y > 105)
			newNote = new Note(31);
		else if (p.y > 91)
			newNote = new Note(33);
		else if (p.y > 77)
			newNote = new Note(35);
		else if (p.y > 63)
			newNote = new Note(36);
		else if (p.y > 49)
			newNote = new Note(38);
		else if (p.y > 35)
			newNote = new Note(40);
		else if (p.y > 21)
			newNote = new Note(41);
		else
			newNote = new Note(43);
		
		notes[noteCount++] = newNote;
		drawMeasure();
	}

	/*
		If the user clicked on a black pixel (hence: a note),
		delete the note in that place,
		shift the notes after this one in the measure forward one spot
		redraw the measure
	*/
	public void deleteNote (Point p) {
		if (view.getRGB(p.x, p.y) == 0xFF000000) {
			place = (p.x-60)/50;
			for (i = place; i < noteCount; ++i) {
				if (i < 7)
					notes[i] = notes[i + 1];
			}
			notes[noteCount - 1] = null;
			--noteCount;
			drawMeasure();
		}
	}

	/*
		Draws the notes within the range of d (below the staff) to g (above the staff) in treble clef
	*/
	public void drawMeasure () {
		initializeMeasure();
		int vert = 0;

		for (i = 0; i != noteCount; ++i) {
			if (notes[i].val == 26)			//d below the staff
				vert = 0;
			else if (notes[i].val == 28)	//e
				vert = 1;
			else if (notes[i].val == 29)	//f
				vert = 2;
			else if (notes[i].val == 31)	//g
				vert = 3;
			else if (notes[i].val == 33)	//a
				vert = 4;
			else if (notes[i].val == 35)	//b
				vert = 5;
			else if (notes[i].val == 36)	//c
				vert = 6;
			else if (notes[i].val == 38)	//d	
				vert = 7;
			else if (notes[i].val == 40)	//e
				vert = 8;
			else if (notes[i].val == 41)	//f
				vert = 9;
			else
				vert = 10;					//g above the staff
			this.g2d.fillOval(50*i + 60, 139 - ((vert) * 14), 35, 30);
		}
	}

	/*
		Returns the number of notes in the measure - not the intended size of the measure, but the number 
		of valid notes added
	*/
	public int getNoteCount () {
		return this.noteCount;
	}

	/*
		Postulates a well-suited key for the measure
		Future: make this better
	*/
	public int getKey () {
		int[] keys = {0,0,0,0,0,0};
		int[] pos;

		for (int i = 0; i != noteCount; ++i) {
			if (notes[i].getName() == 'A') {
				++keys[1];
				keys[3] += 2;
				if (i == 0)
					keys[3] += 4;
				else
					keys[5] += 3;
			}
			else if (notes[i].getName() == 'B') {
				++keys[2];
				keys[4] += 2;
				--keys[1];
				--keys[3];
			}
			else if (notes[i].getName() == 'C') {
				if (i==0)
					keys[0] += 4;
				else
					keys[0] += 3;
				++keys[3];
				keys[5] += 2;
			}
			else if (notes[i].getName() == 'D') {
				if (i==0)
					keys[1] += 4;
				else
					keys[1] += 3;
				++keys[4];
				--keys[2];
			}
			else if (notes[i].getName() == 'E') {
				keys[0] += 2;
				if (i==0)
					keys[2] += 4;
				else
					keys[2] += 3;
				++keys[5];
			}
			else if (notes[i].getName() == 'F') {
				keys[1] += 2;
				if (i==0)
					keys[3] += 4;
				else
					keys[3] += 3;
				--keys[2];
				--keys[4];
			}
			else {
				++keys[0];
				keys[2] += 2;
				if (i==0)
					keys[4] += 4;
				else
					keys[4] += 3;
			}
		}

		int max = keys[0];
		int count = 1;
		for (int i = 1; i != 6; ++i) {
			if (keys[i] >= max) {
				if (keys[i] == max)
					++count;
				else {
					max = keys[i];
					count = 1;
				}
			}
		}
		if (count == 1) {
			if (keys[0] == max)
				return 0;
			else if (keys[1] == max)
				return 2;
			else if (keys[2] == max)
				return 4;
			else if (keys[3] == max)
				return 5;
			else if (keys[4] == max)
				return 7;
			else
				return 9;
		}
		else {
			pos = new int[count];
			int posCount = 0;
			for (int i = 0; i != 6; ++i) {
				if (keys[i] == max) {
					pos[posCount] = i;
					++posCount;
				}
			}
			Random rand = new Random();
			int val = rand.nextInt(count);
			if (val == 0)
				return 0;
			else if (val == 1)
				return 2;
			else if (val == 2)
				return 4;
			else if (val == 3)
				return 5;
			else if (val == 4)
				return 7;
			else
				return 9;
		}
	}

	/*
		Returns the BufferedImage that represents what this measure would look like on sheet music
	*/
	public BufferedImage getView () {
		return this.view;
	}

	/*
		Draws an empty measure in treble clef
	*/
	public void initializeMeasure () {
		//first checks if we have the global vars initialized due to previously having drawn the measure
		if (this.drawn == false) {
			this.view = new BufferedImage(460, 169, BufferedImage.TYPE_INT_ARGB);
			this.g2d = (Graphics2D) view.createGraphics();
			this.drawn = true;
		}
		//draw the background of the measure
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0,0,460,169);

		//draw the treble clef
		try {
            File imagefile = new File("clef.jpg");
            clef = ImageIO.read(imagefile);
        } catch (IOException e) {
              e.printStackTrace();
        }
        g2d.drawImage(clef, null, 0, 12);

        //draw the lines of the staff
		g2d.setColor(Color.BLACK);
		g2d.drawLine(10, 28, 450, 28);
		g2d.drawLine(10, 56, 450, 56);
		g2d.drawLine(10, 84, 450, 84);
		g2d.drawLine(10, 112, 450, 112);
		g2d.drawLine(10, 140, 450, 140);
		//draw the border around the staff
		g2d.setStroke(new BasicStroke(5.0f));
		g2d.drawLine(0, 0, 459, 0);
		g2d.drawLine(0, 0, 0, 168);
		g2d.drawLine(0, 168, 459, 168);
		g2d.drawLine(459, 168, 459, 0);
		g2d.setStroke(new BasicStroke(1.0f));
	}

	/*
		Converts the measure to a string that can be played by the player
	*/
	public String toString() {
		String musicString = "";
		for (i = 0; i != noteCount; ++i)
			musicString += notes[i].toString() + " ";
		return musicString;
	}
}

	/*
		Constructor that takes the number of beats and the value that gets the beat - used in the SuiteMovement
		
		public Measure (int beats, char valGetsBeat) {
			this.notes = new Note[beats];
			this.beats = beats;
			this.valGetsBeat = valGetsBeat;
			for (i = 0; i != this.beats; ++i)
				this.notes[i] = new Note(-1, this.valGetsBeat);
			this.noteCount = beats;
		}

		public void swapNotes(int a, int b) {
			Note temp = new Note(notes[a]);
			notes[a] = notes[b];
			notes[b] = temp;
		}

		public void breakUpDuplicates (Note a, Note b) {
			Random rand = new Random();
			int alter;
			for (i = 1; i != noteCount; ++i) {
				if (notes[i].getName() == notes[i - 1].getName()) {
					alter = rand.nextInt(3);
					if (alter == 0)
						notes[i].setPitchNotOctave(a);
					else if (alter == 1)
						notes[i].setPitchNotOctave(b);
					else if (alter == 2) {
						alter = rand.nextInt(noteCount);
						swapNotes(i, alter);
					}
				}
			}
		}


		public Measure copyAndFiveToOne (Note tonic, Boolean isMajor) {
			Measure newMeasure = new Measure(this);
			Note five = new Note(tonic), seven = new Note(tonic), two = new Note(tonic);
			five.transpose(-5);
			seven.transpose(-1);
			two.transpose(2);
			for (i = 0; i != noteCount; ++i) {
				if ((notes[i].val % 12) == (seven.val % 12))
					newMeasure.notes[i].transpose(1);
				if ((notes[i].val % 12) == (two.val % 12)) {
					if (isMajor)
						newMeasure.notes[i].transpose(2);
					else
						newMeasure.notes[i].transpose(1);
				}
			}

			Note p1 = new Note(five);
			if (isMajor)
				p1.transpose(2);
			else
				p1.transpose(1);

			newMeasure.breakUpDuplicates(two, p1);

			return newMeasure;
		}

		public Measure copyAndFiveToSix (Note tonic, Boolean isMajor) {
			Measure newMeasure = new Measure(this);
			Note five = new Note(tonic), seven = new Note(tonic), two = new Note(tonic);
			five.transpose(-5);
			seven.transpose(-1);
			two.transpose(2);
			for (i = 0; i != noteCount; ++i) {
				if ((notes[i].val % 12) == (five.val % 12)) {
					if (isMajor)
						newMeasure.notes[i].transpose(-3);
					else
						newMeasure.notes[i].transpose(-4);
				}
				if ((notes[i].val % 12) == (seven.val % 12)) {
					if (isMajor)
						newMeasure.notes[i].transpose(-2);
					else
						newMeasure.notes[i].transpose(-3);
				}
				if ((notes[i].val % 12) == (two.val % 12))
					newMeasure.notes[i].transpose(-2);
			}

			Note p1 = new Note(five);
			p1.transpose(-2);

			newMeasure.breakUpDuplicates(two, p1);

			return newMeasure;
		}

		public Measure copyAndFourToFive (Note tonic, Boolean isMajor) {
			Measure newMeasure = new Measure(this);
			Note one = new Note(tonic), four = new Note(tonic), six = new Note(tonic);
			four.transpose(5);
			if (isMajor)
				six.transpose(-3);
			else
				six.transpose(-4);
			for (i = 0; i != noteCount; ++i) {
				if ((notes[i].val % 12) == (four.val % 12))
					newMeasure.notes[i].transpose(-3);
				if ((notes[i].val % 12) == (six.val % 12)) {
					if (isMajor)
						newMeasure.notes[i].transpose(-2);
					else
						newMeasure.notes[i].transpose(-1);
				}
				if ((notes[i].val % 12) == (one.val % 12))
					newMeasure.notes[i].transpose(-1);
			}

			Note p1 = new Note(four);
			p1.transpose(2);

			newMeasure.breakUpDuplicates(p1, six);

			return newMeasure;
		}

		public Measure copyAndFourToSeven (Note tonic, Boolean isMajor) {
			Measure newMeasure = new Measure(this);
			Note one = new Note(tonic), four = new Note(tonic), six = new Note(tonic);
			four.transpose(5);
			if (isMajor)
				six.transpose(-3);
			else
				six.transpose(-4);
			for (i = 0; i != noteCount; ++i) {
				if ((notes[i].val % 12) == (four.val % 12))
					newMeasure.notes[i].transpose(2);
				if ((notes[i].val % 12) == (six.val % 12))
					newMeasure.notes[i].transpose(2);
			}

			Note p1 = new Note(four);
			p1.transpose(2);

			newMeasure.breakUpDuplicates(p1, one);

			return newMeasure;
		}

		public Measure copyAndOneToFour (Note tonic, Boolean isMajor) {
			Measure newMeasure = new Measure (this);
			Note three = new Note(tonic), five = new Note(tonic), one = new Note(tonic);
			five.transpose(-5);
			if (isMajor)
				three.transpose(4);
			else
				three.transpose(3);
			for (i = 0; i != noteCount; ++i) {
				if ((notes[i].val % 12) == (three.val % 12)) {
					if (isMajor)
						newMeasure.notes[i].transpose(1);
					else
						newMeasure.notes[i].transpose(2);
				}
				if ((notes[i].val % 12) == (five.val % 12)) {
					if (isMajor)
						newMeasure.notes[i].transpose(2);
					else
						newMeasure.notes[i].transpose(1);
				}
			}

			Note two = new Note(tonic);
			two.transpose(2);

			newMeasure.breakUpDuplicates(five, two);

			return newMeasure;
		}

		public Measure copyAndOneToSix (Note tonic, Boolean isMajor) {
			Measure newMeasure = new Measure (this);
			Note three = new Note(tonic), five = new Note(tonic), one = new Note(tonic);
			five.transpose(-5);
			if (isMajor)
				three.transpose(4);
			else 
				three.transpose(3);
			for (i = 0; i != noteCount; ++i) {
				if ((notes[i].val % 12) == (five.val % 12)) {
					if (isMajor)
						newMeasure.notes[i].transpose(2);
					else
						newMeasure.notes[i].transpose(1);
				}
			}

			Note p1 = new Note(tonic);
			p1.transpose(2);
			Note p2 = new Note(tonic);
			p2.transpose(-1);
			newMeasure.breakUpDuplicates(p1, p2);

			return newMeasure;
		}

		public Measure copyAndOneToThree (Note tonic, Boolean isMajor) {
			Measure newMeasure = new Measure (this);
			Note three = new Note(tonic), five = new Note(tonic), one = new Note(tonic);
			five.transpose(-5);
			if (isMajor)
				three.transpose(4);
			else
				three.transpose(3);
			for (i = 0; i != noteCount; ++i) {
				if ((notes[i].val % 12) == (one.val % 12)) {
					if (isMajor)
						newMeasure.notes[i].transpose(-1);
					else
						newMeasure.notes[i].transpose(-2);
				}
			}

			Note p1 = new Note(five);
			p1.transpose(-2);
			Note p2 = new Note(one);
			p2.transpose(2);
			newMeasure.breakUpDuplicates(p1, p2);

			return newMeasure;
		}

		public Measure copyAndSevenToOne (Note tonic, Boolean isMajor) {
			Measure newMeasure = new Measure(this);
			Note seven = new Note(tonic), two = new Note(tonic), four = new Note(tonic);
			seven.transpose(-1);
			four.transpose(5);
			two.transpose(2);
			
			for (i = 0; i != noteCount; ++i) {
				if ((notes[i].val % 12) == (seven.val % 12))
					newMeasure.notes[i].transpose(1);
				if ((notes[i].val % 12) == (two.val % 12))
					newMeasure.notes[i].transpose(5);
				if ((notes[i].val % 12) == (four.val % 12)) {
					if (isMajor)
						newMeasure.notes[i].transpose(-1);
					else 
						newMeasure.notes[i].transpose(-2);
				}
			}

			newMeasure.breakUpDuplicates(four, two);

			return newMeasure;
		}

		public Measure copyAndSixToTwo (Note tonic, Boolean isMajor) {
			Measure newMeasure = new Measure (this);
			Note one = new Note(tonic), three = new Note(tonic), six = new Note(tonic);
			if (isMajor) {
				three.transpose(4);
				six.transpose(-3);
			}
			else {
				three.transpose(3);
				six.transpose(-4);
			}
			for (i = 0; i != noteCount; ++i) {
				if ((notes[i].val % 12) == (one.val % 12))
					newMeasure.notes[i].transpose(2);
				if ((notes[i].val % 12) == (three.val % 12)) {
					if (isMajor)
						newMeasure.notes[i].transpose(1);
					else
						newMeasure.notes[i].transpose(2);
				}
			}

			one.transpose(-5);
			newMeasure.breakUpDuplicates(three, one);

			return newMeasure;
		}

		public Measure copyAndThreeToSix (Note tonic, Boolean isMajor) {
			Measure newMeasure = new Measure (this);
			Note seven = new Note(tonic), three = new Note(tonic), five = new Note(tonic);
			five.transpose(-5);
			if (isMajor) {
				three.transpose(4);
				seven.transpose(-1);
			}
			else {
				three.transpose(3);
				seven.transpose(-2);
			}
			for (i = 0; i != noteCount; ++i) {
				if ((notes[i].val % 12) == (five.val % 12)) {
					if (isMajor)
						newMeasure.notes[i].transpose(2);
					else
						newMeasure.notes[i].transpose(1);
				}
				if ((notes[i].val % 12) == (seven.val % 12)) {
					if (isMajor)
						newMeasure.notes[i].transpose(1);
					else
						newMeasure.notes[i].transpose(2);
				}
			}

			Note p1 = new Note(tonic);
			p1.transpose(2);
			Note p2 = new Note(five);
			p2.transpose(-2);

			newMeasure.breakUpDuplicates(p1, p2);

			return newMeasure;
		}

		public Measure copyAndTwoToFive (Note tonic, Boolean isMajor) {
			Measure newMeasure = new Measure (this);
			Note two = new Note(tonic), four = new Note(tonic), six = new Note(tonic);
			two.transpose(2);
			four.transpose(5);
			if (isMajor)
				six.transpose(-3);
			else
				six.transpose(-4);
			for (i = 0; i != noteCount; ++i) {
				if ((notes[i].val % 12) == (four.val % 12))
					newMeasure.notes[i].transpose(2);
				if ((notes[i].val % 12) == (six.val % 12)) {
					if (isMajor)
						newMeasure.notes[i].transpose(2);
					else
						newMeasure.notes[i].transpose(3);
				}
			}

			two.transpose(-2);

			newMeasure.breakUpDuplicates(six, two);

			return newMeasure;
		}
	*/
