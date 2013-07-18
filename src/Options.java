/*
 * Хранит настройки буфера и парсера,
 * предоставляет к ним доступ
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

interface Id{};
	enum BufferOpts implements Id {AUTO_END, PRINT_TOKENS};
	enum ParserOpts implements Id {PRECISION, ERRORS, STRICTED, AUTO_PRINT, GREEDY_FUNC};

/*class c0{};
class c1 extends c0{};

enum e0{};
enum e1 extends e0{}; // lulz
*/
	
class Option<T>{
	T defaultValue;
	
	Option(T defaultValue){
		this.defaultValue = defaultValue;
	}
}


@SuppressWarnings("rawtypes")
public class Options {
	HashMap<Id, Option> opts = new HashMap<Id, Option>(); // Ид : Опция
	HashMap<Id, Object> optsVals = new HashMap<Id, Object>(); // Ид : Значение
	
	// Конструктор
	@SuppressWarnings("unchecked")
	public Options(){
		this.add(BufferOpts.AUTO_END, new Option(false)); // Автодобавление токена END в конце считанной последовательности
		this.add(BufferOpts.PRINT_TOKENS, new Option(true)); // Вывод найденных токенов для просканированной строки
		
		this.add(ParserOpts.PRECISION, new Option(5)); // Отрицательная степень 10, используемая при сравнении малых значений методом doubleCompare()
		this.add(ParserOpts.ERRORS, new Option(0)); // Счётчик возникших ошибок
		this.add(ParserOpts.STRICTED, new Option(false)); // Запрет автосоздания переменных
		this.add(ParserOpts.AUTO_PRINT, new Option(true)); // Автоматический вывод значений выражений
		this.add(ParserOpts.GREEDY_FUNC, new Option(false)); // Жадные функции: скобки не обязательны, всё, что написано после имени функции и до токена END ; считается аргументом функции.
	}
		
	// Добавление опций
	private void add(Id id, Option o){
		opts.put(id, o);
		optsVals.put(id, o.defaultValue);
	}
	
	// Перезапись значений
	void put(Id id, Object o){
		optsVals.put(id, o);
	}
	
	// Сброс
	void reset(Id id){
		Option getted4getDefault = opts.get(id);
		optsVals.put(id, getted4getDefault.defaultValue);
	}
	
	void resetAll(){
		Iterator<Entry<Id, Object>> it = optsVals.entrySet().iterator();
		while (it.hasNext()){
			Entry<Id, Object> li = it.next();
		    //System.out.println(""+li.getKey() + " " + li.getValue());
			reset(li.getKey());
		}
	}
	
	// Вывод Ид : Значение
	void printAll(){
		Iterator<Entry<Id, Object>> it = optsVals.entrySet().iterator();
		while (it.hasNext()){
			Entry<Id, Object> li = it.next();
		    System.out.println(""+li.getKey() + " " + li.getValue());
		}
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
