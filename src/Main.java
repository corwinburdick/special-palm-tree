import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class Main {
	private static final int DEFAULT_SUBSTRING_LENGTH = 4;
	private static int SUBSTRING_LENGTH;
	
	private static boolean UNIQUE_WORDS;
	
	public static void main(String[] args) {

		if(args.length > 0) {
			SUBSTRING_LENGTH = Integer.parseInt(args[0]);
		} else {
			SUBSTRING_LENGTH = DEFAULT_SUBSTRING_LENGTH;
		}
		
		if(args.length > 1 && (args[1].equals("u") || args[1].equals("unique"))) {
			UNIQUE_WORDS = true;
		} else {
			UNIQUE_WORDS = false;
		}
		
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
				if(word.length() < SUBSTRING_LENGTH) {
					continue;
				}
				for(int i = 0; i < word.length()-SUBSTRING_LENGTH-1; i++) {
					Substring substring = new Substring(word.substring(i, i+SUBSTRING_LENGTH), new Line(lineNumber, word, i));
					if(!set.contains(substring)) {
						set.add(substring);
					} else {
						set.get(set.indexOf(substring)).add(substring.lines.get(0), UNIQUE_WORDS);
					}
				}
			}
		}
		
		Collections.sort(set);
		
		for(Substring s : set) {
			if(s.lines.size()<2)
				continue;
			System.out.println(s.sub + " (" + s.lines.size() + " occurences)");
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

class Substring implements Comparable{
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
	public int compareTo(Object obj) {
		Substring other = (Substring) obj;
		
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