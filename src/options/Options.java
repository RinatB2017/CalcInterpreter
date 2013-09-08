package options;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import lexer.BooleanT;
import lexer.IntegerT;
import lexer.Token;
import main.MyException;
import main.OutputSystem;

/**
 * Класс для хранения значения по умолчанию
 * */
class Option<T> {
	T defaultValue;

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
	private HashMap<OptId, Option> opts = new HashMap<OptId, Option>(); // Ид
																				// :
																				// Опция
	private HashMap<OptId, Object> optsVals = new HashMap<OptId, Object>(); // Ид
																					// :
																					// Значение
	private OutputSystem output;

	// Конструктор
	@SuppressWarnings("unchecked")
	public Options(OutputSystem out) {
		this.output = out;

		this.add(OptId.ARGS_AUTO_END, new Option(true)); // Автодобавление
															// токена END в
															// конце считанной
															// последовательности
		this.add(OptId.AUTO_END, new Option(true)); // Автодобавление токена
														// END в конце считанной
														// последовательности
		this.add(OptId.PRINT_TOKENS, new Option(false)); // Вывод найденных
															// токенов для
															// просканированной
															// строки

		this.add(OptId.PRECISION, new Option(5)); // Отрицательная степень
														// 10, используемая при
														// сравнении малых
														// значений методом
														// doubleCompare()
		this.add(OptId.ERRORS, new Option(0)); // Счётчик возникших ошибок
		this.add(OptId.STRICTED, new Option(false)); // Запрет автосоздания
														// переменных
		this.add(OptId.AUTO_PRINT, new Option(true)); // Автоматический вывод
															// значений
															// выражений
		this.add(OptId.GREEDY_FUNC, new Option(false)); // Жадные функции:
															// скобки не
															// обязательны, всё,
															// что написано
															// после имени
															// функции и до
															// токена END ;
															// считается
															// аргументом
															// функции.
	}

	// Добавление опций
	private void add(OptId id, Option o) {
		opts.put(id, o);
		optsVals.put(id, o.defaultValue);
	}

	// Перезапись значений. Object должен совпадать с типом <T>@Option
	public void set(OptId id, Object o) throws MyException {
		if (o.getClass() != optsVals.get(id).getClass()) { // проверка типа
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
	public void set(OptId what, Token value) throws MyException {
		OptId id = what;
		switch (value.name) {
		case BOOLEAN:
			set(id, ((BooleanT) value).value ? true : false);
			break;
		case INTEGER:
			set(id, ((IntegerT) value).value);
			break;
		default:
			throw new MyException("неверный тип значения опции");
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

	// Получение значения
	public int getInt(OptId id) {
		return (int) optsVals.get(id);
	}

	public double getDouble(OptId id) {
		return (double) optsVals.get(id);
	}

	public boolean getBoolean(OptId id) {
		return (boolean) optsVals.get(id);
	}
}
