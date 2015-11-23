/* 
	Chord.java
   	Represents a chord, which contains a number of notes and has a duration

   	by Sarah Klein
*/

public class Chord {
	Note[] notes;	//Actual notes that make up the chord
	Note root;		//Root note of the chord
	String quality;	//Major, Minor, Diminished [, Augmented, Neopolitan, &c to come later]
	int numNotes,	//Number of notes in the chord
		val;		//numerical value of chord

	/*
		Constructor
		Creates a chord with the number of notes, initialized to a rest of the given duration
	*/
	public Chord (int numNotes, char valGetsNote) {
		this.numNotes = numNotes;
		this.notes = new Note[numNotes];
		for (int i = 0; i != numNotes; ++i) {
			this.notes[i] = new Note(valGetsNote);
		}
	}

	/*
		Swaps 2 notes in a chord
	*/
	public void swapNotes (int a, int b) {
		Note temp = new Note(notes[a]);
		notes[a] = notes[b];
		notes[b] = temp;
	}
}
