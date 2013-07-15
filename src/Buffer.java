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
	
	// Конструктор
	public Buffer(Lexer lexer, String[] args, BufferedReader stdin, boolean lexerAutoEnd, boolean lexerPrintTokens){
		tokens = new ArrayList<Token>();
		this.stdin = stdin; // stdin=null используется при тестировании: сначала вызываем Lexer::scan("тестируемая строка"), затем Parser::exprList
		this.lexerAutoEnd = lexerAutoEnd;
		this.lexerPrintTokens = lexerPrintTokens;
		this.lexer=lexer;
	}
	
	long lineNum = 0;
	BufferedReader stdin=null;
	boolean lexerAutoEnd; // Автодобавление токена END в конце считанной последовательности, чтобы не добавлять его вручную при интерактивном вводе
	boolean lexerPrintTokens;
	int tokNum=0;
	String str;
	
	// Главметод, гарантированно возвращает токен (в том числе Tokens.EXIT при невозможности дальнейшего считывания)
	public Token getToken() throws Exception {
		if(tokNum==tokens.size()){
			tokens.clear();
			if(stdin==null) // Для тестов
				return new Token(Names.EXIT, "") ; // когда исчерпали все токены, то возвращаем EXIT
			
			// Для нормальной работы
			tokNum=0;
			
			str = null;
			do{
				lineNum++;
				str = stdin.readLine(); // Считываем строку...
				if(str==null){
					return new Token(Names.EXIT, "") ;
					//throw new Exception("Лексер: закончились строки");
				}
				if(!str.isEmpty()) {
					lexer.scan(str, tokens);
					if(lexerAutoEnd) tokens.add(new Token(Names.END, ";")); // Автодобавление токена END
					if(lexerPrintTokens) printTokens();
				}
			}while(tokens.isEmpty());
		}
		
		return tokens.get(tokNum++);
	}
	
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
	
	public long getLineNum() {
		return lineNum;
	}
	
	public int getTokNum() {
		return tokNum-1; // т. к . в getToken() используется ++
	}
}
