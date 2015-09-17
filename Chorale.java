/* Represents a 4-voice chorale in the style of JS Bach

   by Sarah Klein
   FUTURE
   add passing tones
 */

import java.util.HashMap;
import java.util.Random;

public class Chorale {
	Chord[] chords;
	int[] choices = new int[10],
		  chordValues;
	int chordCount = 32, 
		notesPerMeasure = 8,
		sopUpper = 48, 		//range constrictuions on voices
		sopLower = 36, 
		altUpper = 36, 
		altLower = 21, 
		tenUpper = 29, 
		tenLower = 17, 
		basUpper =  17, 
		basLower = 0, 
		tempo = 120;
		//expand this array to include extended chords:
			// neopolitans, 7th chords, secondary dominance, &c
	public final int[][][] members = {
										{//major
											{0,4,7},{2,5,9},{4,7,11},{5,9,0},{7,11,2},{9,0,4},{11,2,5}
										},
						 				{//minor
						 					{0,3,7},{2,5,8},{3,7,11},{5,8,0},{7,11,2},{8,0,3},{11,2,5}
						 				}
						 			};
	HashMap<Integer,Integer> innerVoiceTranspositions = new HashMap<Integer,Integer>();
	Measure entry;
	Note tonic;
	Boolean major = true;
	Random rand = new Random();

	/*
		Constructor
		Recieves entry that you're harmonizing and the key of the measure
	*/
	public Chorale (Measure entry, int key) {
		this.tonic = new Note(key);
		if ((key == 2) || (key == 4) || (key == 9))
			this.major = false;
		this.entry = entry;
	}

	/*
		Creates the chorale!
		Prints the key,
		initializes the map by which we do the voice-leading
		initializes the chords array,
		fixes any wrong notes in the entry according to the key,
		fills the chords array from a ChordTree,
		fills in the inner voices,
		puts the last chord in root position,
		puts the notes in the proper octaves for their voices
	*/
	public void pseudoCompose() {
		System.out.print(this.tonic.getName() + " ");
		if (this.major)
			System.out.println("Major");
		else
			System.out.println("Minor");

		initializeInnerVoiceHashMap();
		this.chords = new Chord[this.chordCount];
		checkKeyAndFixWrongNotes();
		fillChordsArray(entry.noteCount);
		printChords();
		//The first chord has 4 quarter notes
		this.chords[0] = new Chord(4,'q');
		//The top voice of the first chord is the first note in the entry measure
		this.chords[0].notes[0] = new Note(entry.notes[i], 'q');
		fillFirstChord();
		for (int i = 1; i != this.chordCount; ++i) {
			this.chords[i] = new Chord(4,'q');
			//if we're still harmonizing, only fill the bottom 3 voices
			if (i < entry.noteCount) {
				this.chords[i].notes[0] = new Note(entry.notes[i], 'q');
				fillInnerVoices(i, 1, 4);
			}
			//otherwise, fill all 4 voices
			else
				fillInnerVoices(i,0,4);
		}	
		fillLastChord();	
		putInProperOctaves();
	}

	/*
		Makes sure that every note in the entry is valid in the key that we've selected
		If it's not, it alters it appropriately
		Future: Find ways to harmonize every given note
	*/
	public void checkKeyAndFixWrongNotes () {
		int dif, randVal = rand.nextInt(2);

		for (int i = 0; i != this.entry.noteCount; ++i) {
			dif = this.tonic.getDif(this.entry.notes[i]);
			if ((dif == 1) || (dif == 6) || ((this.major) && ((dif == 3) || (dif == 8) || (dif == 10))) || 
				((!this.major) && (dif == 4)))
				this.entry.notes[i].transpose(1-(2*randVal));
			else if (!this.major) {
				if (dif == 9)
					this.entry.notes[i].transpose(2-(3*randVal));
				else if (dif == 10)
					this.entry.notes[i].transpose(1-(3*randVal));
			}
		}
	}

