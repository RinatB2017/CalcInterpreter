package inter.voidables;

import inter.Voidable;

public class State extends Voidable{
	public State(){
	}
	
	@Override
	public void execute() throws Exception {
		output.addln("Текущее состояние:\nПеременных " + table.size());
		options.printAll();
	}

}
