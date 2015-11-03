/* Represents a 2-voice, contrapuntal composition

   by Sarah Klein
   Future: implement soft rules
   	avoid unisons in the middle of the composition
*/

import java.util.Random;

public class TwoVoiceCounterpoint {
	Note[][] notes;	//Actual notes in the line
	int numNotes;	//Number of notes in the chord
	int[] validNoteValues = {33, 35, 36, 38, 40, 41, 43, 45};	//valid note values in our default mode
	CantusFirmus cantusFirmus;	//the cantus firmus of our composition
	Random rand = new Random();

	/*
		Constructor
		Creates the notes that will construct the composition, initialized to quarter-note rests
	*/
	public TwoVoiceCounterpoint (CantusFirmus cantusFirmus) {
		this.cantusFirmus = cantusFirmus;
		this.numNotes = cantusFirmus.numNotes;
		this.notes = new Note[numNotes][2];
		for (int i = 0; i != numNotes; ++i) {
			this.notes[i][0] = new Note('q');
			this.notes[i][1] = cantusFirmus.notes[i];
		}
		System.out.println("cantus firmus: ");
		System.out.println(toString());
	}

	/*
	Writes a line of first species counterpoint above a given cantus firmus
	future: 
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

			if (notes[place][1].val == 26) {
				randVal = rand.nextInt(4);
				while (attempts < 4 && !noteFound) {
					if (randVal == 0) {
						if (notes[place - 1][1].val == validNoteValues[1]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[1] && !nextMotionStepwise && notes[place - 1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[1]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[1];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[1] == 1 || notes[place - 1][0].val - validNoteValues[1] == 2 ||
								   notes[place - 1][0].val - validNoteValues[1] == -2 || notes[place - 1][0].val - validNoteValues[1] == -1) {
							//if the motion is stepwise
							notes[place++][0] = new Note(validNoteValues[1]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if (notes[place - 1][0].val - validNoteValues[1] == 3 || notes[place - 1][0].val - validNoteValues[1] == 4 ||
								   notes[place - 1][0].val - validNoteValues[1] == -3 || notes[place - 1][0].val - validNoteValues[1] == -4) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[1]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[1] == 5 || notes[place - 1][0].val - validNoteValues[1] == 7 ||
								   notes[place - 1][0].val - validNoteValues[1] == 8 || notes[place - 1][0].val - validNoteValues[1] == 12 ||
								   notes[place - 1][0].val - validNoteValues[1] == -5 || notes[place - 1][0].val - validNoteValues[1] == -7 ||
								   notes[place - 1][0].val - validNoteValues[1] == -8 || notes[place - 1][0].val - validNoteValues[1] == -12) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[1]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else if (randVal == 1) {
						if (notes[place - 1][0].val == validNoteValues[3]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[3] && !nextMotionStepwise && notes[place-1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[3]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[3];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[3] == 1 && notes[place-1][1].val - notes[place][1].val < 0) || 
							(notes[place - 1][0].val - validNoteValues[3] == 2 && notes[place-1][1].val - notes[place][1].val < 0) ||
							(notes[place - 1][0].val - validNoteValues[3] == -2 && notes[place-1][1].val - notes[place][1].val > 0) || 
							(notes[place - 1][0].val - validNoteValues[3] == -1 && notes[place-1][1].val - notes[place][1].val > 0)) {
							//if the motion is stepwise and contrary
							notes[place++][0] = new Note(validNoteValues[3]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if ((notes[place - 1][0].val - validNoteValues[3] == 3 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[3] == 4 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[3] == -3 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[3] == -4 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[3]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[3] == 5 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[3] == 8 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[3] == 7 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[3] == 12 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[3] == -5 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[3] == -8 && notes[place-1][1].val - notes[place][1].val > 0) ||
								   (notes[place - 1][0].val - validNoteValues[3] == -7 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[3] == -12 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[3]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else if (randVal == 2) {
						if (notes[place - 1][1].val == validNoteValues[5]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[5] && !nextMotionStepwise && notes[place - 1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[5]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[5];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[5] == 1 || notes[place - 1][0].val - validNoteValues[5] == 2 ||
								   notes[place - 1][0].val - validNoteValues[5] == -2 || notes[place - 1][0].val - validNoteValues[5] == -1) {
							//if the motion is stepwise
							notes[place++][0] = new Note(validNoteValues[5]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if (notes[place - 1][0].val - validNoteValues[5] == 3 || notes[place - 1][0].val - validNoteValues[5] == 4 ||
								   notes[place - 1][0].val - validNoteValues[5] == -3 || notes[place - 1][0].val - validNoteValues[5] == -4) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[5]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[5] == 5 || notes[place - 1][0].val - validNoteValues[5] == 7 ||
								   notes[place - 1][0].val - validNoteValues[5] == 8 || notes[place - 1][0].val - validNoteValues[5] == 12 ||
								   notes[place - 1][0].val - validNoteValues[5] == -5 || notes[place - 1][0].val - validNoteValues[5] == -7 ||
								   notes[place - 1][0].val - validNoteValues[5] == -8 || notes[place - 1][0].val - validNoteValues[5] == -12) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[5]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else {
						if (notes[place - 1][0].val == validNoteValues[7]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[7] && !nextMotionStepwise && notes[place-1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[7]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[7];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[7] == 1 && notes[place-1][1].val - notes[place][1].val < 0) || 
							(notes[place - 1][0].val - validNoteValues[7] == 2 && notes[place-1][1].val - notes[place][1].val < 0) ||
							(notes[place - 1][0].val - validNoteValues[7] == -2 && notes[place-1][1].val - notes[place][1].val > 0) || 
							(notes[place - 1][0].val - validNoteValues[7] == -1 && notes[place-1][1].val - notes[place][1].val > 0)) {
							//if the motion is stepwise and contrary
							notes[place++][0] = new Note(validNoteValues[7]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if ((notes[place - 1][0].val - validNoteValues[7] == 3 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[7] == 4 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[7] == -3 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[7] == -4 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[7]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[7] == 5 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[7] == 8 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[7] == 7 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[7] == 12 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[7] == -5 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[7] == -8 && notes[place-1][1].val - notes[place][1].val > 0) ||
								   (notes[place - 1][0].val - validNoteValues[7] == -7 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[7] == -12 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[7]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					}
					randVal = (randVal + 1) % 5;
					++attempts;
				}
			} else if (notes[place][1].val == 28) {
				randVal = rand.nextInt(4);
				while (attempts < 4 && !noteFound) {
					if (randVal == 0) {
						if (notes[place - 1][0].val == validNoteValues[1]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[1] && !nextMotionStepwise && notes[place-1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[1]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[1];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[1] == 1 && notes[place-1][1].val - notes[place][1].val < 0) || 
							(notes[place - 1][0].val - validNoteValues[1] == 2 && notes[place-1][1].val - notes[place][1].val < 0) ||
							(notes[place - 1][0].val - validNoteValues[1] == -2 && notes[place-1][1].val - notes[place][1].val > 0) || 
							(notes[place - 1][0].val - validNoteValues[1] == -1 && notes[place-1][1].val - notes[place][1].val > 0)) {
							//if the motion is stepwise and contrary
							notes[place++][0] = new Note(validNoteValues[1]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if ((notes[place - 1][0].val - validNoteValues[1] == 3 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[1] == 4 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[1] == -3 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[1] == -4 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[1]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[1] == 5 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[1] == 8 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[1] == 7 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[1] == 12 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[1] == -5 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[1] == -8 && notes[place-1][1].val - notes[place][1].val > 0) ||
								   (notes[place - 1][0].val - validNoteValues[1] == -7 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[1] == -12 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[1]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else if (randVal == 1) {
						if (notes[place - 1][1].val == validNoteValues[2]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[2] && !nextMotionStepwise && notes[place - 1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[2]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[2];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[2] == 1 || notes[place - 1][0].val - validNoteValues[2] == 2 ||
								   notes[place - 1][0].val - validNoteValues[2] == -2 || notes[place - 1][0].val - validNoteValues[2] == -1) {
							//if the motion is stepwise
							notes[place++][0] = new Note(validNoteValues[2]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if (notes[place - 1][0].val - validNoteValues[2] == 3 || notes[place - 1][0].val - validNoteValues[2] == 4 ||
								   notes[place - 1][0].val - validNoteValues[2] == -3 || notes[place - 1][0].val - validNoteValues[2] == -4) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[2]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[2] == 5 || notes[place - 1][0].val - validNoteValues[2] == 7 ||
								   notes[place - 1][0].val - validNoteValues[2] == 8 || notes[place - 1][0].val - validNoteValues[2] == 12 ||
								   notes[place - 1][0].val - validNoteValues[2] == -5 || notes[place - 1][0].val - validNoteValues[2] == -7 ||
								   notes[place - 1][0].val - validNoteValues[2] == -8 || notes[place - 1][0].val - validNoteValues[2] == -12) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[2]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else if (randVal == 2) {
						if (notes[place - 1][0].val == validNoteValues[4]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[4] && !nextMotionStepwise && notes[place-1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[4]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[4];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[4] == 1 && notes[place-1][1].val - notes[place][1].val < 0) || 
							(notes[place - 1][0].val - validNoteValues[4] == 2 && notes[place-1][1].val - notes[place][1].val < 0) ||
							(notes[place - 1][0].val - validNoteValues[4] == -2 && notes[place-1][1].val - notes[place][1].val > 0) || 
							(notes[place - 1][0].val - validNoteValues[4] == -1 && notes[place-1][1].val - notes[place][1].val > 0)) {
							//if the motion is stepwise and contrary
							notes[place++][0] = new Note(validNoteValues[4]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if ((notes[place - 1][0].val - validNoteValues[4] == 3 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[4] == 4 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[4] == -3 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[4] == -4 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[4]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[4] == 5 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[4] == 8 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[4] == 7 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[4] == 12 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[4] == -5 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[4] == -8 && notes[place-1][1].val - notes[place][1].val > 0) ||
								   (notes[place - 1][0].val - validNoteValues[4] == -7 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[4] == -12 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[4]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else {
						if (notes[place - 1][1].val == validNoteValues[6]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[6] && !nextMotionStepwise && notes[place - 1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[6]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[6];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[6] == 1 || notes[place - 1][0].val - validNoteValues[6] == 2 ||
								   notes[place - 1][0].val - validNoteValues[6] == -2 || notes[place - 1][0].val - validNoteValues[6] == -1) {
							//if the motion is stepwise
							notes[place++][0] = new Note(validNoteValues[6]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if (notes[place - 1][0].val - validNoteValues[6] == 3 || notes[place - 1][0].val - validNoteValues[6] == 4 ||
								   notes[place - 1][0].val - validNoteValues[6] == -3 || notes[place - 1][0].val - validNoteValues[6] == -4) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[6]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[6] == 5 || notes[place - 1][0].val - validNoteValues[6] == 7 ||
								   notes[place - 1][0].val - validNoteValues[6] == 8 || notes[place - 1][0].val - validNoteValues[6] == 12 ||
								   notes[place - 1][0].val - validNoteValues[6] == -5 || notes[place - 1][0].val - validNoteValues[6] == -7 ||
								   notes[place - 1][0].val - validNoteValues[6] == -8 || notes[place - 1][0].val - validNoteValues[6] == -12) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[6]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					}
					randVal = (randVal + 1) % 4;
					++attempts;
				}
			} else if (notes[place][1].val == 29) {
				randVal = rand.nextInt(5);
				while (attempts < 5 && !noteFound) {
					if (randVal == 0) {
						if (notes[place - 1][1].val == validNoteValues[0]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[0] && !nextMotionStepwise && notes[place - 1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[0]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[0];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[0] == 1 || notes[place - 1][0].val - validNoteValues[1] == 2 ||
								   notes[place - 1][0].val - validNoteValues[0] == -2 || notes[place - 1][0].val - validNoteValues[1] == -1) {
							//if the motion is stepwise
							notes[place++][0] = new Note(validNoteValues[0]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if (notes[place - 1][0].val - validNoteValues[0] == 3 || notes[place - 1][0].val - validNoteValues[0] == 4 ||
								   notes[place - 1][0].val - validNoteValues[0] == -3 || notes[place - 1][0].val - validNoteValues[0] == -4) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[0]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[0] == 5 || notes[place - 1][0].val - validNoteValues[0] == 7 ||
								   notes[place - 1][0].val - validNoteValues[0] == 8 || notes[place - 1][0].val - validNoteValues[0] == 12 ||
								   notes[place - 1][0].val - validNoteValues[0] == -5 || notes[place - 1][0].val - validNoteValues[0] == -7 ||
								   notes[place - 1][0].val - validNoteValues[0] == -8 || notes[place - 1][0].val - validNoteValues[0] == -12) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[0]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else if (randVal == 1) {
						if (notes[place - 1][0].val == validNoteValues[2]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[2] && !nextMotionStepwise && notes[place-1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[2]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[2];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[2] == 1 && notes[place-1][1].val - notes[place][1].val < 0) || 
							(notes[place - 1][0].val - validNoteValues[2] == 2 && notes[place-1][1].val - notes[place][1].val < 0) ||
							(notes[place - 1][0].val - validNoteValues[2] == -2 && notes[place-1][1].val - notes[place][1].val > 0) || 
							(notes[place - 1][0].val - validNoteValues[2] == -1 && notes[place-1][1].val - notes[place][1].val > 0)) {
							//if the motion is stepwise and contrary
							notes[place++][0] = new Note(validNoteValues[2]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if ((notes[place - 1][0].val - validNoteValues[2] == 3 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[2] == 4 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[2] == -3 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[2] == -4 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[2]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[2] == 5 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[2] == 8 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[2] == 7 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[2] == 12 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[2] == -5 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[2] == -8 && notes[place-1][1].val - notes[place][1].val > 0) ||
								   (notes[place - 1][0].val - validNoteValues[2] == -7 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[2] == -12 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[2]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else if (randVal == 2) {
						if (notes[place - 1][1].val == validNoteValues[3]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[3] && !nextMotionStepwise && notes[place - 1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[3]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[1];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[3] == 1 || notes[place - 1][0].val - validNoteValues[3] == 2 ||
								   notes[place - 1][0].val - validNoteValues[3] == -2 || notes[place - 1][0].val - validNoteValues[3] == -1) {
							//if the motion is stepwise
							notes[place++][0] = new Note(validNoteValues[3]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if (notes[place - 1][0].val - validNoteValues[3] == 3 || notes[place - 1][0].val - validNoteValues[3] == 4 ||
								   notes[place - 1][0].val - validNoteValues[3] == -3 || notes[place - 1][0].val - validNoteValues[3] == -4) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[3]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[3] == 5 || notes[place - 1][0].val - validNoteValues[3] == 7 ||
								   notes[place - 1][0].val - validNoteValues[3] == 8 || notes[place - 1][0].val - validNoteValues[3] == 12 ||
								   notes[place - 1][0].val - validNoteValues[3] == -5 || notes[place - 1][0].val - validNoteValues[3] == -7 ||
								   notes[place - 1][0].val - validNoteValues[3] == -8 || notes[place - 1][0].val - validNoteValues[3] == -12) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[3]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else if (randVal == 3) {
						if (notes[place - 1][0].val == validNoteValues[5]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[5] && !nextMotionStepwise && notes[place-1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[5]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[5];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[5] == 1 && notes[place-1][1].val - notes[place][1].val < 0) || 
							(notes[place - 1][0].val - validNoteValues[5] == 2 && notes[place-1][1].val - notes[place][1].val < 0) ||
							(notes[place - 1][0].val - validNoteValues[5] == -2 && notes[place-1][1].val - notes[place][1].val > 0) || 
							(notes[place - 1][0].val - validNoteValues[5] == -1 && notes[place-1][1].val - notes[place][1].val > 0)) {
							//if the motion is stepwise and contrary
							notes[place++][0] = new Note(validNoteValues[5]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if ((notes[place - 1][0].val - validNoteValues[5] == 3 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[5] == 4 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[5] == -3 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[5] == -4 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[5]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[5] == 5 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[5] == 8 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[5] == 7 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[5] == 12 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[5] == -5 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[5] == -8 && notes[place-1][1].val - notes[place][1].val > 0) ||
								   (notes[place - 1][0].val - validNoteValues[5] == -7 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[5] == -12 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[5]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else {
						if (notes[place - 1][1].val == validNoteValues[7]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[7] && !nextMotionStepwise && notes[place - 1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[7]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[7];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[7] == 1 || notes[place - 1][0].val - validNoteValues[7] == 2 ||
								   notes[place - 1][0].val - validNoteValues[7] == -2 || notes[place - 1][0].val - validNoteValues[7] == -1) {
							//if the motion is stepwise
							notes[place++][0] = new Note(validNoteValues[7]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if (notes[place - 1][0].val - validNoteValues[7] == 3 || notes[place - 1][0].val - validNoteValues[7] == 4 ||
								   notes[place - 1][0].val - validNoteValues[7] == -3 || notes[place - 1][0].val - validNoteValues[7] == -4) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[7]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[7] == 5 || notes[place - 1][0].val - validNoteValues[7] == 7 ||
								   notes[place - 1][0].val - validNoteValues[7] == 8 || notes[place - 1][0].val - validNoteValues[7] == 12 ||
								   notes[place - 1][0].val - validNoteValues[7] == -5 || notes[place - 1][0].val - validNoteValues[7] == -7 ||
								   notes[place - 1][0].val - validNoteValues[7] == -8 || notes[place - 1][0].val - validNoteValues[7] == -12) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[7]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					}
					randVal = (randVal + 1) % 5;
					++attempts;
				}
			} else if (notes[place][1].val == 31) {
				randVal = rand.nextInt(4);
				while (attempts < 4 && !noteFound) {
					if (randVal == 0) {
						if (notes[place - 1][1].val == validNoteValues[1]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[1] && !nextMotionStepwise && notes[place - 1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[1]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[1];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[1] == 1 || notes[place - 1][0].val - validNoteValues[1] == 2 ||
								   notes[place - 1][0].val - validNoteValues[1] == -2 || notes[place - 1][0].val - validNoteValues[1] == -1) {
							//if the motion is stepwise
							notes[place++][0] = new Note(validNoteValues[1]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if (notes[place - 1][0].val - validNoteValues[1] == 3 || notes[place - 1][0].val - validNoteValues[1] == 4 ||
								   notes[place - 1][0].val - validNoteValues[1] == -3 || notes[place - 1][0].val - validNoteValues[1] == -4) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[1]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[1] == 5 || notes[place - 1][0].val - validNoteValues[1] == 7 ||
								   notes[place - 1][0].val - validNoteValues[1] == 8 || notes[place - 1][0].val - validNoteValues[1] == 12 ||
								   notes[place - 1][0].val - validNoteValues[1] == -5 || notes[place - 1][0].val - validNoteValues[1] == -7 ||
								   notes[place - 1][0].val - validNoteValues[1] == -8 || notes[place - 1][0].val - validNoteValues[1] == -12) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[1]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else if (randVal == 1) {
						if (notes[place - 1][0].val == validNoteValues[3]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[3] && !nextMotionStepwise && notes[place-1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[3]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[3];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[3] == 1 && notes[place-1][1].val - notes[place][1].val < 0) || 
							(notes[place - 1][0].val - validNoteValues[3] == 2 && notes[place-1][1].val - notes[place][1].val < 0) ||
							(notes[place - 1][0].val - validNoteValues[3] == -2 && notes[place-1][1].val - notes[place][1].val > 0) || 
							(notes[place - 1][0].val - validNoteValues[3] == -1 && notes[place-1][1].val - notes[place][1].val > 0)) {
							//if the motion is stepwise and contrary
							notes[place++][0] = new Note(validNoteValues[3]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if ((notes[place - 1][0].val - validNoteValues[3] == 3 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[3] == 4 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[3] == -3 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[3] == -4 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[3]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[3] == 5 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[3] == 8 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[3] == 7 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[3] == 12 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[3] == -5 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[3] == -8 && notes[place-1][1].val - notes[place][1].val > 0) ||
								   (notes[place - 1][0].val - validNoteValues[3] == -7 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[3] == -12 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[3]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else if (randVal == 2) {
						if (notes[place - 1][1].val == validNoteValues[4]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[4] && !nextMotionStepwise && notes[place - 1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[4]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[4];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[4] == 1 || notes[place - 1][0].val - validNoteValues[4] == 2 ||
								   notes[place - 1][0].val - validNoteValues[4] == -2 || notes[place - 1][0].val - validNoteValues[4] == -1) {
							//if the motion is stepwise
							notes[place++][0] = new Note(validNoteValues[4]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if (notes[place - 1][0].val - validNoteValues[4] == 3 || notes[place - 1][0].val - validNoteValues[4] == 4 ||
								   notes[place - 1][0].val - validNoteValues[4] == -3 || notes[place - 1][0].val - validNoteValues[4] == -4) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[4]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[4] == 5 || notes[place - 1][0].val - validNoteValues[4] == 7 ||
								   notes[place - 1][0].val - validNoteValues[4] == 8 || notes[place - 1][0].val - validNoteValues[4] == 12 ||
								   notes[place - 1][0].val - validNoteValues[4] == -5 || notes[place - 1][0].val - validNoteValues[4] == -7 ||
								   notes[place - 1][0].val - validNoteValues[4] == -8 || notes[place - 1][0].val - validNoteValues[4] == -12) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[4]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else {
						if (notes[place - 1][0].val == validNoteValues[6]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[6] && !nextMotionStepwise && notes[place-1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[6]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[6];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[6] == 1 && notes[place-1][1].val - notes[place][1].val < 0) || 
							(notes[place - 1][0].val - validNoteValues[6] == 2 && notes[place-1][1].val - notes[place][1].val < 0) ||
							(notes[place - 1][0].val - validNoteValues[6] == -2 && notes[place-1][1].val - notes[place][1].val > 0) || 
							(notes[place - 1][0].val - validNoteValues[6] == -1 && notes[place-1][1].val - notes[place][1].val > 0)) {
							//if the motion is stepwise and contrary
							notes[place++][0] = new Note(validNoteValues[6]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if ((notes[place - 1][0].val - validNoteValues[6] == 3 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[6] == 4 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[6] == -3 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[6] == -4 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[6]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[6] == 5 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[6] == 8 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[6] == 7 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[6] == 12 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[6] == -5 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[6] == -8 && notes[place-1][1].val - notes[place][1].val > 0) ||
								   (notes[place - 1][0].val - validNoteValues[6] == -7 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[6] == -12 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[6]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					}
					randVal = (randVal + 1) % 4;
					++attempts;
				}
			} else if (notes[place][1].val == 33) {
				randVal = rand.nextInt(5);
				while (attempts < 5 && !noteFound) {
					if (randVal == 0) {
						if (notes[place - 1][0].val == validNoteValues[0]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[0] && !nextMotionStepwise && notes[place-1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[0]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[0];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[0] == 1 && notes[place-1][1].val - notes[place][1].val < 0) || 
							(notes[place - 1][0].val - validNoteValues[0] == 2 && notes[place-1][1].val - notes[place][1].val < 0) ||
							(notes[place - 1][0].val - validNoteValues[0] == -2 && notes[place-1][1].val - notes[place][1].val > 0) || 
							(notes[place - 1][0].val - validNoteValues[0] == -1 && notes[place-1][1].val - notes[place][1].val > 0)) {
							//if the motion is stepwise and contrary
							notes[place++][0] = new Note(validNoteValues[0]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if ((notes[place - 1][0].val - validNoteValues[0] == 3 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[0] == 4 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[0] == -3 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[0] == -4 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[0]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[0] == 5 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[0] == 8 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[0] == 7 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[0] == 12 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[0] == -5 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[0] == -8 && notes[place-1][1].val - notes[place][1].val > 0) ||
								   (notes[place - 1][0].val - validNoteValues[0] == -7 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[0] == -12 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[0]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else if (randVal == 1) {
						if (notes[place - 1][1].val == validNoteValues[2]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[2] && !nextMotionStepwise && notes[place - 1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[2]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[2];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[2] == 1 || notes[place - 1][0].val - validNoteValues[2] == 2 ||
								   notes[place - 1][0].val - validNoteValues[2] == -2 || notes[place - 1][0].val - validNoteValues[2] == -1) {
							//if the motion is stepwise
							notes[place++][0] = new Note(validNoteValues[2]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if (notes[place - 1][0].val - validNoteValues[2] == 3 || notes[place - 1][0].val - validNoteValues[2] == 4 ||
								   notes[place - 1][0].val - validNoteValues[2] == -3 || notes[place - 1][0].val - validNoteValues[2] == -4) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[2]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[2] == 5 || notes[place - 1][0].val - validNoteValues[2] == 7 ||
								   notes[place - 1][0].val - validNoteValues[2] == 8 || notes[place - 1][0].val - validNoteValues[2] == 12 ||
								   notes[place - 1][0].val - validNoteValues[2] == -5 || notes[place - 1][0].val - validNoteValues[2] == -7 ||
								   notes[place - 1][0].val - validNoteValues[2] == -8 || notes[place - 1][0].val - validNoteValues[2] == -12) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[2]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else if (randVal == 2) {
						if (notes[place - 1][0].val == validNoteValues[4]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[4] && !nextMotionStepwise && notes[place-1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[4]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[4];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[4] == 1 && notes[place-1][1].val - notes[place][1].val < 0) || 
							(notes[place - 1][0].val - validNoteValues[4] == 2 && notes[place-1][1].val - notes[place][1].val < 0) ||
							(notes[place - 1][0].val - validNoteValues[4] == -2 && notes[place-1][1].val - notes[place][1].val > 0) || 
							(notes[place - 1][0].val - validNoteValues[4] == -1 && notes[place-1][1].val - notes[place][1].val > 0)) {
							//if the motion is stepwise and contrary
							notes[place++][0] = new Note(validNoteValues[4]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if ((notes[place - 1][0].val - validNoteValues[4] == 3 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[4] == 4 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[4] == -3 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[4] == -4 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[4]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[4] == 5 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[4] == 8 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[4] == 7 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[4] == 12 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[4] == -5 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[4] == -8 && notes[place-1][1].val - notes[place][1].val > 0) ||
								   (notes[place - 1][0].val - validNoteValues[4] == -7 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[4] == -12 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[4]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else if (randVal == 3) {
						if (notes[place - 1][1].val == validNoteValues[5]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[5] && !nextMotionStepwise && notes[place - 1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[5]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[5];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[5] == 1 || notes[place - 1][0].val - validNoteValues[5] == 2 ||
								   notes[place - 1][0].val - validNoteValues[5] == -2 || notes[place - 1][0].val - validNoteValues[5] == -1) {
							//if the motion is stepwise
							notes[place++][0] = new Note(validNoteValues[5]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if (notes[place - 1][0].val - validNoteValues[5] == 3 || notes[place - 1][0].val - validNoteValues[5] == 4 ||
								   notes[place - 1][0].val - validNoteValues[5] == -3 || notes[place - 1][0].val - validNoteValues[5] == -4) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[5]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[5] == 5 || notes[place - 1][0].val - validNoteValues[5] == 7 ||
								   notes[place - 1][0].val - validNoteValues[5] == 8 || notes[place - 1][0].val - validNoteValues[5] == 12 ||
								   notes[place - 1][0].val - validNoteValues[5] == -5 || notes[place - 1][0].val - validNoteValues[5] == -7 ||
								   notes[place - 1][0].val - validNoteValues[5] == -8 || notes[place - 1][0].val - validNoteValues[5] == -12) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[5]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else {
						if (notes[place - 1][0].val == validNoteValues[7]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[7] && !nextMotionStepwise && notes[place-1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[7]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[7];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[7] == 1 && notes[place-1][1].val - notes[place][1].val < 0) || 
							(notes[place - 1][0].val - validNoteValues[7] == 2 && notes[place-1][1].val - notes[place][1].val < 0) ||
							(notes[place - 1][0].val - validNoteValues[7] == -2 && notes[place-1][1].val - notes[place][1].val > 0) || 
							(notes[place - 1][0].val - validNoteValues[7] == -1 && notes[place-1][1].val - notes[place][1].val > 0)) {
							//if the motion is stepwise and contrary
							notes[place++][0] = new Note(validNoteValues[7]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if ((notes[place - 1][0].val - validNoteValues[7] == 3 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[7] == 4 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[7] == -3 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[7] == -4 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[7]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[7] == 5 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[7] == 8 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[7] == 7 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[7] == 12 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[7] == -5 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[7] == -8 && notes[place-1][1].val - notes[place][1].val > 0) ||
								   (notes[place - 1][0].val - validNoteValues[7] == -7 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[7] == -12 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[7]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					}
					randVal = (randVal + 1) % 5;
					++attempts;
				}
			} else if (notes[place][1].val == 35) {
				randVal = rand.nextInt(3);
				while (attempts < 3 && !noteFound) {
					if (randVal == 0) {
						if (notes[place - 1][0].val == validNoteValues[1]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[1] && !nextMotionStepwise && notes[place-1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[1]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[1];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[1] == 1 && notes[place-1][1].val - notes[place][1].val < 0) || 
							(notes[place - 1][0].val - validNoteValues[1] == 2 && notes[place-1][1].val - notes[place][1].val < 0) ||
							(notes[place - 1][0].val - validNoteValues[1] == -2 && notes[place-1][1].val - notes[place][1].val > 0) || 
							(notes[place - 1][0].val - validNoteValues[1] == -1 && notes[place-1][1].val - notes[place][1].val > 0)) {
							//if the motion is stepwise and contrary
							notes[place++][0] = new Note(validNoteValues[1]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if ((notes[place - 1][0].val - validNoteValues[1] == 3 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[1] == 4 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[1] == -3 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[1] == -4 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[1]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[1] == 5 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[1] == 8 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[1] == 7 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[1] == 12 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[1] == -5 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[1] == -8 && notes[place-1][1].val - notes[place][1].val > 0) ||
								   (notes[place - 1][0].val - validNoteValues[1] == -7 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[1] == -12 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[1]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else if (randVal == 1) {
						if (notes[place - 1][1].val == validNoteValues[3]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[3] && !nextMotionStepwise && notes[place - 1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[3]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[1];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[3] == 1 || notes[place - 1][0].val - validNoteValues[3] == 2 ||
								   notes[place - 1][0].val - validNoteValues[3] == -2 || notes[place - 1][0].val - validNoteValues[3] == -1) {
							//if the motion is stepwise
							notes[place++][0] = new Note(validNoteValues[3]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if (notes[place - 1][0].val - validNoteValues[3] == 3 || notes[place - 1][0].val - validNoteValues[3] == 4 ||
								   notes[place - 1][0].val - validNoteValues[3] == -3 || notes[place - 1][0].val - validNoteValues[3] == -4) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[3]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[3] == 5 || notes[place - 1][0].val - validNoteValues[3] == 7 ||
								   notes[place - 1][0].val - validNoteValues[3] == 8 || notes[place - 1][0].val - validNoteValues[3] == 12 ||
								   notes[place - 1][0].val - validNoteValues[3] == -5 || notes[place - 1][0].val - validNoteValues[3] == -7 ||
								   notes[place - 1][0].val - validNoteValues[3] == -8 || notes[place - 1][0].val - validNoteValues[3] == -12) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[3]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else {
						if (notes[place - 1][1].val == validNoteValues[6]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[6] && !nextMotionStepwise && notes[place - 1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[6]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[6];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[6] == 1 || notes[place - 1][0].val - validNoteValues[6] == 2 ||
								   notes[place - 1][0].val - validNoteValues[6] == -2 || notes[place - 1][0].val - validNoteValues[6] == -1) {
							//if the motion is stepwise
							notes[place++][0] = new Note(validNoteValues[6]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if (notes[place - 1][0].val - validNoteValues[6] == 3 || notes[place - 1][0].val - validNoteValues[6] == 4 ||
								   notes[place - 1][0].val - validNoteValues[6] == -3 || notes[place - 1][0].val - validNoteValues[6] == -4) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[6]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[6] == 5 || notes[place - 1][0].val - validNoteValues[6] == 7 ||
								   notes[place - 1][0].val - validNoteValues[6] == 8 || notes[place - 1][0].val - validNoteValues[6] == 12 ||
								   notes[place - 1][0].val - validNoteValues[6] == -5 || notes[place - 1][0].val - validNoteValues[6] == -7 ||
								   notes[place - 1][0].val - validNoteValues[6] == -8 || notes[place - 1][0].val - validNoteValues[6] == -12) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[6]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					}
					randVal = (randVal + 1) % 3;
					++attempts;
				}
			} else if (notes[place][1].val == 36) {
				randVal = rand.nextInt(4);
				while (attempts < 4 && !noteFound) {
					if (randVal == 0) {
						if (notes[place - 1][0].val == validNoteValues[2]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[2] && !nextMotionStepwise && notes[place-1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[2]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[2];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[2] == 1 && notes[place-1][1].val - notes[place][1].val < 0) || 
							(notes[place - 1][0].val - validNoteValues[2] == 2 && notes[place-1][1].val - notes[place][1].val < 0) ||
							(notes[place - 1][0].val - validNoteValues[2] == -2 && notes[place-1][1].val - notes[place][1].val > 0) || 
							(notes[place - 1][0].val - validNoteValues[2] == -1 && notes[place-1][1].val - notes[place][1].val > 0)) {
							//if the motion is stepwise and contrary
							notes[place++][0] = new Note(validNoteValues[2]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if ((notes[place - 1][0].val - validNoteValues[2] == 3 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[2] == 4 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[2] == -3 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[2] == -4 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[2]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[2] == 5 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[2] == 8 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[2] == 7 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[2] == 12 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[2] == -5 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[2] == -8 && notes[place-1][1].val - notes[place][1].val > 0) ||
								   (notes[place - 1][0].val - validNoteValues[2] == -7 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[2] == -12 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[2]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else if (randVal == 1) {
						if (notes[place - 1][1].val == validNoteValues[4]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[4] && !nextMotionStepwise && notes[place - 1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[4]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[4];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[4] == 1 || notes[place - 1][0].val - validNoteValues[4] == 2 ||
								   notes[place - 1][0].val - validNoteValues[4] == -2 || notes[place - 1][0].val - validNoteValues[4] == -1) {
							//if the motion is stepwise
							notes[place++][0] = new Note(validNoteValues[4]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if (notes[place - 1][0].val - validNoteValues[4] == 3 || notes[place - 1][0].val - validNoteValues[4] == 4 ||
								   notes[place - 1][0].val - validNoteValues[4] == -3 || notes[place - 1][0].val - validNoteValues[4] == -4) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[4]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[4] == 5 || notes[place - 1][0].val - validNoteValues[4] == 7 ||
								   notes[place - 1][0].val - validNoteValues[4] == 8 || notes[place - 1][0].val - validNoteValues[4] == 12 ||
								   notes[place - 1][0].val - validNoteValues[4] == -5 || notes[place - 1][0].val - validNoteValues[4] == -7 ||
								   notes[place - 1][0].val - validNoteValues[4] == -8 || notes[place - 1][0].val - validNoteValues[4] == -12) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[4]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else if (randVal == 2) {
						if (notes[place - 1][0].val == validNoteValues[6]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[6] && !nextMotionStepwise && notes[place-1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[6]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[6];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[6] == 1 && notes[place-1][1].val - notes[place][1].val < 0) || 
							(notes[place - 1][0].val - validNoteValues[6] == 2 && notes[place-1][1].val - notes[place][1].val < 0) ||
							(notes[place - 1][0].val - validNoteValues[6] == -2 && notes[place-1][1].val - notes[place][1].val > 0) || 
							(notes[place - 1][0].val - validNoteValues[6] == -1 && notes[place-1][1].val - notes[place][1].val > 0)) {
							//if the motion is stepwise and contrary
							notes[place++][0] = new Note(validNoteValues[6]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if ((notes[place - 1][0].val - validNoteValues[6] == 3 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[6] == 4 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[6] == -3 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[6] == -4 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[6]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[6] == 5 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[6] == 8 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[6] == 7 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[6] == 12 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[6] == -5 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[6] == -8 && notes[place-1][1].val - notes[place][1].val > 0) ||
								   (notes[place - 1][0].val - validNoteValues[6] == -7 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[6] == -12 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[6]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else {
						if (notes[place - 1][1].val == validNoteValues[7]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[7] && !nextMotionStepwise && notes[place - 1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[7]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[7];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[7] == 1 || notes[place - 1][0].val - validNoteValues[7] == 2 ||
								   notes[place - 1][0].val - validNoteValues[7] == -2 || notes[place - 1][0].val - validNoteValues[7] == -1) {
							//if the motion is stepwise
							notes[place++][0] = new Note(validNoteValues[7]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if (notes[place - 1][0].val - validNoteValues[7] == 3 || notes[place - 1][0].val - validNoteValues[7] == 4 ||
								   notes[place - 1][0].val - validNoteValues[7] == -3 || notes[place - 1][0].val - validNoteValues[7] == -4) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[7]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[7] == 5 || notes[place - 1][0].val - validNoteValues[7] == 7 ||
								   notes[place - 1][0].val - validNoteValues[7] == 8 || notes[place - 1][0].val - validNoteValues[7] == 12 ||
								   notes[place - 1][0].val - validNoteValues[7] == -5 || notes[place - 1][0].val - validNoteValues[7] == -7 ||
								   notes[place - 1][0].val - validNoteValues[7] == -8 || notes[place - 1][0].val - validNoteValues[7] == -12) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[7]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					}
					randVal = (randVal + 1) % 4;
					++attempts;
				}
			} else {
				randVal = rand.nextInt(3);
				while (attempts < 3 && !noteFound) {
					if (randVal == 0) {
						if (notes[place - 1][0].val == validNoteValues[3]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[3] && !nextMotionStepwise && notes[place-1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[3]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[3];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[3] == 1 && notes[place-1][1].val - notes[place][1].val < 0) || 
							(notes[place - 1][0].val - validNoteValues[3] == 2 && notes[place-1][1].val - notes[place][1].val < 0) ||
							(notes[place - 1][0].val - validNoteValues[3] == -2 && notes[place-1][1].val - notes[place][1].val > 0) || 
							(notes[place - 1][0].val - validNoteValues[3] == -1 && notes[place-1][1].val - notes[place][1].val > 0)) {
							//if the motion is stepwise and contrary
							notes[place++][0] = new Note(validNoteValues[3]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if ((notes[place - 1][0].val - validNoteValues[3] == 3 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[3] == 4 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[3] == -3 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[3] == -4 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[3]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[3] == 5 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[3] == 8 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[3] == 7 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[3] == 12 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[3] == -5 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[3] == -8 && notes[place-1][1].val - notes[place][1].val > 0) ||
								   (notes[place - 1][0].val - validNoteValues[3] == -7 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[3] == -12 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[3]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else if (randVal == 1) {
						if (notes[place - 1][1].val == validNoteValues[5]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[5] && !nextMotionStepwise && notes[place - 1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[5]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[5];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[5] == 1 || notes[place - 1][0].val - validNoteValues[5] == 2 ||
								   notes[place - 1][0].val - validNoteValues[5] == -2 || notes[place - 1][0].val - validNoteValues[5] == -1) {
							//if the motion is stepwise
							notes[place++][0] = new Note(validNoteValues[5]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if (notes[place - 1][0].val - validNoteValues[5] == 3 || notes[place - 1][0].val - validNoteValues[5] == 4 ||
								   notes[place - 1][0].val - validNoteValues[5] == -3 || notes[place - 1][0].val - validNoteValues[5] == -4) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[5]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if (notes[place - 1][0].val - validNoteValues[5] == 5 || notes[place - 1][0].val - validNoteValues[5] == 7 ||
								   notes[place - 1][0].val - validNoteValues[5] == 8 || notes[place - 1][0].val - validNoteValues[5] == 12 ||
								   notes[place - 1][0].val - validNoteValues[5] == -5 || notes[place - 1][0].val - validNoteValues[5] == -7 ||
								   notes[place - 1][0].val - validNoteValues[5] == -8 || notes[place - 1][0].val - validNoteValues[5] == -12) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[5]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					} else {
						if (notes[place - 1][0].val == validNoteValues[7]) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[7] && !nextMotionStepwise && notes[place-1][1].val != notes[place][1].val) {
								notes[place++][0] = new Note(validNoteValues[7]);
								numberOfSkips = 0;
								numberOfNoteRepetitions++;
								valueOfRepeatedNote = validNoteValues[7];
								previousMotionStepwise = true;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[7] == 1 && notes[place-1][1].val - notes[place][1].val < 0) || 
							(notes[place - 1][0].val - validNoteValues[7] == 2 && notes[place-1][1].val - notes[place][1].val < 0) ||
							(notes[place - 1][0].val - validNoteValues[7] == -2 && notes[place-1][1].val - notes[place][1].val > 0) || 
							(notes[place - 1][0].val - validNoteValues[7] == -1 && notes[place-1][1].val - notes[place][1].val > 0)) {
							//if the motion is stepwise and contrary
							notes[place++][0] = new Note(validNoteValues[7]);
							numberOfSkips = 0;
							numberOfNoteRepetitions = 0;
							previousMotionStepwise = true;
							nextMotionStepwise = false;
							noteFound = true;
						} else if ((notes[place - 1][0].val - validNoteValues[7] == 3 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[7] == 4 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[7] == -3 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[7] == -4 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[7]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								noteFound = true;
							}
						} else if ((notes[place - 1][0].val - validNoteValues[7] == 5 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[7] == 8 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[7] == 7 && notes[place-1][1].val - notes[place][1].val < 0) || 
								   (notes[place - 1][0].val - validNoteValues[7] == 12 && notes[place-1][1].val - notes[place][1].val < 0) ||
								   (notes[place - 1][0].val - validNoteValues[7] == -5 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[7] == -8 && notes[place-1][1].val - notes[place][1].val > 0) ||
								   (notes[place - 1][0].val - validNoteValues[7] == -7 && notes[place-1][1].val - notes[place][1].val > 0) || 
								   (notes[place - 1][0].val - validNoteValues[7] == -12 && notes[place-1][1].val - notes[place][1].val > 0)) {
							if (previousMotionStepwise) {
								notes[place++][0] = new Note(validNoteValues[7]);
								numberOfSkips++;
								numberOfNoteRepetitions = 0;
								previousMotionStepwise = false;
								nextMotionStepwise = true;
								noteFound = true;
							}
						}
					}
					randVal = (randVal + 1) % 3;
					++attempts;
				}
			}
					
			if (noteFound == false) {
				--place;
			}
		}
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