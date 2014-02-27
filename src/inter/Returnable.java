package inter;

import java.util.HashMap;

import main.OutputSystem;
import options.Options;
import types.*;

public abstract class Returnable extends EnvSetable{
	protected TypedValue left;
	protected TypedValue right;
	
	public abstract TypedValue execute() throws Exception;
	
	public void convert() throws Exception {
		if(left==null || right==null) return;
		
		Types maxtype;
		maxtype = TypedValue.max(left.getType(), right.getType());
		left.toType(maxtype);
		right.toType(maxtype);
	}
	
	public void lateInit(Options options, HashMap<String, TypedValue> table, OutputSystem output){
		this.output=output;
		this.table=table;
		this.options=options;
	}
	
	// Конструирует Returnable одного (максимального) типа
	public Returnable(TypedValue left, TypedValue right) throws Exception{
		this.left=left;
		this.right=right;
		
		convert();
	}
	
	public Returnable(){
		this.left=null;
		this.right=null;
	}
}
