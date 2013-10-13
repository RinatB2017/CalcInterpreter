package options;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import exceptions.MyException;
import types.func.def.Dimension;
import lexer.BooleanT;
import lexer.IntegerT;
import lexer.Token;
import main.OutputSystem;
import static types.func.def.Dimension.checkNoDimensionless;

/**
 * Класс для хранения значения по умолчанию
 * */
class Option<T> {
	T defaultValue;
	// TODO допустимые значения(тоже T) и их проверку
	Option(T defaultValue) {
		this.defaultValue = defaultValue;
	}
}

/**
 * Хранит настройки буфера и парсера, предоставляет к ним доступ: получение
 * значения get*(); установка нового значения set*(); сброс значения to default
 * reset*()
 * */

@SuppressWarnings("rawtypes")
public final class Options {
	// Ид : Опция
	private HashMap<OptId, Option> opts = new HashMap<OptId, Option>();
	
	// Ид : Значение
	private HashMap<OptId, Object> optsVals = new HashMap<OptId, Object>();
	
	private OutputSystem output;

	// Конструктор
	@SuppressWarnings("unchecked")
	public Options(OutputSystem out) {
		this.output = out;
		
		// Автодобавление токена END в конце считанной последовательности
		this.add(OptId.ARGS_AUTO_END, new Option(true));
		
		// Автодобавление токена END в конце считанной последовательности
		this.add(OptId.AUTO_END, new Option(true));
		
		// Вывод найденных токенов для просканированной строки
		this.add(OptId.PRINT_TOKENS, new Option(false));

		// Отрицательная степень 10, используемая при сравнении малых значений
		//методом doubleCompare()
		this.add(OptId.PRECISION, new Option(5));
		
		// Счётчик возникших ошибок
		this.add(OptId.ERRORS, new Option(0));
		
		// Запрет автосоздания переменных
		this.add(OptId.STRICTED, new Option(true));
		
		// Автоматический вывод значений выражений
		this.add(OptId.AUTO_PRINT, new Option(true));
		
		// Жадные функции:
		// скобки не обязательны, всё, что написано после имени функции и до
		// токена END ;
		// считается аргументом функции.
		this.add(OptId.GREEDY_FUNC, new Option(false));
		
		// Размерность по умолчанию
		this.add(OptId.DIM, new Option(Dimension.DEG));
	}

	// Добавление опций
	private void add(OptId id, Option o) {
		opts.put(id, o);
		optsVals.put(id, o.defaultValue);
	}

	// Перезапись значений. Object должен совпадать с типом <T>@Option
	public void set(OptId id, Object o) throws Exception {
		Object defaultObj = optsVals.get(id);
		if(defaultObj==null){
			throw new Exception("Нет значения по умолчанию для "+id);
		}
		if (o.getClass() != defaultObj.getClass()) { // проверка типа
			// System.err.println("Неверный класс "+o.getClass()+", требуется "+optsVals.get(id).getClass());
			throw new MyException("Неверный класс " + o.getClass()
					+ ", требуется " + optsVals.get(id).getClass());
		}
		// System.err.println("класс "+o.getClass().getName()+", перезаписал класс "+optsVals.get(id).getClass().getName());
		optsVals.put(id, o);
	}

	// Перезапись значений для Parser.set()
	// Когда 2-й параметр Token, вызывается эта перегруженная функция, т. к.
	// Token точнее чем Object
	public void set(OptId what, Token value) throws Exception {
		OptId id = what;
		switch (value.tag) {
		case BOOLEAN:
			set(id, ((BooleanT) value).value ? true : false);
			break;
		case INTEGER:
			set(id, ((IntegerT) value).value);
			break;
		case NAME:
			if(id==OptId.DIM){
				Dimension dim=dimname(value.toString());
				checkNoDimensionless(dim,false);
				set(id, dim);
			}
			break;
		default:
			throw new MyException("неверный тип значения опции: "+value.tag);
		}
		output.addln("Установлена опция " + id.toString() + " в "
				+ optsVals.get(id));
	}

	// Сброс
	public void reset(OptId id) {
		Option getted4getDefault = opts.get(id);
		optsVals.put(id, getted4getDefault.defaultValue);
		output.addln("Сброшена опция " + id.toString() + " в "
				+ getted4getDefault.defaultValue);
	}

	public void resetAll() {
		Iterator<Entry<OptId, Object>> it = optsVals.entrySet().iterator();
		while (it.hasNext()) {
			Entry<OptId, Object> li = it.next();
			// System.out.println(""+li.getKey() + " " + li.getValue());
			reset(li.getKey());
		}
	}

	// Вывод Ид : Значение
	public void printAll() {
		Iterator<Entry<OptId, Object>> it = optsVals.entrySet().iterator();
		while (it.hasNext()) {
			Entry<OptId, Object> li = it.next();
			output.addln("" + li.getKey() + " " + li.getValue());
		}
	}
	
	// Преобразует строку в OptId
	public static OptId setname(String t) throws MyException {
		for(OptId id: OptId.values()){
			//System.out.println("trying "+i.toString());
			if(t.toLowerCase().equals(id.toString().toLowerCase())){
				//System.out.println("match on "+i.toString());
				return id;
			}
		}
		throw new MyException("Нет такой опции "+t+"; посмотреть список возможных опций можно вызовом state");
	}
	
	public static Dimension dimname(String t) throws MyException{
		for(Dimension id: Dimension.values()){
			if(t.toLowerCase().equals(id.toString().toLowerCase())){
				return id;
			}
		}
		throw new MyException("Нет такого режима конвертации "+t+".");
	}

	// Получение значения
	public int getInt(OptId id) {
		return (Integer) optsVals.get(id);
	}

	public double getDouble(OptId id) {
		return (Double) optsVals.get(id);
	}

	public boolean getBoolean(OptId id) {
		return (Boolean) optsVals.get(id);
	}

	public Dimension getDimension() {
		return (Dimension) optsVals.get(OptId.DIM);
	}
}
