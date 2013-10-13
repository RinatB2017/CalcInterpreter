package inter;

import java.util.*;

import exceptions.MyException;
import inter.returnables.*;
import inter.voidables.*;
import inter.voidables.Set;
import options.*;
import types.TypedValue;
import lexer.*;
import main.Buffer;

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
		if (echoPrint && currTok.tag != Tag.END && !inter.skip)
			output.append(currTok.toString() + ' '); // Печать предыдущего считанного
												// токена, т. к. в exprList()
												// токен уже считан до включения
												// флага echoPrint

		currTok = buf.getToken();

		return currTok.tag;
	}

	// if(currTok.name!=Terminal.LP) error("Ожидается (");
	void match(Tag t) throws Exception {
		if (currTok.tag != t)
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

			switch (currTok.tag) {
			case EXIT:
				return;
			default:
				get = instr();
				output.flush();
			}
		}
	}

	private boolean instr() throws Exception {
		echoPrint = false; // Отменяем эхопечать токенов, если она не была
							// отменена из-за вызова error() -> MyException

		switch (currTok.tag) {
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
				if (options.getBoolean(OptId.AUTO_PRINT) && !inter.skip){
					output.finishAppend("= " + v);
				}
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
	
		//boolean get = 
		block(condition.getBoolean());
		//if(get)
		getToken(); // считываем очередной токен
		
		if (currTok.tag == Tag.ELSE) {
			block(!condition.getBoolean());
			return true;
		} else { // если после if { expr_list } идёт не else
			return false; // тогда в следующией итерации цикла в program() мы
							// просмотрим уже считанный выше токен, а не будем
							// считывать новый
		}
	}

	// ( instr { instr } '}' ) | instr
	private boolean block(boolean condition) throws Exception {
		if(!condition) inter.skip=true;
		inter.incrDepth();
		
		getToken();
		if(currTok.tag==Tag.LF) { // '{'
			boolean get = true; // нужно ли считывать токен в самом начале
			do {
				if (get)
					getToken();
	
				switch (currTok.tag) {
				case RF:
					inter.decrDepth();
					return true; // '}'
				default:
					get = instr();
				}
			} while (currTok.tag != Tag.EXIT);
			error("block() Ожидается RF }");
			return true;
		}else{
			instr(); // оставляет END в currTok
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
		switch (currTok.tag) {
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
		if (currTok.tag == Tag.END) { // a. если нет expression, то
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
		switch(currTok.tag){
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
		if (currTok.tag == Tag.MUL) {
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

		switch (currTok.tag) {
		case NAME:
			String string = ((WordT)currTok).value;
			getToken();
			match(Tag.ASSIGN);

			getToken();

			inter.exec(new Set(string, currTok));
			break;
		default:
			error("set: ожидается токен NAME");
		}
	}

	// Сброс опций или таблицы переменных
	private void reset() throws Exception {
		getToken();
		switch (currTok.tag) {
		case MUL:
			inter.exec(new Reset(null));
			break;
		case NAME:
			String string = ((WordT)currTok).value;
			inter.exec(new Reset(string));
			break;
		default:
			error("reset: ожидается токен NAME либо *");
		}
	}

	// Выводит информацию о текущем состоянии
	void state() throws Exception {
		inter.exec(new State());
	};

	// складывает и вычитает
	private TypedValue expr(boolean get) throws Exception {
		TypedValue left = term(get);
		Tag s;
		for (;;){	// ``вечно''
			switch (s=currTok.tag) {
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
			switch (s=currTok.tag) {
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
		switch (currTok.tag) {
		case POW:
			left = inter.exec(new Degree(left, degree(true)));
		default:
			return left;
		}
	}

	// факториал
	private TypedValue factorial(boolean get) throws Exception {
		TypedValue left = prim(get);
		
		// Здесь перехватываем многочисленные prim()'овские return'ы
		inter.addAns(left);
		
		for (;;){
			switch (currTok.tag) {
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

		switch (currTok.tag) {
		case INTEGER:
		case DOUBLE:
		case BOOLEAN:
			TypedValue v = new TypedValue();
			currTok.getTypedValueTo(v);
			getToken();// получить следующий токен ...
			return v;
		case NAME: {
			String name = new String(((WordT)currTok).value); // нужно, ибо expr() может
													// затереть stringValue

			getToken();
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
			error("требуется первичное_выражение (нетерминал prim)");
			return null;
		}
		}
	}
	
	private TypedValue right(String name) throws Exception{
		switch (currTok.tag) {
		case LP:
			ArrayList<TypedValue> args = funcArgs();
			return inter.exec(new Func(name, args));
		case NAME:
		case BOOLEAN:
		case INTEGER:
		case DOUBLE:
		//case PLUS: // т.к. из-за возможности "sin -30" страдает работа "b=a+1"
		//case MINUS:
			ArrayList<TypedValue> arg = funcArg();
			return inter.exec(new Func(name, arg));
		case ASSIGN:
			inter.exec(new TablePut(name, expr(true)));
		case END:
		case RP: // для funcArgs(), в котором есть getToken(). От ошибки aaa=8-9) защищает instr()->match(Tag.END)
		default:
			return inter.exec(new TableGet(name));
		}
		
	}
	
	private ArrayList<TypedValue> funcArgs() throws Exception {
		ArrayList<TypedValue> args = null;
		boolean get=true;
		getToken();
		switch(currTok.tag){
		case RP:
			break;
		default:
			args = new ArrayList<TypedValue>();
			get = false;
		
			do{
				TypedValue t=expr(get);
				args.add(t);
				get=true;
			}while(currTok.tag==Tag.COMMA);
			match(Tag.RP);
		}

		getToken(); // получаем следующий токен, его проверка на соответствие END - в instr()
		return args;
	}
	
	// Возвращает единственный аргумент из токена
	private ArrayList<TypedValue> funcArg() throws Exception {
		ArrayList<TypedValue> args = new ArrayList<TypedValue>();
		TypedValue v;
		boolean isNeedNegative=false;
		
		switch(currTok.tag){
		case MINUS:
			isNeedNegative=true;
		case PLUS:
			getToken();
		default:
				
		}
		
		switch(currTok.tag){
		case INTEGER:
		case DOUBLE:
		case BOOLEAN:
			v = new TypedValue();
			currTok.getTypedValueTo(v);
			break;
		case NAME:
			String name = new String(((WordT)currTok).value);
			v = inter.exec(new TableGet(name));
			break;
		default:
			throw new MyException("Плохой токен после '+' или '-' : "+currTok.tag+
					". Допустимы BOOLEAN, INTEGER, DOUBLE, NAME.");
		}
		getToken(); // Продвигаемся на END
		
		if(isNeedNegative)
			v.negative();
		
		args.add(v);
		return args;
	}
	
	// Бросает исключение MyException и увеичивает счётчик ошибок
	public void error(String string) throws Exception  {
		throw new MyException(string);
	}

	// Возвращает Название текущего токена для проверок в вызывающем методе main
	public Token getCurrTok() {
		return currTok;
	}
}