/* 
   by Sarah Klein
  
*/
/* 
	TwoVoiceCounterpoint.java
   	Represents a 2-voice, contrapuntal composition
		 future: 
		 	implement soft rules
   				check that there are more steps than skips
   			allow user to select range/mode
   			check for tritone outlines
   			Make sure first interval is perfect
   			prevent slow trills

   	by Sarah Klein
*/

public class TwoVoiceCounterpoint extends Counterpoint {
	Note[][] notes;	//Actual notes in the line
	int[] validNoteValues = {33, 35, 36, 38, 40, 41, 43, 45};	//valid note values in our default mode
	CantusFirmus cantusFirmus;	//the cantus firmus of our composition

	/*
		Constructor
		Creates the notes that will construct the composition, initialized to quarter-note rests
	*/
	public TwoVoiceCounterpoint (CantusFirmus cantusFirmus, int species) {
		super();
		this.cantusFirmus = cantusFirmus;
		if (species == 1) {
			this.numNotes = cantusFirmus.numNotes;
		} else if (species == 2) {
			this.numNotes = (cantusFirmus.numNotes * 2) - 1;
		} else if (species == 3) {
			this.numNotes = (cantusFirmus.numNotes * 4) - 3;
		}
		this.notes = new Note[numNotes][2];
		this.species = species;
		if (species == 1) {
			for (int i = 0; i != numNotes; ++i) {
				this.notes[i][0] = new Note('q');
				this.notes[i][1] = cantusFirmus.notes[i];
			}
		} else if (species == 2) {
			for (int i = 0; i != numNotes - 1; ++i) {
				this.notes[i][0] = new Note('i');
				this.notes[i][1] = cantusFirmus.notes[i/2];
			}
			this.notes[numNotes - 1][0] = new Note('q');
			this.notes[numNotes - 1][1] = cantusFirmus.notes[numNotes/2];
		} else if (species == 3) {
			for (int i = 0; i != numNotes - 1; ++i) {
				this.notes[i][0] = new Note('i');
				this.notes[i][1] = cantusFirmus.notes[i/4];
			}
			this.notes[numNotes - 1][0] = new Note('q');
			this.notes[numNotes - 1][1] = cantusFirmus.notes[numNotes/4];
		}
	}

