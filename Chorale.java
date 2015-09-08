import java.util.HashMap;
import java.util.Random;

public class Chorale extends Staff {
	Chord[] music;
	int[] chords, choices = new int[10];
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
	int chordCount = 32, notesPerMeasure = 8;
	//range constrictuions on voices
	int sopUpper = 60, 
		sopLower = 48, 
		altUpper = 48, 
		altLower = 33, 
		tenUpper = 41, 
		tenLower = 29, 
		basUpper =  29, 
		basLower = 12, 
		tempo = 120;
	Note tonic;
	Boolean major = true;
	Random rand = new Random();
	String instrument = "WARM";

	public Chorale (Measure entry, int key, String inst) {
		if (inst != "")
			this.instrument = inst;
		this.tonic = new Note(key);
		if ((key == 2) || (key == 4) || (key == 9))
			this.major = false;
		this.entry = entry;
	}

	public void pseudoCompose() {
		System.out.println(this.tonic.getName() + " " + this.major);
		initializeInnerVoiceHashMap();
		this.music = new Chord[this.chordCount];
		checkKeyAndFixWrongNotes();
		fillChordsArray(entry.noteCount);
		printChords();
		this.music[0] = new Chord(4,'q');
		this.music[0].notes[0] = new Note(entry.notes[i], 'q');
		fillFirstChord();	
		for (int i = 1; i != this.chordCount; ++i) {
			this.music[i] = new Chord(4,'q');
			if (i < entry.noteCount) {
				this.music[i].notes[0] = new Note(entry.notes[i], 'q');
				fillInnerVoices(i, 1, 4);
			}
			else
				fillInnerVoices(i,0,4);
		}		
		this.music[this.chordCount - 1].notes[3] = new Note(tonic, 'q');	
		putInProperOctaves();
	}

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

	public void fillChordsArray (int numNotes) {
		ChordTree path = new ChordTree(1, numNotes, null);
		Boolean working = true;
		int here = this.entry.noteCount, counting = 0, preCadencePlace = this.chordCount - 5;
		this.chords = new int[this.chordCount];
		this.chords[0] = 1;
		harmonizeEnteredNotes(this.entry.noteCount, path);

		while (here < this.chordCount-4) {
			this.chords[here] = getNextChord(this.chords[here - 1]);
			++here;
		}
		
		if ((this.chords[preCadencePlace] == 7) || (this.chords[preCadencePlace] == 5))
			this.chords[preCadencePlace + 1] = 1;
		else
			this.chords[preCadencePlace + 1] = getPredominant();
		this.chords[preCadencePlace + 2] = getPredominantOrDominant();
		this.chords[preCadencePlace + 3] = getDominant();
		this.chords[preCadencePlace + 4] = 1;

		if (counting == 200)
			System.out.println("infinite looped - check your Chorale.java fillChorsAray code!");
	}

	public void fillFirstChord () {
		int majorVal = (this.major ? 1 : 0), randVal = this.rand.nextInt(2);
		for (int i = 1; i != 4; ++i)
			this.music[0].notes[i] = new Note(tonic, 'q');
		int interval = this.tonic.getDif(this.music[0].notes[0]);

		if ((interval == 0) || (interval == 7)) {
			this.music[0].notes[2-randVal].transpose(3 + majorVal);
			if (interval == 0)
				this.music[0].notes[2-(1-randVal)].transpose(-5);
		}
		else if ((interval == 3) || (interval == 4))
			this.music[0].notes[2-randVal].transpose(-5);
		else {
			randVal = this.rand.nextInt(3);
			this.music[0].notes[0] = new Note(tonic, 'q');
			if (randVal == 0)
				this.music[0].notes[0].transpose(3 + majorVal);
			else if (randVal == 1)
				this.music[0].notes[0].transpose(-5);
			fillFirstChord();
		}
	}

	/*
		Maps the way that the inner voices should move
		lvalue: difference between notes * 1000 + chord moving from * 100 + chord moving to + 10 + isMajor
		rvalue: transposition of the voice
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

	public void fillInnerVoices (int chordIndex, int firstVoice, int lastVoice) {
		Chord fromChord = music[chordIndex - 1], toFillChord = music[chordIndex];
		int from = chords[chordIndex - 1], toFill = chords[chordIndex], randval, dif, majorVal = (this.major ? 1 : 0);
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

	public int getDominant () {
		int[] choices = {5,5,5,7};
		int choice = rand.nextInt(4);
		return choices[choice];
	}

	public int getPredominant () {
		int[] choices = {2,2,4,4,6};
		int choice = rand.nextInt(5);
		return choices[choice];
	}

	public int getPredominantOrDominant () {
		int[] choices = {2,2,4,4,5,5,6,6,7};
		int choice = rand.nextInt(9);
		return choices[choice];
	}

	public void harmonizeEnteredNotes (int enteredNotes, ChordTree path) {
		int here = 1, counting = 0;
		while ((here < enteredNotes) && (counting < 200)) {
			if (path.hasChordLeft() || (here == enteredNotes)) {
				path.selected = true;
				this.chords[here] = path.getChordAndSetPath(rand, members, entry.notes[here], tonic, major);
				path = path.toSet;
				++here;
			}
			else {
				path.tried = true;
				path.selected = false;
				path = path.prevTree;
				--here;
			}
			++counting;
		}
		if (counting == 200)
			System.out.println("infinite looped - check your Chorale.java fillChorsAray code!");
	}

	public void printChords () {
		for (int i = 0; i != 32; ++i) {
			System.out.print(chords[i]);
			if (i%4 == 3)
				System.out.println();
		}
	}

	public void putInProperOctaves () {
		for (int i = 0; i != this.chordCount; ++i) {
			for (int j = 0; j != 4; ++j) {
				if (j == 0) {
					while (this.music[i].notes[j].val < sopLower)
						this.music[i].notes[j].val += 12;
					while (this.music[i].notes[j].val > sopLower)
						this.music[i].notes[j].val -= 12;
				}
				else if (j == 1) {
					while (this.music[i].notes[j].val < altLower)
						this.music[i].notes[j].val += 12;
					while (this.music[i].notes[j].val > altLower)
						this.music[i].notes[j].val -= 12;
				}
				else if (j == 2) {
					while (this.music[i].notes[j].val < tenLower)
						this.music[i].notes[j].val += 12;
					while (this.music[i].notes[j].val > tenLower)
						this.music[i].notes[j].val -= 12;
				}
				else {
					while (this.music[i].notes[j].val < basLower)
						this.music[i].notes[j].val += 12;
					while (this.music[i].notes[j].val > basLower)
						this.music[i].notes[j].val -= 12;
				}
			}
		}
	}

	public String toString () {
		try {
			String voice0 = "V0 | ";
			String voice1 = "V1 | ";
			String voice2 = "V2 | ";
			String voice3 = "V3 | ";
			for (i = 0; i != chordCount; ++i) {
				voice0 += music[i].notes[0].toString() + " ";
				voice1 += music[i].notes[1].toString() + " ";
				voice2 += music[i].notes[2].toString() + " ";
				voice3 += music[i].notes[3].toString() + " ";
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