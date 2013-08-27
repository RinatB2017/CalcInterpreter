package interpretator;

import java.util.*;
import main.OutputSystem;


public class Interpreter {
	public boolean skip=false; // Пропуск инструкций через себя без выполнения при if, for, do, while, ...
	private int depth=0;
	
	public OutputSystem output;
	public HashMap<String, TypedValue> table; // Таблица переменных
	
	// Конструктор
	public Interpreter(OutputSystem output) {
		table = new HashMap<String, TypedValue>();
		this.output = output;
	}

	public void incrDepth(){
		if(skip) depth++;
	}
	
	public void decrDepth(){
		if(skip) depth--;
		if(depth==0) skip=false;
	}
	
	public void printAll(){
		/*
		if (table.isEmpty()) {
			output.addln("table is empty!");
		} else {
			output.addln("[table]");
			Iterator<Entry<String, TypedValue>> it = table.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, TypedValue> li = it.next();
				output.addln("" + li.getKey() + " " + li.getValue());
			}
			output.addln("[/table]");
		}
		*/
	}
	
	public void print(TypedValue v){
		//output.finishAppend("= " + v);
	}
	/*public void add(){
		
	}
	public void del(){
		
	}
	public void reset(){
		
	}/
	//public void set(){
		/*
		setname =
				ARGS_AUTO_END | AUTO_END | PRINT_TOKENS |
				PRECISION | ERRORS | STRICTED | AUTO_PRINT | GREEDY_FUNC
		*/
		
	//}
	/*public void help(){
		
	}
	public void state(){
		
	}*/
	
	
	public TypedValue plus(TypedValue left, TypedValue right) throws Exception{
		if(skip) return null;
		return left.plus(right);
	}
	
	public TypedValue minus(TypedValue left, TypedValue right) throws Exception{
		if(skip) return null;
		return left.minus(right);
	}
	
	public TypedValue mul(TypedValue left, TypedValue right) throws Exception{
		if(skip) return null;
		return left.mul(right);
	}
	
	public TypedValue div(TypedValue left, TypedValue right) throws Exception{
		if(skip) return null;
		return left.div(right);
	}
	
	public TypedValue power(){
		return null;
	}
	public TypedValue factorial(){
		return null;
	}
	public TypedValue of_radian(){
		return null;
	}
}
