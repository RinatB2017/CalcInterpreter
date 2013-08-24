import java.util.*;
import java.util.Map.Entry;

/**
 * Парсер пытается выполнить ArrayList токенов, которые по одному получает из
 * лексера при вызове Parser.getToken(). При ошибке парсер вызывает метод
 * Parser.error(), который генерирует исключение MyException, перехватив которое
 * можно продолжать работу.
 * 
 * Переменные можно создавать вручную и инициализировать, например aabc = 9.3;
 * Если переменная не существует при попытке обращения к ней, то она
 * автоматически создаётся со значением 0.0, см. prim().
 * 
 * @see Parser#getToken()
 * */
public class Parser {
	private Token currTok = null; // текущий обрабатываемый токен, изменяется
									// методом getToken()
	private Buffer buf = null;
	private Options options = null;
	private HashMap<String, TypedValue> table; // Таблица переменных
	private OutputSystem output;
	private Interpreter interpreter;

	// Конструктор
	public Parser(Buffer buf, Options options, OutputSystem output) {
		table = new HashMap<String, TypedValue>();
		resetTable();
		this.buf = buf;
		this.options = options;
		this.output = output;
	}

	/*private double numberValue;
	private String stringValue;
	*/
	private boolean echoPrint = false; // Используется для эхопечати токенов при
										// их чтении методом getToken() при
										// void_func() : print

	/**
	 * Получает очередной токен -> currTok, изменяет numbeValue и strinValue
	 * 
	 * @see Buffer#getToken()
	 */
	private Tag getToken() throws Exception {
		if (echoPrint && currTok.name != Tag.END)
			output.append(currTok.toString() + ' '); // Печать предыдущего считанного
												// токена, т. к. в exprList()
												// токен уже считан до включения
												// флага echoPrint

		currTok = buf.getToken();

		/*if (currTok.name == Tag.INTEGER)
			numberValue = Double.parseDouble(currTok.value);
		if (currTok.name == Tag.NAME)
			stringValue = currTok.value;
		*/
		return currTok.name;
	}

	// if(currTok.name!=Terminal.LP) error("Ожидается (");
	void match(Tag t) throws Exception {
		if (currTok.name != t)
			error("ожидается терминал " + t);
	}

	/**
	 * Главный метод - список выражений - с него начинается парсинг
	 * 
	 * @throws Exception
	 */
	public void program() throws Exception {
		boolean get = true; // нужно ли считывать токен в самом начале
		while (true) {
			if (get)
				getToken();
			get = true;

			switch (currTok.name) {
			case EXIT:
				return;
			default:
				output.clear();
				get = instr();
				table.put("ans", lastResult);
				output.flush();
			}
		}
	}

	private boolean instr() throws Exception {
		echoPrint = false; // Отменяем эхопечать токенов, если она не была
							// отменена из-за вызова error() -> MyException

		switch (currTok.name) {
		case END:
			break;
		case IF:
			return (if_());
		default:
			if (voidFunc()) {
			} else { // expr или любой другой символ, который будет оставлен в
						// currTok
				if (options.getBoolean(Id.AUTO_PRINT))
					echoPrint = true; // ... включаем эхо-печать в
										// this.getToken() ...
				TypedValue v = expr(false);
				if (options.getBoolean(Id.AUTO_PRINT))
					output.finishAppend("= " + v);
				echoPrint = false; // ... а теперь выключаем
			}
			match(Tag.END);
		}// switch

		return true;
	}

	// if-else
	private boolean if_() throws Exception {
		getToken();
		match(Tag.LP); // '('

		TypedValue condition = expr(true);
		// expr отставляет не обработанный токен в curr_tok.name, здесь мы его
		// анализируем
		match(Tag.RP); // ')'

		if (!doubleCompare(condition, 0)) { // если condition==true
			block();
		} else { // если condition==false
			skipBlock(); // пропусить true brach {}
		}

		getToken(); // считываем очередной токен

		if (currTok.name == Tag.ELSE) {
			if (doubleCompare(condition, 0)) {
				block();
			} else {
				skipBlock(); // пропусить false brach {}
			}
		} else { // если после if { expr_list } идёт не else
			return false; // тогда в следующией итерации цикла в program() мы
							// просмотрим уже считанный выше токен, а не будем
							// считывать новый
		}
		return true;
	}