	/*
		Puts the last note in root position (by putting tonic in the bass voice) and ensures
		that there is a 3rd in the chord, in case the third was in the bass voice previously,
		by replacing one of the tonic notes with the third
	*/
	public void fillLastChord () {
		int shouldTransposeThisNote = rand.nextInt(2),
			majorVal = this.major ? 1 : 0,
			i;

		//if the bass note is the third
		if (this.tonic.getDif(this.chords[this.chordCount - 1].notes[3]) == 3 || 
			this.tonic.getDif(this.chords[this.chordCount - 1].notes[3]) == 4 ||
			this.tonic.getDif(this.chords[this.chordCount - 1].notes[3]) == 8 ||
			this.tonic.getDif(this.chords[this.chordCount - 1].notes[3]) == 9) {

			for (int i = 0; i != 3; ++i) {
				if (this.tonic.getDif(this.chords[this.chordCount - 1].notes[i]) == 0) {
					if (shouldTransposeThisNote-- == 0) {
						this.chords[this.chordCount - 1].swapNotes(i,3);
						return;
					}
				}
			}
		}
		else {
			this.chords[this.chordCount - 1].notes[3] = new Note(tonic, 'q');
		}
	}

	/*
		Picks a chord progression for our chorale by finding a valid path with a ChordTree
		for the given entry, and then picking next chords pseudorandomly until it gets to
		the cadence, at which point it resolves with dominant-tonic motion
	*/
	public void fillChordsArray (int numNotes) {
		ChordTree path = new ChordTree(1, numNotes, null);
		int here = this.entry.noteCount, counting = 0, preCadencePlace = this.chordCount - 5;
		this.chordValues = new int[this.chordCount];
		//start with a I/i chord
		this.chordValues[0] = 1;
		harmonizeEnteredNotes(this.entry.noteCount, path);

		while (here < this.chordCount-4) {
			this.chordValues[here] = getNextChord(this.chordValues[here - 1]);
			++here;
		}
		
		if ((this.chordValues[preCadencePlace] == 7) || (this.chordValues[preCadencePlace] == 5))
			this.chordValues[preCadencePlace + 1] = 1;
		else
			this.chordValues[preCadencePlace + 1] = getPredominant();
		this.chordValues[preCadencePlace + 2] = getPredominantOrDominant();
		this.chordValues[preCadencePlace + 3] = getDominant();
		this.chordValues[preCadencePlace + 4] = 1;

		if (counting == 200)
			System.out.println("infinite looped - check your Chorale.java fillChorsAray code!");
	}

	/*
		Initializes the first chord with 2 tonics, a 3rd, and a 5th
	*/
	public void fillFirstChord () {
		int majorVal = (this.major ? 1 : 0), 
			randVal = this.rand.nextInt(2),
			interval;
		// put tonic in the other 3 voices
		for (int i = 1; i != 4; ++i)
			this.chords[0].notes[i] = new Note(tonic, 'q');

		// get the interval of the first note
		interval = this.tonic.getDif(this.chords[0].notes[0]);

		// if it's tonic or the 5th
		if ((interval == 0) || (interval == 7)) {
			// put the appropriate 3rd in one of the other notes
			this.chords[0].notes[2-randVal].transpose(3 + majorVal);
			//if it's tonic, put a 5th in the other note
			if (interval == 0)
				this.chords[0].notes[2-(1-randVal)].transpose(-5);
		}
		//if it's the third, put a 5th in one of the other voices
		else if ((interval == 3) || (interval == 4))
			this.chords[0].notes[2-randVal].transpose(-5);
		//otherwise, the note isn't in a i/I chord, so make it one of the correct notes, then fill the chord appropriately
		else {
			randVal = this.rand.nextInt(3);
			this.chords[0].notes[0] = new Note(tonic, 'q');
			if (randVal == 0)
				this.chords[0].notes[0].transpose(3 + majorVal);
			else if (randVal == 1)
				this.chords[0].notes[0].transpose(-5);
			fillFirstChord();
		}
	}

