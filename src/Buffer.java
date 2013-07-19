/* Буфер между лексером и парсером.
 * Нужен для соединения между собой
 * лексера, обрабатывающего строку и выдающего список токенов
 * и парсера, требующего один токен.
 * */

import java.io.BufferedReader;
import java.util.ArrayList;

public class Buffer {
	ArrayList<Token> tokens; // Массив токенов <название, значение>
	Lexer lexer;
	String[] args;
	Options options = null;
	
	// Конструктор
	public Buffer(Lexer lexer, String[] args, BufferedReader stdin, Options options){
		tokens = new ArrayList<Token>();
		this.stdin = stdin; // stdin=null используется при тестировании: сначала вызываем Lexer::scan("тестируемая строка"), затем Parser::exprList
		this.lexer=lexer;
		this.args=args;
		this.options=options;
	}
	
	long lineNum = 0;
	BufferedReader stdin=null;
	int tokNum=0;
	String str;
	int numAgrs=0;
	//private enum NowProcessed{NOTHING, ARGS, STDIN};
	//NowProcessed now=NowProcessed.NOTHING;
	
	// Главметод, гарантированно возвращает токен (в том числе Tokens.EXIT при невозможности дальнейшего считывания)
	public Token getToken() throws Exception {
		if(tokNum==tokens.size()){
			tokens.clear();
			tokNum=0;// Для нормальной работы ^
						
			str = null;
			
			do{
				lineNum++;
				
				//if
				
				if(numAgrs<args.length)
					str = args[numAgrs];
				else if(stdin!=null) // stdin==null Для тестов
					str = stdin.readLine(); // Считываем строку..., null когда строки закончились
				else return new Token(Names.EXIT, "") ; // когда исчерпали все токены в args и нельзя читать из stdin, то возвращаем EXIT
				
				if(str==null){
					return new Token(Names.EXIT, "") ;
					//throw new Exception("Лексер: закончились строки");
				}
				if(!str.isEmpty()) {
					lexer.scan(str, tokens);
					// autoending :)
					if(numAgrs<args.length) // Если args - не пустой массив и мы сейчас его обрабатываем
						if(options.getBoolean(BufferOpts.ARGS_AUTO_END)) tokens.add(new Token(Names.END, ";")); // Автодобавление токена END
					if(stdin!=null) // если есть поток ввода и мы сейчас его обрабатываем
						if(options.getBoolean(BufferOpts.AUTO_END)) tokens.add(new Token(Names.END, ";")); // Автодобавление токена END
					if(options.getBoolean(BufferOpts.PRINT_TOKENS)) printTokens();
				}
				if(numAgrs<args.length) numAgrs++;
			}while(tokens.isEmpty());
		}
		
		return tokens.get(tokNum++);
	}
	
	// Вывод найденных токенов
	public void printTokens() {
		System.out.print("lexer at line "+ lineNum+" ");
		if(!tokens.isEmpty()){
			System.out.println("\""+str+"\" found next tokens:");
			//System.out.println("<name> <value>\n");
			for (int i =0; i < tokens.size(); i++) {
				Token t = tokens.get(i);
				//System.out.println(""+i+ " " + t.name + " " + t.value);
				System.out.println(t);
			}
		}else
			System.out.println("Nothing found for \""+ str+"\".");
		//System.out.println();
	}
	
	public String getLineNum() {
		return ""+lineNum;
	}
	
	public int getTokNum() {
		return tokNum-1; // т. к . в getToken() используется ++
	}
	
	// Для тестов
	public void setArgs(String[] args){
		this.args = args;
	}
}
