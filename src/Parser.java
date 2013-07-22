/* Парсер пытается выполнить ArrayList токенов, 
 * которые по одному получает из лексера при вызове Parser.getToken().
 * При ошибке парсер вызывает метод Parser.error(),
 * который генерирует исключение MyException,
 * перехватив которое можно продолжать работу.
 *
 * Переменные можно создавать вручную и инициализировать, например aabc = 9.3;
 * Если переменная не существует при попытке обращения к ней,
 * то она автоматически создаётся со значением 0.0, см. prim().
 * */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class Parser {
	private Token currTok = null; // текущий обрабатываемый токен, изменяется методом getToken()
	private Buffer buf = null;
	private Options options = null;
	private HashMap<String, Double> table; // Таблица переменных
		
	//Конструктор
	public Parser(Buffer buf, Options options){
		table = new HashMap<String, Double>();
		resetTable();
		this.buf = buf;
		this.options = options;
	}
	
	
	private double numberValue;
	private String stringValue;
	private boolean echoPrint=false; // Используется для эхопечати токенов при их чтении методом getToken() при void_func() : print
	
	// Получает очередной токен, изменяет number_value и string_value
	private Terminal getToken() throws Exception{
		if(echoPrint  &&  currTok.name != Terminal.END)
			System.out.print(currTok.value+' '); // Печать предыдущего считанного токена, т. к. в exprList() токен уже считан до включения флага echoPrint
		
		currTok=buf.getToken();
		
		if(currTok.name==Terminal.NUMBER)
			numberValue= Double.parseDouble(currTok.value);
		if(currTok.name==Terminal.USER_DEFINED_NAME)
			stringValue=currTok.value;
		return currTok.name;
	}
	
	// Главный метод - список выражений - с него начинается парсинг
	public void exprList() throws Exception{
		echoPrint = false; // Отменяем эхопечать токенов, если она не была отменена из-за вызова error() -> MyException
		boolean get=true;//нужно ли считывать токен в самом начале
	    
		while(true){
	        if(get) getToken();
	        get=true;

	        switch(currTok.name){
	        case EXIT: return;
	        case END: continue;
	        case RF: return;
	        case IF: 
	        	get=if_();
	        	break;//switch
	        default:
				if(voidFunc()){
					if(currTok.name!=Terminal.END) error("Не верный конец, нужен токен END ;");
				}else{ // expr
					// если AUTO_PRINT, то ...
					if(options.getBoolean(Terminal.AUTO_PRINT)) { // TODO исправить глюк autoprint из-за lexerAutoEnd=false : сделать очередь сообщений
						echoPrint=true; // ... включаем эхо-печать в this.getToken() ...
						double v = expr(false);
						System.out.println("= " + v + '\n');
						echoPrint=false; // ... а теперь выключаем
					}else{
						expr(false);
					}
					if (currTok.name!=Terminal.END) error("Не верный конец, нужен токен END ;");
				}
	        }//switch
	        table.put("ans", lastResult);
	    }
	}

	// один из вариантов решения проблемы с возвращением разных типов : void, а case NUMBER, TERUE и FALSE записывают соответствующие поля класса + записывают поле класса - индикатор, какой конкретно тип мы возвращаем 
	// обрабатывает первичное
	private double prim(boolean get) throws Exception
	{
		if(get) getToken();

	    switch (currTok.name){
	    case NUMBER:{ // константа с плавающей точкой
			double v = numberValue;
	        getToken();//получить следующий токен ...
	        return v;
	    	}
	    case USER_DEFINED_NAME:
	    {
			String name = new String(stringValue); // нужно, ибо expr() может затереть stringValue 
			
			if(!table.containsKey(name))
				if(options.getBoolean(Terminal.STRICTED)) error("Запрещено автоматическое создание переменных в stricted-режиме");
				else{
					table.put(name, 0.0); // Если в table нет переменной, то добавляем её со зачением 0.0
					if(!echoPrint) System.out.println("Создана переменная "+name);
				}
			double v=table.get(name);
	        if (getToken()==Terminal.ASSIGN){
	        	v = expr(true);
	        	table.put(name, v);}
	        return v;
	    	}
	    case MINUS:{ // унарный минус
	        return -prim(true);
	    	}
	    case LP:{
	        double e = expr(true);
	        if (currTok.name!=Terminal.RP) error("требуется )");
	        getToken(); // пропустить ')' //получить следующий токен ...
	        return e;
	    	}
	    default:{
	    	if(func()) return y;
	    	
	        error("требуется первичное_выражение (нетерминал prim)");
	        return 0;
	    	}
	    }
	}
	
	/*
	private boolean trig(Terminal name){
		return ofRadian(name) || returnsRadian(name);
	}
	*/
	
	private boolean ofRadian(Terminal name){
		switch(name){
		case SIN: case COS:
			return true;
		default: return false;
		}
	}
	
	/*
	private boolean returnsRadian(Terminal name){
		return false; // TODO это заглушка
	}
	*/
	
	private double y; // для временного хранения результата func()
	
	// функции, возвращающие значение (non-void): sin, cos
	private boolean func() throws Exception{
		if(ofRadian(currTok.name)){
			Terminal funcName = currTok.name; // Запоминаем для дальнейшего использования
			if(!options.getBoolean(Terminal.GREEDY_FUNC)){
				getToken(); // Проверка наличия (
				if(currTok.name!=Terminal.LP) error("Ожидается (");
			}
			
			switch(funcName){
				case SIN:
					y = Math.sin(expr(true)); // следующий токен END для prim()<-term()<-expr()<-expr_list() получен в этом вызове expr()
					break;
				case COS:
					y = Math.cos(expr(true)); // следующий токен END для prim()<-term()<-expr()<-expr_list() получен в этом вызове expr()
					break;
				default:
					error("Не хватает обработчика для функции " + funcName.toString());
			}
			
			if(!options.getBoolean(Terminal.GREEDY_FUNC)){
				 // Проверка наличия ')' - её оставил expr()
				if(currTok.name!=Terminal.RP) error("Ожидается )");
				getToken(); // считываем токен, следующий за ')'
			} // если Нежадные, то в currTok останется токен, на котором "запнулся" expr
			// Таким образом достигается единообразие оставленного в currTok токена для не- и жадного режимов
			
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
	
	
	boolean doubleCompare(double a, double b){
		if (Math.abs(a-b) < 1.0/Math.pow(10, options.getInt(Terminal.PRECISION))) return true;
		return false;
	}


	// умножает и делит
	private double term(boolean get) throws Exception
	{
	    double left = power(get);
	    for(;;)
	        switch(currTok.name)
	        {
	        case MUL:{
	            // случай '*'
	            left *= power(true);
	            }break; // этот break относится к switch
	        case DIV:{
	            // случай '/'
	        	double d = power(true);
	            if (d != 0) {
					left /= d;
					break;} // этот break относится к switch
	            error("деление на 0");
	            };
	        default:{
	            return left;}
	        }
	}
	
	// складывает и вычитает
	private double expr(boolean get) throws Exception
	{
	    double left = term(get);
	    for(;;)
	        // ``вечно''
	        switch(currTok.name)
	        {
	        case PLUS:
	            // случай '+'
	            left += term(true);
	            break; // этот break относится к switch
	        case MINUS:
	            // случай '-'
	            left -= term(true);
	            break; // этот break относится к switch
	        default:
	        	lastResult=left;
	            return left;
	        }
	}
	
	// Степень a^b
	private double power(boolean get) throws Exception{
	    double left = factorial(get);
	    for(;;)
	        // ``вечно''
	        switch(currTok.name)
	        {
	        case POW:
	            left = Math.pow(left, power(true));
	            break; // этот break относится к switch
	        default:
	            return left;
	        }
	}
	
	private double factorial(boolean get) throws Exception
	{
	    double left = prim(get);
	    for(;;)
	        // ``вечно''
	        switch(currTok.name)
	        {
	        case FACTORIAL:
	        	if(left<0) error("Факториал отрицательного числа не определён!");
	        	int t = (int) Math.rint(left); // TODO сделать невозможным взятие факториала от 4.5, 4.8, 4.1, ...
	        	left=1.0;
	        	while(t!=0){
	        		left *= t--;
	        	}
	        	getToken(); // для следующих
	        	break;
	        default:
	            return left;
	        }
	}
	
	private boolean if_() throws Exception{
		getToken();
		if(currTok.name!=Terminal.LP){ // '('
			error("Ожидается LP (");

		}
		double condition = expr(true);
		// expr отставляет не обработанный токен в curr_tok.name, здесь мы его анализируем
		if(currTok.name!=Terminal.RP) { // ')'
			error("Ожидается RP )");
		}
		if(!doubleCompare(condition, 0)){	// если condition==true
			block();
			if(currTok.name==Terminal.EXIT) return false; // возвращаем false, чтобы функиии, вызвавшие if_() увидели EXIT
		}else{				// если condition==false
			skipBlock();	// пропусить true brach {}
		}
		
		getToken(); //считываем очередной токен

		if(currTok.name==Terminal.ELSE){
			if(doubleCompare(condition, 0)){
				block();
				if(currTok.name==Terminal.EXIT) return false; // возвращаем false, чтобы функимии, вызвавшие if_() увидели EXIT
			}else{
				skipBlock();	// пропусить false brach {}
			}
		}else{ // если после if { expr_list } идёт не else
			return false; // тогда в следующией итерации цикла expr_list мы просмотрим уже считанный выше токен, а не будем считывать новый
		}
		return true;
	}
	
	// { expr_list }
	// Внимание: после вызова всегда нужно проверять currTok.name==Names.EXIT, как - см. в if_()
	private void block() throws Exception{
		getToken();
		if(currTok.name!=Terminal.LF){ // '{'
			error("Ожидается LF {");
			//return;
		}
		exprList();
		if(currTok.name==Terminal.EXIT) return;
		if(currTok.name!=Terminal.RF){
			error("block() Ожидается RF }"); // '}'
			//return;
		}
	}
	
	// Пропуск блока {}
	private boolean skipBlock() throws Exception{
		int num = 0;
		Terminal ch;
		
		do{
			ch=getToken();
			if(num==0 && ch!=Terminal.LF) error("Ожидается {");
			if(ch==Terminal.LF) num++;
			if(ch==Terminal.RF) num--;
			if(num==0) return true;
		}while(num>0);
		error("Забыли токен токен LF {");
		return false;//Ошибка
	}
	
	// Функции, не возвращающие значение (void): print, add, del, reset, help, state
	private boolean voidFunc() throws Exception{
		boolean isNeedReadEndToken=true; // Нужно ли считать токен END для анализа в exprList или он уже считан expr
		switch(currTok.name){
		case PRINT: 
			print(); // expr() оставляет токен в currTok.name ...
			isNeedReadEndToken=false; //...и здесь мы отменяем считывние нового токена для проверки END в expr_List
			break;
		case ADD:
			add(); // expr() оставляет токен в currTok.name ...
			isNeedReadEndToken=false; //...и здесь мы отменяем считывние нового токена для проверки END в expr_List
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
		
		System.out.println();
		if(isNeedReadEndToken)
			getToken();
		return true;
	}
	
	// Выводит значение выражения expr либо всю таблицу переменных
	private void print() throws Exception{
		getToken();
		if(currTok.name==Terminal.END){ // a. если нет expression, то выводим все переменные
			if(table.isEmpty()){
				System.out.println("table is empty!");
			}else{
				System.out.println("[table]:");
				Iterator<Entry<String, Double>> it = table.entrySet().iterator();
				while (it.hasNext()){
					Entry<String, Double> li = it.next();
				    System.out.println(""+li.getKey() + " " + li.getValue());
				}
			}
		}else{ // b. выводим значение expression
			echoPrint = true;
			double v = expr(false); // expr() оставляет токен в currTok.name ...
			System.out.println("= "+v);
			echoPrint = false;
		}
	}
	
	// Добавляет переменную
	private void add() throws Exception{
		getToken();
		if(currTok.name==Terminal.USER_DEFINED_NAME){
			String varName = new String(stringValue); // ибо stringValue может затереться при вызове expr()
			getToken();
			if(currTok.name==Terminal.ASSIGN){
				table.put(varName, expr(true)); // expr() оставляет токен в currTok.name ...
			}else if(currTok.name==Terminal.END){
				table.put(varName, 0.0);
			}
			System.out.println("Создана переменная "+varName);
		}else error("add: ожидается имя переменной");
	}
	
	// Удаляет переменную
	private void del() throws Exception{
		getToken();
		if(currTok.name==Terminal.MUL){
			table.clear();
			System.out.println("Все переменные удалены!");
		}else if(currTok.name!=Terminal.USER_DEFINED_NAME) error("После del ожидается токен имя_переменной NAME либо токен MUL *");
		
		if(!table.isEmpty()){
			if(!table.containsKey(stringValue)){
				System.out.println("del: Переменной "+stringValue+" нет в таблице переменных!");
			}else{
				table.remove(stringValue);
				System.out.println("del: Переменная "+stringValue+" удалена.");
			}
		}
	}
	
	private boolean setname(Terminal name){
		switch(name){
		case ARGS_AUTO_END: case AUTO_END: case PRINT_TOKENS:
		case PRECISION: case ERRORS: case STRICTED: case AUTO_PRINT: case GREEDY_FUNC:
			return true;
		default: return false;
		}
	}
	
	// Установка опций
	private void set() throws Exception{
		getToken();
		if(!setname(currTok.name)) error("set: неверная опция");
		Terminal what = currTok.name;
		
		getToken();
		if(currTok.name!=Terminal.ASSIGN) error("set: ожидается =");
		
		getToken();
		options.set(what, currTok); // Поскольку мы отправили Текущий токен ... expr не пройдёт!
	}
	
	// Сброс опций или таблицы переменных
	private void reset() throws Exception{
		getToken();
		switch(currTok.name){
		case MUL:
			options.resetAll();
			resetTable();
			System.out.println("Всё сброшено.");
			break;
		case TABLE:
			resetTable();
			System.out.println("Таблица переменных сброшена.");
			break;
		default :
			if(!setname(currTok.name)) error("reset: неверная опция");
			options.reset(currTok.name);
		}
	}
	
	void resetTable(){
		table.clear();
		table.put("e", Math.E);
		table.put("pi", Math.PI);
		table.put("ans", lastResult);
	}
	
	// Помощь по грамматике
	void help(){
		System.out.println
		("Грамматика(не актуальная):\n"+
			"program:\n"+
				"\texpr_list* EXIT\n"+
			"\n"+
			"expr_list:\n"+
				"\texpr END\n"+
				"\tvoid_func END\n"+
				"\tif_ END\n"+

			"\n"+
			"if_:\n"+
				"\t\"if\" '('expr')' '{' expt_list '}'\n"+
				"\t\"if\" '('expr')' '{' expt_list '}' \"else\" '{' expt_list '}'\n"+
			"expr:\n"+
				"\texpr + term\n"+
				"\texpr - term\n"+
				"\tterm\n"+
			"\n"+
			"term:\n"+
				"\tterm / pow\n"+
				"\tterm * pow\n"+
				"\tpow\n"+
			"\n"+
			"pow:\n"+
				"\tpow ^ prim\n"+
				"\tprim\n"+
			"\n"+
			"prim:\n"+
				"\tNUMBER\n"+
				"\tNAME\n"+
				"\tNAME = expr\n"+
				"\t-prim\n"+
				"\t(expr)\n"+
				"\tfunc\n"+
			"\n"+
			"func:\n"+
				"\tsin expr\n"+
				"\tcos expr\n"+
			"\n"+
			"void_func:\n"+
				"\tprint\n"+
				"\tadd\n"+
				"\tdel\n"+
				"\treset\n"+
				"\tset\n"+
				"\tunset\n"+
				"\thelp\n"+
				"\tstate\n\n"
			);

	};
	
	// Выводит информацию о текущем состоянии
	void state(){
		System.out.println("Текущее состояние:\nПеременных "+table.size());
		options.printAll();
	};
	
	// Бросает исключение MyException и увеичивает счётчик ошибок
	public void error(String string) throws MyException{
		int errors = options.getInt(Terminal.ERRORS);
		errors++;
		options.set(Terminal.ERRORS, errors);
		//System.err.println("error: "+string);
		throw new MyException(string);
	}
	
	public Token getCurrTok() {// Возвращает Название текущего токена для проверок в вызывающем методе main
		return currTok;
	}
		
	public double lastResult=Double.NaN;
	
	// Нижеприведённые методы нужны только лишь для тестов и отладки
	public int getErrors() {
		return options.getInt(Terminal.ERRORS);
	}

}