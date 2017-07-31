import java.util.Arrays;

import searchers.*;

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
				searcher = new WordLengthSearcher(programSpecificArgs);
				break;
			case "match-regex":
				searcher = new RegexMatcher(programSpecificArgs);
				break;
			case "regex-fullstring":
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