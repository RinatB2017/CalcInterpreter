package my.pack;
/* 
 * Начало: Лексер-Java:			lab-2-tokens-2013-03-30--03-08.zip
 * Начало: Парсер&Лексер-C++: 	calculatorsStroustroup-2013-04-29--16-54.zip
 * Начало: Парсер-Java: 		CalcInterpreter-java-2013-05-10--00-18-relese.zip
 * */

import java.io.BufferedReader;
import java.io.InputStreamReader;

import lexer.Lexer;
import lexer.Tag;

public class Executor {
	public static void main(String[] args) throws Exception {
    	System.out.println("Добро пожаловать в интерпретатор.\n");
    	BufferedReader stdin = null;
		stdin = new BufferedReader(new InputStreamReader(System.in));
		
		Options o = new Options();
		Lexer l = new Lexer();
	    Buffer b = new Buffer(l,  args, stdin,  o);
	    Parser p = new Parser(b, o);
	    	    
	    while(true){
		    try{
			    p.exprList();
			   	if (p.getCurrTok().name==Tag.RF) p.error("Неправильный выход из expr_list, из-за лишней RF }");
			   	if (p.getCurrTok().name==Tag.EXIT) break;
		    }catch(MyException m){
		    	System.err.println("Ошибка на " + b.getLineNum() + " на токене №" + b.getTokNum() + " "+p.getCurrTok() + ":");
		    	System.err.println(m.getMessage() + "\n");
		    	continue;
		    }catch(Exception e){
		    	System.err.println("Критическая ошибка на "+b.getLineNum()+", продолжение работы невозможно.");
		    	System.err.println(e.getMessage() + "\n");
		    	e.printStackTrace();
		    	break;//while
		    }
	    }//while
	    System.out.println("Выход...");
    }
}