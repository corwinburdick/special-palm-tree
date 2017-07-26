package searchers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import structures.Line;
import structures.Substring;

public class RegexMatcher implements Searcher {

	private Pattern regex;
	private boolean hideRepeatedWords;

	public RegexMatcher(String[] args) {
		if(args.length > 0) {
			String regexString = args[0].replaceAll("\\\\L", "[^\\\\.]"); //Replace "\L" with "[^\.]{2}"
			this.regex = Pattern.compile(regexString);
		} else {
			System.out.println("The whole point of this is that you give me a regex! Silly bobby...");
		}
		
		if(args.length > 1 && (args[1].equals("h") || args[1].equals("hide-repeats"))) {
			this.hideRepeatedWords = true;
		} else {
			this.hideRepeatedWords = false;
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

				Matcher matcher = regex.matcher(word);
				if(!matcher.find()) { // if there are no matches in the string
					continue;
				}								

				Substring substring = new Substring(word, new Line(lineNumber, word, -1)); // using -1 for "not using offset"
				if(!set.contains(substring)) {
					set.add(substring);
				} else {
					set.get(set.indexOf(substring)).add(substring.lines.get(0), false);
				}
			}
		}

		Collections.sort(set);
		
		// print total occurrences
		int totalOccurrences = 0;
		int longestMatchLength = 0;
		int uniqueWordForms = set.size();
		for(Substring s : set) {
			totalOccurrences += s.lines.size();
			if (s.sub.length() > longestMatchLength) {
				longestMatchLength = s.sub.length();
			}
		}
		System.out.println(regex.toString() + " (" + uniqueWordForms + " unique word forms, " + totalOccurrences + " occurrences)");
		System.out.println();

		for(Substring s : set) {
			char[] spaces = new char[longestMatchLength - s.sub.length() + 5]; // 5 for padding
			Arrays.fill(spaces, ' ');				
			System.out.println(s.sub + new String(spaces) + "(" + s.lines.size() + " occurrences)");
		
			if (this.hideRepeatedWords == false) {
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
					// pad with spaces
					char[] perOccurrenceSpaces = new char[maxLineLength + maxOffset - line.lineNumber.length() - line.offset];
					Arrays.fill(perOccurrenceSpaces, ' ');				
					System.out.println("<" + line.lineNumber + "> " + new String(perOccurrenceSpaces) + line.word);
				}
				System.out.println();
			}
			
		}

	}

}