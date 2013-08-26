package inter;
import lexer.Tag;


public class Interpreter {
	public boolean isNeedExecute = true;
	public boolean skip=false;
	
	/*public void print(){
		
	}
	public void add(){
		
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
	
	public void voidFunc(){
	/*
	print
|	add
|	del
|	reset
|	set
|	help
|	state*/
	}
	public TypedValue plus(TypedValue left, TypedValue right) throws Exception{
		if(skip) return left;
		return left.plus(right);
	}
	
	public TypedValue minus(TypedValue left, TypedValue right) throws Exception{
		if(skip) return left;
		return left.minus(right);
	}
	
	public TypedValue mul(TypedValue left, TypedValue right) throws Exception{
		if(skip) return left;
		return left.mul(right);
	}
	
	public TypedValue div(TypedValue left, TypedValue right) throws Exception{
		if(skip) return left;
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
