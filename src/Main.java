import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	
	public static void main(String[] args) {
		Main.parseInputArguments(args);
	}

	public static void parseInputArguments(String[] args) {

		if(args.length < 1) {
			return;
		}

		String programToRun = args[0];
		String[] programSpecificArgs = Arrays.copyOfRange(args, 1, args.length);
		Searcher searcher;
		switch (programToRun) {
			case "match-length":
				searcher = new WorldLengthSearcher(programSpecificArgs);
				break;
			case "match-regex":
				searcher = new RegexMatcher(programSpecificArgs);
				break;
			case "match-regex-fullstring":
				searcher = new RegexMatcherFullString(programSpecificArgs);
				break;
			default: 
				String errorMessage = "You wrote a command that doesn't match our current list of algorithms. Silly Bobby...";
				System.out.println(errorMessage);
				return;
		}
		searcher.start();
		
	}
}

interface Searcher {
	public void start();
}

class WorldLengthSearcher implements Searcher {

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

class RegexMatcher implements Searcher {

	private Pattern regex;
	private boolean hideRepeatedWords;

	public RegexMatcher(String[] args) {
		if(args.length > 0) {
			this.regex = Pattern.compile(args[0]);
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
		Substring last = set.get(0);
		for(Substring s : set) {
			last = s;
			totalOccurrences += s.lines.size();
			if (s.sub.length() > longestMatchLength) {
				longestMatchLength = s.sub.length();
			}
		}
		System.out.println(last.sub);
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

class RegexMatcherFullString implements Searcher {

	private Pattern regex;
	private boolean hideRepeatedWords, groupByMatchedSequence;
	
	public RegexMatcherFullString(String[] args) {
		if(args.length > 0) {
			this.regex = Pattern.compile(args[0]);
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
				if(fullTextString.charAt(matcher.start()) == '.') {
					startIndex -= 1;
				}
				if(startIndex < 0) {
					startIndex = 0;
				}
				int endIndex = fullTextString.indexOf(".", matcher.end());
				if(fullTextString.charAt(matcher.end()) == '.') {
					endIndex += 1;
				}
				if(endIndex >= fullTextString.length() || endIndex < 0) {
					endIndex = fullTextString.length()-1;
				}
				
				int offset = matcher.start() - startIndex;
				String lineNumber = getLineNumber(lineNumberIndices, lineNumbers, startIndex);
				
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
			} while(matcher.find(matcher.end()+1));
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
		System.out.println(regex.toString() + " (" + uniqueWordForms + " unique word forms, " + totalOccurrences + " occurrences)");
		System.out.println();
		
		for(Substring s : set) {
			System.out.println(s.sub + " (" + s.lines.size() + " occurrences)");
			
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

class Substring implements Comparable<Substring>{
	public String sub;
	public ArrayList<Line> lines;
	
	public Substring(String sub, Line line) {
		this.sub = sub;
		lines = new ArrayList<Line>();
		lines.add(line);
	}
	
	public void add(Line line, boolean uniqueWords) {
		if(uniqueWords) {
			boolean exists = false;
			for(Line l : lines) {
				if(l.word.equals(line.word)) {
					exists = true;
					break;
				}
			}
			if(!exists) {
				lines.add(line);
			}
		} else {
			lines.add(line);
		}
	}
	
	@Override
	public int hashCode() {
		return sub.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null || !(obj instanceof Substring))
			return false;
		Substring other = (Substring) obj;
		
		return this.sub.equals(other.sub);
	}
	
	@Override
	public int compareTo(Substring other) {
		return other.lines.size() - this.lines.size();
	}
}

class Line {
	public String lineNumber;
	public String word;
	public int offset;
	public Line(String lineNumber, String word, int offset) {
		this.lineNumber = lineNumber;
		this.word = word;
		this.offset = offset;
	}
}