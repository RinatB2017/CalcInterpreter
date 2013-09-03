package inter;

import types.TypedValue;

public abstract class Returnable extends SetableEnv{
	public abstract TypedValue execute() throws Exception;
}
