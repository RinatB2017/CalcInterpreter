package inter.voidables;

import static options.Options.setname;
import options.OptId;
import lexer.*;
import inter.Voidable;

public class Set extends Voidable{
	String whats;
	Token t;
	public Set(String whats, Token currTok){
		this.whats=whats;
		this.t=currTok;
	}
	
	@Override
	public void execute() throws Exception {
		OptId what = setname(whats);
		options.set(what, t);
	}
}