	/*
		Maps the way that the inner voices should move
		lvalue: difference between notes * 1000 + chord moving from * 100 + chord moving to + 10 + isMajor
		rvalue: transposition of the voice
		Future: MAKE BETTER
	*/
	public void initializeInnerVoiceHashMap () {
		this.innerVoiceTranspositions.put(  121,-3);
		this.innerVoiceTranspositions.put(  120,-4);
		this.innerVoiceTranspositions.put( 4121,-2);
		this.innerVoiceTranspositions.put( 3120,-1);
		this.innerVoiceTranspositions.put( 7121,-2);
		this.innerVoiceTranspositions.put( 7120,-2);
		this.innerVoiceTranspositions.put(  130,-2);
		this.innerVoiceTranspositions.put(  131,-1);
		this.innerVoiceTranspositions.put( 4141, 1);
		this.innerVoiceTranspositions.put( 3140, 2);
		this.innerVoiceTranspositions.put( 7140, 1);
		this.innerVoiceTranspositions.put( 7141, 2);
		this.innerVoiceTranspositions.put(  150,-1);
		this.innerVoiceTranspositions.put(  151,-1);
		this.innerVoiceTranspositions.put( 4151,-2);
		this.innerVoiceTranspositions.put( 3150,-1);
		this.innerVoiceTranspositions.put( 7160, 1);
		this.innerVoiceTranspositions.put( 7161, 2);
		this.innerVoiceTranspositions.put(  170,-1);
		this.innerVoiceTranspositions.put(  171,-1);
		this.innerVoiceTranspositions.put( 4171,-2);
		this.innerVoiceTranspositions.put( 3170,-1);
		this.innerVoiceTranspositions.put( 7170,-2);
		this.innerVoiceTranspositions.put( 7171,-2);
		this.innerVoiceTranspositions.put( 2240,-2);
		this.innerVoiceTranspositions.put( 2241,-2);
		this.innerVoiceTranspositions.put( 5250, 2);
		this.innerVoiceTranspositions.put( 5251, 2);
		this.innerVoiceTranspositions.put( 8250, 3);
		this.innerVoiceTranspositions.put( 9251, 2);
		this.innerVoiceTranspositions.put( 2260,-2);
		this.innerVoiceTranspositions.put( 2261,-2);
		this.innerVoiceTranspositions.put( 5260,-2);
		this.innerVoiceTranspositions.put( 5261,-1);
		this.innerVoiceTranspositions.put( 8270, 3);
		this.innerVoiceTranspositions.put( 9271, 2);
		this.innerVoiceTranspositions.put( 3320, 2);
		this.innerVoiceTranspositions.put( 4321, 1);
		this.innerVoiceTranspositions.put( 7320, 1);
		this.innerVoiceTranspositions.put( 7321, 2);
		this.innerVoiceTranspositions.put(11320, 4);
		this.innerVoiceTranspositions.put(11321, 3);
		this.innerVoiceTranspositions.put( 3340,-3);
		this.innerVoiceTranspositions.put( 4341,-4);
		this.innerVoiceTranspositions.put( 7340,-2);
		this.innerVoiceTranspositions.put( 7341,-2);
		this.innerVoiceTranspositions.put(11340,-2);
		this.innerVoiceTranspositions.put(11341,-2);
		this.innerVoiceTranspositions.put( 7360, 1);
		this.innerVoiceTranspositions.put( 7361, 2);
		this.innerVoiceTranspositions.put(11360, 2);
		this.innerVoiceTranspositions.put(11361, 1);
		this.innerVoiceTranspositions.put(  420, 2);
		this.innerVoiceTranspositions.put(  421, 2);
		this.innerVoiceTranspositions.put( 5450,-3);
		this.innerVoiceTranspositions.put( 5451,-3);
		this.innerVoiceTranspositions.put( 8450,-1);
		this.innerVoiceTranspositions.put( 9451,-2);
		this.innerVoiceTranspositions.put(  450,-1);
		this.innerVoiceTranspositions.put(  451,-1);
		this.innerVoiceTranspositions.put( 5460,-2);
		this.innerVoiceTranspositions.put( 5461,-1);
		this.innerVoiceTranspositions.put( 8470, 3);
		this.innerVoiceTranspositions.put( 9471, 2);
		this.innerVoiceTranspositions.put(  470, 2);
		this.innerVoiceTranspositions.put(  471, 2);
		this.innerVoiceTranspositions.put(11510, 1);
		this.innerVoiceTranspositions.put(11511, 1);
		this.innerVoiceTranspositions.put( 2510, 1);
		this.innerVoiceTranspositions.put( 2511, 2);
		this.innerVoiceTranspositions.put( 7560,-4);
		this.innerVoiceTranspositions.put( 7561,-3);
		this.innerVoiceTranspositions.put(11560,-3);
		this.innerVoiceTranspositions.put(11561,-2);
		this.innerVoiceTranspositions.put( 2560,-2);
		this.innerVoiceTranspositions.put( 2561,-2);
		this.innerVoiceTranspositions.put( 7570,-2);
		this.innerVoiceTranspositions.put( 7571,-2);
		this.innerVoiceTranspositions.put(  620, 2);
		this.innerVoiceTranspositions.put(  621, 2);
		this.innerVoiceTranspositions.put( 3620, 2);
		this.innerVoiceTranspositions.put( 4621, 1);
		this.innerVoiceTranspositions.put( 3640, 2);
		this.innerVoiceTranspositions.put( 4641, 1);
		this.innerVoiceTranspositions.put( 8650, 3);
		this.innerVoiceTranspositions.put( 9651, 2);
		this.innerVoiceTranspositions.put(  650, 2);
		this.innerVoiceTranspositions.put(  651, 2);
		this.innerVoiceTranspositions.put( 3650, 4);
		this.innerVoiceTranspositions.put( 4651, 3);
		this.innerVoiceTranspositions.put( 8670,-3);
		this.innerVoiceTranspositions.put( 9671,-4);
		this.innerVoiceTranspositions.put(  670,-1);
		this.innerVoiceTranspositions.put(  671,-1);
		this.innerVoiceTranspositions.put( 3670,-1);
		this.innerVoiceTranspositions.put( 4671,-2);
		this.innerVoiceTranspositions.put(11710, 1);
		this.innerVoiceTranspositions.put(11711, 1);
		this.innerVoiceTranspositions.put( 2710, 5);
		this.innerVoiceTranspositions.put( 2711, 5);
		this.innerVoiceTranspositions.put( 5710,-2);
		this.innerVoiceTranspositions.put( 5711,-1);
		this.innerVoiceTranspositions.put( 5750, 2);
		this.innerVoiceTranspositions.put( 5751, 2);
		this.innerVoiceTranspositions.put(11760, 1);
		this.innerVoiceTranspositions.put(11761, 1);
		this.innerVoiceTranspositions.put( 2760, 1);
		this.innerVoiceTranspositions.put( 2761, 2);
		this.innerVoiceTranspositions.put( 5760, 3);
		this.innerVoiceTranspositions.put( 5761, 4);
	}

