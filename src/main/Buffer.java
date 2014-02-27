package main;
import inter.Parser;

import java.io.BufferedReader;
import java.util.ArrayList;

import options.OptId;
import options.Options;
import lexer.Lexer;
import lexer.Tag;
import lexer.Token;

/**
 * Буфер между лексером и парсером. Нужен для соединения между собой лексера,
 * обрабатывающего строку и выдающего список токенов и парсера, требующего один
 * токен. Помимо функций буфера выполняет роль менеджера ввода :)
 * 
 * @author Ник
 * @see #getToken()
 * */

public final class Buffer {
	private ArrayList<Token> tokens; // Массив токенов <название, значение>
	private Lexer lexer;
	private String[] args;
	private Options options = null;
	private OutputSystem output;

	/**
	 * Конструктор
	 * 
	 * @param lexer
	 *            ссылка на лексер
	 * @param args
	 *            ссылка на массив args[] из main()
	 * @param stdin
	 *            ссылка на BufferedReader. stdin==null допустимо, означает не
	 *            использовать BufferedReader, а читать тоько из args[]
	 * @param options
	 *            ссылка на опции
	 * @param output
	 * 			  ссылка на систему вывода
	 */
	public Buffer(Lexer lexer, String[] args, BufferedReader stdin,
			Options options, OutputSystem output) {
		tokens = new ArrayList<Token>();
		this.stdin = stdin; // stdin=null используется при тестировании: сначала
							// вызываем Lexer::scan("тестируемая строка"), затем
							// Parser::exprList
		this.lexer = lexer;
		this.args = args;
		this.options = options;
		this.output = output;
	}

	private long lineNum = 0;
	private BufferedReader stdin = null;
	private int tokNum = 0;
	private String str;
	private int numAgrs = 0;

	private enum NowProcessed {
		NOTHING, ARGS, STDIN
	};

	/** Откуда сейчас происходит считывание */
	private NowProcessed now = NowProcessed.NOTHING;

	/**
	 * Главный метод, используется парсером для получения очередного токена
	 * 
	 * @return Token (в том числе new Token(Terminal.EXIT, "") при невозможности
	 *         дальнейшего считывания)
	 * @see Lexer#scan(String, ArrayList)
	 * @see Parser#getToken()
	 */
	public Token getToken() throws Exception {
		// Если все токены из списка уже были получены, либо список пуст
		if (tokNum == tokens.size()) {
			tokens.clear();
			tokNum = 0;// Для нормальной работы ^

			str = null;

			do {
				if (numAgrs < args.length)
					now = NowProcessed.ARGS; // Если args - не пустой массив
				else if (stdin != null)
					now = NowProcessed.STDIN; // если есть поток ввода и мы уже
												// обработали все args
				else
					now = NowProcessed.NOTHING; // когда исчерпали все токены в
												// args и нельзя читать из
												// stdin, то возвращаем EXIT

				switch (now) {
				case ARGS:
					str = args[numAgrs];
					break;
				case STDIN:
					str = stdin.readLine(); // Считываем строку..., null когда
											// строки закончились
					if (str == null)
						return new Token(Tag.EXIT);
					lineNum++;
					break;
				case NOTHING:
					return new Token(Tag.EXIT);
				}

				if (!str.isEmpty()) {
					lexer.scan(str, tokens);

					// autoending :)
					switch (now) {
					case ARGS:
						if (options.getBoolean(OptId.ARGS_AUTO_END))
							addEnd();
						break;
					case STDIN:
						if (options.getBoolean(OptId.AUTO_END))
							addEnd();
						break;
					default:
						break;
					}

					if (options.getBoolean(OptId.PRINT_TOKENS))
						printTokens();
				}

				if (numAgrs < args.length)
					numAgrs++;
			} while (tokens.isEmpty());

		}

		// Возвращаем очередной токен
		return tokens.get(tokNum++);
	}
	
	// Автодобавление токена END
	private void addEnd(){
		tokens.add(new Token(Tag.END));
		output.addln("Автоматически добавлен токен END.");
	}

	/**
	 * Вывод найденных токенов в System.out
	 */
	public void printTokens() {
		output.addln("на " + getLineNum() + " "+"\"" + str + "\" :");
		if (!tokens.isEmpty()) {
			output.addln("[founded tokens]");
			
			for (int i = 0; i < tokens.size(); i++) {
				Token t = tokens.get(i);
				output.addln(t.toStringWithName());
			}
			output.addln("[/founded tokens]");
		} else
			output.addln("Nothing found.");

	}

	/**
	 * @return строку с номером аргумента из args[], либо с номером строки из
	 *         BufferedReader
	 * */
	public String getLineNum() {
		switch (now) {
		case ARGS:
			return "входном параметре №" + numAgrs;
		case STDIN:
			return "строке №" + lineNum;
		default:
			return "нигде, numAgrs=" + numAgrs + ", lineNum=" + lineNum;
		}
	}

	/**
	 * @return номер токена
	 */
	public int getTokNum() {
		return tokNum - 1; // т. к . в getToken() используется ++
	}

	// Для тестов
	public void setArgs(String[] args) {
		this.args = args;
	}
}
