public class Option <T>{
	public T value;
	private T defaultValue;
	
	Names n;

	public void reset(){value = defaultValue;}
	Option(Names n, T defaultValue) { this.n=n; this.defaultValue=defaultValue; }
	
}