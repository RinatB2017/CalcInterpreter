package lexer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Лексер считывает строки, получаемые от буфера, разбивает на токены в
 * соответствии с грамматикой и помещает токены в полученный по ссылке ArrayList
 * Пробельные символы игнорируются, строки, не совпавшие ни с одной маской –
 * добавляются как ILLEGAL_TOKEN.
 * 
 * @see Lexer#scan(String, ArrayList)
 * @author Ник
 */

public final class Lexer {

	/**
	 * Элемент Регексп : Имя
	 */
	class TokenMask {
		final String regexp;
		final Tag name;
		final Pattern pattern;

		public TokenMask(String r, Tag n) {
			regexp = r;
			name = n;
			pattern = Pattern.compile(regexp);
		}
	}

	private ArrayList<TokenMask> masks; // массив масок токенов <рег_выраж,
										// название>

	private boolean withinComment = false; // Индикатор нахождения внутри
											// комментария для getToken()

	/**
	 * Конструктор лексера, добавляет маски, инициализирует ссылки
	 */
	public Lexer() {
		// Инициализируем
		masks = new ArrayList<TokenMask>();

		// Заполняем регулярки
		// строчные терминалы должны быть первыми, т. к. isMatchWithMasks()
		// возвращает истину на первом совпадении
		this.addItem("!", Tag.FACTORIAL);

		this.addItem("true", Tag.BOOLEAN);
		this.addItem("false", Tag.BOOLEAN);

		this.addItem("exit", Tag.EXIT);
		this.addItem("quit", Tag.EXIT);
		this.addItem("shutdown", Tag.EXIT);
		this.addItem("print", Tag.PRINT);
		this.addItem("add", Tag.ADD);
		this.addItem("var", Tag.ADD);
		this.addItem("del", Tag.DEL);
		this.addItem("reset", Tag.RESET);
		this.addItem("set", Tag.SET);
		this.addItem("help", Tag.HELP);
		this.addItem("state", Tag.STATE);
		this.addItem("if", Tag.IF);
		this.addItem("else", Tag.ELSE);

		this.addItem("\\s+", Tag.SKIPABLE); // пробелы
		this.addItem("//.*$", Tag.SKIPABLE); // комментарии "//" и символы
													// в строке после
		this.addItem("/\\*", Tag.L_COMMENT); // начало многострокового
													// комментария "/*"
		this.addItem("\\*/", Tag.R_COMMENT); // конец многострокового
													// комментария "*/"


		this.addItem("[A-Za-z_]+[A-Za-z_0-9]*", Tag.NAME);
		this.addItem("[0-9]+[0-9]*", Tag.INTEGER);
		this.addItem("[0-9]{1,}[\\.]{0,1}[0-9]{0,}", Tag.DOUBLE); // Здесь
																		// - заэкранированная
																		// точка
		this.addItem(",", Tag.COMMA);
		
		this.addItem("\\+", Tag.PLUS);
		this.addItem("-", Tag.MINUS);
		this.addItem("\\*", Tag.MUL);
		this.addItem("/", Tag.DIV);
		this.addItem("\\^", Tag.POW);
		this.addItem(";", Tag.END);
		this.addItem("=", Tag.ASSIGN);
		this.addItem("\\(", Tag.LP);
		this.addItem("\\)", Tag.RP);
		this.addItem("\\{", Tag.LF);
		this.addItem("\\}", Tag.RF);

		this.addItem(".+", Tag.ILLEGAL_TOKEN); // Должен добавляться в
													// самом конце, чтобы не
													// перехватывал валидные
													// токены
	}

	private Token Cur = null; // Текущий полученный токен

