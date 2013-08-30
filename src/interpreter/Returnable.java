package interpreter;

import types.TypedValue;

public abstract class Returnable extends SetableEnv{
	public TypedValue execute() throws Exception { throw new Exception(); }
}
