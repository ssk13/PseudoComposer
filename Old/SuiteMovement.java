import java.util.Random;

public class SuiteMovement extends Staff {
	Measure[] bass;
	Measure entry;
	int bassCount = 0, tempo = 40;
	Note tonic;
	Boolean major = true;
	String instrument = "ACOUSTIC_GRAND";
	Random rand = new Random();
	Measure measure1;		//tonic			I 					i
	Measure measure2;		//predominant	ii, IV, vi			ii0, iv, VI 
	Measure measure3;		//dominant		V, vii0				V, vii0
	Measure measure4;		//'tonic'		I, vi 				i, VI
	Measure measure5;		//predominant	ii, IV, vi			ii0, iv, VI 
	Measure measure6;		//predominant	ii, IV, vi			ii0, iv, VI 
	Measure measure7;		//dominant		V, vii0				V, vii0
	Measure measure8;		//'tonic'		I, vi 				i, VI
	Measure measure9;		//tonic or pred	I, ii, iii, IV, vi 	i, ii0, III, iv, VI
	Measure measure10;		//dominant		V, vii0				V, vii0
	Measure measure11;		//tonic			I, vi 				i, VI
	Measure measure12;		//tonic or pred I, ii, iii, IV, vi 	i, ii0, III, iv, VI
	Measure measure13;		//predominant	ii, IV, vi			ii0, iv, VI 
	Measure measure14;		//predominant	ii, IV, vi			ii0, iv, VI 
	Measure measure15;		//dominant		V, vii0				V, vii0
	Measure measure16;		//tonic			I 					z
	Measure measure17;		//cadence


	public SuiteMovement (Measure entry, int key, String inst) {
		if (inst != "")
			this.instrument = inst;
		this.tonic = new Note(key);
		if ((key == 2) || (key == 4) || (key == 9))
			this.major = false;
		this.entry = entry;
	}

	public void pseudoCompose() {
		System.out.println(this.tonic.getName() + " " + this.major);
		//based on prelude from suite no 1
		int noteCount = entry.noteCount;
		
		bass = new Measure[17];

		measure1 = new Measure(noteCount*2, 's');
		for (int i = 0; i != 2; ++i) {
			for (int j = 0; j != noteCount; ++j) {
				if (noteCount %2 == 1) {
					if (j == 0)
						measure1.notes[(noteCount*i) +j] = new Note(entry.notes[j], 'i');
					else
						measure1.notes[(noteCount*i) +j] = new Note(entry.notes[j], 's');
				}
				else
					measure1.notes[(noteCount*i) +j] = new Note(entry.notes[j], 's');
			}
		}
		bass[0] = measure1;

		checkKey(noteCount);
		Measure temp = new Measure(noteCount*2, 's');
		for (int i = 0; i != 2; ++i) {
			for (int j = 0; j != noteCount; ++j) {
				if (noteCount %2 == 1) {
					if (j == 0) 
						temp.notes[(noteCount*i) +j] = new Note(entry.notes[j], 'i');
					else 
						temp.notes[(noteCount*i) +j] = new Note(entry.notes[j], 's');
				}
				else
					temp.notes[(noteCount*i) +j] = new Note(entry.notes[j], 's');
			}
		}

		measure2 = new Measure(temp.copyAndOneToFour(this.tonic, this.major));
		bass[1] = measure2;

		measure3 = new Measure(measure2.copyAndFourToSeven(this.tonic, this.major));
		bass[2] = measure3;

		measure4 = new Measure(measure3.copyAndSevenToOne(this.tonic, this.major));
		bass[3] = measure4;

		measure5 = new Measure(temp.copyAndOneToSix(this.tonic, this.major));
		bass[4] = measure5;

		measure6 = new Measure(measure5.copyAndSixToTwo(this.tonic, this.major));
		bass[5] = measure6;

		measure7 = new Measure(measure6.copyAndTwoToFive(this.tonic, this.major));
		bass[6] = measure7;

		measure8 = new Measure(measure7.copyAndFiveToSix(this.tonic, this.major));
		bass[7] = measure8;

		measure9 = new Measure(temp.copyAndOneToFour(this.tonic, this.major));
		bass[8] = measure9;

		measure10 = new Measure(measure9.copyAndFourToFive(this.tonic, this.major));
		bass[9] = measure10;

		measure11 = new Measure(measure10.copyAndFiveToSix(this.tonic, this.major));
		bass[10] = measure11;

		measure12 = new Measure(temp.copyAndOneToThree(this.tonic, this.major));
		bass[11] = measure12;

		measure13 = new Measure(measure12.copyAndThreeToSix(this.tonic, this.major));
		bass[12] = measure13;

		measure14 = new Measure(measure13.copyAndSixToTwo(this.tonic, this.major));
		bass[13] = measure14;

		measure15 = new Measure(measure14.copyAndTwoToFive(this.tonic, this.major));
		bass[14] = measure15;

		measure16 = new Measure(measure15.copyAndFiveToOne(this.tonic, this.major));
		bass[15] = measure16;

		measure17 = new Measure(1, 'w');
		measure17.notes[0] = new Note(tonic, 'w');
		measure17.notes[0].setOctaveNotPitch(entry.notes[0]);
		bass[16] = measure17;
		
		bassCount = 17;
	}

	public void checkKey (int noteCount) {
		int dif;

		if (this.major) {
			for (int i = 0; i != noteCount; ++i) {
				if (this.entry.notes[i].val > this.tonic.val)
					dif = (this.entry.notes[i].val - this.tonic.val)%12;
				else 
					dif = (this.tonic.val - this.entry.notes[i].val)%12;

				if ((dif == 1) || (dif == 3) || (dif == 6) || (dif == 8) || (dif == 10)) {
					if (rand.nextInt(2) == 0)
						this.entry.notes[i].transpose(1);
					else
						this.entry.notes[i].transpose(-1);
				}
					
			}
		}
		else {
			for (int i = 0; i != noteCount; ++i) {
				if (this.entry.notes[i].val > this.tonic.val)
					dif = (this.entry.notes[i].val - this.tonic.val)%12;
				else 
					dif = (this.tonic.val - this.entry.notes[i].val)%12;
				if ((dif == 1) || (dif == 4) || (dif == 6)) {
					if (rand.nextInt(2) == 0)
						this.entry.notes[i].transpose(1);
					else
						this.entry.notes[i].transpose(-1);
				}
				else if (dif == 9) {
					if (rand.nextInt(2) == 0)
						this.entry.notes[i].transpose(2);
					else
						this.entry.notes[i].transpose(-1);
				}
				else if (dif == 10) {
					if (rand.nextInt(2) == 0)
						this.entry.notes[i].transpose(1);
					else
						this.entry.notes[i].transpose(-2);
				}
			}
		}
	}

	public String toString() {
		this.musicString = "V0 | ";
		for (i = 0; i != bassCount; ++i) {
			musicString += bass[i].toString();
			musicString += " | ";
		}
		return musicString;
	}
}