import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Main {
	private static final int DEFAULT_SUBSTRING_LENGTH = 4;
	private static int SUBSTRING_LENGTH;
	public static void main(String[] args) {

		if(args.length > 0) {
			SUBSTRING_LENGTH = Integer.parseInt(args[0]);
		} else {
			SUBSTRING_LENGTH = DEFAULT_SUBSTRING_LENGTH;
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
					Substring substring = new Substring(word.substring(i, i+SUBSTRING_LENGTH), input);
					if(!set.contains(substring)) {
						set.add(substring);
					} else {
						set.get(set.indexOf(substring)).add(input);
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
			for(String line : s.lines) {
				System.out.println(line);
			}
			System.out.println();
		}
	}
}

class Substring implements Comparable{
	public String sub;
	public ArrayList<String> lines;
	
	public Substring(String sub, String line) {
		this.sub = sub;
		lines = new ArrayList<String>();
		lines.add(line);
	}
	
	public void add(String line) {
		lines.add(line);
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