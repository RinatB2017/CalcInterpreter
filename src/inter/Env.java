package inter;

import java.util.HashMap;

import options.Options;
import main.OutputSystem;
import types.TypedValue;

public abstract class Env {
	// final
	public final OutputSystem output;
	public final HashMap<String, TypedValue> table;
	public final Options options;
	
	public Env(OutputSystem output, HashMap<String, TypedValue> table, Options options){
		this.output=output;
		this.table=table;
		this.options=options;
	}
}