	/*
		Writes a line of first species counterpoint above a given cantus firmus
		future: 
	*/
	public void pseudoComposeFromScratchInFirstSpecies() {
		int place = 0,
			randVal = rand.nextInt(3),
			numberOfSkips = 0, 
			numberOfNoteRepetitions = 0,
			valueOfRepeatedNote = 0,
			attempts,
			counter = 0,
			diff;
		boolean nextMotionSmallAndOppositeDirection = false,
			noteFound = false;

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

			randVal = rand.nextInt(8);	//we're going to try every note possible for this line
			while (attempts < 8 && !noteFound) {	//while we haven't tried every note and we haven't found the right note
				if (place == 0) {
					//fill the first note with either the tonic or the dominant
					if (randVal == 0) {
						 notes[place++][0] = new Note(validNoteValues[0]);
					} else if (randVal == 1) {
						notes[place++][0] = new Note(validNoteValues[3]);
					} else {
						notes[place++][0] = new Note(validNoteValues[7]);
					}
					valueOfRepeatedNote = notes[0][0].val;
					noteFound = true;
				} else {
					if (isConsonantVertically(notes[place][1].val, validNoteValues[randVal]) && 
						(isImperfectConsonance(notes[place][1].val, validNoteValues[randVal]) || 
						 isContraryOrOblique(notes[place-1][0].val, notes[place-1][1].val, validNoteValues[randVal], notes[place][1].val)) &&
						 notes[place][1].val != validNoteValues[randVal] //omits internal unisons
						) {
						diff = notes[place - 1][0].val - validNoteValues[randVal];
						if (diff == 0) {
							//if it's the same note
							if (numberOfNoteRepetitions != 2 && valueOfRepeatedNote != validNoteValues[randVal] && !nextMotionSmallAndOppositeDirection && notes[place - 1][1].val != notes[place][1].val) {
								noteFound = true;
								valueOfRepeatedNote = validNoteValues[randVal];	//keep track in case we get repetitive
							}
						} else if (diff == 1 || diff == 2 || diff == -2 || diff == -1) {
							//if it's stepwise movement
							if (place > 1) {
								if (!nextMotionSmallAndOppositeDirection || isApproachedFromOppositeDirection(notes[place-2][0].val, notes[place-1][0].val, validNoteValues[randVal])) {
									noteFound = true;
								}
							} else {
								noteFound = true;
							}
						} else if (diff == 3 || diff == 4 || diff == -3 || diff == -4) {
							//if it's motion by a third
							if (numberOfSkips != 2) {
								if (place > 1) {
									if (!nextMotionSmallAndOppositeDirection || isApproachedFromOppositeDirection(notes[place-2][0].val, notes[place-1][0].val, validNoteValues[randVal])) {
										noteFound = true;
									}
								}
								else {
									noteFound = true;
								}
							}
						} else if (diff == 5 || diff == 7 || diff == 8 || diff == 12 || diff == -5 || diff == -7 ||  diff == -8 || diff == -12) {
							//if it's gonna leap
							if (numberOfSkips == 0) {
								if (place > 1) {
									if (isApproachedFromOppositeDirection(notes[place-2][0].val, notes[place-1][0].val, validNoteValues[randVal])) {
										noteFound = true;
									}
								} else {
									noteFound = true;
								}
							}
						}

						if (noteFound) {
							notes[place++][0] = new Note(validNoteValues[randVal]);	//assign the note
							numberOfSkips = (diff < 3 && diff > -3) ? 0 : numberOfSkips + 1;	//add a skip if we're skipping, otherwise reset
							nextMotionSmallAndOppositeDirection = (diff > 4 || diff < -4) ? true : false;	//dictate next motion stepwise if it's a large leap
							numberOfNoteRepetitions = (diff == 0) ? numberOfNoteRepetitions + 1 : 0; //increase number of repetitions of we're repeating
						}
					}
				}
				randVal = (randVal + 1) % 8;
				++attempts;
			}
					
			if (noteFound == false) {
				--place;
				if (place < 0)
					place = 0;
			}

			if (place == numNotes - 2) {
				if (!voiceLeadingIntoCadenceIsValid(nextMotionSmallAndOppositeDirection, numberOfSkips == 2)) {
					place -= 2;
				}
			}
		}
	}

	/*
		Writes a line of second species counterpoint above a given cantus firmus
		future:
	*/
	public void pseudoComposeFromScratchInSecondSpecies() {
		int place = 0,
			randVal = rand.nextInt(3),
			numberOfSkips = 0, 
			attempts,
			counter = 0,
			diff;
		boolean nextMotionSmallAndOppositeDirection = false,
			nextMotionStepwise = false,
			nextMotionAscending = false,
			noteFound = false;

		//put tonic in the last space and the appropriate pentultimate note based on the cantus firmus
		if (notes[numNotes - 2][1].val == validNoteValues[2] + 1) {
			notes[numNotes - 2][0] = new Note(validNoteValues[4], 'i');
		} else {
			notes[numNotes - 2][0] = new Note(validNoteValues[2], 'i');
			notes[numNotes - 2][0].transpose(1);
		}
		notes[numNotes - 1][0] = new Note(validNoteValues[3], 'q');

		//fill all of the notes to the end using a path-finding algorithm
		while (place < numNotes - 2 && counter < 2000) {
			System.out.println("place: " + place);
			++counter;
			attempts = 0;
			noteFound = false;

			randVal = rand.nextInt(8);	//we're going to try every note possible for this line
			while (attempts < 8 && !noteFound) {	//while we haven't tried every note and we haven't found the right note
				if (place == 0) {
					//fill the first note with either the tonic or the dominant
					if (randVal == 0) {
						notes[place++][0] = new Note(validNoteValues[0], 'i');
					} else if (randVal == 1) {
						notes[place++][0] = new Note(validNoteValues[3], 'i');
					} else {
						notes[place++][0] = new Note(validNoteValues[7], 'i');
					}
					noteFound = true;
				} else if (place % 2 == 0) {
					if (isConsonantVertically(notes[place][1].val, validNoteValues[randVal]) && 
						(isImperfectConsonance(notes[place][1].val, validNoteValues[randVal]) || 
						 isContraryOrOblique(notes[place-1][0].val, notes[place-1][1].val, validNoteValues[randVal], notes[place][1].val)) &&
						 notes[place][1].val != validNoteValues[randVal] //omits internal unisons
						) {
						diff = notes[place - 1][0].val - validNoteValues[randVal];
						if (diff == 1 || diff == 2 || diff == -2 || diff == -1) {
							if (!nextMotionAscending || diff > 0) {
								//if it's stepwise movement
								if (place > 1) {
									if (!nextMotionSmallAndOppositeDirection || isApproachedFromOppositeDirection(notes[place-2][0].val, notes[place-1][0].val, validNoteValues[randVal])) {
										noteFound = true;
									}
									else {
										noteFound = true;
									}
								}
							}
						} else if (diff == 3 || diff == 4 || diff == -3 || diff == -4) {
							//if it's motion by a third
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								if (place > 1) {
									if (!nextMotionSmallAndOppositeDirection || isApproachedFromOppositeDirection(notes[place-2][0].val, notes[place-1][0].val, validNoteValues[randVal])) {
										noteFound = true;
									}
								}
								else {
									noteFound = true;
								}
							}
						} else if (diff == 5 || diff == 7 || diff == 8 || diff == 12 || diff == -5 || diff == -7 ||  diff == -8 || diff == -12) {
							//if it's gonna leap
							if (numberOfSkips == 0 && !nextMotionStepwise) {
								if (place > 1) {
									if (isApproachedFromOppositeDirection(notes[place-2][0].val, notes[place-1][0].val, validNoteValues[randVal])) {
										noteFound = true;
									}
								} else {
									noteFound = true;
								}
							}
						}

						if (noteFound) {
							notes[place++][0] = new Note(validNoteValues[randVal], 'i');	//assign the note
							numberOfSkips = (diff < 3 && diff > -3) ? 0 : numberOfSkips + 1;	//add a skip if we're skipping, otherwise reset
							nextMotionSmallAndOppositeDirection = (diff > 4 || diff < -4) ? true : false;	//dictate next motion stepwise if it's a large leap
						}
					}
				} else {
					if (isConsonantVertically(notes[place][1].val, validNoteValues[randVal]) && 
						(isImperfectConsonance(notes[place][1].val, validNoteValues[randVal]) || 
						 isContraryOrOblique(notes[place-1][0].val, notes[place-1][1].val, validNoteValues[randVal], notes[place][1].val)) &&
						 notes[place][1].val != validNoteValues[randVal] //omits internal unisons
						) {
						diff = notes[place - 1][0].val - validNoteValues[randVal];
						if (diff == 0) {
							//if it's the same note
							if (!nextMotionSmallAndOppositeDirection && notes[place - 1][1].val != notes[place][1].val) {
								noteFound = true;
							}
						} else if (diff == 1 || diff == 2 || diff == -2 || diff == -1) {
							//if it's stepwise movement
							if (place > 1) {
								if (!nextMotionSmallAndOppositeDirection || isApproachedFromOppositeDirection(notes[place-2][0].val, notes[place-1][0].val, validNoteValues[randVal])) {
									noteFound = true;
								}
							}
							else {
								noteFound = true;
							}
						} else if (diff == 3 || diff == 4 || diff == -3 || diff == -4) {
							//if it's motion by a third
							if (numberOfSkips != 2) {
								if (place > 1) {
									if (!nextMotionSmallAndOppositeDirection || isApproachedFromOppositeDirection(notes[place-2][0].val, notes[place-1][0].val, validNoteValues[randVal])) {
										noteFound = true;
									}
								}
								else {
									noteFound = true;
								}
							}
						} else if (diff == 5 || diff == 7 || diff == 8 || diff == 12 || diff == -5 || diff == -7 ||  diff == -8 || diff == -12) {
							//if it's gonna leap
							if (numberOfSkips == 0) {
								if (place > 1) {
									if (isApproachedFromOppositeDirection(notes[place-2][0].val, notes[place-1][0].val, validNoteValues[randVal])) {
										noteFound = true;
									}
								} else {
									noteFound = true;
								}
							}
						}

						if (noteFound) {
							notes[place++][0] = new Note(validNoteValues[randVal], 'i');	//assign the note
							numberOfSkips = (diff < 3 && diff > -3) ? 0 : numberOfSkips + 1;	//add a skip if we're skipping, otherwise reset
							nextMotionSmallAndOppositeDirection = (diff > 4 || diff < -4) ? true : false;	//dictate next motion stepwise if it's a large leap
						}
					} else { //if it's a dissonance
						diff = notes[place - 1][0].val - validNoteValues[randVal];
						if (diff == 1 || diff == 2 || diff == -2 || diff == -1) {
							//if it's stepwise movement
							if (place > 1) {
								if (!nextMotionSmallAndOppositeDirection || isApproachedFromOppositeDirection(notes[place-2][0].val, notes[place-1][0].val, validNoteValues[randVal])) {
									noteFound = true;
								}
							} else {
								noteFound = true;
							}
						}

						if (noteFound) {
							notes[place++][0] = new Note(validNoteValues[randVal], 'i');	//assign the note
							numberOfSkips = 0;	//reset skips
							nextMotionSmallAndOppositeDirection = false;	//dictate next motion stepwise if it's a large leap
							nextMotionStepwise = true;
							nextMotionAscending = (diff < 0) ? true : false;
						}
					}
				}

				randVal = (randVal + 1) % 8;
				++attempts;
			}
					
			if (noteFound == false) {
				place -= 3;
				if (place < 0)
					place = 0;
			}

			if (place == numNotes - 2) {
				if (!voiceLeadingIntoCadenceIsValid(nextMotionSmallAndOppositeDirection, numberOfSkips == 2)) {
					place -= 2;
				}
			}
		}
	}

	/*
		Writes a line of third species counterpoint above a given cantus firmus
		future: include cambiata
	*/
	public void pseudoComposeFromScratchInThirdSpecies() {
		int place = 0,
			randVal = rand.nextInt(3),
			numberOfSkips = 0, 
			attempts,
			counter = 0,
			diff;
		boolean nextMotionSmallAndOppositeDirection = false,
			nextMotionStepwise = false,
			nextMotionAscending = false,
			noteFound = false;

		//put tonic in the last space and the appropriate pentultimate note based on the cantus firmus
		if (notes[numNotes - 2][1].val == validNoteValues[2] + 1) {
			notes[numNotes - 2][0] = new Note(validNoteValues[4], 's');
		} else {
			notes[numNotes - 2][0] = new Note(validNoteValues[2], 's');
			notes[numNotes - 2][0].transpose(1);
		}
		notes[numNotes - 1][0] = new Note(validNoteValues[3], 'q');

		//fill all of the notes to the end using a path-finding algorithm
		while (place < numNotes - 2 && counter < 4000) {
			System.out.println("place: " + place);
			++counter;
			attempts = 0;
			noteFound = false;

			randVal = rand.nextInt(8);	//we're going to try every note possible for this line
			while (attempts < 8 && !noteFound) {	//while we haven't tried every note and we haven't found the right note
				if (place == 0) {
					//fill the first note with either the tonic or the dominant
					if (randVal == 0) {
						notes[place++][0] = new Note(validNoteValues[0], 's');
					} else if (randVal == 1) {
						notes[place++][0] = new Note(validNoteValues[3], 's');
					} else {
						notes[place++][0] = new Note(validNoteValues[7], 's');
					}
					noteFound = true;
				} else if (place % 2 == 0) {
					if (isConsonantVertically(notes[place][1].val, validNoteValues[randVal]) && 
						(isImperfectConsonance(notes[place][1].val, validNoteValues[randVal]) || 
						 isContraryOrOblique(notes[place-1][0].val, notes[place-1][1].val, validNoteValues[randVal], notes[place][1].val)) &&
						 notes[place][1].val != validNoteValues[randVal] //omits internal unisons
						) {
						diff = notes[place - 1][0].val - validNoteValues[randVal];
						if (diff == 1 || diff == 2 || diff == -2 || diff == -1) {
							if (!nextMotionAscending || diff > 0) {
								//if it's stepwise movement
								if (place > 1) {
									if (!nextMotionSmallAndOppositeDirection || isApproachedFromOppositeDirection(notes[place-2][0].val, notes[place-1][0].val, validNoteValues[randVal])) {
										noteFound = true;
									}
									else {
										noteFound = true;
									}
								}
							}
						} else if (diff == 3 || diff == 4 || diff == -3 || diff == -4) {
							//if it's motion by a third
							if (numberOfSkips != 2 && !nextMotionStepwise) {
								if (place > 1) {
									if (!nextMotionSmallAndOppositeDirection || isApproachedFromOppositeDirection(notes[place-2][0].val, notes[place-1][0].val, validNoteValues[randVal])) {
										noteFound = true;
									}
								}
								else {
									noteFound = true;
								}
							}
						} else if (diff == 5 || diff == 7 || diff == 8 || diff == 12 || diff == -5 || diff == -7 ||  diff == -8 || diff == -12) {
							//if it's gonna leap
							if (numberOfSkips == 0 && !nextMotionStepwise) {
								if (place > 1) {
									if (isApproachedFromOppositeDirection(notes[place-2][0].val, notes[place-1][0].val, validNoteValues[randVal])) {
										noteFound = true;
									}
								} else {
									noteFound = true;
								}
							}
						}

						if (noteFound) {
							notes[place++][0] = new Note(validNoteValues[randVal], 's');	//assign the note
							numberOfSkips = (diff < 3 && diff > -3) ? 0 : numberOfSkips + 1;	//add a skip if we're skipping, otherwise reset
							nextMotionSmallAndOppositeDirection = (diff > 4 || diff < -4) ? true : false;	//dictate next motion stepwise if it's a large leap
						}
					}
				} else {
					if (isConsonantVertically(notes[place][1].val, validNoteValues[randVal]) && 
						(isImperfectConsonance(notes[place][1].val, validNoteValues[randVal]) || 
						 isContraryOrOblique(notes[place-1][0].val, notes[place-1][1].val, validNoteValues[randVal], notes[place][1].val)) &&
						 notes[place][1].val != validNoteValues[randVal] //omits internal unisons
						) {
						diff = notes[place - 1][0].val - validNoteValues[randVal];
						if (diff == 0) {
							//if it's the same note
							if (!nextMotionSmallAndOppositeDirection && notes[place - 1][1].val != notes[place][1].val) {
								noteFound = true;
							}
						} else if (diff == 1 || diff == 2 || diff == -2 || diff == -1) {
							//if it's stepwise movement
							if (place > 1) {
								if (!nextMotionSmallAndOppositeDirection || isApproachedFromOppositeDirection(notes[place-2][0].val, notes[place-1][0].val, validNoteValues[randVal])) {
									noteFound = true;
								}
							}
							else {
								noteFound = true;
							}
						} else if (diff == 3 || diff == 4 || diff == -3 || diff == -4) {
							//if it's motion by a third
							if (numberOfSkips != 2) {
								if (place > 1) {
									if (!nextMotionSmallAndOppositeDirection || isApproachedFromOppositeDirection(notes[place-2][0].val, notes[place-1][0].val, validNoteValues[randVal])) {
										noteFound = true;
									}
								}
								else {
									noteFound = true;
								}
							}
						} else if (diff == 5 || diff == 7 || diff == 8 || diff == 12 || diff == -5 || diff == -7 ||  diff == -8 || diff == -12) {
							//if it's gonna leap
							if (numberOfSkips == 0) {
								if (place > 1) {
									if (isApproachedFromOppositeDirection(notes[place-2][0].val, notes[place-1][0].val, validNoteValues[randVal])) {
										noteFound = true;
									}
								} else {
									noteFound = true;
								}
							}
						}

						if (noteFound) {
							notes[place++][0] = new Note(validNoteValues[randVal], 's');	//assign the note
							numberOfSkips = (diff < 3 && diff > -3) ? 0 : numberOfSkips + 1;	//add a skip if we're skipping, otherwise reset
							nextMotionSmallAndOppositeDirection = (diff > 4 || diff < -4) ? true : false;	//dictate next motion stepwise if it's a large leap
						}
					} else { //if it's a dissonance
						diff = notes[place - 1][0].val - validNoteValues[randVal];
						if (diff == 1 || diff == 2 || diff == -2 || diff == -1) {
							//if it's stepwise movement
							if (place > 1) {
								if (!nextMotionSmallAndOppositeDirection || isApproachedFromOppositeDirection(notes[place-2][0].val, notes[place-1][0].val, validNoteValues[randVal])) {
									noteFound = true;
								}
							} else {
								noteFound = true;
							}
						}

						if (noteFound) {
							notes[place++][0] = new Note(validNoteValues[randVal], 's');	//assign the note
							numberOfSkips = 0;	//reset skips
							nextMotionSmallAndOppositeDirection = false;	//dictate next motion stepwise if it's a large leap
							nextMotionStepwise = true;
							nextMotionAscending = (diff < 0) ? true : false;
						}
					}
				}

				randVal = (randVal + 1) % 8;
				++attempts;
			}
					
			if (noteFound == false) {
				place -= 3;
				if (place < 0)
					place = 0;
			}

			if (place == numNotes - 2) {
				if (!voiceLeadingIntoCadenceIsValid(nextMotionSmallAndOppositeDirection, numberOfSkips == 2)) {
					place -= 2;
				}
			}
		}
	}

	/*
		Checks the voice leading in the soprano line going into the cadence - returns true if valid
	*/
	public boolean voiceLeadingIntoCadenceIsValid(boolean mustLeaveInOppositeDirection, boolean mustBeStepwise) {
		int prevSoprano = notes[numNotes - 3][0].val,
			cadSoprano = notes[numNotes - 2][0].val,
			finalSoprano = notes[numNotes - 1][0].val;

		if (mustBeStepwise) {
			if (cadSoprano - prevSoprano > 2 || prevSoprano - cadSoprano > 2) {
				return false;
			}
		}
		if (isConsonantMelodically(prevSoprano, cadSoprano) && 
			(!mustLeaveInOppositeDirection || isApproachedFromOppositeDirection(prevSoprano, cadSoprano, finalSoprano))) {
			if (cadSoprano - prevSoprano > 4) {
				if (finalSoprano - cadSoprano < 0)
					return true;
			} else if (prevSoprano - cadSoprano > 4) {
				if (finalSoprano - cadSoprano > 0) {
					return true;
				}
			}
			else {
				return true;	
			}
		}
		return false;

	}

	/*
		Converts the cantus firmus to a string that can be played by the JFugue player
	*/
	public String toString() {
		try {
			String voice0 = "V0 | ";
			String voice1 = "V1 | ";
			if (this.species == 1) {
				for (int i = 0; i != numNotes; ++i) {
					voice0 += notes[i][0].toString() + " ";
					voice1 += notes[i][1].toString() + " ";
				}
			} else if (this.species == 2) {
				for (int i = 0; i != numNotes; ++i) {
					voice0 += notes[i][0].toString() + " ";
					if (i%2 == 0)
						voice1 += notes[i][1].toString() + " ";
				}
			} else if (this.species == 3) {
				for (int i = 0; i != numNotes; ++i) {
					voice0 += notes[i][0].toString() + " ";
					if (i%4 == 0)
						voice1 += notes[i][1].toString() + " ";
				}
			}
			
			return (voice0 + " \n" + voice1);
		}
		catch (NullPointerException e) {
		}
		return "";
	}
}
