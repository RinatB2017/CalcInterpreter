package interpreter;

import java.util.HashMap;

import options.Options;
import main.OutputSystem;
import types.TypedValue;

public abstract class SetableEnv {
	public OutputSystem output;
	public HashMap<String, TypedValue> table;
	public Options options;
}
