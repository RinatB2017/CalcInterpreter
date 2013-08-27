package interpretator;

import types.TypedValue;

public interface Returnable {
	public TypedValue execute() throws Exception;
}
