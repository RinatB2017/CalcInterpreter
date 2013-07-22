import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Лексер считывает строки, получаемые от буфера,
 * разбивает на токены в соответствии с грамматикой и
 * помещает токены в полученный по ссылке ArrayList
 * Пробельные символы игнорируются,
 * строки, не совпавшие ни с одной маской – добавляются как ILLEGAL_TOKEN.
 * @see Lexer#scan(String, ArrayList)
 * @author Ник
 */

public class Lexer {
	
	/**
	 * Элемент Регексп : Имя
	 */
	class TokenMask { 
		final String regexp;
		final Terminal name;
		final Pattern pattern;
		
		public TokenMask(String r, Terminal n) {
			regexp = r;
			name = n;
			pattern = Pattern.compile(regexp); 
		}
	}

	private ArrayList<TokenMask> masks; // массив масок токенов <рег_выраж, название>
	

	private boolean withinComment=false; // Индикатор нахождения внутри комментария для getToken()
	
	/**
	 *  Конструктор лексера, добавляет маски, инициализирует ссылки
	 */
	public Lexer() {
		// Инициализируем
		masks = new ArrayList<TokenMask>();

		// Заполняем регулярки
		// строчные терминалы должны быть первыми, т. к. isMatchWithMasks() возвращает истину на первом совпадении
		this.addItem("sin", Terminal.SIN);
		this.addItem("cos", Terminal.COS);
		this.addItem("!", Terminal.FACTORIAL);
		
		this.addItem("true", Terminal.TRUE);
		this.addItem("false", Terminal.FALSE);
		
		this.addItem("exit", Terminal.EXIT);
		this.addItem("quit", Terminal.EXIT);
		this.addItem("shutdown", Terminal.EXIT);
		this.addItem("print", Terminal.PRINT);
		this.addItem("add", Terminal.ADD);
		this.addItem("del", Terminal.DEL);
		this.addItem("reset", Terminal.RESET);
		this.addItem("set", Terminal.SET);
		this.addItem("help", Terminal.HELP);
		this.addItem("state", Terminal.STATE);
		this.addItem("if", Terminal.IF);
		this.addItem("else", Terminal.ELSE);
		
		this.addItem("\\s+", Terminal.SKIPABLE); // пробелы
		this.addItem("//.*$", Terminal.SKIPABLE); // комментарии "//" и символы в строке после
		this.addItem("/\\*", Terminal.L_COMMENT); // начало многострокового комментария "/*"
		this.addItem("\\*/", Terminal.R_COMMENT); // конец многострокового комментария "*/"
		
		this.addItem("args_auto_end", Terminal. ARGS_AUTO_END);
		this.addItem("auto_end", Terminal. AUTO_END);
		this.addItem("print_tokens", Terminal. PRINT_TOKENS);
		
		this.addItem("var_table", Terminal.TABLE);
		this.addItem("precision", Terminal. PRECISION);
		this.addItem("errors", Terminal. ERRORS);
		this.addItem("stricted", Terminal. STRICTED);
		this.addItem("auto_print", Terminal. AUTO_PRINT);
		this.addItem("greedy_func", Terminal. GREEDY_FUNC);
		
		this.addItem("[A-Za-z_]+[A-Za-z_0-9]*", Terminal.USER_DEFINED_NAME);
		this.addItem("[0-9]{1,}[\\.]{0,1}[0-9]{0,}", Terminal.NUMBER); // Здесь - заэкранированная точка
		this.addItem("\\+", Terminal.PLUS);
		this.addItem("-", Terminal.MINUS);
		this.addItem("\\*", Terminal.MUL);
		this.addItem("/", Terminal.DIV);
		this.addItem("\\^", Terminal.POW);
		this.addItem(";", Terminal.END);
		this.addItem("=", Terminal.ASSIGN);
		this.addItem("\\(", Terminal.LP);
		this.addItem("\\)", Terminal.RP);
		this.addItem("\\{", Terminal.LF);
		this.addItem("\\}", Terminal.RF);
				
		this.addItem(".+", Terminal.ILLEGAL_TOKEN); // Должен добавляться в самом конце, чтобы не перехватывал валидные токены
	}
	
	private Token Cur=null; // Текущий полученный токен
	
	
	/**
	 * Сканирует строку, перезаписывает массив токенов tokens найдеными токенами
	 * 
	 * @param string Входная строка
	 * @param tokens Ссылка на ArrayList, в который будут помещены найденные токены
	 * @throws Exception в случае отсутствия регэкспа на ILLEGAL_TOKEN
	 */
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
					if(Cur.name==Terminal.ILLEGAL_TOKEN){ // случай PRINT"print" -> ILLEGAL_TOKEN"print " 
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
					throw new Exception("Возможно нет регулярного выражения на ILLEGAL_TOKEN"); // Если зашли сюда, то ошибка. Проверь регулярку на ILLEGAL_TOKEN.
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
	private boolean isMatchWithMasks(String substr) {
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

	
	
	
	private void addItem(String regexp, Terminal name) {
		masks.add(new TokenMask(regexp, name));
	}
	
	/**
	 * Выводит все маски - пары Имя токена : Регулярное выражение
	 * @see Lexer.TokenMask
	 * */
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
