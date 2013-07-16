/* 
 * Начало: Лексер-Java:			lab-2-tokens-2013-03-30--03-08.zip
 * Начало: Парсер&Лексер-C++: 	calculatorsStroustroup-2013-04-29--16-54.zip
 * Начало: Парсер-Java: 		CalcInterpreter-java-2013-05-10--00-18-relese.zip
 * */

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
	public static void main(String[] args) throws Exception {
    	// TODO сделать класс опций и обработку их в виде "set lexer_auto_end"
    	boolean lexerAutoEnd = true; // Автодобавление токена END в конце считанной последовательности, чтобы не добавлять его вручную при интерактивном вводе
    	boolean lexerPrintTokens = false; // Вывод найденных лексем 
    	boolean autoPrint = true; // Автоматический вывод значений без print
    	boolean greedyFunc = false; // Жадные функции: всё написанное после имени ф-ии и до токена ; считается аргументом функции
    	if(args.length > 0) for(String s: args){
	    	if(s.equals("lexer_auto_end")) lexerAutoEnd = true;
	    	if(s.equals("no_lexer_auto_end")) lexerAutoEnd = false;
	    		
	    	if(s.equals("lexer_print")) lexerPrintTokens = true;
	    	if(s.equals("no_lexer_print")) lexerPrintTokens = false;
	    		
	    	if(s.equals("autoprint")) autoPrint = true;
	    	if(s.equals("no_autoprint")) autoPrint = false;
	    	
	    	if(s.equals("greedy_func")) greedyFunc = true;
	    	if(s.equals("no_greedy_func")) greedyFunc = false;
	    }// Применяем параметры командной строки
    	/*
		@SuppressWarnings("rawtypes")
		HashSet<Option> options = new HashSet<Option>();
    	options.add(new Option<Boolean>(Names.STRICTED, false));
    	options.add(new Option<Boolean>(Names.LEXER_AUTO_END, false));
    	options.add(new Option<Boolean>(Names.LEXER_PRINT_TOKENS, false));
    	options.add(new Option<Boolean>(Names.AUTO_PRINT, true));
    	options.add(new Option<Boolean>(Names.GREEDY_FUNC, false));
    	*/

    	System.out.println("Добро пожаловать в интерпретатор.");
    	BufferedReader stdin = null;
		stdin = new BufferedReader(new InputStreamReader(System.in));
		
	    Lexer l = new Lexer();
	    Buffer b = new Buffer(l,  args, stdin,  lexerAutoEnd, lexerPrintTokens);
	    Parser p = new Parser(b, autoPrint, greedyFunc);
	    p.reset(Parser.what.ALL);
	    
	    while(true){
		    try{
			    p.exprList();
			   	if (p.getCurrTok().name==Names.RF) Parser.error("Неправильный выход из expr_list, из-за лишней RF }");
			   	if (p.getCurrTok().name==Names.EXIT) break;
		    }catch(MyException m){
		    	System.err.println("Ошибка на строке №" + b.getLineNum() + " на токене №" + b.getTokNum() + " "+p.getCurrTok() + ":");
		    	System.err.println(m.getMessage() + "\n");
		    	continue;
		    }catch(Exception e){
		    	System.err.println("Критическая ошибка на строке "+b.getLineNum()+", продолжение работы невозможно.");
		    	System.err.println(e.getMessage() + "\n");
		    	e.printStackTrace();
		    	break;//while
		    }
	    }//while
	    System.out.println("Выход...");
    }
}