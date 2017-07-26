package structures;
import java.util.ArrayList;

public class Substring implements Comparable<Substring>{
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