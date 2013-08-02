import java.util.*;


public class OutputSystem {
	private ArrayList<String> buffer;

	public OutputSystem() {
		this.buffer = new ArrayList<String>();
	}
	
	public void add(String s){
		buffer.add(s);
	}
	
	public void flush(){
		for(String s: buffer){
			System.out.print(s);
		}
		buffer.clear();
	}
}
