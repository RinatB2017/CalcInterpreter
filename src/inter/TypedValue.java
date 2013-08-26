package inter;


public class TypedValue {
	private int i;
	private double d;
	private boolean b;
	private String s;
	public Types type;
	
	public TypedValue(int o){
		this.i=o;
		this.type=Types.INTEGER;
	}
	
	public TypedValue(double e) {
		this.d=e;
		this.type=Types.DOUBLE;
	}

	public int getInt(){
		return i;
	}
	public double getDouble(){
		return d;
	}
	public boolean getBoolean(){
		return b;
	}
	public String getString(){
		return s;
	}
	
	
	@Override
	public String toString(){
		switch (type){
		case INTEGER:
			return ""+i;
		case DOUBLE:
			return ""+d;
		case BOOLEAN:
			return ""+b;
		}
		return null;
	}

	public TypedValue negative() throws Exception {
		switch (type){
		case INTEGER:
			i = -i;
			return this;
		case DOUBLE:
			d = -d;
			return this;
		default:
			throw new Exception("Забыл BOOLEAN и VECTOR ");
		}
	}

	public TypedValue plus(TypedValue right) throws Exception {
		switch (type){
		case INTEGER:
			i += right.getInt();
			return this;
		case DOUBLE:
			d += right.getDouble();
			return this;
		case BOOLEAN:
		case VECTOR:
			throw new Exception("Забыл BOOLEAN и VECTOR");
		}
		return null;
	}

	public TypedValue minus(TypedValue right) throws Exception {
		switch (type){
		case INTEGER:
			i -= right.getInt();
			return this;
		case DOUBLE:
			d -= right.getDouble();
			return this;
		case BOOLEAN:
		case VECTOR:
			throw new Exception("Забыл BOOLEAN и VECTOR");
		}
		return null;
	}
	
	public TypedValue mul(TypedValue right) throws Exception {
		switch (type){
		case INTEGER:
			i *= right.getInt();
			return this;
		case DOUBLE:
			d *= right.getDouble();
			return this;
		case BOOLEAN:
		case VECTOR:
			throw new Exception("Забыл BOOLEAN и VECTOR");
		}
		return null;
	}
	
	public TypedValue div(TypedValue right) throws Exception {
		switch (type){
		case INTEGER:
			i /= right.getInt();
			return this;
		case DOUBLE:
			d /= right.getDouble();
			return this;
		case BOOLEAN:
		case VECTOR:
			throw new Exception("Забыл BOOLEAN и VECTOR");
		}
		return null;
	}
}
