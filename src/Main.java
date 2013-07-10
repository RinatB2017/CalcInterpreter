/* 
 * Начало: Лексер-Java:			lab-2-tokens-2013-03-30--03-08.zip
 * Начало: Парсер&Лексер-C++: 	calculatorsStroustroup-2013-04-29--16-54.zip
 * Начало: Парсер-Java: 		CalcInterpreter-java-2013-05-10--00-18-relese.zip
 * */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
	public static void main(String[] args) throws Exception {
    	// Применяем параметры командной строки
    	boolean lexerAutoEnd = true; // Автодобавление токена END в конце считанной последовательности, чтобы не добавлять его вручную при интерактивном вводе
    	boolean lexerPrintTokens = false; // Вывод найденных лексем 
    	boolean autoPrint = true;
    	boolean greedyFunc = false;
    	
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
    	
    	@SuppressWarnings("rawtypes")
		HashSet<Option> options = new HashSet<Option>();
    	options.add(new Option<Boolean>(Names.STRICTED, false));
    	options.add(new Option<Boolean>(Names.LEXER_AUTO_END, false));
    	options.add(new Option<Boolean>(Names.LEXER_PRINT_TOKENS, false));
    	options.add(new Option<Boolean>(Names.AUTO_PRINT, true));
    	options.add(new Option<Boolean>(Names.GREEDY_FUNC, false));
    	
    	
    	System.out.println("Добро пожаловать в интерпретатор.");
    	BufferedReader stdin = null;
		stdin = new BufferedReader(new InputStreamReader(System.in));
	    Lexer l = new Lexer(stdin, lexerAutoEnd, lexerPrintTokens);
	    Parser p = new Parser(l, autoPrint, greedyFunc);
	    p.reset(Parser.what.ALL);
	    
	    while(true){
		    try{
			    p.exprList();
			   	if (p.getCurrTok()==Names.RF) Parser.error("Неправильный выход из expr_list, из-за лишней RF }");
			   	if (p.getCurrTok()==Names.EXIT) break;
		    }catch(MyException m){
		    	System.err.println("Ошибка на строке "+l.getLineNum());
		    	//m.printStackTrace();
		    	continue;
		    }catch(Exception e){
		    	System.err.println("Критическая ошибка на строке "+l.getLineNum()+", продолжение работы невозможно.");
		    	e.printStackTrace(); // TODO Debug-Mode
		    	break;//while
		    }
	    }//while
	    System.out.println("Выход...");
    }
}