	/*
		Assigns notes to the voices based on the previous chord, the chord that you're
		moving to, and the place in the chord that the previous note was, according to
		the hashmap
	*/
	public void fillInnerVoices (int chordIndex, int firstVoice, int lastVoice) {
		Chord fromChord = chords[chordIndex - 1], 
			  toFillChord = chords[chordIndex];
		int from = chordValues[chordIndex - 1], 
			toFill = chordValues[chordIndex], 
			majorVal = (this.major ? 1 : 0),
			randval, 
			dif;
		Note newNote; 
		Integer hashKey;

		for (int j = firstVoice; j < lastVoice; ++j) {
			newNote = new Note(fromChord.notes[j]);
			dif = this.tonic.getDif(newNote);

			if (from == toFill) {
				int firstVal = rand.nextInt(lastVoice - firstVoice);
				int secondVal = rand.nextInt(lastVoice - firstVoice);
				for (int i = firstVoice; i != lastVoice; ++i)
					toFillChord.notes[i] = new Note(fromChord.notes[i]);
				toFillChord.swapNotes(firstVoice + firstVal,firstVoice + secondVal);
				j = lastVoice;
			}
			else {
				hashKey = new Integer((dif * 1000) + (from * 100) + (toFill * 10) + majorVal);
				if (innerVoiceTranspositions.containsKey(hashKey))
					newNote.transpose(innerVoiceTranspositions.get(hashKey).intValue());
				toFillChord.notes[j] = newNote;
			}
		}
	}

