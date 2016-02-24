/* Represents a 2-voice, contrapuntal composition

   by Sarah Klein
   Future: implement soft rules
   		avoid unisons in the middle of the composition
   		check that there are more steps than skips
   	allow user to select range/mode
   	check for tritone outlines
   	go into cadence properly
*/

public class TwoVoiceCounterpoint extends Counterpoint {
	Note[][] notes;	//Actual notes in the line
	int[] validNoteValues = {33, 35, 36, 38, 40, 41, 43, 45};	//valid note values in our default mode
	CantusFirmus cantusFirmus;	//the cantus firmus of our composition

	/*
		Constructor
		Creates the notes that will construct the composition, initialized to quarter-note rests
	*/
	public TwoVoiceCounterpoint (CantusFirmus cantusFirmus) {
		super();
		this.cantusFirmus = cantusFirmus;
		this.numNotes = cantusFirmus.numNotes;
		this.notes = new Note[numNotes][2];
		for (int i = 0; i != numNotes; ++i) {
			this.notes[i][0] = new Note('q');
			this.notes[i][1] = cantusFirmus.notes[i];
		}
	}

	/*
		Writes a line of first species counterpoint above a given cantus firmus
		future: 
	*/
	public void pseudoComposeFromScratchInFirstSpecies() {
		int place = 0,
			randVal = rand.nextInt(3),
			numberOfSkips = 0, 
			numberOfNoteRepetitions = 0,
			valueOfRepeatedNote,
			attempts,
			counter = 0,
			diff;
		boolean nextMotionStepwise = false,
			noteFound = false;

		//fill the first note with either the tonic or the dominant
		if (randVal == 0) {
			 notes[place++][0] = new Note(validNoteValues[0]);
		} else if (randVal == 1) {
			notes[place++][0] = new Note(validNoteValues[3]);
		} else {
			notes[place++][0] = new Note(validNoteValues[7]);
		}
		valueOfRepeatedNote = notes[0][0].val;

		//put tonic in the last space and the appropriate pentultimate note based on the cantus firmus
		if (notes[numNotes - 2][1].val == validNoteValues[2] + 1) {
			notes[numNotes - 2][0] = new Note(validNoteValues[4]);
		} else {
			notes[numNotes - 2][0] = new Note(validNoteValues[2]);
			notes[numNotes - 2][0].transpose(1);
		}
		notes[numNotes - 1][0] = new Note(validNoteValues[3]);

		//fill all of the notes to the end using a path-finding algorithm
		while (place < numNotes - 2 && counter < 1000) {
			System.out.println("place: " + place);
			++counter;
			attempts = 0;
			noteFound = false;

			randVal = rand.nextInt(8);	//we're going to try every note possible for this line
			while (attempts < 8 && !noteFound) {	//while we haven't tried every note and we haven't found the right note
				if (isConsonantVertically(notes[place][1].val, validNoteValues[randVal]) && 
					(isImperfectConsonance(notes[place][1].val, validNoteValues[randVal]) || 
					 isContraryOrOblique(notes[place-1][0].val, notes[place-1][1].val, validNoteValues[randVal], notes[place][1].val))
					) {
					diff = notes[place - 1][0].val - validNoteValues[randVal];
					if (diff == 0) {
						//if it's the same note
						if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[randVal] && !nextMotionStepwise && notes[place - 1][1].val != notes[place][1].val) {
							noteFound = true;
							valueOfRepeatedNote = validNoteValues[randVal];	//keep track in case we get repetitive
						}
					} else if (diff == 1 || diff == 2 || diff == -2 || diff == -1) {
						//if it's stepwise movement
						noteFound = true;
					} else if (diff == 3 || diff == 4 || diff == -3 || diff == -4) {
						//if it's motion by a third
						if (numberOfSkips != 2 && !nextMotionStepwise) {
							noteFound = true;
						}
					} else if (diff == 5 || diff == 7 || diff == 8 || diff == 12 || diff == -5 || diff == -7 ||  diff == -8 || diff == -12) {
						//if it's gonna leap
						if (numberOfSkips == 0) {
							noteFound = true;
						}
					}

					if (noteFound) {
						notes[place++][0] = new Note(validNoteValues[randVal]);	//assign the note
						numberOfSkips = (diff < 3 && diff > -3) ? 0 : numberOfSkips + 1;	//add a skip if we're skipping, otherwise reset
						nextMotionStepwise = (diff > 4 || diff < -4) ? true : false;	//dictate next motion stepwise if it's a large leap
						numberOfNoteRepetitions = (diff == 0) ? numberOfNoteRepetitions + 1 : 0; //increase number of repetitions of we're repeating
					}
				}
				randVal = (randVal + 1) % 8;
				++attempts;
			}
					
			if (noteFound == false) {
				--place;
				if (place < 0)
					place = 0;
			}

			if (place == numNotes - 2) {
				if (!voiceLeadingIntoCadenceIsValid()) {
					place -= 2;
				}
			}
		}
	}

	/*
		Checks the voice leading in the soprano line going into the cadence - returns true if valid
	*/
	public boolean voiceLeadingIntoCadenceIsValid() {
		int prevSoprano = notes[numNotes - 3][0].val,
			cadSoprano = notes[numNotes - 2][0].val;
		if (isConsonantMelodically(prevSoprano, cadSoprano)) {
			return true;
		}
		return false;
	}

	/*
		Converts the cantus firmus to a string that can be played by the JFugue player
	*/
	public String toString() {
		try {
			String voice0 = "V0 | ";
			String voice1 = "V1 | ";
			for (int i = 0; i != numNotes; ++i) {
				voice0 += notes[i][0].toString() + " ";
				voice1 += notes[i][1].toString() + " ";
			}
			return (voice0 + " \n" + voice1);
		}
		catch (NullPointerException e) {
		}
		return "";
	}
}