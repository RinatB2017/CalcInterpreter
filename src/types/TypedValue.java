package types;

import options.OptId;
import options.Options;
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
	private static Options options;
	
	public static void setOptions(Options options2){
		options=options2;
	}
	
	private int i;
	private double d;
	private boolean b;
	private MathVector v;
	private Function f;
	private Types type;
	private Object dimension; // километры, часы, килограммы и т. д.
	
    public Types getType() {
		return type;
	}

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
	
	// Конструкторы
	public TypedValue(int o){
		setInt(o);
	}
	
	public TypedValue(double e) {
		setDouble(e);
	}

	public TypedValue(boolean b) {
		setBoolean(b);
	}
	
	public TypedValue(Function f) {
		this.type=Types.FUNCTION;
		this.f=f;
	}

	public TypedValue() {}
	
	
	public static Types max(Types left, Types right) throws MyException {
		// Запрет возврата максимального типа для INTEGER и DOUBLE
		if(left==Types.BOOLEAN||right==Types.BOOLEAN) throw new MyException("В преобразовании участвует BOOLEAN");
		
		int t1 = Types.get(left);
		int t2 = Types.get(right);
		int t3;
		t3 = (t1>t2) ? t1 : t2;
		
		Types ret;
		ret=Types.set(t3);
		return ret;
	}
	
	// Преобразует в тип to
	public void toType(Types to) throws Exception{
		switch(to){
		case INTEGER:
			toInt();
			break;
		case DOUBLE:
			toDouble();
			break;
		case BOOLEAN:
			toBoolean();
			break;
		default:
			throw new Exception("Не реализовано преобразование "+this.type+" в "+to);
		}
	}

	void toInt() throws Exception{
		switch (this.type){
		case INTEGER:
			return;
		case DOUBLE:
			type=Types.INTEGER;
			i=(int)d;
			break;
		case BOOLEAN:
			type=Types.INTEGER;
			i=b?1:0;
			break;
		default:
			throw new Exception(" Не реализовано преобразование "+this.type);
		}
	}
	
	void toDouble() throws Exception{
		switch (this.type){
		case INTEGER:
			type=Types.DOUBLE;
			d=(double)i;
			break;
		case DOUBLE:
			return;
		case BOOLEAN:
			type=Types.DOUBLE;
			d=b?1:0;
			break;
		default:
			throw new Exception(" Не реализовано преобразование "+this.type);
		}
	}
	
	void toBoolean() throws Exception{
		switch (this.type){
		case INTEGER:
			type=Types.BOOLEAN;
			b=(i!=0)?true:false;
			break;
		case DOUBLE:
			type=Types.BOOLEAN;
			b=(doubleCompare(d, 0))?true:false;
			break;
		case BOOLEAN:
			return;
		default:
			throw new Exception(" Не реализовано преобразование "+this.type);
		}
	}
	
	
	static boolean doubleCompare(double a, double b) {
		if (Math.abs(a - b) < 1.0 / Math.pow(10, options.getInt(OptId.PRECISION)))
			return true;
		return false;
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
	public Function getFunction(){
		return f;
	}
	
	
	public void setInt(int i) {
		type=Types.INTEGER;
		this.i = i;
	}

	public void setDouble(double d) {
		type=Types.DOUBLE;
		this.d = d;
	}

	public void setBoolean(boolean b) {
		type=Types.BOOLEAN;
		this.b = b;
	}

	@Override
	public String toString(){
		switch (type){
		case INTEGER:
			return String.valueOf(i);
		case DOUBLE:
			return String.valueOf(d);
		case BOOLEAN:
			return String.valueOf(b);
		default:
			break;
		}
		return null;
	}
	
	public String toStringForPrintTable(){
		String s = type.toString();
		switch (type){
		case INTEGER:
			return s+" "+i;
		case DOUBLE:
			return s+" "+d;
		case BOOLEAN:
			return s+" "+b;
		case FUNCTION:
			return f.toString();
		default:
			break;
		}
		return null;
	}
	
	public String getFuncArgs(){
		return f.getFuncArgs();
	}
	
	public String getFuncRet(){
		return f.getFuncRet();
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

	// Меняет знак. 
	public TypedValue negative() throws Exception {
		switch (type){
		case INTEGER:
			i = -i;
			return this;
		case DOUBLE:
			d = -d;
			return this;
		case BOOLEAN:
			b=!b;
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
		default:
			throw new Exception("Забыл BOOLEAN и VECTOR");
		}
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
		default:
			throw new Exception("Забыл BOOLEAN и VECTOR");
		}
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
		default:
			throw new Exception("Забыл BOOLEAN и VECTOR");
		}
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
		default:
			throw new Exception("Забыл BOOLEAN и VECTOR");
		}
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
		default:
			throw new Exception("Забыл BOOLEAN и VECTOR");
		}
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
	}
}
