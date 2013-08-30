package interpreter;

public abstract class Voidable extends SetableEnv {
	public void execute() throws Exception {throw new Exception();};
}
