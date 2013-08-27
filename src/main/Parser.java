package main;
import interpretator.Interpreter;
import interpretator.TypedValue;
import interpretator.Types;

import java.util.*;
import java.util.Map.Entry;

import options.OptId;
import options.Options;
import lexer.*;

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
	private Interpreter interpreter;

	// Конструктор
	public Parser(Buffer buf, Options options, OutputSystem output) {
		this.buf = buf;
		this.options = options;
		interpreter = new Interpreter(output);
		resetTable();
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
		if (echoPrint && currTok.name != Tag.END && !interpreter.skip)
			interpreter.output.append(currTok.toString() + ' '); // Печать предыдущего считанного
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
				interpreter.output.clear();
				get = instr();
				interpreter.table.put("ans", lastResult);
				interpreter.output.flush();
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
				if (options.getBoolean(OptId.AUTO_PRINT))
					echoPrint = true; // ... включаем эхо-печать в
										// this.getToken() ...
				TypedValue v = expr(false);
				if (options.getBoolean(OptId.AUTO_PRINT) && !interpreter.skip)
					interpreter.output.finishAppend("= " + v);
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
		
		if(condition.getBoolean()) // если condition==true 
			block();
		else if(interpreter.skip==false)
			skipBlock(); // защита от рекурсии, в результате которой может выключиться interpreter.skip  
		else block(); // если interpreter.skip==true, то не заходим в skipBlock(), чтобы не выключить interpreter.skip 

		getToken(); // считываем очередной токен

		if (currTok.name == Tag.ELSE) {
			if(!condition.getBoolean()) // если condition==false
				block();
			else if(interpreter.skip==false) // защита от рекурсии, в результате которой может выключиться interpreter.skip 
				skipBlock();
			else block(); // если interpreter.skip==true, то не заходим в skipBlock(), чтобы не выключить interpreter.skip 
		} else { // если после if { expr_list } идёт не else
			return false; // тогда в следующией итерации цикла в program() мы
							// просмотрим уже считанный выше токен, а не будем
							// считывать новый
		}
		return true;
	}

	private void skipBlock() throws Exception {
		interpreter.skip=true;
		block();
		interpreter.skip=false;
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
			interpreter.printAll();
		} else { // b. выводим значение expression
			echoPrint = true;
			TypedValue v = expr(false); // expr() оставляет токен в currTok.name ...
			interpreter.print(v);
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
		interpreter.output.add("Создана переменная " + varName);
		getToken();
		if (currTok.name == Tag.ASSIGN) {
			interpreter.table.put(varName, expr(true)); // expr() оставляет токен в
											// currTok.name ...
		} else if (currTok.name == Tag.END) {
			interpreter.table.put(varName, new TypedValue(0));
		}
		interpreter.output.addln(" со значением " + interpreter.table.get(varName));
	}

	// Удаляет переменную
	private void del() throws Exception {
		
		getToken();
		if (currTok.name == Tag.MUL) {
			interpreter.table.clear();
			interpreter.output.addln("Все переменные удалены!");
		} else
			match(Tag.NAME);
		String stringValue = new String(((WordT)currTok).value);
		if (!interpreter.table.isEmpty()) {
			if (!interpreter.table.containsKey(stringValue)) {
				interpreter.output.addln("del: Переменной " + stringValue
						+ " нет в таблице переменных!");
			} else {
				interpreter.table.remove(stringValue);
				interpreter.output.addln("del: Переменная " + stringValue + " удалена.");
			}
		}
	}

	// Установка опций
	private void set() throws Exception {
		getToken();
		if (setname(currTok)==null)
			error("set: неверная опция");
		OptId what = setname(currTok);

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
			interpreter.output.addln("Всё сброшено.");
			break;
		case NAME:
			if(((WordT)currTok).value.equals("interpreter.table")){
				resetTable();
				interpreter.output.addln("Таблица переменных сброшена.");
			}else error("должен быть токен "+new WordT(Tag.NAME, "interpreter.table"));
			break;
		default:
			if (setname(currTok)==null)
				error("reset: неверная опция");
			options.reset(setname(currTok));
		}
	}

	OptId setname(Token t) {
		for(OptId i: OptId.values()){
			//System.out.println("trying "+i.toString());
			if(((WordT)t).value.toLowerCase().equals(i.toString().toLowerCase())){
				//System.out.println("match on "+i.toString());
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
		interpreter.table.clear();
		interpreter.table.put("e", new TypedValue(Math.E));
		interpreter.table.put("pi", new TypedValue(Math.PI));
		interpreter.table.put("ans", lastResult);
	}

	// Помощь по грамматике
	void help() {
		interpreter.output.addln("Грамматика(не актуальная):\n"
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
		interpreter.output.addln("Текущее состояние:\nПеременных " + interpreter.table.size());
		options.printAll();
	};

	// складывает и вычитает
	private TypedValue expr(boolean get) throws Exception {
		TypedValue left = term(get);
		for (;;){	// ``вечно''
			switch (currTok.name) {
			case PLUS:
				left = interpreter.plus(left, term(true));
				break;
			case MINUS:
				left = interpreter.minus(left, term(true));
				break; // этот break относится к switch
			default:
				if(left!=null) lastResult = left;
				return left;
			}
		}
	}
	
	// умножает и делит
	private TypedValue term(boolean get) throws Exception {
		TypedValue left = prim(get);

		for (;;){
			switch (currTok.name) {
			case MUL:
				left = interpreter.mul(left, term(true));
				break;
			case DIV:
				left = interpreter.div(left, term(true));
				//right = prim(true);//left *= power(true);
				break; // этот break относится к switch
			default:
				return left;
			}
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
		case INTEGER: { // целочисленная константа
			TypedValue v = new TypedValue(((IntegerT)currTok).value);
			getToken();// получить следующий токен ...
			return v;
		}
		case DOUBLE: { // константа с плавающей точкой
			TypedValue v = new TypedValue(((DoubleT)currTok).value);
			getToken();// получить следующий токен ...
			return v;
		}
		case BOOLEAN: { // булева константа
			TypedValue v = new TypedValue(((BooleanT)currTok).value);
			getToken();// получить следующий токен ...
			return v;
		}
		case NAME: {
			String name = new String(((WordT)currTok).value); // нужно, ибо expr() может
													// затереть stringValue

			TypedValue v = interpreter.table.get(name);
			if (getToken() == Tag.ASSIGN) {
				v = expr(true);
				interpreter.table.put(name, v);
				interpreter.output.addln("Значение переменой " + name + " изменено на " + v);
			}
			return v;
		}
		case MINUS: { // унарный минус
			return prim(true).negative();
		}
		case LP: {
			TypedValue e = expr(true);
			match(Tag.RP); // ')'
			getToken(); // получить следующий токен ...
			return e;
		}
		default: {
			if(func())
				return new TypedValue(y);

			error("требуется первичное_выражение (нетерминал prim)");
			return null;
		}
		}
	}

	private double y; // для временного хранения результата func()

	// функции, возвращающие значение (non-void): sin, cos
	private boolean func() throws Exception {
		if (ofRadian(currTok.name)) {
			Tag funcName = currTok.name; // Запоминаем для дальнейшего
												// использования
			if (!options.getBoolean(OptId.GREEDY_FUNC)) {
				getToken();
				match(Tag.LP); // Проверка наличия (
			}

			switch (funcName) {
			case SIN:
				y = (Math.sin(expr(true).getDouble())); // следующий токен END для
											// prim()<-term()<-expr()<-expr_list()
											// получен в этом вызове expr()
				break;
			case COS:
				y = (Math.cos(expr(true).getDouble())); // следующий токен END для
											// prim()<-term()<-expr()<-expr_list()
											// получен в этом вызове expr()
				break;
			default:
				error("Не хватает обработчика для функции "
						+ funcName.toString());
			}

			if (!options.getBoolean(OptId.GREEDY_FUNC)) {
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
				options.getInt(OptId.PRECISION)))
			return true;
		return false;
	}

	// Бросает исключение MyException и увеичивает счётчик ошибок
	public void error(String string) throws MyException {
		int errors = options.getInt(OptId.ERRORS);
		errors++;
		options.set(OptId.ERRORS, errors);
		interpreter.output.flush();
		throw new MyException(string);
	}

	// Возвращает Название текущего токена для проверок в вызывающем методе main
	public Token getCurrTok() {
		return currTok;
	}

	public TypedValue lastResult = new TypedValue(0);

	// Нижеприведённые методы нужны только лишь для тестов и отладки
	public int getErrors() {
		return options.getInt(OptId.ERRORS);
	}

}