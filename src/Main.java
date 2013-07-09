import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.NoSuchElementException;


public class Main {
	
	public static void main(String[] args) throws Exception {
    	// Применяем параметры командной строки
    	boolean lexerAutoEnd = true; // Автодобавление токена END в конце считанной последовательности, чтобы не добавлять его вручную при интерактивном вводе
    	boolean lexerPrint = false; // Вывод найденных лексем 
    	boolean interactiveMode = true;
    	boolean autoPrint = true;
    	boolean greedyFunc = false;
    	
    	if(args.length > 0) for(String s: args){
	    	if(s.equals("lexer_auto_end")) lexerAutoEnd = true;
	    	if(s.equals("no_lexer_auto_end")) lexerAutoEnd = false;
	    		
	    	if(s.equals("lexer_print")) lexerPrint = true;
	    	if(s.equals("no_lexer_print")) lexerPrint = false;
	    		
	    	if(s.equals("interactive_mode")) interactiveMode = true;
	    	if(s.equals("no_interactive_mode")) interactiveMode = false;
	    		
	    	if(s.equals("autoprint")) autoPrint = true;
	    	if(s.equals("no_autoprint")) autoPrint = false;
	    	
	    	if(s.equals("greedy_func")) greedyFunc = true;
	    	if(s.equals("no_greedy_func")) greedyFunc = false;
	    }// Применяем параметры командной строки
    	
    	
    	
    	
    	
    	System.out.println("Добро пожаловать в интерпретатор.");
    	BufferedReader stdin = null;
		stdin = new BufferedReader(new InputStreamReader(System.in));
	    Lexer l = new Lexer(stdin, lexerAutoEnd, interactiveMode);
	    Parser p = new Parser(l, true, false);
	    
	    while(true){
	    	//ArrayList<Token> al = null;
	    	//al=l.getTokens();
	    	//l.printTokens();
	    	//for(Token t: al){System.out.println(t);}
	    	p.exprList();
	    	if (p.getCurrTok()==Names.RF) Parser.error("Неправильный выход из expr_list, из-за лишней RF }");
	    	if (p.getCurrTok()==Names.EXIT) break;
	    	// TODO сделать возможность работы после ошибки
	    }
	    
    	//System.out.println("Выход...");

    }
}