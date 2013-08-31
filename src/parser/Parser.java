package parser;

import java.util.LinkedList;

import executables.*;
import interpreter.*;
import options.*;
import types.TypedValue;
import lexer.*;
import main.Buffer;
import main.MyException;

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
public final class Parser extends Env{
	private Token currTok = null; // текущий обрабатываемый токен, изменяется
									// методом getToken()
	private final Buffer buf;
	private final Interpreter inter;
	
	// Конструктор
	public Parser(Buffer buf, Interpreter inter) {
		super(inter.output, inter.table, inter.options);
		this.buf = buf;
		this.inter=inter;
		inter.resetTable();
	}

	private boolean echoPrint = false; // Используется для эхопечати токенов при
										// их чтении методом getToken() при
										// void_func() : print

	/**
	 * Получает очередной токен -> currTok, изменяет numbeValue и strinValue
	 * 
	 * @see Buffer#getToken()
	 */
	private Tag getToken() throws Exception {
		if (echoPrint && currTok.name != Tag.END && !inter.skip)
			output.append(currTok.toString() + ' '); // Печать предыдущего считанного
												// токена, т. к. в exprList()
												// токен уже считан до включения
												// флага echoPrint

		currTok = buf.getToken();

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
				if (options.getBoolean(OptId.AUTO_PRINT))
					echoPrint = true; // ... включаем эхо-печать в
										// this.getToken() ...
				TypedValue v = expr(false);
				if (options.getBoolean(OptId.AUTO_PRINT) && !inter.skip)
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
	
		boolean get = block(condition.getBoolean());
		if(get) getToken(); // считываем очередной токен

		if (currTok.name == Tag.ELSE) {
			block(!condition.getBoolean());
			return true;
		} else { // если после if { expr_list } идёт не else
			return false; // тогда в следующией итерации цикла в program() мы
							// просмотрим уже считанный выше токен, а не будем
							// считывать новый
		}
	}

	// { expr_list }
	private boolean block(boolean condition) throws Exception {
		if(!condition) inter.skip=true;
		inter.incrDepth();
		
		getToken();
		if(currTok.name==Tag.LF) { // '{'
			boolean get = true; // нужно ли считывать токен в самом начале
			do {
				if (get)
					getToken();
	
				switch (currTok.name) {
				case RF:
					inter.decrDepth();
					return true; // '}'
				default:
					get = instr();
				}
			} while (currTok.name != Tag.EXIT);
			error("block() Ожидается RF }");
			return true;
		}else{
			instr(); 
			inter.decrDepth();
			return false;
		}
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
			inter.exec(new Print(null));
		} else { // b. выводим значение expression
			echoPrint = true;
			TypedValue v = expr(false); // expr() оставляет токен в currTok.name ...
			inter.exec(new Print(v));
			echoPrint = false;
		}
	}

	// Добавляет переменную
	private void add() throws Exception {
		getToken();
		match(Tag.NAME);
		String name = new String(((WordT)currTok).value); // ибо stringValue может
													// затереться при вызове
													// expr()
		getToken();
		switch(currTok.name){
			case ASSIGN:
				inter.exec(new TablePut(name, expr(true))); // expr() оставляет токен в
												// currTok.name ...
				break;
			case END:
				inter.exec(new TablePut(name, new TypedValue(0)));
				break;
			default:
				error("Ожидается '=' или ';' после имени.");
		}
	}

	// Удаляет переменную
	private void del() throws Exception {
		getToken();
		if (currTok.name == Tag.MUL) {
			inter.exec(new Del(null));
		} else{
			match(Tag.NAME);
			String stringValue = new String(((WordT)currTok).value);
			inter.exec(new Del(stringValue));
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
			inter.resetTable();
			output.addln("Всё сброшено.");
			break;
		case NAME:
			if(((WordT)currTok).value.equals("table")){
				inter.resetTable();
				output.addln("Таблица переменных сброшена.");
			}else error("должен быть токен "+new WordT(Tag.NAME, "table"));
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
		Tag s;
		for (;;){	// ``вечно''
			switch (s=currTok.name) {
			case PLUS:
			case MINUS:
				left = inter.exec(new Expr(left, term(true), s));
				break; // этот break относится к switch
			default:
				return left;
			}
		}
	}
	
	// умножает и делит
	private TypedValue term(boolean get) throws Exception {
		TypedValue left = degree(get);
		Tag s;
		for (;;){
			switch (s=currTok.name) {
			case MUL:
			case DIV:
				left = inter.exec(new Term(left, degree(true), s));
				break; // этот break относится к switch
			default:
				return left;
			}
		}
	}

	
	// Степень a^b
	private TypedValue degree(boolean get) throws Exception {
		TypedValue left = factorial(get);
		switch (currTok.name) {
		case POW:
			left = inter.exec(new Degree(left, degree(true)));
		default:
			return left;
		}
	}

	// факториал
	private TypedValue factorial(boolean get) throws Exception {
		TypedValue left = prim(get);
		for (;;){
			switch (currTok.name) {
			case FACTORIAL:
				left = inter.exec(new Factorial(left));
				getToken(); // для следующих
				break;
			default:
				return left;
			}
		}
	}
	
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

			getToken();
			/*if (getToken() == Tag.ASSIGN)
				inter.exec(new TablePut(name, expr(true)));
			*/
			
			
			//TypedValue v = inter.exec(new TableGet(name));
			return right(name);//v;
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
	
	private TypedValue right(String name) throws Exception{
		switch (currTok.name) {
		case LP:
			LinkedList<TypedValue> args = funcArgs();
			return inter.exec(new Func(name, args));
		case ASSIGN:
			inter.exec(new TablePut(name, expr(true)));
		case END:
		case RP: // для funcArgs(), в котором есть getToken(). От ошибки aaa=8-9) защищает instr()->match(Tag.END)
		default:
			return inter.exec(new TableGet(name));
		}
		
	}
	
	private LinkedList<TypedValue> funcArgs() throws Exception {
		LinkedList<TypedValue> args = null;
		boolean get=true;
		getToken();
		switch(currTok.name){
		case RP:
			break;
		default:
			args = new LinkedList<TypedValue>();
			get = false;
		
			do{
				TypedValue t=expr(get);
				args.add(t);
				get=true;
			}while(currTok.name==Tag.COMMA);
			match(Tag.RP);
		}

		getToken(); // получаем следующий токен, его проверка на соответствие END - в instr()
		return args;
	}

	private double y; // для временного хранения результата func()

	// функции, возвращающие значение (non-void): sin, cos
	private boolean func() throws Exception {
		return false;
	}
	

	// Бросает исключение MyException и увеичивает счётчик ошибок
	public void error(String string) throws Exception  {
		/*int errors = options.getInt(OptId.ERRORS);
		errors++;
		options.set(OptId.ERRORS, errors);
		output.flush();*/
		throw new MyException(string);
	}

	// Возвращает Название текущего токена для проверок в вызывающем методе main
	public Token getCurrTok() {
		return currTok;
	}

	// Нижеприведённые методы нужны только лишь для тестов и отладки
	/*public int getErrors() {
		return options.getInt(OptId.ERRORS);
	}*/

}