	/**
	 * Сканирует строку, перезаписывает массив токенов tokens найдеными токенами
	 * 
	 * @param string
	 *            Входная строка
	 * @param tokens
	 *            Ссылка на ArrayList, в который будут помещены найденные токены
	 * @throws Exception
	 *             в случае отсутствия регэкспа на ILLEGAL_TOKEN
	 */
	public void scan(final String string, final ArrayList<Token> tokens)
			throws Exception {
		tokens.clear();

		if (string == null || string.isEmpty()) {
			throw new Exception("scan(): argument string is null or empty.");
		}

		// Выделяем подстроку
		int start = 0; // индекс первого символа, который войдёт в подстроку
		int end = 1; // индекс первого символа за концом подстроки, который не
						// войдёт в подстроку
		Token Prev = null; // Предыдущий токен
		boolean prevMatched = false;

		for (boolean isContinue = true; isContinue;) { // Наращиваем подстроки
														// посимвольно
			boolean isNeedAddToken = false;
			String substr = string.substring(start, end);

			boolean matched = isMatchWithMasks(substr); // matched==true
														// гаранирует что
														// Cur!=null
			if (Prev == null && matched)
				Prev = Cur; // Для нормальной работы с первой полученной
							// подстрокой

			if (matched && Cur.tag == Prev.tag) { // если подстрока совпала с
													// маской токена...
				// Для следующей итерации цикла
				Prev = Cur; // настраиваем ссылку Prev на объект по ссылке Cur;
							// это гаранирует что Prev!=null
				prevMatched = true;

				// ...то пробуем добавить ещё символ к подстроке:
				if (end < string.length()) {
					end++;// инкремент end,
				} else {// невозможность дальнейшего захвата символов (для start
						// и end) - во входной строке не осталось символов
					isContinue = false; // выходим, когда просмотрели всю
										// входную строку
					isNeedAddToken = true;
				}

			} else { // ни с одним регекспом подстрока не совпала либо резко
						// поменялось имя токена NAME -> EXIT, NAME -> IF, ...
				if (matched) { // если для новой подстроки резко поменялось имя
								// токена NAME -> EXIT
					if (Cur.tag == Tag.ILLEGAL_TOKEN) { // случай
																// PRINT"print"
																// ->
																// ILLEGAL_TOKEN"print "
						isNeedAddToken = true; // добавляем PRINT
					} else { // нормальный случай NAME"i" -> IF"if"
						Prev = Cur; // Настраиваем ссылку Prev для корректной
									// работы блока if (matched &&
									// Cur.name.equals(Prev.name))
						continue; // делаем пропуск, для того чтобы нормально
									// сработал механизм анализа&добавления при
									// первом несовпадении
						// поскольку из-за пропуска мы не обновили start и end,
						// происходит ещё однин проход для той же подстроки(но с
						// новым Prev.name==IF,EXIT, ...),
						// в котором устанавливается prevMatched
					}
				} else if (prevMatched) { // если были совпадения(с одной
											// маской) в предыдущих
											// подстроках(на предыд итерациях)
					isNeedAddToken = true;
				}

				// ни с одним регекспом подстрока не совпала
				if ((matched == false && prevMatched == false)) {
					throw new Exception(
							"Возможно нет регулярного выражения на ILLEGAL_TOKEN"); // Если
																					// зашли
																					// сюда,
																					// то
																					// ошибка.
																					// Проверь
																					// регулярку
																					// на
																					// ILLEGAL_TOKEN.
				}
			}

			if (isNeedAddToken) {
				// System.out.println("Adding token name=\""+Prev.name+
				// "\", value=\"" + Prev.value+"\"\n");
				switch (Prev.tag) {
				case SKIPABLE:
					break;
				case L_COMMENT:
					withinComment = true;
					break;
				case R_COMMENT:
					withinComment = false;
					break;
				default:
					if (!withinComment) {
						switch (Prev.tag) {
						case INTEGER:
							tokens.add(new IntegerT(Prev.tag, Integer
									.parseInt(Prev.string)));
							break;
						case DOUBLE:
							tokens.add(new DoubleT(Prev.tag, Double
									.parseDouble(Prev.string)));
							break;
						case BOOLEAN:
							tokens.add(new BooleanT(Prev.tag, Boolean
									.parseBoolean(Prev.string)));
							break;
						case NAME:
							tokens.add(new WordT(Prev.tag, Prev.string));
							break;
						default:
							tokens.add(new Token(Prev.tag, Prev.string));
							break;

						}

					}
					break;
				}
				isNeedAddToken = false;
				prevMatched = false;

				start = start + Prev.string.length();
				Prev = null;

				if (start >= string.length())
					isContinue = false; // выходим, когда просмотрели всю
										// входную строку
				else
					end = start + 1; // не обязательно, ибо end уже сдвинут при
										// попытке захватить очередной символ
			}
		}// for
	}

	// Сопоставляет подстроку с масками-регэкспами, при первом же совпадении
	// возвращает true и (ре)инициализирует ссылку Cur
	private boolean isMatchWithMasks(String substr) {
		// Этот метод генерит новые объекты и настраивает на них ссылку Cur
		// System.out.println("Trying \""+substr+"\"...");
		for (TokenMask tm : masks) {
			Matcher myMatcher = tm.pattern.matcher(substr); // Ищет совпадения
			if (myMatcher.matches()) {
				// Эта ссылка должна быть полем класса, а не аргументом метода,
				// для того чтобы ниженаписанное присвоение было видно в scan()
				// http://docs.oracle.com/javase/tutorial/java/javaOO/arguments.html
				Cur = new Token(tm.name, substr);

				// System.out.println("" + Cur.name + " " + Cur.value+"\n");
				return true;
			} else {
				Cur = null;
			}
		}
		// System.out.println("NOT passed\n");
		return false;
	}

	private void addItem(String regexp, Tag name) {
		masks.add(new TokenMask(regexp, name));
	}

	/**
	 * Выводит все маски - пары Имя токена : Регулярное выражение
	 * 
	 * @see Lexer.TokenMask
	 * */
	public void printMasks() {
		System.out.print("[lexer]: ");
		System.out.println("ALL masks:");
		// System.out.println("ALL masks:\n<name> <regexp>\n");
		for (TokenMask t : masks) {
			System.out.println("" + t.name + " " + t.regexp);
		}
		System.out.println();
	}

	/*
	 * "{n}" - n раз встретится символ "{n, m}" - символ встретится от n до m
	 * раз
	 * "[a-zA-Z]{1}[a-zA-Z\\d\\u002E\\u005F]+@([a-zA-Z]+\\u002E){1,2}((net)|(com)|(org))"
	 * - for email "." - любой символ "?" is {0,1} "*" is {0,} "+" is {1,}
	 */
}
