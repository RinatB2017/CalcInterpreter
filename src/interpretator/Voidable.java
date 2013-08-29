package interpretator;

public abstract class Voidable extends SetableEnv {
	public void execute() throws Exception {throw new Exception();};
}
