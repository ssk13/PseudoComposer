public class Chord {
	Note[] notes;
	int numNotes;

	public Chord (Note[] notes, int numNotes, char valGetsNote) {
		this.numNotes = numNotes;
		this.notes = new Note[numNotes];
		for (int i = 0; i != numNotes; ++i)
			this.notes[i] = new Note(notes[i], valGetsNote);
	}

	public Chord (int numNotes, char valGetsNote) {
		this.numNotes = numNotes;
		this.notes = new Note[numNotes];
		for (int i = 0; i != numNotes; ++i) {
			this.notes[i] = new Note(valGetsNote);
		}
	}

	public void swapNotes (int a, int b) {
		Note temp = new Note(notes[a]);
		notes[a] = notes[b];
		notes[b] = temp;
	}
}