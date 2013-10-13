package main;
/*
 * Начало: Лексер-Java:			lab-2-tokens-2013-03-30--03-08.zip
 * Начало: Парсер&Лексер-C++: 	calculatorsStroustroup-2013-04-29--16-54.zip
 * Начало: Парсер-Java: 		CalcInterpreter-java-2013-05-10--00-18-relese.zip
 * 
 * Последняя версия на GitHub: https://github.com/nikit-cpp/CalcInterpreter.git
 * */

import inter.Interpreter;
import inter.Parser;

import java.io.*;
import java.util.HashMap;

import exceptions.MyException;
import exceptions.UserWantsExit;
import options.Options;
import types.TypedValue;
import lexer.Lexer;
import lexer.Tag;

public class Executor {
	public static void main(String[] args) throws Exception {
		final String hello = "Добро пожаловать в интерпретатор.\n";
		//System.out.println(hello);

		BufferedReader stdin = new BufferedReader(new InputStreamReader(
				System.in));
		OutputSystem output = new OutputSystem();
		output.addln(hello);
		output.flush();
		Options options = new Options(output);
		MyException.staticInit(options);
		Lexer lexer = new Lexer();
		Buffer buffer = new Buffer(lexer, args, stdin, options, output);
		HashMap<String, TypedValue> table = new HashMap<String, TypedValue>(); 
		Interpreter inter = new Interpreter(options, table, output);
		Parser p = new Parser(buffer, inter);
		int exitcode = 0;
		while (true) {
			try {
				p.program();
				if (p.getCurrTok().tag == Tag.EXIT)
					break;// while
			} catch (MyException m) {
				output.flush();
				System.err.println("Ошибка на " + buffer.getLineNum()
						+ " на токене №" + buffer.getTokNum() + " " + p.getCurrTok().toStringWithName()
						+ ":");
				System.err.println(m.getMessage());
				continue;
			} catch(UserWantsExit u) {
				exitcode=u.getExitcode();
				output.addln("Выход по желанию пользователя");
				output.flush();
				break;// while
			} catch (Exception e) {
				output.flush();
				System.err.println("Критическая ошибка на " + buffer.getLineNum()
						+ " на токене №" + buffer.getTokNum() + " " + p.getCurrTok().toStringWithName()
						+ ", продолжение работы невозможно.");
				System.err.println(e.getMessage() + "\n");
				e.printStackTrace();
				exitcode=1;
				break;// while
			}
		}// while
		System.out.println("Выход с кодом "+exitcode);
		System.exit(exitcode);
	}
}