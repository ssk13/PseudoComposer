public class Note {
	int val;
	char durr;
	char[] names = {'C', 'C', 'D', 'D', 'E', 'F', 'F', 'G', 'G', 'A', 'A', 'B'};

	public Note () {
		this.val = -1;
		this.durr = 'q';
	}

	public Note (int val) {
		this.val = val;
		this.durr = 'q';
	}

	public Note (int val, char durr) {
		this.val = val;
		this.durr = durr;
	}

	public Note (Note note) {
		this.val = note.val;
		this.durr = note.durr;
	}

	public Note (Note note, char durr) {
		this.val = note.val;
		this.durr = durr;
	}

	public Note (char durr) {
		this.val = -1;
		this.durr = durr;
	}

	public int getDif (Note next) {
		if (this.val < next.val)
			return (next.val - this.val)%12;
		return (12 - (this.val - next.val))%12;
	}

	public char getName() {
		return names[val%12];
	}

	public int getOctave() {
		return ((this.val)/12) + 2;
	}

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

	public void setPitchNotOctave (Note note) {
			this.val = ((this.val/12)*12) + (note.val % 12);
	}

	public void setOctaveNotPitch (Note note) {
		int rem = this.val%12;
		this.val = ((note.val/12) * 12) + rem;
		if (this.val > 48)
			this.val -= 8;
	}

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