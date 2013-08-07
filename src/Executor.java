/*
 * Начало: Лексер-Java:			lab-2-tokens-2013-03-30--03-08.zip
 * Начало: Парсер&Лексер-C++: 	calculatorsStroustroup-2013-04-29--16-54.zip
 * Начало: Парсер-Java: 		CalcInterpreter-java-2013-05-10--00-18-relese.zip
 * 
 * Последняя версия на GitHub: https://github.com/nikit-cpp/CalcInterpreter.git
 * */

import java.io.*;

public class Executor {
	public static void main(String[] args) throws Exception {
		System.out.println("Добро пожаловать в интерпретатор.\n");

		BufferedReader stdin = new BufferedReader(new InputStreamReader(
				System.in));
		OutputSystem output = new OutputSystem();
		Options o = new Options(output);
		Lexer l = new Lexer();
		Buffer b = new Buffer(l, args, stdin, o, output);
		Parser p = new Parser(b, o, output);
		
		while (true) {
			try {
				p.program();
				if (p.getCurrTok().name == Tag.EXIT)
					break;
			} catch (MyException m) {
				System.err.println("Ошибка на " + b.getLineNum()
						+ " на токене №" + b.getTokNum() + " " + p.getCurrTok()
						+ ":");
				System.err.println(m.getMessage());
				continue;
			} catch (Exception e) {
				System.err.println("Критическая ошибка на " + b.getLineNum()
						+ " на токене №" + b.getTokNum() + " " + p.getCurrTok()
						+ ", продолжение работы невозможно.");
				System.err.println(e.getMessage() + "\n");
				e.printStackTrace();
				break;// while
			}
		}// while
		System.out.println("Выход...");
	}
}