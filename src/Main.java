import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;


public class Main {
	
	public static void main(String[] args) {
    	// Применяем параметры командной строки
    	boolean lexerAutoEnd = true; // Автодобавление токена END в конце считанной последовательности, чтобы не добавлять его вручную при интерактивном вводе
    	boolean lexerPrint = false; // Вывод найденных лексем 
    	boolean interactiveMode = true;
    	boolean autoPrint = true;
    	
    	if(args.length > 0) for(String s: args){
	    	if(s.equals("lexer_auto_end")) lexerAutoEnd = true;
	    	if(s.equals("no_lexer_auto_end")) lexerAutoEnd = false;
	    		
	    	if(s.equals("lexer_print")) lexerPrint = true;
	    	if(s.equals("no_lexer_print")) lexerPrint = false;
	    		
	    	if(s.equals("interactive_mode")) interactiveMode = true;
	    	if(s.equals("no_interactive_mode")) interactiveMode = false;
	    		
	    	if(s.equals("autoprint")) autoPrint = true;
	    	if(s.equals("no_autoprint")) autoPrint = false;
	    }
    	// Применяем параметры командной строки

    	
    	System.out.println("Добро пожаловать в интерпретатор.");
    	System.out.println("Введите help для получения помощи по грамматике.");
    	

	    // Лексер. Первый аргумент - Автодобавление токена END в конце считанной последовательности
	    // второй аргумент - интерактивный режим
	    Lexer l = new Lexer(lexerAutoEnd, interactiveMode);
	    //Парсер
	    Parser p = new Parser(autoPrint);
	    p.reset(Parser.what.ALL);
	    BufferedReader stdin = null;
	    try{
	    	stdin = new BufferedReader(new InputStreamReader(System.in/*, "UTF-8"*/));
	    	String str = new String();
			
    				
			// Считываем строки из файла...
		    do{
		    	try{
		    		str = stdin.readLine(); // Считываем строку...
		    		if(str==null || str.isEmpty()) continue;
		    		l.scan(str); // Получаем из неё токены
		    		
		    		if(lexerPrint)
		    			l.printTokens();
			    	
		    		// Если мы в интерактивном режиме, то сразу выполняем полученную строку
		    		if(interactiveMode){
			    		p.addTokens(l.getTokens());
						p.exprList();
		    							
						//System.out.println("CurrTok is "+p.getCurr_tok());
						if (p.getCurrTok()==Names.EXIT) return;
						if (p.getCurrTok()==Names.RF) Parser.error("Неправильный выход из expr_list, возможно лишняя RF }");
					}
				}catch(MyException m){
					System.out.println("на токене №" + p.getTokNum() + " "+p.getCurrTok()+":`"+p.getCurrTokValue()+"`");
					System.out.println("Введите reset errors для сброса счётчика ошибок.\n");
					continue;
				}catch(NoSuchElementException nsee){
		    		System.out.println("Нет строки, возможно вы ввели CTRL+Z !");
		    		stdin.close();
		    		break;
		    	}
	    	}while(str!=null);
		    
		    // ...если мы в пакетном режиме, то выполнение полученных токенов происходит только один раз здесь
		    if(interactiveMode==false){
		    	try{
			    	p.addTokens(l.getTokens());
					p.exprList();
									
					if (p.getCurrTok()==Names.EXIT) return;
					if (p.getCurrTok()==Names.RF) Parser.error("Неправильный выход из expr_list, возможно лишняя RF }");
		    	}catch(MyException m){
		    		System.out.println("на токене №" + p.getTokNum() + " "+p.getCurrTok()+":`"+p.getCurrTokValue()+"`");
		    		System.out.println();
				}
		    }
		    
		    
    	}catch(Exception ex){
    		System.out.println("Перехваченно непредвиденное исключение; дальнейшая работа не возможна.");
    		System.out.println("exception: "+ex.toString());
    		ex.printStackTrace();
    	}finally{
    		try{ stdin.close();} catch(Exception ex){ System.out.println("Ошибка при закрытии файла");}
    	}
	    
    	System.out.println("Выход...");

    }
}