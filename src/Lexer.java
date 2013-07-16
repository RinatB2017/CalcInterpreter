/* Лексер считывает строки из stdin,
 * разбивая на токены в соответствии с грамматикой и помещает токены в ArrayList.
 * Пробельные символы игнорируются,
 * символы, не совпавшие ни с одной маской – добавляются как ILLEGAL_TOKEN.
 * Токены передаются в парсер по одному с каждым вызовом Lexer.getToken()
 */

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
	
	class TokenMask { // Элемент Регексп-Имя
		public String regexp;
		public Names name;
		Pattern pattern;
		
		public TokenMask(String r, Names n) {
			regexp = r;
			name = n;
			pattern = Pattern.compile(regexp); 
		}
	}

	ArrayList<TokenMask> masks; // массив масок токенов <рег_выраж, название>
	

	boolean withinComment=false; // Индикатор нахождения внутри комментария для getToken()
	
	// Конструктор, добавляет маски, инициализирует ссылки
	Lexer() {
		// Инициализируем
		masks = new ArrayList<TokenMask>();

		// Заполняем регулярки
		// строчные терминалы должны быть первыми, т. к. isMatchWithMasks() возвращает истину на первом совпадении
		this.addItem("sin", Names.SIN);
		this.addItem("cos", Names.COS);
		this.addItem("[!]{1}", Names.FACTORIAL);
		
		this.addItem("exit", Names.EXIT);
		this.addItem("quit", Names.EXIT);
		this.addItem("shutdown", Names.EXIT);
		this.addItem("print", Names.PRINT);
		this.addItem("add", Names.ADD);
		this.addItem("del", Names.DEL);
		this.addItem("reset", Names.RESET);
		this.addItem("set", Names.SET);
		this.addItem("unset", Names.UNSET); 
		this.addItem("help", Names.HELP);
		this.addItem("state", Names.STATE);
		this.addItem("if", Names.IF);
		this.addItem("else", Names.ELSE);
		
		this.addItem("\\s+", Names.SKIPABLE); // пробелы
		this.addItem("//.*$", Names.SKIPABLE); // комментарии "//" и символы в строке после
		this.addItem("/\\*", Names.L_COMMENT); // начало многострокового комментария "/*"
		this.addItem("\\*/", Names.R_COMMENT); // конец многострокового комментария "*/"
		
		this.addItem("[A-Za-z_]+[A-Za-z_0-9]*", Names.NAME);
		this.addItem("[0-9]{1,}[\\.]{0,1}[0-9]{0,}", Names.NUMBER); // Здесь - заэкранированная точка
		this.addItem("\\+", Names.PLUS);
		this.addItem("-", Names.MINUS);
		this.addItem("\\*", Names.MUL);
		this.addItem("/", Names.DIV);
		this.addItem("\\^", Names.POW);
		this.addItem(";", Names.END);
		this.addItem("=", Names.ASSIGN);
		this.addItem("\\(", Names.LP);
		this.addItem("\\)", Names.RP);
		this.addItem("\\{", Names.LF);
		this.addItem("\\}", Names.RF);
		
		//this.addItem("//.+$", Names.SKIPABLE); // пробелы и комментарии
		//this.addItem("\\s+|//$", Names.SKIPABLE); // пробелы и комментарии
		
		this.addItem(".+", Names.ILLEGAL_TOKEN); // Должен добавляться в самом конце, чтобы не перехватывал валидные токены
	}
	
	Token Cur=null; // Текущий полученный токен
	
	
	// Сканирует строку, перезаписывает массив токенов tokens найдеными токенами
	public void scan(final String string, final ArrayList<Token> tokens) throws Exception {
		tokens.clear();
				
		if(string==null || string.isEmpty()){
			throw new Exception("scan(): argument string is null or empty.");
		}
		
		// Выделяем подстроку
		int start = 0; // индекс первого символа, который войдёт в подстроку
		int end = 1; // индекс первого символа за концом подстроки, который не войдёт в подстроку
		Token Prev = null; // Предыдущий токен 
		boolean prevMatched=false;
		
		for (boolean isContinue=true; isContinue; ) { // Наращиваем подстроки посимвольно
			boolean isNeedAddToken=false;
			String substr = string.substring(start, end);
			
			boolean matched = isMatchWithMasks(substr); // matched==true гаранирует что Cur!=null
			if(Prev==null && matched) Prev=Cur; // Для нормальной работы с первой полученной подстрокой
			
			if (matched && Cur.name==Prev.name) { // если подстрока совпала с маской токена...			
				// Для следующей итерации цикла
				Prev = Cur; // настраиваем ссылку Prev на объект по ссылке Cur; это гаранирует что Prev!=null
				prevMatched = true;
				
				// ...то пробуем добавить ещё символ к подстроке:
				if(end<string.length()){
					end++;// инкремент end,
				}else{// невозможность дальнейшего захвата символов (для start и end) - во входной строке не осталось символов
					isContinue = false; // выходим, когда просмотрели всю входную строку
					isNeedAddToken=true;
				}

			} else { // ни с одним регекспом подстрока не совпала либо резко поменялось имя токена NAME -> EXIT, NAME -> IF, ...
				if(matched){ // если для новой подстроки резко поменялось имя токена NAME -> EXIT
					if(Cur.name==Names.ILLEGAL_TOKEN){ // случай PRINT"print" -> ILLEGAL_TOKEN"print " 
						isNeedAddToken = true; // добавляем PRINT
					}else{ // нормальный случай NAME"i" -> IF"if"
						Prev=Cur; // Настраиваем ссылку Prev для корректной работы блока if (matched && Cur.name.equals(Prev.name))
						continue; // делаем пропуск, для того чтобы нормально сработал механизм анализа&добавления при первом несовпадении
						// поскольку из-за пропуска мы не обновили start и end,
						// происходит ещё однин проход для той же подстроки(но с новым Prev.name==IF,EXIT, ...),
						// в котором устанавливается prevMatched
					}
				}
				else if(prevMatched){ // если были совпадения(с одной маской) в предыдущих подстроках(на предыд итерациях)
					isNeedAddToken = true;
				}
				
				// ни с одним регекспом подстрока не совпала
				if((matched==false && prevMatched==false)){
					throw new Exception(); // Если зашли сюда, то ошибка. Проверь регулярку на ILLEGAL_TOKEN.
				}
			}
			
			if(isNeedAddToken){
				//System.out.println("Adding token name=\""+Prev.name+ "\", value=\"" + Prev.value+"\"\n");
				switch(Prev.name){
					case SKIPABLE:
						break;
					case L_COMMENT:
						withinComment=true;
						break;
					case R_COMMENT:
						withinComment=false;
						break;
					default:
						if(!withinComment){
							tokens.add(new Token(Prev.name, Prev.value));
						}
						break;
				}
				isNeedAddToken = false;
				prevMatched=false;
				
				start = start + Prev.value.length();
				Prev=null;
				
				if (start >= string.length()) isContinue=false; // выходим, когда просмотрели всю входную строку
				else end = start + 1; // не обязательно, ибо end уже сдвинут при попытке захватить очередной символ
			}
		}//for
	}

	// Сопоставляет подстроку с масками-регэкспами, при первом же совпадении
	// возвращает true и (ре)инициализирует ссылку Cur
	public boolean isMatchWithMasks(String substr) {
		// Этот метод генерит новые объекты и настраивает на них ссылку Cur
		//System.out.println("Trying \""+substr+"\"...");
		for (TokenMask tm : masks){
			Matcher myMatcher = tm.pattern.matcher(substr); // Ищет совпадения
			if (myMatcher.matches()) {
				// Эта ссылка должна быть полем класса, а не аргументом метода, для того чтобы ниженаписанное присвоение было видно в scan() 
				// http://docs.oracle.com/javase/tutorial/java/javaOO/arguments.html
				Cur = new Token(tm.name, substr); 
				
				//System.out.println("" + Cur.name + " " + Cur.value+"\n");
				return true;
			} else {
				Cur=null;
			}
		}
		//System.out.println("NOT passed\n");
		return false;
	}

	
	
	
	private void addItem(String regexp, Names name) {
		masks.add(new TokenMask(regexp, name));
	}

	public void printMasks() {
		System.out.print("[lexer]: ");
		System.out.println("ALL masks:");
		//System.out.println("ALL masks:\n<name> <regexp>\n");
		for (TokenMask t : masks) {
			System.out.println("" + t.name + " " + t.regexp);
		}
		System.out.println();
	}
	
	
	/*
	 * "{n}" - n раз встретится символ
	 * "{n, m}" - символ встретится от n до m раз 
	 * "[a-zA-Z]{1}[a-zA-Z\\d\\u002E\\u005F]+@([a-zA-Z]+\\u002E){1,2}((net)|(com)|(org))" - for email
	 * "." - любой символ
	 * "?" is {0,1}
	 * "*" is {0,} 
	 * "+" is {1,}
	 */
}
