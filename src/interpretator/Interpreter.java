package interpretator;

import java.util.*;
import types.TypedValue;
import main.OutputSystem;


public class Interpreter {
	public boolean skip=false; // Пропуск инструкций через себя без выполнения при if, for, do, while, ...
	private int depth=0;
	
	public OutputSystem output;
	public HashMap<String, TypedValue> table; // Таблица переменных
		
	// Конструктор
	public Interpreter(OutputSystem output ) {
		table = new HashMap<String, TypedValue>();
		this.output = output;
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
		
		return n.execute();
	}
	
	/**
	 * Выполняет действие без возврата результата.
	 * @param n Входной объект, реализующий интерфейс Voidable
	 * @throws Exception
	 */
	public void exec(Voidable n) throws Exception{
		if(skip)
			return;
		
		n.execute();
	}
	
}
