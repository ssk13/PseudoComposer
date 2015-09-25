/* Represents a cantus firmus, which contains a monophonic line of notes, defaultedly in
	d-dorian mode
	future: add more modes

   by Sarah Klein
*/

public class CantusFirmus {
	Note[] notes;	//Actual notes in the line
	int numNotes;	//Number of notes in the chord
	Note lowest = new Note(),
		highest = new Note();

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

	}

	/*
		Converts the cantus firmus to a string that can be played by the JFugue player
	*/
	public String toString() {
		String cantusFirmusString = "";
		try {
			for (int i = 0; i != numNotes; ++i) {
				cantusFirmusString += notes[0].toString() + " ";
			}
			return cantusFirmusString;
		}
		catch (NullPointerException e) {
		}
		return "";
	}
}