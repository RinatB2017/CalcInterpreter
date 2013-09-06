package inter;

import java.util.HashMap;

import main.OutputSystem;
import options.Options;
import types.TypedValue;

public abstract class Returnable extends SetableEnv{
	public abstract TypedValue execute() throws Exception;
	
	public void lateInit(Options options, HashMap<String, TypedValue> table, OutputSystem output){
		this.output=output;
		this.table=table;
		this.options=options;
	}
}
