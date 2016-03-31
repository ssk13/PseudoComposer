import java.io.*;

class MusicXMLWriter {
	String headerLine1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>",
		headerLine2 = "<!DOCTYPE score-partwise PUBLIC \"-//Recordare//DTD MusicXML 3.0 Partwise//EN\" \"http://www.musicxml.org/dtds/partwise.dtd\">\n";
	int stringLocation = 27;
	public void write (String playerString) {
		try {
			PrintWriter transcriber = new PrintWriter("PseudoComposition.XML", "UTF-8");
			transcriber.println(this.headerLine1);
			transcriber.println(this.headerLine2);
			transcriber.println("<score-partwise version=\"3.0\">");
			transcriber.println("\t<part-list>");
			transcriber.println("\t\t<score-part id=\"P1\">");
			transcriber.println("\t\t\t<part-name>Part 1</part-name>");
			transcriber.println("\t\t</score-part>");
			if (playerString.contains("V1")) {
				transcriber.println("\t\t<score-part id=\"P2\">");
				transcriber.println("\t\t\t<part-name>Part 2</part-name>");
				transcriber.println("\t\t</score-part>");
			}
			transcriber.println("\t</part-list>");
			transcriber.println("\t<part id=\"P1\">");
			transcriber.println("\t\t<measure number=\"1\">");
			transcriber.println("\t\t\t<attributes>");
			transcriber.println("\t\t\t\t<divisions>2</divisions>");
			transcriber.println("\t\t\t\t<key>");
			transcriber.println("\t\t\t\t\t<fifths>0</fifths>");
			transcriber.println("\t\t\t\t</key>");
			transcriber.println("\t\t\t\t<time>");
			transcriber.println("\t\t\t\t\t<beats>4</beats>");
			transcriber.println("\t\t\t\t\t<beat-type>4</beat-type>");
			transcriber.println("\t\t\t\t</time>");
			transcriber.println("\t\t\t\t<clef>");
			transcriber.println("\t\t\t\t\t<sign>G</sign>");
			transcriber.println("\t\t\t\t\t<line>2</line>");
			transcriber.println("\t\t\t\t</clef>");
			transcriber.println("\t\t\t</attributes>");
			while (stringLocation < playerString.length() && playerString.charAt(stringLocation) != 'V') {
				if (playerString.charAt(stringLocation) == 'A' ||
					playerString.charAt(stringLocation) == 'B' ||
					playerString.charAt(stringLocation) == 'C' ||
					playerString.charAt(stringLocation) == 'D' ||
					playerString.charAt(stringLocation) == 'E' ||
					playerString.charAt(stringLocation) == 'F' ||
					playerString.charAt(stringLocation) == 'G'
					) {
					transcriber.println("\t\t\t<note>");
					transcriber.println("\t\t\t\t<pitch>");
					transcriber.println("\t\t\t\t\t<step>" + playerString.charAt(stringLocation) + "</step>");
					if (playerString.charAt(stringLocation + 1) == '#') {
						transcriber.println("\t\t\t\t\t<alter>1</alter>");
						transcriber.print("\t\t\t\t\t<octave>");
						transcriber.print(String.valueOf(playerString.charAt(stringLocation + 2)));
					} else {
						transcriber.print("\t\t\t\t\t<octave>");
						transcriber.print(String.valueOf(playerString.charAt(stringLocation + 1)));
					}
					transcriber.println("</octave>");
					transcriber.println("\t\t\t\t</pitch>");
					transcriber.println("\t\t\t\t<duration>1</duration>");
					transcriber.println("\t\t\t\t<type>eighth</type>");
					transcriber.println("\t\t\t</note>");
				}
				stringLocation++;
			}
			transcriber.println("\t\t</measure>");
			transcriber.println("\t</part>");
			if (playerString.contains("V1")) {
				stringLocation = playerString.indexOf("V1") + 20;
				transcriber.println("\t<part id=\"P2\">");
				transcriber.println("\t\t<measure number=\"1\">");
				transcriber.println("\t\t\t<attributes>");
				transcriber.println("\t\t\t\t<divisions>1</divisions>");
				transcriber.println("\t\t\t\t<key>");
				transcriber.println("\t\t\t\t\t<fifths>0</fifths>");
				transcriber.println("\t\t\t\t</key>");
				transcriber.println("\t\t\t\t<time>");
				transcriber.println("\t\t\t\t\t<beats>4</beats>");
				transcriber.println("\t\t\t\t\t<beat-type>4</beat-type>");
				transcriber.println("\t\t\t\t</time>");
				transcriber.println("\t\t\t\t<clef>");
				transcriber.println("\t\t\t\t\t<sign>G</sign>");
				transcriber.println("\t\t\t\t\t<line>2</line>");
				transcriber.println("\t\t\t\t</clef>");
				transcriber.println("\t\t\t</attributes>");
				while (stringLocation < playerString.length() && playerString.charAt(stringLocation) != 'V') {
					if (playerString.charAt(stringLocation) == 'A' ||
						playerString.charAt(stringLocation) == 'B' ||
						playerString.charAt(stringLocation) == 'C' ||
						playerString.charAt(stringLocation) == 'D' ||
						playerString.charAt(stringLocation) == 'E' ||
						playerString.charAt(stringLocation) == 'F' ||
						playerString.charAt(stringLocation) == 'G'
						) {
						transcriber.println("\t\t\t<note>");
						transcriber.println("\t\t\t\t<pitch>");
						transcriber.println("\t\t\t\t\t<step>" + playerString.charAt(stringLocation) + "</step>");
						if (playerString.charAt(stringLocation + 1) == '#') {
							transcriber.println("\t\t\t\t\t<alter>1</alter>");
							transcriber.print("\t\t\t\t\t<octave>");
							transcriber.print(String.valueOf(playerString.charAt(stringLocation + 2)));
						} else {
							transcriber.print("\t\t\t\t\t<octave>");
							transcriber.print(String.valueOf(playerString.charAt(stringLocation + 1)));
						}
						transcriber.println("</octave>");
						transcriber.println("\t\t\t\t</pitch>");
						transcriber.println("\t\t\t\t<duration>1</duration>");
						transcriber.println("\t\t\t\t<type>quarter</type>");
						transcriber.println("\t\t\t</note>");
					}
					stringLocation++;
				}
				transcriber.println("\t\t</measure>");
				transcriber.println("\t</part>");
			}
			transcriber.println("</score-partwise>");
			transcriber.close();
		}
		catch (IOException e) {
		}
	}
}

