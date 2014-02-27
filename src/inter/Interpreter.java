package inter;

import inter.voidables.Reset;
import inter.voidables.TablePut;

import java.util.*;

import options.*;
import types.TypedValue;
import main.OutputSystem;


public final class Interpreter extends Env{
	// protected либо default для видимости в Parser
	protected boolean skip=false; // Пропуск инструкций через себя без выполнения при if, for, do, while, ...
	
	private int depth=0;
		
	// Конструктор
	public Interpreter(Options options, HashMap<String, TypedValue> table, OutputSystem output) {
		super(output, table, options);
		
		TypedValue.staticInit(this);
		
		try {
			this.exec(new Reset(null)); // reset all
			output.clear(); // Очищаем сообщения о сбросе
		} catch (Exception e) {
			System.out.println("error while reset all in Interpreter construstor");
			e.printStackTrace();
		}
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
		
		// Доинициализируем объект значениями из суперкласса Env здесь для сокращения числа аргументов конструктора наследника Returnable
		n.lateInit(options, table, output);
		
		lastResult=n.execute();
		
		addAns(lastResult);
		
		return lastResult;
	}
	
	/**
	 * Выполняет действие без возврата результата.
	 * @param n Входной объект, реализующий интерфейс Voidable
	 * @throws Exception
	 */
	public void exec(Voidable n) throws Exception{
		if(skip)
			return;

		// Доинициализируем объект значениями из суперкласса Env здесь для сокращения числа аргументов конструктора наследника Voidable
		n.lateInit(options, table, output);
		
		n.execute();
	}
	
	public TypedValue lastResult = new TypedValue(0);
	
	// Добавляет последнее значение
	public void addAns(TypedValue left) throws Exception{
		if(skip) return;
			
		TypedValue ans = table.get("ans"); 
		if(ans==null){
			exec(new TablePut("ans", left));
		}else if(!left.equals(ans)){
			exec(new TablePut("ans", left));
		}
	}
}
