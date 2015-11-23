/* 
	CantusFirmus.java
   	Represents a cantus firmus, which contains a monophonic line of notes, defaultedly in d-dorian mode
		future: add more mode

   	by Sarah Klein
*/


public class CantusFirmus extends Counterpoint {
	Note[] notes;	//Actual notes in the line
	int[] validNoteValues = {26, 28, 29, 31, 33, 35, 36, 38};	//valid note values in our default mode

	/*
		Constructor
		Creates the notes that will construct the cantus firmus, initialized to whole-note rests
	*/
	public CantusFirmus (int numNotes) {
		super();
		this.numNotes = numNotes;
		this.notes = new Note[numNotes];
		for (int i = 0; i != numNotes; ++i) {
			this.notes[i] = new Note('w');
		}
	}

	/*
		Writes a cantus firmus with no entry measure to work from
		future: make large leaps more likely to follow and precede contrary motion
				make end of line lead into cadence more fully
				eliminate descending minor sixths
	*/
	public void pseudoComposeFromScratch() {
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
			 notes[place++] = new Note(validNoteValues[0]);
			 valueOfRepeatedNote = validNoteValues[0];
		} else if (randVal == 1) {
			notes[place++] = new Note(validNoteValues[4]);
			valueOfRepeatedNote = validNoteValues[4];
		} else {
			notes[place++] = new Note(validNoteValues[7]);
			valueOfRepeatedNote = validNoteValues[7];
		}

		//put tonic in the last space and the appropriate pentultimate note
		randVal = rand.nextInt(2);
		if (randVal == 0) {
			notes[numNotes - 1] = new Note(validNoteValues[0]);
			notes[numNotes - 2] = new Note(validNoteValues[1]);
		} else {
			notes[numNotes - 1] = new Note(validNoteValues[7]);
			notes[numNotes - 2] = new Note(validNoteValues[6]);
			notes[numNotes - 2].transpose(1);
		}

		//fill all of the notes to the end using a path-finding algorithm
		while (place < numNotes - 2 && counter < 1000) {
			++counter;
			randVal = rand.nextInt(8);
			attempts = 0;
			noteFound = false;

			while ((attempts < 8) && !noteFound) {
				diff = notes[place - 1].val - validNoteValues[randVal];
				if (diff == 0) {
					//if it's the same note
					if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[randVal] && !nextMotionStepwise) {
						valueOfRepeatedNote = validNoteValues[randVal];
						noteFound = true;
					}
				} else if (diff == 1 || diff == 2 || diff == -2 || diff == -1) {
					//if the motion is stepwise
					noteFound = true;
				} else if (diff == 3 || diff == 4 || diff == -3 || diff == -4) {
					if (numberOfSkips != 2 && !nextMotionStepwise) {
						noteFound = true;
					}
				} else if (diff == 5 || diff == 7 || diff == 8 || diff == 12 || diff == -5 || diff == -7 ||
						   diff == -8 || diff == -12) {
					if (numberOfSkips == 0) {
						noteFound = true;
					}
				}

				if (noteFound) {
					notes[place++] = new Note(validNoteValues[randVal]);	//assign the note
					numberOfSkips = (diff < 3 && diff > -3) ? 0 : numberOfSkips + 1;	//add a skip if we're skipping, otherwise reset
					nextMotionStepwise = (diff > 4 || diff < -4) ? true : false;	//dictate next motion stepwise if it's a large leap
					numberOfNoteRepetitions = (diff == 0) ? numberOfNoteRepetitions + 1 : 0; //increase number of repetitions of we're repeating
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
		Checks the voice leading  going into the cadence - returns true if valid
	*/
	public boolean voiceLeadingIntoCadenceIsValid() {
		int prevNote = notes[numNotes - 3].val,
			cadNote = notes[numNotes - 2].val;
		if (isConsonantMelodically(prevNote, cadNote)) {
			return true;
		}
		return false;
	}

	/*
		Converts the cantus firmus to a string that can be played by the JFugue player
	*/
	public String toString() {
		String cantusFirmusString = "";
		try {
			for (int i = 0; i != numNotes; ++i) {
				cantusFirmusString += notes[i].toString() + " ";
			}
			return cantusFirmusString;
		}
		catch (NullPointerException e) {
		}
		return "";
	}
}
