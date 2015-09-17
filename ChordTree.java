/*
	Creates a tree-like structure displaying all of the possible "next chords"
	These chord qualities are assuming the same treatment regardless of major or minor key
	a 1 chord goes to any other chord
	a 2, 4, or 6 chord goes to 2, 4, 5, 6, or 7 (predominant goes to predominant or to dominant)
	a 3 chord goes to 2, 3, 4, or 6 (mediant goes to predominant)
	a a 5 or 7 chord goes to 5, 7, 6, or 1 (dominant goes to dominant, tonic, or a deceptive cadence)
*/

import java.util.Random;

public class ChordTree {
	ChordTree[] next;
	ChordTree prevTree,
			  toSet;
	int[] chords;
	int chordVal,
		depth;
	Boolean tried = false,
			selected = false;

	/*
		Constructor - sets the branches according to the current chord's quality
	*/
	public ChordTree (int chordVal, int depth, ChordTree prevTree) {
		this.depth = depth;
		this.prevTree = prevTree;
		this.chordVal = chordVal;
		if (chordVal == 1) {
			chords = new int[7];
			for (int i = 0; i != 7; ++i) 
				chords[i] = i + 1;
			if (depth > 1) {
				this.next = new ChordTree[7];
				for (int j = 0; j != 7; ++j)
					next[j] = new ChordTree(j + 1, depth - 1, this);
			}
		}
		else if (chordVal%2 == 0) {
			chords = new int[5];
			chords[0] = 2;
			chords[1] = 4;
			chords[2] = 5;
			chords[3] = 6;
			chords[4] = 7;

			if (depth > 1) {
				this.next = new ChordTree[5];
				next[0] = new ChordTree(2, depth - 1, this);
				next[1] = new ChordTree(4, depth - 1, this);
				next[2] = new ChordTree(5, depth - 1, this);
				next[3] = new ChordTree(6, depth - 1, this);
				next[4] = new ChordTree(7, depth - 1, this);
			}
		}
		else if (chordVal == 3) {
			chords = new int[4];
			chords[0] = 2;
			chords[1] = 3;
			chords[2] = 4;
			chords[3] = 6;
			if (depth > 1) {
				this.next = new ChordTree[4];
				next[0] = new ChordTree(2, depth - 1, this);
				next[1] = new ChordTree(3, depth - 1, this);
				next[2] = new ChordTree(4, depth - 1, this);
				next[3] = new ChordTree(6, depth - 1, this);
			}
		}
		else {
			chords = new int[4];
			chords[0] = 1;
			chords[1] = 5;
			chords[2] = 6;
			chords[3] = 7;
			if (depth > 1) {
				this.next = new ChordTree[4];
				next[0] = new ChordTree(1, depth - 1, this);
				next[1] = new ChordTree(5, depth - 1, this);
				next[2] = new ChordTree(6, depth - 1, this);
				next[3] = new ChordTree(7, depth - 1, this);
			}			
		}
	}

	/*
		Finds a possible "next chord" by cross-referencing the difference between the next note and this note
		with a 3-d array of chordal/intervallic relationships
	*/
	public int getChordAndSetPath (Random rand, int[][][] members, Note sop, Note tonic, Boolean isMajor) {	
		int dif = tonic.getDif(sop), major = (isMajor) ? 0 : 1,
			chordChoice = 0,
			possChords;
		Boolean trying = true;

		if (this.chordVal == 1)
			possChords = 7;
		else if (this.chordVal % 2 == 0)
			possChords = 5;
		else
			possChords = 4;
		while (trying) {
			chordChoice = rand.nextInt(possChords);
			if (!next[chordChoice].tried && ((members[major][chords[chordChoice] - 1][0] == dif) || 
				(members[major][chords[chordChoice] - 1][1] == dif) ||
				(members[major][chords[chordChoice] - 1][2] == dif))) {
				this.toSet = next[chordChoice];
				trying = false;
			}
		}
		return chords[chordChoice];
	}

	/*
		checks that the music doesn't suck yet
	*/
	public Boolean hasChordLeft () {
		for (int i = 0; i != next.length; ++i) {
			if (!next[i].tried)
				return true;
		}
		return false;
	}
}