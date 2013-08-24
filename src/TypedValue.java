

public class TypedValue {
	private Object o;
	
	public TypedValue(Object o){
		this.o=o;
	}
	
	public int getInt(){
		return (int) o;
	}
	public double getDouble(){
		return (double) o;
	}
	public boolean getBool(){
		return (boolean) o;
	}
	public String getWord(){
		return (String) o;
	}
	
	@Override
	public String toString(){
		
	}
}
