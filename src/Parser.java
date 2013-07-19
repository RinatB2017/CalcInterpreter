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
	Token currTok = null; // текущий обрабатываемый токен, изменяется методом getToken()
	Buffer buf = null;
	Options options = null;
	HashMap<String, Double> table; // Таблица переменных
		
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
	private Names getToken() throws Exception{
		if(echoPrint  &&  currTok.name != Names.END)
			System.out.print(currTok.value+' '); // Печать предыдущего считанного токена, т. к. в exprList() токен уже считан до включения флага echoPrint
		
		currTok=buf.getToken();
		
		if(currTok.name==Names.NUMBER)
			numberValue= Double.parseDouble(currTok.value);
		if(currTok.name==Names.NAME)
			stringValue=currTok.value;
		return currTok.name;
	}
	
	// Главный метод - список выражений - с него начинается парсинг
	public void exprList() throws Exception{
		echoPrint = false; // Отменяем эхопечать токенов, если она не была отменена из-за вызова error() -> MyException
		boolean get=true;//нужно ли считывать токен в самом начале
	    
		while (true)
	    {
	        if(get) getToken();
	        get=true;

	        if (currTok.name==Names.EXIT) break;
			if (currTok.name==Names.END) continue;
			if (currTok.name==Names.RF) return;
			if (voidFunc()){
				;
			}else if (currTok.name==Names.IF){
				get=if_();
			}else{ // expr
				if(options.getBoolean(ParserOpts.AUTO_PRINT)) { // TODO исправить глюк autoprint из-за lexerAutoEnd=false : сделать очередь сообщений
					echoPrint=true;
					double v = expr(false);
					System.out.println("= " + v + '\n');
					echoPrint=false;
				}else{
					expr(false);
				}
				if (currTok.name!=Names.END) error("Не верный конец, нужен токен END ;");
			}
			table.put("ans", lastResult);
	    }
	}

	// обрабатывает первичное
	double prim(boolean get) throws Exception
	{
		if(get) getToken();

	    switch (currTok.name)
	    {
	    case NUMBER:{ // константа с плавающей точкой
			double v = numberValue;
	        getToken();//получить следующий токен ...
	        return v;
	    	}
	    case NAME:case SIN:case COS:
	    {
			if(func(/*y*/)) return y;
			String name = new String(stringValue);
			
			if(!table.containsKey(name))
				if(options.getBoolean(ParserOpts.STRICTED)) error("Запрещено автоматическое создание переменных в stricted-режиме");
				else{
					table.put(name, 0.0); // Если в table нет переменной, то добавляем её со зачением 0.0
					if(!echoPrint) System.out.println("Создана переменная "+name);
				}
			double v=table.get(name);
	        if (getToken()==Names.ASSIGN){
	        	v = expr(true);
	        	table.put(name, v);}
	        return v;
	    	}
	    case MINUS:{ // унарный минус
	        return -prim(true);
	    	}
	    case LP:{
	        double e = expr(true);
	        if (currTok.name!=Names.RP) error("требуется )");
	        getToken(); // пропустить ')' //получить следующий токен ...
	        return e;
	    	}
	    default:{
	        error("требуется первичное_выражение (нетерминал prim)");
	        return 0;
	    	}
	    }
	}
		
	double y; // для временного хранения результата func()
	
	// функции, возвращающие значение (non-void): sin, cos
	boolean func() throws Exception{
		switch(currTok.name){
		case SIN: // для режима greedyFunc
		case COS:
		{
			Names funcName = currTok.name; // Запоминаем для дальнейшего использования
			if(!options.getBoolean(ParserOpts.GREEDY_FUNC)){
				getToken(); // Проверка наличия (
				if(currTok.name!=Names.LP) error("Ожидается (");
			}
					
			// "Настоящая" обработка sin и cos
			switch(funcName){
				case SIN:
					y = Math.sin(expr(true)); // следующий токен END для prim()<-term()<-expr()<-expr_list() получен в этом вызове expr()
					break;
				case COS:
					y = Math.cos(expr(true)); // следующий токен END для prim()<-term()<-expr()<-expr_list() получен в этом вызове expr()
					break;
				default:
					error("Не хватает обработчика для функции " + funcName.toString());
			}// "Настоящая" обработка sin и cos
			
			if(!options.getBoolean(ParserOpts.GREEDY_FUNC)){
				 // Проверка наличия )
				if(currTok.name!=Names.RP) error("Ожидается )");
				getToken();
			}
			
			// Округление до привычных значений
			y = (doubleCompare(y, 0)) ? 0 : y;
			y = (doubleCompare(y, 0.5)) ? 0.5 : y;
			y = (doubleCompare(y, -0.5)) ? -0.5 : y;
			y = (doubleCompare(y, 1)) ? 1 : y;
			y = (doubleCompare(y, -1)) ? -1 : y;
			return true;
		}
		
		default:
			return false;
		}
	}
	
	
	boolean doubleCompare(double a, double b){
		if (Math.abs(a-b) < 1.0/Math.pow(10, options.getInt(ParserOpts.PRECISION))) return true;
		return false;
	}


	// умножает и делит
	double term(boolean get) throws Exception
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
	double expr(boolean get) throws Exception
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
	double power(boolean get) throws Exception{
	    double left = factorial(get);
	    for(;;)
	        // ``вечно''
	        switch(currTok.name)
	        {
	        case POW:
	            // случай '+'
	            //left += prim(true);
	            left = Math.pow(left, factorial(true));
	            break; // этот break относится к switch
	        default:
	            return left;
	        }
	}
	
	double factorial(boolean get) throws Exception
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
	
	boolean if_() throws Exception{
		getToken();
		if(currTok.name!=Names.LP){ // '('
			error("Ожидается LP (");

		}
		double condition = expr(true);
		// expr отставляет не обработанный токен в curr_tok.name, здесь мы его анализируем
		if(currTok.name!=Names.RP) { // ')'
			error("Ожидается RP )");
		}
		if(!doubleCompare(condition, 0)){	// если condition==true
			block();
			if(currTok.name==Names.EXIT) return false; // возвращаем false, чтобы функиии, вызвавшие if_() увидели EXIT
		}else{				// если condition==false
			skipBlock();	// пропусить true brach {}
		}
		
		getToken(); //считываем очередной токен

		if(currTok.name==Names.ELSE){
			if(doubleCompare(condition, 0)){
				block();
				if(currTok.name==Names.EXIT) return false; // возвращаем false, чтобы функимии, вызвавшие if_() увидели EXIT
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
	void block() throws Exception{
		getToken();
		if(currTok.name!=Names.LF){ // '{'
			error("Ожидается LF {");
			//return;
		}
		exprList();
		if(currTok.name==Names.EXIT) return;
		if(currTok.name!=Names.RF){
			error("block() Ожидается RF }"); // '}'
			//return;
		}
	}
	
	// Пропуск блока {}
	boolean skipBlock() throws Exception{
		int num = 0;
		Names ch;
		
		do{
			ch=getToken();
			if(num==0 && ch!=Names.LF) error("Ожидается {");
			if(ch==Names.LF) num++;
			if(ch==Names.RF) num--;
			if(num==0) return true;
		}while(num>0);
		error("Забыли токен токен LF {");
		return false;//Ошибка
	}
	
	// функции, не возвращающие значение (void): print, del, reset, help, state
	boolean voidFunc() throws Exception{
		switch(currTok.name){
		case PRINT: {
			getToken();
			if(currTok.name==Names.END){ // a. если нет expression, то выводим все переменные
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
				
				double v = expr(false);
				System.out.println("= "+v);
				//lastResult = v;
				// expr() оставляет токен в currTok.name, мы здесь его анализируем...
				if (currTok.name!=Names.END) error("Не верный конец, нужен токен END ;");
				
				echoPrint = false;
			}
			System.out.println();
			return true; //...и здесь же выходим
		} //break;
		
		case ADD: {
			getToken();
			if(currTok.name==Names.NAME){
				String varName = new String(stringValue); 
				getToken();
				if(currTok.name==Names.ASSIGN){
					table.put(varName, expr(true));
				}else if(currTok.name==Names.END){
					table.put(varName, 0.0);
				} else error("Неверное использование ADD: правильно так: add VARIABLE_NAME; add VARIABLE_NAME = expr;");
				System.out.println("Создана переменная "+varName);
				// expr() оставляет токен в currTok.name, мы здесь его анализируем...
				if (currTok.name!=Names.END) error("Не верный конец, нужен токен END ;");

			}else error("Неверное использование ADD: правильно так: add VARIABLE_NAME; add VARIABLE_NAME = expr;");
			return true; //...и здесь же выходим
		} //break;
		
		case DEL: {
			getToken();
			if(currTok.name==Names.MUL){
				table.clear();
				System.out.println("Все переменные удалены!");
			}else if(currTok.name!=Names.NAME) error("После del ожидается токен имя_переменной NAME либо токен MUL *");
			
			if(!table.isEmpty()){
				if(!table.containsKey(stringValue)){
					System.out.println("del: Переменной "+stringValue+" нет в таблице переменных!");
				}else{
					table.remove(stringValue);
					System.out.println("del: Переменная "+stringValue+" удалена.");
				}
			}
		} break;
		
		case RESET: {
			getToken();
			if(currTok.name==Names.MUL){ // Если reset * то сбрасываем всё
				System.out.println("Таблица переменных и все насройки сброшены!");
				options.resetAll();
				resetTable();
			}else if(currTok.name==Names.NAME){
				reset(stringValue);
			}else error("После reset ожидается токен имя_переменной NAME либо токен MUL *");
		} break;
		
		case SET:{
			getToken();
			if(currTok.name==Names.NAME){
				if(stringValue.equals("stricted")){
					//stricted=true;
					System.out.println("Автоматическое создание переменных при обращении к ним запрещено.");
				}
				else error("После set оказался токен NAME, указывающий на несуществующую системную переменную, разрешённые значения: stricted.");
			}else error("После set ожидается токен имя_переменной NAME");
		} break;
		
		case UNSET:{
			getToken();
			if(currTok.name==Names.NAME){
				if(stringValue.equals("stricted")){
					//stricted=false;
					System.out.println("Автоматическое создание переменных разрешено.");
				}
				else error("После unset оказался токен NAME, указывающий на несуществующую системную переменную, разрешённые значения: stricted.");
			}else error("После unset ожидается токен имя_переменной NAME");
		} break;
		
		case HELP:{
			help();
		} break;
		
		case STATE: {
			state();
		} break;
		
		default: // не совпало ни с одним именем функции
			return false;
		}
		
		System.out.println();
		
		getToken();
		if (currTok.name!=Names.END) error("Не верный конец, нужен токен END ;");
		return true;
	}
		
	// Бросает исключение MyException и увеичивает счётчик ошибок
	public void error(String string) throws MyException{
		int errors = options.getInt(ParserOpts.ERRORS);
		errors++;
		options.set(ParserOpts.ERRORS, errors);
		//System.err.println("error: "+string);
		throw new MyException(string);
	}
	
	public enum what{ALL, TABLE, ERRORS, STRICTED};
	
	// Сброс
	void reset(String what){
		/*if(stringValue.equals("table")) reset(what.TABLE);
		else if(stringValue.equals("errors")) reset(what.ERRORS);
		else if(stringValue.equals("stricted")) reset(what.STRICTED);
		else error("После reset оказался токен NAME, указывающий на несуществующую системную переменную, разрешённые значения: table, errors.");
		*/
		/*switch(w){
			case ALL:
				table.clear();
				table.put("e", Math.E);
				table.put("pi", Math.PI);
				//errors=0;
				//precision = 5;
				//stricted = false;
				System.out.println("reset *: Таблица переменных и счётчик ошибок установлены в исходное состояние.");
				break;
			case TABLE:
				table.clear();
				table.put("e", Math.E);
				table.put("pi", Math.PI);
				System.out.println("reset: Таблица переменных установлена в исходное состояние.");
				break;
			case ERRORS:
				//errors=0;
				System.out.println("reset: Счётчик ошибок установлен в исходное состояние.");
				break;
			case STRICTED:
				//stricted = false;
				System.out.println("reset: Режим stricted установлен в исходное состояние.");
			default:
				break;
		}*/
		System.out.println("нужно убрать эту заглушку");
	};
	
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
		System.out.println("Текущее состояние:\nПеременных: "+table.size());
		options.printAll();
	};
	
	public Token getCurrTok() {// Возвращает Название текущего токена для проверок в вызывающем методе main
		return currTok;
	}
		
	public double lastResult=Double.NaN;
	
	// Нижеприведённые методы нужны только лишь для тестов и отладки
	public int getErrors() {
		return options.getInt(ParserOpts.ERRORS);
	}

}