	// { expr_list }
	private void block() throws Exception {
		// TODO boolean fbrackets = true; после того как уберу skipBlock()
		getToken();
		match(Tag.LF); // '{'

		boolean get = true; // нужно ли считывать токен в самом начале
		do {
			if (get)
				getToken();

			switch (currTok.name) {
			case RF:
				return; // '}'
			default:
				get = true;
				get = instr();
			}
		} while (currTok.name != Tag.EXIT);

		error("block() Ожидается RF }");
	}

	// TODO будет убрано после создания интерпретатора
	private boolean skipBlock() throws Exception {
		int num = 0;
		Tag ch;

		do {
			ch = getToken();
			if (num == 0 && ch != Tag.LF)
				error("Ожидается {");
			if (ch == Tag.LF)
				num++;
			if (ch == Tag.RF)
				num--;
			if (num == 0)
				return true;
		} while (num > 0);
		error("Забыли токен токен LF {");
		return false;// Ошибка
	}
	
	// Функции, не возвращающие значение (void): print, add, del, reset, help,
	// state
	private boolean voidFunc() throws Exception {
		boolean isNeedReadEndToken = true; // Нужно ли считать токен END для
											// анализа в exprList или он уже
											// считан expr
		switch (currTok.name) {
		case PRINT:
			print(); // expr() оставляет токен в currTok.name ...
			isNeedReadEndToken = false; // ...и здесь мы отменяем считывние
										// нового токена для проверки END в
										// expr_List
			break;
		case ADD:
			add(); // expr() оставляет токен в currTok.name ...
			isNeedReadEndToken = false; // ...и здесь мы отменяем считывние
										// нового токена для проверки END в
										// expr_List
			break;
		case DEL:
			del();
			break;
		case RESET:
			reset();
			break;
		case SET:
			set();
			break;
		case HELP:
			help();
			break;
		case STATE:
			state();
			break;
		default: // не совпало ни с одним именем функции
			return false;
		}

		if (isNeedReadEndToken)
			getToken();
		return true;
	}

