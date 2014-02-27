package types.func.def;

import java.util.ArrayList;

public class Definition {
	// Список типов и размерностей аргументов
	public ArrayList <Argument> args;
	
	// Тип и размерность возвращаемого значения
	public Argument ret;
	
	public Argument getArg(int i){
		return args.get(i);
	}
	
	public Definition(ArrayList<Argument> args, Argument ret) {
		this.args = args;
		this.ret = ret;
	}
}
