package searchers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import structures.Line;
import structures.Substring;

public class WorldLengthSearcher implements Searcher {

	private final int DEFAULT_SUBSTRING_LENGTH = 4;
	private int minSubstringLength;
	
	private boolean requiresUniqueWords;

	public WorldLengthSearcher(String[] args) {
		if(args.length > 0) {
			this.minSubstringLength = Integer.parseInt(args[0]);
		} else {
			this.minSubstringLength = DEFAULT_SUBSTRING_LENGTH;
		}
		
		if(args.length > 1 && (args[1].equals("u") || args[1].equals("unique"))) {
			this.requiresUniqueWords = true;
		} else {
			this.requiresUniqueWords = false;
		}
	}

	public void start() {

		Scanner sc = new Scanner(System.in);
				
		ArrayList<Substring> set = new ArrayList<Substring>();
		
		while(sc.hasNextLine()) {
			String input = sc.nextLine();
			if(input == null || input.isEmpty()) {
				continue;
			}
			
			String[] line = input.split("\\<|\\>");
			
			String lineNumber = line[1];
			String[] words = line[2].split("\\.");
			for(String word : words) {
				if(word.length() < this.minSubstringLength) {
					continue;
				}
				for(int i = 0; i < word.length()-this.minSubstringLength-1; i++) {
					Substring substring = new Substring(word.substring(i, i+this.minSubstringLength), new Line(lineNumber, word, i));
					if(!set.contains(substring)) {
						set.add(substring);
					} else {
						set.get(set.indexOf(substring)).add(substring.lines.get(0), this.requiresUniqueWords);
					}
				}
			}
		}
		
		Collections.sort(set);
		
		for(Substring s : set) {
			if(s.lines.size()<2)
				continue;
			System.out.println(s.sub + " (" + s.lines.size() + " occurrences)");
			System.out.println();
		
			int maxOffset = 0;
			int maxLineLength = 0;
			for(Line line : s.lines) {
				if(line.offset > maxOffset) {
					maxOffset = line.offset;
				}
				if(line.lineNumber.length() > maxLineLength) {
					maxLineLength = line.lineNumber.length();
				}
			}
			
			for(Line line : s.lines) {
				//pad with spaces
				char[] spaces = new char[maxLineLength + maxOffset - line.lineNumber.length() - line.offset];
				Arrays.fill(spaces, ' ');				
				System.out.println("<" + line.lineNumber + "> " + new String(spaces) + line.word);
			}
			System.out.println();
		}

	}

}