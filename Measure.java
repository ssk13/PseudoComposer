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
	Note newNote, oldNote;
	int noteCount = 0, place, i, beats;
	char valGetsBeat;
	Boolean drawn = false;
	String instrument = "";

	public Measure () {
		this.notes = new Note[8];
		this.beats = 8;
		this.valGetsBeat = 'i';
		for (i = 0; i != this.beats; ++i)
			this.notes[i] = new Note(0);
	}

	public Measure (int beats, char valGetsBeat) {
		this.notes = new Note[beats];
		this.beats = beats;
		this.valGetsBeat = valGetsBeat;
		for (i = 0; i != this.beats; ++i)
			this.notes[i] = new Note(-1, this.valGetsBeat);
		this.noteCount = beats;
	}

	public Measure (Measure measure) {
		this.notes = new Note[measure.beats];
		this.noteCount = measure.noteCount;
		this.beats = measure.beats;
		this.valGetsBeat = measure.valGetsBeat;
		for (i = 0; i != measure.noteCount; ++i)
			this.notes[i] = new Note(measure.notes[i]);
	}

	public Measure (Measure measure, int beatsPerMeasure, char valGetsBeat) {
		this.notes = new Note[beatsPerMeasure];
		this.noteCount = measure.noteCount;
		this.beats = beatsPerMeasure;
		this.valGetsBeat = valGetsBeat;
		if (noteCount < beatsPerMeasure) {
			for (i = 0; i != measure.noteCount; ++i)
				this.notes[i] = new Note(measure.notes[i], valGetsBeat);
		}
		else {
			for (i = 0; i != beatsPerMeasure; ++i)
				this.notes[i] = new Note(measure.notes[i], valGetsBeat);
		}
	}

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

	public Measure copyAndTranspose (int halfsteps) {
		Measure newMeasure = new Measure(this);
		for (i = 0; i != this.noteCount; ++i)
			newMeasure.notes[i].transpose(halfsteps);
		return newMeasure;	
	}

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

	public void drawMeasure () {
		initializeMeasure();
		int vert = 0;

		for (i = 0; i != noteCount; ++i) {
			if (notes[i].val == 26)
				vert = 0;
			else if (notes[i].val == 28)
				vert = 1;
			else if (notes[i].val == 29)
				vert = 2;
			else if (notes[i].val == 31)
				vert = 3;
			else if (notes[i].val == 33)
				vert = 4;
			else if (notes[i].val == 35)
				vert = 5;
			else if (notes[i].val == 36)
				vert = 6;
			else if (notes[i].val == 38)
				vert = 7;
			else if (notes[i].val == 40)
				vert = 8;
			else if (notes[i].val == 41)
				vert = 9;
			else
				vert = 10;
			this.g2d.fillOval(50*i + 60, 139 - ((vert) * 14), 35, 30);
		}
	}

	public int getNoteCount () {
		return this.noteCount;
	}

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

	public BufferedImage getView () {
		return this.view;
	}

	public Boolean hasNote (int note) {
		if (notes[note].val != -1)
			return true;
		return false;
	}

	public void initializeMeasure () {
		if (this.drawn == false) {
			this.view = new BufferedImage(460, 169, BufferedImage.TYPE_INT_ARGB);
			this.g2d = (Graphics2D) view.createGraphics();
			this.drawn = true;
		}
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0,0,460,169);

		try {
            File imagefile = new File("clef.jpg");
            clef = ImageIO.read(imagefile);
        } catch (IOException e) {
              e.printStackTrace();
        }
        g2d.drawImage(clef, null, 0, 12);

		g2d.setColor(Color.BLACK);
		g2d.drawLine(10, 28, 450, 28);
		g2d.drawLine(10, 56, 450, 56);
		g2d.drawLine(10, 84, 450, 84);
		g2d.drawLine(10, 112, 450, 112);
		g2d.drawLine(10, 140, 450, 140);
		g2d.setStroke(new BasicStroke(5.0f));
		g2d.drawLine(0, 0, 459, 0);
		g2d.drawLine(0, 0, 0, 168);
		g2d.drawLine(0, 168, 459, 168);
		g2d.drawLine(459, 168, 459, 0);
		g2d.setStroke(new BasicStroke(1.0f));
	}

	public void play (Player player) {
		String patternString = toString();
		new Thread (new Runnable () {
			public void run() {
				Pattern pattern = new Pattern(patternString);
		 		player.play(pattern);
			}
		}).start(); 
	}

	public void swapNotes(int a, int b) {
		Note temp = new Note(notes[a]);
		notes[a] = notes[b];
		notes[b] = temp;
	}

	public String toString() {
		String musicString = "";
		if (this.instrument != "")
			musicString += "I[" + this.instrument + "] ";
		for (i = 0; i != noteCount; ++i)
			musicString += notes[i].toString() + " ";
		return musicString;
	}
}