package types;

import types.func.FuncExpr;
import types.func.def.Dimension;
import main.MyException;

/**
 * Обеспечиват поддержку типов:
 * простых (INTEGER, DOUBLE, BOOLEAN)
 * и сложных (VECTOR).
 * @author Ник
 *
 */
public class TypedValue implements Cloneable{
	private int i;
	private double d;
	private boolean b;
	//private String s;
	private MathVector v;
	private FuncExpr f;
	public Types type;
	private Dimension ft;
	private int numOfArgs;
	
    public TypedValue clone() throws CloneNotSupportedException{
    	TypedValue obj=(TypedValue)super.clone();
        return obj;
    }
	
	// Конструктор копирования, вызывается при создании копии для временного объекта в TableGet
	/*public TypedValue(TypedValue t){
		this.i=t.i;
		this.d=t.d;
		this.b=t.b;
		//this.s=t.s;
		this.v=t.v;
		this.f=t.f;
		this.type=t.type;
		this.ft=t.ft;
		this.numOfArgs=t.numOfArgs;
	}*/
	
	public TypedValue(int o){
		this.i=o;
		this.type=Types.INTEGER;
	}
	
	public TypedValue(double e) {
		this.d=e;
		this.type=Types.DOUBLE;
	}

	public TypedValue(boolean b) {
		this.b=b;
		this.type=Types.BOOLEAN;
	}
	
	public TypedValue(int numOfArgs, Dimension t) {
		this.type=Types.FUNCTION;
		this.ft=t;
	}

	public TypedValue() {
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
	/*public String getString(){
		return s;
	}*/
	
	
	public void setI(int i) {
		this.i = i;
	}

	public void setD(double d) {
		this.d = d;
	}

	public void setB(boolean b) {
		this.b = b;
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
		default:
			break;
		}
		return null;
	}
	
	public boolean equals(TypedValue sec) throws Exception{
		if (this.type!=sec.type) return false;
		
		switch (type){
		case INTEGER:
			return (i==sec.i);
		case DOUBLE:
			return (d==sec.d);
		default:
			// TODO TypedValue equals for case FUNCTION
			throw new Exception("Забыл BOOLEAN и VECTOR ");
		}
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
			int r = right.getInt();
			if(r==0)
				throw new MyException("Целочисленное деление на 0.");
			i /= r;
			return this;
		case DOUBLE:
			double rd=right.getDouble();
			if(rd==0)
				throw new MyException("Вещественное деление на 0.");
			d /= rd;
			return this;
		case BOOLEAN:
		case VECTOR:
			throw new Exception("Забыл BOOLEAN и VECTOR");
		}
		return null;
	}

	public TypedValue degree(TypedValue degree) throws Exception {
		switch (type){
		case INTEGER:
			i = (int) Math.pow(i, degree.getInt());
			return this;
		case DOUBLE:
			d = Math.pow(d, degree.getDouble());
			return this;
		case BOOLEAN:
		case VECTOR:
			throw new Exception("Забыл BOOLEAN и VECTOR");
		}
		return null;
	}
	
	public TypedValue factorial() throws Exception {
		switch (type){
		case INTEGER:
			if (i < 0)
				throw new MyException("Факториал отрицательного числа не определён!");
			int t = i;
			i = 1;
			while (t != 0) {
				i *= t--;
			}
			
			return this;
		default:
			throw new MyException("Факториал определён только для INTEGER");
		}
		//return null;
	}
}