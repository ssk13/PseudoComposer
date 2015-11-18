/* 
	Note.java
   	Each contains a numerical value
  		0 is a C, each additional int is another halfstep, 
  		with 60 being the maximum
  		-1 indicates that the note is a rest
    Contains a char representing duration (q = quarter, e = eighth)

   	by Sarah Klein
*/

public class Note {
	int val;
	char durr;
	char[] names = {'C', 'C', 'D', 'D', 'E', 'F', 'F', 'G', 'G', 'A', 'A', 'B'};

	/*
		Default Constructor
		Not used - value of '-1' corresponds to a rest
	*/
	public Note () {
		this.val = -1;
		this.durr = 'q';
	}

	/*
		Constructor
		Assigns note to numerical value - 
	*/
	public Note (int val) {
		this.val = val;
		this.durr = 'q';
	}

	/*
		Constructor
		Given int value of note and char value of duration
	*/
	public Note (int val, char durr) {
		this.val = val;
		this.durr = durr;
	}

	/*
		Constructor
		Note initialized with another note
	*/
	public Note (Note note) {
		this.val = note.val;
		this.durr = note.durr;
	}

	/*
		Constructor
		Note initialized with other note (from where it gets its value) and duration
	*/
	public Note (Note note, char durr) {
		this.val = note.val;
		this.durr = durr;
	}

	/*
		Constructor
		Creates a rest for the given duration
	*/
	public Note (char durr) {
		this.val = -1;
		this.durr = durr;
	}

	/*
		Returns the interval between this note and the next note, reduced to a 
		value <= an octave, from the bottom-up
	*/
	public int getDif (Note next) {
		if (this.val < next.val)
			return (next.val - this.val)%12;
		return (12 - (this.val - next.val))%12;
	}

	/*
		Returns the letter name corresponding to the note
	*/
	public char getName() {
		return names[val%12];
	}

	/*
		Returns the octave of a pitch
	*/
	public int getOctave() {
		return ((this.val)/12) + 2;
	}

	/*
		Returns whether the pitch has an accidental (currently only supporting #)
		Future: Include flats, double-sharps and double-flats
	*/
	public Boolean hasAcc() {
		if (this.val%12 < 5) {
			if (this.val%2 == 1)
				return true;
		}
		else {
			if (this.val%2 == 0)
				return true;
		}
		return false;
	}

	/*
		Converts the note into a string that can be used in a PlayerString
	*/
	public String toString() {
		while (this.val < 0)
			this.val += 12;
		String string = "";
		if (this.val == -1)
			string += 'R';
		else {
			string += (char) this.getName();
			if (this.hasAcc())
				string += "#";
			string += (char) (this.getOctave() + 48);
		}
		string += durr;
		return string;
	}

	/*
		Transposes a note a number of halfsteps, keeping it within the valid range
	*/
	public void transpose(int halfsteps) {
		try {
			Exception e = new Exception();
			this.val += halfsteps;
			if ((val < 0) || (val > 60))
				throw e;
		}
		catch (Exception e) {
			if (val < 12)
				this.transpose(12);
			else if (val > 60)
				this.transpose(-12);
		}
	}
}

/*
	Used in SuiteMovement
	public void setPitchNotOctave (Note note) {
			this.val = ((this.val/12)*12) + (note.val % 12);
	}

	public void setOctaveNotPitch (Note note) {
		int rem = this.val%12;
		this.val = ((note.val/12) * 12) + rem;
		if (this.val > 48)
			this.val -= 8;
	}
*/
	