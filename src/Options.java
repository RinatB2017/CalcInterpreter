import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

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
public class Options {
	private HashMap<Tag, Option> opts = new HashMap<Tag, Option>(); // Ид
																				// :
																				// Опция
	private HashMap<Tag, Object> optsVals = new HashMap<Tag, Object>(); // Ид
																					// :
																					// Значение
	private OutputSystem output;

	// Конструктор
	@SuppressWarnings("unchecked")
	public Options(OutputSystem out) {
		this.output = out;

		this.add(Tag.ARGS_AUTO_END, new Option(true)); // Автодобавление
															// токена END в
															// конце считанной
															// последовательности
		this.add(Tag.AUTO_END, new Option(true)); // Автодобавление токена
														// END в конце считанной
														// последовательности
		this.add(Tag.PRINT_TOKENS, new Option(false)); // Вывод найденных
															// токенов для
															// просканированной
															// строки

		this.add(Tag.PRECISION, new Option(5)); // Отрицательная степень
														// 10, используемая при
														// сравнении малых
														// значений методом
														// doubleCompare()
		this.add(Tag.ERRORS, new Option(0)); // Счётчик возникших ошибок
		this.add(Tag.STRICTED, new Option(false)); // Запрет автосоздания
														// переменных
		this.add(Tag.AUTO_PRINT, new Option(true)); // Автоматический вывод
															// значений
															// выражений
		this.add(Tag.GREEDY_FUNC, new Option(false)); // Жадные функции:
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
	private void add(Tag id, Option o) {
		opts.put(id, o);
		optsVals.put(id, o.defaultValue);
	}

	// Перезапись значений. Object должен совпадать с типом <T>@Option
	public void set(Tag id, Object o) throws MyException {
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
	public void set(Tag what, Token value) throws MyException {
		Tag id = what;
		switch (value.name) {
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
		output.addln("Установлена опция " + id.toString() + " в "
				+ optsVals.get(id));
	}

	// Сброс
	public void reset(Tag id) {
		Option getted4getDefault = opts.get(id);
		optsVals.put(id, getted4getDefault.defaultValue);
		output.addln("Сброшена опция " + id.toString() + " в "
				+ getted4getDefault.defaultValue);
	}

	public void resetAll() {
		Iterator<Entry<Tag, Object>> it = optsVals.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Tag, Object> li = it.next();
			// System.out.println(""+li.getKey() + " " + li.getValue());
			reset(li.getKey());
		}
	}

	// Вывод Ид : Значение
	public void printAll() {
		Iterator<Entry<Tag, Object>> it = optsVals.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Tag, Object> li = it.next();
			output.addln("" + li.getKey() + " " + li.getValue());
		}
	}

	// Получение значения
	public int getInt(Tag id) {
		return (int) optsVals.get(id);
	}

	public double getDouble(Tag id) {
		return (double) optsVals.get(id);
	}

	public boolean getBoolean(Tag id) {
		return (boolean) optsVals.get(id);
	}
}
