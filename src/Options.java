import java.util.HashMap;

//enum Type{INT, DOUBLE, BOOL};
enum Id{PRECISION, TABLE, ERRORS, STRICTED, LEXER_AUTO_END, LEXER_PRINT_TOKENS, AUTO_PRINT, GREEDY_FUNC};

class Option<T>{
	//Type type;
	T defaultValue;
	
	Option(/*Type type,*/ T defaultValue){
		this.defaultValue = defaultValue;
		//this.type = type;
	}
}

public class Options {
	
	HashMap<Id, Option> opts = new HashMap<Id, Option>();
	HashMap<Id, Object> optsVals = new HashMap<Id, Object>(); 
	
	public Options(){
		this.add(Id.PRECISION, new Option(/*Type.INT,*/ 0.0001));
		//this.add(Id.TABLE, new Option(/*Type.INT, */2));
		this.add(Id.ERRORS, new Option(/*Type.INT,*/ 0));
		this.add(Id.STRICTED, new Option(/*Type.BOOL,*/ false));
		this.add(Id.LEXER_AUTO_END, new Option(/*Type.BOOL,*/ true));
		this.add(Id.LEXER_PRINT_TOKENS, new Option(/*Type.BOOL,*/ false));
		this.add(Id.AUTO_PRINT, new Option(/*Type.BOOL,*/ true));
		this.add(Id.GREEDY_FUNC, new Option(/*Type.BOOL,*/ false));
	}
		
	// Добавление опций
	void add(Id id, Option o){
		opts.put(id, o);
		optsVals.put(id, o.defaultValue);
	}
	
	// Перезапись значений
	void put(Id id, Object o){
		optsVals.put(id, o);
	}
	
	void reset(Id id){
		Option getted4getDefault = opts.get(id);
		optsVals.put(id, getted4getDefault.defaultValue);
	}
	
	// Получение значения
	int getInt(Id id){
		return (int) optsVals.get(id);
	}
	
	double getDouble(Id id){
		return (double) optsVals.get(id);
	}
	
	boolean getBoolean(Id id){
		return (boolean) optsVals.get(id);
	}
}
