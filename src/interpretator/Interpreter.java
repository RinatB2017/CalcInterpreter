package interpretator;

import java.util.*;

import options.Options;
import types.TypedValue;
import main.OutputSystem;


public class Interpreter extends Env{
	public boolean skip=false; // Пропуск инструкций через себя без выполнения при if, for, do, while, ...
	private int depth=0;
		
	// Конструктор
	public Interpreter(Options options, HashMap<String, TypedValue> table, OutputSystem output) {
		super(output, table, options);
	}

	// incrDepth() и decrDepth() считают глубину вложенности относительно
	// той точки, где в Parser.block() был установлен Interpreter.skip=true.
	// При глубине 0 Interpreter.skip сбрасывается в false.
	public void incrDepth(){
		if(skip) depth++;
	}
	
	public void decrDepth(){
		if(skip) depth--;
		if(depth==0) skip=false;
	}
	
	/**
	 * Выполняет действие и возвращает результат.
	 * @param n Входной объект, реализующий интерфейс Returnable
	 * @return Вычисленный результат, либо null когда установлен фолаг skip
	 * @throws Exception
	 */
	public TypedValue exec(Returnable n) throws Exception{
		if(skip)
			return null;
		
		n.output=output;
		n.table=table;
		n.options=options;
		
		return lastResult=n.execute();
	}
	
	/**
	 * Выполняет действие без возврата результата.
	 * @param n Входной объект, реализующий интерфейс Voidable
	 * @throws Exception
	 */
	public void exec(Voidable n) throws Exception{
		if(skip)
			return;

		n.output=output;
		n.table=table;
		n.options=options;
		
		n.execute();
	}
	
	public TypedValue lastResult = new TypedValue(0);
	
	// Сброс таблицы переменных в исходное состояние
	public void resetTable() {
		table.clear();
		table.put("e", new TypedValue(Math.E));
		table.put("pi", new TypedValue(Math.PI));
		table.put("ans", lastResult);
	}
	
}
