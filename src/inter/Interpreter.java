package inter;

import java.util.*;

import options.Options;
import types.TypedValue;
import types.func.def.Dimension;
import main.OutputSystem;


public final class Interpreter extends Env{
	public boolean skip=false; // Пропуск инструкций через себя без выполнения при if, for, do, while, ...
	private int depth=0;
		
	// Конструктор
	public Interpreter(Options options, HashMap<String, TypedValue> table, OutputSystem output) {
		super(output, table, options);
		resetTable();
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
		
		// Доинициализируем объект значениями из суперкласса Env здесь для сокращения числа аргументов конструктора
		n.output=output;
		n.table=table;
		n.options=options;
		
		lastResult=n.execute();
		table.put("ans", lastResult);
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

		// Доинициализируем объект значениями из суперкласса Env здесь для сокращения числа аргументов конструктора
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
		
		// TODO переработать
		table.put("sin", new TypedValue(1, Dimension.RADIAN)); // TODO 1 доб-ть арг-т: FuncPrim(BuiltIn.SIN)
		table.put("cos", new TypedValue(1, Dimension.RADIAN));
		table.put("arcsin", new TypedValue(1, Dimension.RADIAN));
		table.put("arccos", new TypedValue(1, Dimension.RADIAN));
		table.put("log", new TypedValue(1, Dimension.DIMENSIONLESS));
		table.put("pow", new TypedValue(2, Dimension.DIMENSIONLESS));
	}
	
}
