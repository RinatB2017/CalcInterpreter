/*
 * Хранит настройки буфера и парсера,
 * предоставляет к ним доступ
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/*interface Terminals{};
	enum Terminals implements Terminals {ARGS_AUTO_END, AUTO_END, PRINT_TOKENS};
	enum Terminals implements Terminals {PRECISION, ERRORS, STRICTED, AUTO_PRINT, GREEDY_FUNC};
*/
class Option<T>{
	T defaultValue;
	
	Option(T defaultValue){
		this.defaultValue = defaultValue;
	}
}


@SuppressWarnings("rawtypes")
public class Options {
	HashMap<Terminal, Option> opts = new HashMap<Terminal, Option>(); // Ид : Опция
	HashMap<Terminal, Object> optsVals = new HashMap<Terminal, Object>(); // Ид : Значение
	
	// Конструктор
	@SuppressWarnings("unchecked")
	public Options(){
		this.add(Terminal.ARGS_AUTO_END, new Option(true)); // Автодобавление токена END в конце считанной последовательности
		this.add(Terminal.AUTO_END, new Option(false)); // Автодобавление токена END в конце считанной последовательности
		this.add(Terminal.PRINT_TOKENS, new Option(false)); // Вывод найденных токенов для просканированной строки
		
		this.add(Terminal.PRECISION, new Option(5)); // Отрицательная степень 10, используемая при сравнении малых значений методом doubleCompare()
		this.add(Terminal.ERRORS, new Option(0)); // Счётчик возникших ошибок
		this.add(Terminal.STRICTED, new Option(false)); // Запрет автосоздания переменных
		this.add(Terminal.AUTO_PRINT, new Option(true)); // Автоматический вывод значений выражений
		this.add(Terminal.GREEDY_FUNC, new Option(false)); // Жадные функции: скобки не обязательны, всё, что написано после имени функции и до токена END ; считается аргументом функции.
	}
		
	// Добавление опций
	private void add(Terminal id, Option o){
		opts.put(id, o);
		optsVals.put(id, o.defaultValue);
	}
	
	// Перезапись значений. Object должен совпадать с типом <T>@Option
	public void set(Terminal id, Object o) throws MyException{
		if(o.getClass()!=optsVals.get(id).getClass()){ // проверка типа
			//System.err.println("Неверный класс "+o.getClass()+", требуется "+optsVals.get(id).getClass());
			throw new MyException("Неверный класс "+o.getClass()+", требуется "+optsVals.get(id).getClass());
		}
		//System.err.println("класс "+o.getClass().getName()+", перезаписал класс "+optsVals.get(id).getClass().getName());
		optsVals.put(id, o);
	}
	
	// Перезапись значений для Parser.set()
	// Когда 2-й параметр Token, вызывается эта перегруженная функция, т. к. Token точнее чем Object 
	public void set(Terminal what, Token value) throws MyException{
		Terminal id = what;
		switch(value.name){
		case TRUE:
			set(id, true);
			break;
		case FALSE: // для будущей поддержки типов
			set(id, false);
			break;
		case NUMBER:
			set(id, Integer.parseInt(value.value));
			break;
		default:
			throw new MyException("неверный тип значения опции"); 
		}
		System.out.println("Установлена опция "+id.toString());
	}
	
	// Сброс
	public void reset(Terminal id){
		Option getted4getDefault = opts.get(id);
		optsVals.put(id, getted4getDefault.defaultValue);
		System.out.println("Сброшена опция "+id.toString());
	}
		
	public void resetAll(){
		Iterator<Entry<Terminal, Object>> it = optsVals.entrySet().iterator();
		while (it.hasNext()){
			Entry<Terminal, Object> li = it.next();
		    //System.out.println(""+li.getKey() + " " + li.getValue());
			reset(li.getKey());
		}
	}
	
	// Вывод Ид : Значение
	public void printAll(){
		Iterator<Entry<Terminal, Object>> it = optsVals.entrySet().iterator();
		while (it.hasNext()){
			Entry<Terminal, Object> li = it.next();
		    System.out.println(""+li.getKey() + " " + li.getValue());
		}
	}
	
	// Получение значения
	public int getInt(Terminal id){
		return (int) optsVals.get(id);
	}
	
	public double getDouble(Terminal id){
		return (double) optsVals.get(id);
	}
	
	public boolean getBoolean(Terminal id){
		return (boolean) optsVals.get(id);
	}
}
