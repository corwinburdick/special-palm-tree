package structures;

public class Line {
	public String lineNumber;
	public String word;
	public int offset;
	public Line(String lineNumber, String word, int offset) {
		this.lineNumber = lineNumber;
		this.word = word;
		this.offset = offset;
	}
}