	/*
		Returns a likely next chord according to Bach's harmonic progressions, with the different
		choices chosen randomly but weighted appropriately
		FUTURE: make better by improving accuracy and including more chord types
	*/
	public int getNextChord (int lastChord) {
		if (lastChord == 1) {
			this.choices[0] = 1;
			this.choices[1] = 2;
			this.choices[2] = 3;
			this.choices[3] = 4;
			this.choices[4] = 4;
			this.choices[5] = 5;
			this.choices[6] = 5;
			this.choices[7] = 6;
			this.choices[8] = 6;
			this.choices[9] = 7;
		}
		else if (lastChord == 2) {
			this.choices[0] = 2;
			this.choices[1] = 4;
			this.choices[2] = 4;
			this.choices[3] = 5;
			this.choices[4] = 5;
			this.choices[5] = 5;
			this.choices[6] = 5;
			this.choices[7] = 6;
			this.choices[8] = 6;
			this.choices[9] = 7;
		}
		else if (lastChord == 3) {
			this.choices[0] = 2;
			this.choices[1] = 3;
			this.choices[2] = 3;
			this.choices[3] = 4;
			this.choices[4] = 4;
			this.choices[5] = 4;
			this.choices[6] = 6;
			this.choices[7] = 6;
			this.choices[8] = 6;
			this.choices[9] = 6;
		}
		else if (lastChord == 4) {
			this.choices[0] = 2;
			this.choices[1] = 2;
			this.choices[2] = 4;
			this.choices[3] = 5;
			this.choices[4] = 5;
			this.choices[5] = 5;
			this.choices[6] = 6;
			this.choices[7] = 6;
			this.choices[8] = 6;
			this.choices[9] = 7;
		}
		else if (lastChord == 5) {
			this.choices[0] = 1;
			this.choices[1] = 1;
			this.choices[2] = 1;
			this.choices[3] = 1;
			this.choices[4] = 1;
			this.choices[5] = 5;
			this.choices[6] = 6;
			this.choices[7] = 6;
			this.choices[8] = 6;
			this.choices[9] = 7;
		}
		else if (lastChord == 6) {
			this.choices[0] = 2;
			this.choices[1] = 2;
			this.choices[2] = 2;
			this.choices[3] = 2;
			this.choices[4] = 4;
			this.choices[5] = 4;
			this.choices[6] = 5;
			this.choices[7] = 5;
			this.choices[8] = 6;
			this.choices[9] = 7;
		}
		else {
			this.choices[0] = 1;
			this.choices[1] = 1;
			this.choices[2] = 1;
			this.choices[3] = 1;
			this.choices[4] = 1;
			this.choices[5] = 5;
			this.choices[6] = 5;
			this.choices[7] = 5;
			this.choices[8] = 6;
			this.choices[9] = 7;
		}
		return this.choices[rand.nextInt(10)];
	}

