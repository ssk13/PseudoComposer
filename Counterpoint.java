/* 
	Counterpoint.java
    Base class for all of our later counterpoint classes

   	by Sarah Klein
*/

import java.util.Random;

public class Counterpoint {
	int numNotes;	//Number of notes in the chord
	Random rand = new Random();
	
	public Counterpoint() {

	}

	/*
		Returns whether 2 notes would be considered consonant melodically in early 16th century counterpoint
			(Unisons, steps (no ascending half step), thirds, perfect fourths, perfect fifths, sixths (minor ascending), octaves)
			future: add a secondary check for complex intervals (augmented 5th, for example)
	*/
	public boolean isConsonantMelodically (int firstNote, int secondNote) {
		int diff = secondNote - firstNote;
		boolean isConsonant = false;
		switch (diff) {
			case -12:
			case -7:
			case -5:
			case -4:
			case -3:
			case -2:
			case -1:
			case 0:
			case 2:
			case 3:
			case 4:
			case 5:
			case 7:
			case 8:
			case 12:
				isConsonant = true;
				break;
			default:
				isConsonant = false;
				break;
		}
		return isConsonant;
	}

	public boolean isApproachedFromOppositeDirection (int firstNote, int secondNote, int thirdNote) {
		if (secondNote > firstNote && secondNote > thirdNote) {
			return true;
		}
		if (secondNote < firstNote && secondNote < thirdNote) {
			return true;
		}
		return false;
	}

	/*
		Returns whether 2 notes would be considered consonant harmonically in early 16th century counterpoint
			(Unisons, thirds, fifths, and sixths are considered consonant)
	*/
	public boolean isConsonantVertically (int tenor, int soprano) {
		int diff = soprano - tenor;
		boolean isConsonant = false;
		switch (diff) {
			case -19:
			case -16:
			case -15:
			case -12:
			case -9:
			case -8:
			case -7:
			case -4:
			case -3:
			case 0:
			case 3:
			case 4:
			case 7:
			case 8:
			case 9:
			case 12:
			case 15:
			case 16:
			case 19:
				isConsonant = true;
				break;
			default:
				isConsonant = false;
				break;
		}
		return isConsonant;
	}

	
	/*
		Returns whether the motion between 2 voices is contrary or oblique, as required for movement to a perfect consonance
	*/
	public boolean isContraryOrOblique (int prevSoprano, int prevTenor, int currSoprano, int currTenor) {
		if (currSoprano - prevSoprano > 0) {
			if (currTenor - prevTenor <= 0) {
				return true;
			}
		} else if (currSoprano - prevSoprano == 0) {
			if (currTenor - prevTenor != 0) {
				return true;
			}
		} else if (currSoprano - prevSoprano < 0) {
			if (currTenor - prevTenor >= 0) {
				return true;
			}
		}
		return false;
	}

	/*
		Returns whether 2 notes would be considered imperfectly consonant in early 16th century counterpoint
			(Thirds and sixths are considered consonant)
	*/
	public boolean isImperfectConsonance (int tenor, int soprano) {
		int diff = soprano - tenor;
		boolean isImperfectConsonance = false;
		switch (diff) {
			case 3:
			case 4:
			case 8:
			case 9:
			case 15:
			case 16:
				isImperfectConsonance = true;
				break;
			default:
				isImperfectConsonance = false;
				break;
		}
		return isImperfectConsonance;
	}
}
