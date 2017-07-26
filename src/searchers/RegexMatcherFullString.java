package searchers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import structures.Line;
import structures.Substring;

public class RegexMatcherFullString implements Searcher {

	private Pattern regex;
	private boolean hideRepeatedWords, groupByMatchedSequence;
	
	public RegexMatcherFullString(String[] args) {
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
		
		if(args.length > 2 && (args[2].equals("g") || args[2].equals("group-by-matched"))) {
			this.groupByMatchedSequence = true;
		} else {
			this.groupByMatchedSequence = false;
		}
	}
	
	public void start() {
		Scanner sc = new Scanner(System.in);
		StringBuilder fullTextString = new StringBuilder();
		
		ArrayList<Substring> set = new ArrayList<Substring>();
		
		ArrayList<Integer> lineNumberIndices = new ArrayList<Integer>();
		ArrayList<String> lineNumbers = new ArrayList<String>();

		while(sc.hasNextLine()) {
			String input = sc.nextLine();
			if(input == null || input.isEmpty()) {
				continue;
			}
			
			String[] line = input.split("\\<|\\>");
			lineNumbers.add(line[1]);
			lineNumberIndices.add(fullTextString.length());
			fullTextString.append(line[2]);
		}
		
		Matcher matcher = regex.matcher(fullTextString);
		if(matcher.find()) {
			do {
				//will need to change these lines if other delimiters are used (i.e ',')
				int startIndex = fullTextString.lastIndexOf(".", matcher.start());
				if(startIndex >= fullTextString.length()) {
					continue;
				}
				if(fullTextString.charAt(matcher.start()) == '.') {
					startIndex -= 1;
				}
				if(startIndex < 0) {
					startIndex = 0;
				} else if(startIndex >= fullTextString.length()) {
					startIndex = fullTextString.length() - 1;
				}
				int endIndex = fullTextString.indexOf(".", matcher.end());
				if(matcher.end() > 0 && fullTextString.charAt(matcher.end() - 1) == '.') {
					endIndex += 1;
				}
				if(endIndex >= fullTextString.length() || endIndex < 0) {
					endIndex = fullTextString.length()-1;
				} else if(endIndex == 0) {
					endIndex = fullTextString.length()-1;
				}
				
				int offset = matcher.start() - startIndex;
				String lineNumber = getLineNumber(lineNumberIndices, lineNumbers, startIndex);
				
//				if(Math.max(startIndex+1, endIndex) >= fullTextString.length() - 1 || Math.min(startIndex + 1, endIndex) <= 1)
//					System.err.println(startIndex + "\t" + endIndex);
				
				String matchedString = fullTextString.substring(startIndex+1, endIndex);
				
				Substring substring = null;
				if(groupByMatchedSequence) {
					substring = new Substring(matcher.group(), new Line(lineNumber, matchedString, offset));					
				} else {
					substring = new Substring(matchedString, new Line(lineNumber, matchedString, offset));
				}
				if(!set.contains(substring)) {
					set.add(substring);
				} else {
					set.get(set.indexOf(substring)).add(substring.lines.get(0), false);
				}
			} while(matcher.end() < fullTextString.length() && matcher.find(matcher.end()+1));
		}
		
		Collections.sort(set);
		
		int totalOccurrences = 0;
		int longestMatchLength = 0;
		int uniqueWordForms = set.size();
		for(Substring s : set) {
			totalOccurrences += s.lines.size();
			if(s.sub.length() > longestMatchLength) {
				longestMatchLength = s.sub.length();
			}
		}
		if(groupByMatchedSequence) {
			System.out.println(regex.toString() + " (" + uniqueWordForms + " unique matches, " + totalOccurrences + " total matches)");
		} else {
			System.out.println(regex.toString() + " (" + uniqueWordForms + " unique word forms, " + totalOccurrences + " occurrences)");
		}
		System.out.println();
		
		for(Substring s : set) {
			if(groupByMatchedSequence) {
				char[] spaces = new char[longestMatchLength - s.sub.length() + 2];
				Arrays.fill(spaces, ' ');	
				System.out.println(s.sub + new String(spaces) + "(" + s.lines.size() + " occurrences)");
			} else {
				System.out.println(s.sub + " (" + s.lines.size() + " occurrences)");
			}
			
			if(!this.hideRepeatedWords) {
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
	
	private String getLineNumber(ArrayList<Integer> lineNumberIndices, ArrayList<String> lineNumbers, int index) {
		int i = Collections.binarySearch(lineNumberIndices, index);
		if(i < 0) {
			i = -1 * i - 2;
		}
		return lineNumbers.get(i);
	}
}