package types.func.def;

import java.util.ArrayList;

public class Definition {
	public ArrayList <Argument> args;
	public Argument ret;
	
	public Argument getArg(int i){
		return args.get(i);
	}
	
	public Definition(ArrayList<Argument> args, Argument ret) {
		this.args = args;
		this.ret = ret;
	}
}
