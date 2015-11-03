/* Represents a cantus firmus, which contains a monophonic line of notes, defaultedly in
	d-dorian mode
	future: add more modes

   by Sarah Klein
*/

import java.util.Random;

public class CantusFirmus {
	Note[] notes;	//Actual notes in the line
	int numNotes;	//Number of notes in the chord
	int[] validNoteValues = {26, 28, 29, 31, 33, 35, 36, 38};	//valid note values in our default mode
	Random rand = new Random();

	/*
		Constructor
		Creates the notes that will construct the cantus firmus, initialized to whole-note rests
	*/
	public CantusFirmus (int numNotes) {
		this.numNotes = numNotes;
		this.notes = new Note[numNotes];
		for (int i = 0; i != numNotes; ++i) {
			this.notes[i] = new Note('w');
		}
	}

	/*
		Writes a cantus firmus with no entry measure to work from
		future: implement
	*/
	public void pseudoComposeFromScratch() {
		int place = 0,
			randVal = rand.nextInt(3),
			numberOfSkips = 0, 
			numberOfNoteRepetitions = 0,
			valueOfRepeatedNote,
			attempts,
			counter = 0;
		boolean previousMotionStepwise = true,
			nextMotionStepwise = false,
			noteFound = false;
		//fill the first note with either the tonic or the dominant
		if (randVal == 0) {
			 notes[place] = new Note(validNoteValues[0]);
			 valueOfRepeatedNote = validNoteValues[0];
		} else if (randVal == 1) {
			notes[place] = new Note(validNoteValues[4]);
			valueOfRepeatedNote = validNoteValues[4];
		} else {
			notes[place] = new Note(validNoteValues[7]);
			valueOfRepeatedNote = validNoteValues[7];
		}
		++place;

		//put tonic in the last space and the appropriate pentultimate note
		randVal = rand.nextInt(2);
		if (randVal == 0) {
			notes[numNotes - 1] = new Note(validNoteValues[0]);
			notes[numNotes - 2] = new Note(validNoteValues[1]);
		} else {
			notes[numNotes - 1] = new Note(validNoteValues[7]);
			notes[numNotes - 2] = new Note(validNoteValues[6]);
		}

		//fill all of the notes to the end using a path-finding algorithm
		while (place < numNotes - 2 && counter < 1000) {
			++counter;
			randVal = rand.nextInt(8);
			attempts = 0;
			noteFound = false;
			if (notes[place - 1].val == validNoteValues[0]) {
				while (attempts < 8 && !noteFound) {
					if (randVal == 0) {
						if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[0] && !nextMotionStepwise) {
							notes[place++] = new Note(validNoteValues[0]);
							numberOfSkips = 0;
							numberOfNoteRepetitions++;
							valueOfRepeatedNote = validNoteValues[0];
							previousMotionStepwise = true;
							noteFound = true;
						}
					}
					else if (randVal == 1) {
						notes[place++] = new Note(validNoteValues[1]);
						numberOfSkips = 0;
						numberOfNoteRepetitions = 0;
						previousMotionStepwise = true;
						nextMotionStepwise = false;
						noteFound = true;
					}
					else if (randVal == 2) {
						if (numberOfSkips != 2 && !nextMotionStepwise) {
							notes[place++] = new Note(validNoteValues[2]);
							numberOfSkips++;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = false;
							noteFound = true;
						}
					}
					else if (randVal == 3 || randVal == 4 || randVal == 5 || randVal == 7) {
						if (previousMotionStepwise) {
							notes[place++] = new Note(validNoteValues[randVal]);
							numberOfSkips++;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = false;
							nextMotionStepwise = true;
							noteFound = true;
						}
					}
					randVal = (randVal + 1) % 8;
					++attempts;
				}
				if (noteFound == false) {
					--place;
				}
			}
			else if (notes[place - 1].val == validNoteValues[1]) {
				while (attempts < 8 && !noteFound) {
					if (randVal == 1) {
						if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[1] && !nextMotionStepwise) {
							notes[place++] = new Note(validNoteValues[1]);
							numberOfSkips = 0;
							numberOfNoteRepetitions++;
							valueOfRepeatedNote = validNoteValues[1];
							previousMotionStepwise = true;
							noteFound = true;
						}
					}
					else if (randVal == 0 || randVal == 2) {
						notes[place++] = new Note(validNoteValues[randVal]);
						numberOfSkips = 0;
						numberOfNoteRepetitions = 0;
						previousMotionStepwise = true;
						nextMotionStepwise = false;
						noteFound = true;
					}
					else if (randVal == 3) {
						if (numberOfSkips != 2 && !nextMotionStepwise) {
							notes[place++] = new Note(validNoteValues[3]);
							numberOfSkips++;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = false;
							noteFound = true;
						}
					}
					else if (randVal == 4 || randVal == 5 || randVal == 6) {
						if (previousMotionStepwise) {
							notes[place++] = new Note(validNoteValues[randVal]);
							numberOfSkips++;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = false;
							nextMotionStepwise = true;
						}
					}
					randVal = (randVal + 1) % 8;
					++attempts;
				}
				if (noteFound == false) {
					--place;
				}
			}
			else if (notes[place - 1].val == validNoteValues[2]) {
				while (attempts < 8 && !noteFound) {
					if (randVal == 2) {
						if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[2] && !nextMotionStepwise) {
							notes[place++] = new Note(validNoteValues[2]);
							numberOfSkips = 0;
							numberOfNoteRepetitions++;
							valueOfRepeatedNote = validNoteValues[2];
							previousMotionStepwise = true;
							noteFound = true;
						}
					}
					else if (randVal == 1 || randVal == 3) {
						notes[place++] = new Note(validNoteValues[randVal]);
						numberOfSkips = 0;
						numberOfNoteRepetitions = 0;
						previousMotionStepwise = true;
						nextMotionStepwise = false;
						noteFound = true;
					}
					else if (randVal == 0 || randVal == 4) {
						if (numberOfSkips != 2 && !nextMotionStepwise) {
							notes[place++] = new Note(validNoteValues[randVal]);
							numberOfSkips++;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = false;
							noteFound = true;
						}
					}
					else if (randVal == 6) {
						if (previousMotionStepwise) {
							notes[place++] = new Note(validNoteValues[randVal]);
							numberOfSkips++;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = false;
							nextMotionStepwise = true;
							noteFound = true;
						}
					}
					randVal = (randVal + 1) % 8;
					++attempts;
				}
				if (noteFound == false) {
					--place;
				}
			}
			else if (notes[place - 1].val == validNoteValues[3]) {
				while (attempts < 8 && !noteFound) {
					if (randVal == 3) {
						if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[randVal] && !nextMotionStepwise) {
							notes[place++] = new Note(validNoteValues[3]);
							numberOfSkips = 0;
							numberOfNoteRepetitions++;
							valueOfRepeatedNote = validNoteValues[3];
							previousMotionStepwise = true;
							noteFound = true;
						}
					}
					else if (randVal == 2 || randVal == 4) {
						notes[place++] = new Note(validNoteValues[randVal]);
						numberOfSkips = 0;
						numberOfNoteRepetitions = 0;
						previousMotionStepwise = true;
						nextMotionStepwise = false;
						noteFound = true;
					}
					else if (randVal == 1 || randVal == 5) {
						if (numberOfSkips != 2 && !nextMotionStepwise) {
							notes[place++] = new Note(validNoteValues[randVal]);
							numberOfSkips++;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = false;
							noteFound = true;
						}
					}
					else if (randVal == 0 || randVal == 6 || randVal == 7) {
						if (previousMotionStepwise) {
							notes[place++] = new Note(validNoteValues[randVal]);
							numberOfSkips++;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = false;
							nextMotionStepwise = true;
							noteFound = true;
						}
					}
					randVal = (randVal + 1) % 8;
					++attempts;
				}
				if (noteFound == false) {
					--place;
				}
			}
			else if (notes[place - 1].val == validNoteValues[4]) {
				while (attempts < 8 && !noteFound) {
					if (randVal == 4) {
						if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[randVal] && !nextMotionStepwise) {
							notes[place++] = new Note(validNoteValues[randVal]);
							numberOfSkips =0 ;
							numberOfNoteRepetitions++;
							valueOfRepeatedNote = validNoteValues[randVal];
							previousMotionStepwise = true;
							noteFound = true;
						}
					}
					else if (randVal == 3 || randVal == 5) {
						notes[place++] = new Note(validNoteValues[randVal]);
						numberOfSkips = 0;
						numberOfNoteRepetitions = 0;
						previousMotionStepwise = true;
						nextMotionStepwise = false;
						noteFound = true;
					}
					else if (randVal == 2 || randVal == 6) {
						if (numberOfSkips != 2 && !nextMotionStepwise) {
							notes[place++] = new Note(validNoteValues[randVal]);
							numberOfSkips++;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = false;
							noteFound = true;
						}
					}
					else if (randVal == 0 || randVal == 1 || randVal == 7) {
						if (previousMotionStepwise) {
							notes[place++] = new Note(validNoteValues[randVal]);
							numberOfSkips++;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = false;
							nextMotionStepwise = true;
							noteFound = true;
						}
					}
					randVal = (randVal + 1) % 8;
					++attempts;
				}
				if (noteFound == false) {
					--place;
				}
			}
			else if (notes[place - 1].val == validNoteValues[5]) {
				while (attempts < 8 && !noteFound) {
					if (randVal == 5) {
						if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[randVal] && !nextMotionStepwise) {
							notes[place++] = new Note(validNoteValues[5]);
							numberOfSkips = 0;
							numberOfNoteRepetitions++;
							valueOfRepeatedNote = validNoteValues[5];
							previousMotionStepwise = true;
							noteFound = true;
						}
					}
					else if (randVal == 4 || randVal == 6) {
						notes[place++] = new Note(validNoteValues[randVal]);
						numberOfSkips = 0;
						numberOfNoteRepetitions = 0;
						previousMotionStepwise = true;
						nextMotionStepwise = false;
						noteFound = true;
					}
					else if (randVal == 3 || randVal == 7) {
						if (numberOfSkips != 2 && !nextMotionStepwise) {
							notes[place++] = new Note(validNoteValues[randVal]);
							numberOfSkips++;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = false;
							noteFound = true;
						}
					}
					else if (randVal == 1) {
						if (previousMotionStepwise) {
							notes[place++] = new Note(validNoteValues[randVal]);
							numberOfSkips++;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = false;
							nextMotionStepwise = true;
							noteFound = true;
						}
					}
					randVal = (randVal + 1) % 8;
					++attempts;
				}
				if (noteFound == false) {
					--place;
				}
			}
			else if (notes[place - 1].val == validNoteValues[6]) {
				while (attempts < 8 && !noteFound) {
					if (randVal == 6) {
						if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[randVal] && !nextMotionStepwise) {
							notes[place++] = new Note(validNoteValues[6]);
							numberOfSkips = 0;
							numberOfNoteRepetitions++;
							valueOfRepeatedNote = validNoteValues[6];
							previousMotionStepwise = true;
							noteFound = true;
						}
					}
					else if (randVal == 5 || randVal == 7) {
						notes[place++] = new Note(validNoteValues[randVal]);
						numberOfSkips = 0;
						numberOfNoteRepetitions = 0;
						previousMotionStepwise = true;
						nextMotionStepwise = false;
						noteFound = true;
					}
					else if (randVal == 4) {
						if (numberOfSkips != 2 && !nextMotionStepwise) {
							notes[place++] = new Note(validNoteValues[randVal]);
							numberOfSkips++;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = false;
							noteFound = true;
						}
					}
					else if (randVal == 2 || randVal == 3) {
						if (previousMotionStepwise) {
							notes[place++] = new Note(validNoteValues[randVal]);
							numberOfSkips++;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = false;
							nextMotionStepwise = true;
							noteFound = true;
						}
					}
					randVal = (randVal + 1) % 8;
					++attempts;
				}
				if (noteFound == false) {
					--place;
				}
			}
			else {
				while (attempts < 8 && !noteFound) {
					if (randVal == 7) {
						if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[randVal] && !nextMotionStepwise) {
							notes[place++] = new Note(validNoteValues[7]);
							numberOfSkips = 0;
							numberOfNoteRepetitions++;
							valueOfRepeatedNote = validNoteValues[7];
							previousMotionStepwise = true;
							noteFound = true;
						}
					}
					else if (randVal == 6) {
						notes[place++] = new Note(validNoteValues[randVal]);
						numberOfSkips = 0;
						numberOfNoteRepetitions = 0;
						previousMotionStepwise = true;
						nextMotionStepwise = false;
						noteFound = true;
					}
					else if (randVal == 5) {
						if (numberOfSkips != 2 && !nextMotionStepwise) {
							notes[place++] = new Note(validNoteValues[randVal]);
							numberOfSkips++;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = false;
							noteFound = true;
						}
					}
					else if (randVal == 0 || randVal == 3 || randVal == 4) {
						if (previousMotionStepwise) {
							notes[place++] = new Note(validNoteValues[randVal]);
							numberOfSkips++;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = false;
							nextMotionStepwise = true;
							noteFound = true;
						}
					}
					randVal = (randVal + 1) % 8;
					++attempts;
				}
				if (noteFound == false) {
					--place;
				}
			}
		}
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