	/*
		Returns a dominant chord, weighted to *probably* return a 5 chord
	*/
	public int getDominant () {
		int[] choices = {5,5,5,7};
		int choice = rand.nextInt(4);
		return choices[choice];
	}

	/*
		Returns a predominant chord
	*/
	public int getPredominant () {
		int[] choices = {2,2,4,4,6};
		int choice = rand.nextInt(5);
		return choices[choice];
	}

	/*
		Returns either a predominant or a dominant chord
	*/
	public int getPredominantOrDominant () {
		int[] choices = {2,2,4,4,5,5,6,6,7};
		int choice = rand.nextInt(9);
		return choices[choice];
	}

	/*
		not done
		Traverses a pre-initialized tree representing possible chord paths, iterating through the options and,
		if it hits a point where it's tried all the options, marks that path as "tried" and returns to a previous
		node to try a new path
	*/
	public void harmonizeEnteredNotes (int enteredNotes, ChordTree path) {
		int here = 1, counting = 0;
		while ((here < enteredNotes) && (counting++ < 200)) {
			if (path.hasChordLeft() || (here == enteredNotes)) {
				path.selected = true;
				this.chordValues[here] = path.getChordAndSetPath(rand, members, entry.notes[here], tonic, major);
				path = path.toSet;
				++here;
			}
			else {
				path.tried = true;
				path.selected = false;
				path = path.prevTree;
				--here;
			}
			if (counting == 200) {
				System.out.println("infinite looped - check your Chorale.java fillChorsAray code!");
				return;
			}
		}
	}

	/*
		Prints out the numerical value of the chords, 4 to a line
	*/
	public void printChords () {
		for (int i = 0; i != 32; ++i) {
			System.out.print(chordValues[i]);
			if (i%4 == 3)
				System.out.println();
		}
	}

	/*
		Makes sure that each note is in the proper register for its voice
		Future: rather than having octave leaps, find the closest diatonic note
			(making sure not to omit any required notes)
	*/
	public void putInProperOctaves () {
		for (int i = 0; i != this.chordCount; ++i) {
			for (int j = 0; j != 4; ++j) {
				if (j == 0) {
					while (this.chords[i].notes[j].val < sopLower)
						this.chords[i].notes[j].val += 12;
					while (this.chords[i].notes[j].val > sopUpper)
						this.chords[i].notes[j].val -= 12;
				}
				else if (j == 1) {
					while (this.chords[i].notes[j].val < altLower)
						this.chords[i].notes[j].val += 12;
					while (this.chords[i].notes[j].val > altUpper)
						this.chords[i].notes[j].val -= 12;
				}
				else if (j == 2) {
					while (this.chords[i].notes[j].val < tenLower)
						this.chords[i].notes[j].val += 12;
					while (this.chords[i].notes[j].val > tenUpper)
						this.chords[i].notes[j].val -= 12;
				}
				else {
					while (this.chords[i].notes[j].val < basLower)
						this.chords[i].notes[j].val += 12;
					while (this.chords[i].notes[j].val > basUpper)
						this.chords[i].notes[j].val -= 12;
				}
			}
		}
	}

	/*
		Returns a string representation of the work that can be played by a player
	*/
	public String toString () {
		try {
			String voice0 = "V0 | ";
			String voice1 = "V1 | ";
			String voice2 = "V2 | ";
			String voice3 = "V3 | ";
			for (int i = 0; i != chordCount; ++i) {
				voice0 += chords[i].notes[0].toString() + " ";
				voice1 += chords[i].notes[1].toString() + " ";
				voice2 += chords[i].notes[2].toString() + " ";
				voice3 += chords[i].notes[3].toString() + " ";
				if (i%4 == 3) {
					voice0 += " | ";
					voice1 += " | ";
					voice2 += " | ";
					voice3 += " | ";
				}
			}
			return (voice0 + " \n" + voice1 + " \n" + voice2 + " \n" + voice3);
		}
		catch (NullPointerException e) {
			System.out.println(i);
		}
		return "";
	}
}