	// Выводит значение выражения expr либо всю таблицу переменных
	private void print() throws Exception {
		getToken();
		if (currTok.name == Tag.END) { // a. если нет expression, то
											// выводим все переменные
			if (table.isEmpty()) {
				output.addln("table is empty!");
			} else {
				output.addln("[table]");
				Iterator<Entry<String, TypedValue>> it = table.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, TypedValue> li = it.next();
					output.addln("" + li.getKey() + " " + li.getValue());
				}
				output.addln("[/table]");
			}
		} else { // b. выводим значение expression
			echoPrint = true;
			TypedValue v = expr(false); // expr() оставляет токен в currTok.name ...
			output.finishAppend("= " + v);
			echoPrint = false;
		}
	}

	// Добавляет переменную
	private void add() throws Exception {
		getToken();
		match(Tag.NAME);
		String varName = new String(((WordT)currTok).value); // ибо stringValue может
													// затереться при вызове
													// expr()
		output.add("Создана переменная " + varName);
		getToken();
		if (currTok.name == Tag.ASSIGN) {
			table.put(varName, expr(true)); // expr() оставляет токен в
											// currTok.name ...
		} else if (currTok.name == Tag.END) {
			table.put(varName, new TypedValue(0));
		}
		output.addln(" со значением " + table.get(varName));
	}

	// Удаляет переменную
	private void del() throws Exception {
		
		getToken();
		if (currTok.name == Tag.MUL) {
			table.clear();
			output.addln("Все переменные удалены!");
		} else
			match(Tag.NAME);
		String stringValue = new String(((WordT)currTok).value);
		if (!table.isEmpty()) {
			if (!table.containsKey(stringValue)) {
				output.addln("del: Переменной " + stringValue
						+ " нет в таблице переменных!");
			} else {
				table.remove(stringValue);
				output.addln("del: Переменная " + stringValue + " удалена.");
			}
		}
	}

	// Установка опций
	private void set() throws Exception {
		getToken();
		if (setname(currTok)==null)
			error("set: неверная опция");
		Id what = setname(currTok);

		getToken();
		match(Tag.ASSIGN);

		getToken();
		options.set(what, currTok); // Поскольку мы отправили Текущий токен ...
									// expr не пройдёт!
	}

	// Сброс опций или таблицы переменных
	private void reset() throws Exception {
		getToken();
		switch (currTok.name) {
		case MUL:
			options.resetAll();
			resetTable();
			output.addln("Всё сброшено.");
			break;
		case NAME:
			if(((WordT)currTok).value.equals("table")){
				resetTable();
				output.addln("Таблица переменных сброшена.");
			}else error("должен быть токен "+new WordT(Tag.NAME, "table"));
			break;
		default:
			if (setname(currTok)==null)
				error("reset: неверная опция");
			options.reset(setname(currTok));
		}
	}

	Id setname(Token t) {
		for(Id i: Id.values()){
			System.out.println("trying "+i.toString());
			if(((WordT)t).value.toLowerCase().equals(i.toString().toLowerCase())){
				System.out.println("match on "+i.toString());
				return i;
			}
		}
		return null;
		
		/*case ARGS_AUTO_END:
		case AUTO_END:
		case PRINT_TOKENS:
		case PRECISION:
		case ERRORS:
		case STRICTED:
		case AUTO_PRINT:
		case GREEDY_FUNC:
			return true;
		default:
			return false;
		*/
	}
	
	// Сброс таблицы переменных в исходное состояние
	void resetTable() {
		table.clear();
		table.put("e", new TypedValue(Math.E));
		table.put("pi", new TypedValue(Math.PI));
		table.put("ans", new TypedValue(lastResult));
	}

	// Помощь по грамматике
	void help() {
		output.addln("Грамматика(не актуальная):\n"
				+ "program:\n"
				+ "\texpr_list* EXIT\n"
				+ "\n"
				+ "expr_list:\n"
				+ "\texpr END\n"
				+ "\tvoid_func END\n"
				+ "\tif_ END\n"
				+

				"\n"
				+ "if_:\n"
				+ "\t\"if\" '('expr')' '{' expt_list '}'\n"
				+ "\t\"if\" '('expr')' '{' expt_list '}' \"else\" '{' expt_list '}'\n"
				+ "expr:\n" + "\texpr + term\n" + "\texpr - term\n"
				+ "\tterm\n" + "\n" + "term:\n" + "\tterm / pow\n"
				+ "\tterm * pow\n" + "\tpow\n" + "\n" + "pow:\n"
				+ "\tpow ^ prim\n" + "\tprim\n" + "\n" + "prim:\n"
				+ "\tINTEGER\n" + "\tNAME\n" + "\tNAME = expr\n" + "\t-prim\n"
				+ "\t(expr)\n" + "\tfunc\n" + "\n" + "func:\n" + "\tsin expr\n"
				+ "\tcos expr\n" + "\n" + "void_func:\n" + "\tprint\n"
				+ "\tadd\n" + "\tdel\n" + "\treset\n" + "\tset\n" + "\tunset\n"
				+ "\thelp\n" + "\tstate\n\n");

	};

	// Выводит информацию о текущем состоянии
	void state() {
		output.addln("Текущее состояние:\nПеременных " + table.size());
		options.printAll();
	};

	// складывает и вычитает
	private TypedValue expr(boolean get) throws Exception {
		TypedValue left = term(get);
		TypedValue right;
		Tag sign;
		for (;;){	// ``вечно''
			switch (sign = currTok.name) {
			case PLUS:
			case MINUS:
				// случаи '+' и '-'
				right = term(true); // left += -= term(true);
				break; // этот break относится к switch
			default:
				lastResult = left;
				return left;
			}
			
			left = interpreter.expr(left, sign, right);
		}
	}
	
	// умножает и делит
	private TypedValue term(boolean get) throws Exception {
		TypedValue left = prim(get);
		TypedValue right;
		Tag sign;
		for (;;){
			switch (sign = currTok.name) {
			case MUL:
			case DIV:
				// случай '*' '/'
				right = prim(true);//left *= power(true);
				break; // этот break относится к switch
			default:
				return left;
			}
			
			left = interpreter.term(left, sign, right);
		}
	}

	/*
	// Степень a^b
	private TypedValue power(boolean get) throws Exception {
		TypedValue left = factorial(get);
		switch (currTok.name) {
		case POW:
			left = Math.pow(left, power(true));
		default:
			return left;
		}
	}

	// факториал
	private double factorial(boolean get) throws Exception {
		double left = prim(get);
		for (;;)
			// ``вечно''
			switch (currTok.name) {
			case FACTORIAL:
				if (left < 0)
					error("Факториал отрицательного числа не определён!");
				int t = (int) Math.rint(left); // TODO сделать невозможным
												// взятие факториала от 4.5,
												// 4.8, 4.1, ...
				left = 1.0;
				while (t != 0) {
					left *= t--;
				}
				getToken(); // для следующих
				break;
			default:
				return left;
			}
	}
	 */
	
	// обрабатывает первичное
	private TypedValue prim(boolean get) throws Exception {
		if (get)
			getToken();

		switch (currTok.name) {
		case INTEGER: { // константа с плавающей точкой
			TypedValue v = ((IntegerT)currTok).value;
			getToken();// получить следующий токен ...
			return v;
		}
		case NAME: {
			String name = new String(((WordT)currTok).value); // нужно, ибо expr() может
													// затереть stringValue

			TypedValue v = table.get(name);
			if (getToken() == Tag.ASSIGN) {
				v = expr(true);
				table.put(name, v);
				output.addln("Значение переменой " + name + " изменено на " + v);
			}
			return v;
		}
		case MINUS: { // унарный минус
			return -prim(true);
		}
		case LP: {
			double e = expr(true);
			match(Tag.RP); // ')'
			getToken(); // получить следующий токен ...
			return e;
		}
		default: {
			if (func())
				return y;

			error("требуется первичное_выражение (нетерминал prim)");
			return 0;
		}
		}
	}

	private double y; // для временного хранения результата func()

	// функции, возвращающие значение (non-void): sin, cos
	private boolean func() throws Exception {
		if (ofRadian(currTok.name)) {
			Tag funcName = currTok.name; // Запоминаем для дальнейшего
												// использования
			if (!options.getBoolean(Id.GREEDY_FUNC)) {
				getToken();
				match(Tag.LP); // Проверка наличия (
			}

			switch (funcName) {
			case SIN:
				y = Math.sin(expr(true)); // следующий токен END для
											// prim()<-term()<-expr()<-expr_list()
											// получен в этом вызове expr()
				break;
			case COS:
				y = Math.cos(expr(true)); // следующий токен END для
											// prim()<-term()<-expr()<-expr_list()
											// получен в этом вызове expr()
				break;
			default:
				error("Не хватает обработчика для функции "
						+ funcName.toString());
			}

			if (!options.getBoolean(Id.GREEDY_FUNC)) {
				match(Tag.RP);// Проверка наличия ')' - её оставил expr()
				getToken(); // считываем токен, следующий за ')'
			} // если Нежадные, то в currTok останется токен, на котором
				// "запнулся" expr
				// Таким образом достигается единообразие оставленного в currTok
				// токена для не- и жадного режимов

			// Округление до привычных значений
			y = (doubleCompare(y, 0)) ? 0 : y;
			y = (doubleCompare(y, 0.5)) ? 0.5 : y;
			y = (doubleCompare(y, -0.5)) ? -0.5 : y;
			y = (doubleCompare(y, 1)) ? 1 : y;
			y = (doubleCompare(y, -1)) ? -1 : y;

			return true;
		}

		return false;
	}
	
	// Функция от аргумента в радианной мере
	private boolean ofRadian(Tag name) {
		switch (name) {
		case SIN:
		case COS:
			return true;
		default:
			return false;
		}
	}
	
	// Сравнивает 2 double с заданной в
	// options.getInt(Terminal.PRECISION) точностью
	boolean doubleCompare(double a, double b) {
		if (Math.abs(a - b) < 1.0 / Math.pow(10,
				options.getInt(Id.PRECISION)))
			return true;
		return false;
	}

	// Бросает исключение MyException и увеичивает счётчик ошибок
	public void error(String string) throws MyException {
		int errors = options.getInt(Id.ERRORS);
		errors++;
		options.set(Id.ERRORS, errors);
		output.flush();
		throw new MyException(string);
	}

	// Возвращает Название текущего токена для проверок в вызывающем методе main
	public Token getCurrTok() {
		return currTok;
	}

	public TypedValue lastResult = new TypedValue(Double.NaN);

	// Нижеприведённые методы нужны только лишь для тестов и отладки
	public int getErrors() {
		return options.getInt(Id.ERRORS);
	}

}