package types;

import inter.Interpreter;
import inter.EnvSetable;
import inter.EnvSetableStatic;
import inter.voidables.TablePut;

import java.util.HashMap;

import exceptions.MyException;
import options.OptId;
import options.Options;

/**
 * Обеспечиват поддержку типов:
 * простых (INTEGER, DOUBLE, BOOLEAN)
 * и сложных (VECTOR).
 * @author Ник
 *
 */
public class TypedValue extends EnvSetableStatic implements Cloneable{
	private static Interpreter inter;
	
	public static void staticInit(Interpreter inter2) {
		inter=inter2;
		table=inter.table;
		options=inter.options;
		output=inter.output;
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
	
	
	public void tryMaximizeTo(Types requiredType) throws Exception {
		int t1 = Types.get(this.type); // меньший
		int t2 = Types.get(requiredType); // больший
		if(t1>t2) throw new MyException("Не удаётся повысить "+this.type+" до "+requiredType);
		toType(requiredType);
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
	
	public boolean equals(TypedValue sec) throws Exception{
		if (this.type!=sec.type) return false;
		
		switch (type){
		case BOOLEAN:
			return (b==sec.b);
		case INTEGER:
			return (i==sec.i);
		case DOUBLE:
			return (d==sec.d);
		default:
			throw new Exception("Забыл equals() для "+type);
		}
	}

	// Меняет знак. 
	public TypedValue negative() throws Exception {
		switch (type){
		case BOOLEAN:
			b=!b;
			return this;
		case INTEGER:
			i = -i;
			return this;
		case DOUBLE:
			d = -d;
			return this;
		default:
			throw new Exception("Забыл negative() для "+type);
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
		default:
			throw new Exception("Забыл plus() для "+type);
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
		default:
			throw new Exception("Забыл minus() для "+type);
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
		default:
			throw new Exception("Забыл mul() для "+type);		
		}
	}
	
	public TypedValue div(TypedValue right) throws Exception {
		switch (type){
		case INTEGER:
			int r = right.getInt();
			if(r==0)
				throw new MyException("Целочисленное деление на 0.");
			inter.exec(new TablePut("modulo", new TypedValue(i%r))); // добавляем остаток от деления
			i /= r;
			return this;
		case DOUBLE:
			double rd=right.getDouble();
			if(rd==0)
				throw new MyException("Вещественное деление на 0.");
			d /= rd;
			return this;
		default:
			throw new Exception("Забыл div() для "+type);		
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
		default:
			throw new Exception("Забыл degree() для "+type);		
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
