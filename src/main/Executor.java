package main;
/*
 * Начало: Лексер-Java:			lab-2-tokens-2013-03-30--03-08.zip
 * Начало: Парсер&Лексер-C++: 	calculatorsStroustroup-2013-04-29--16-54.zip
 * Начало: Парсер-Java: 		CalcInterpreter-java-2013-05-10--00-18-relese.zip
 * 
 * Последняя версия на GitHub: https://github.com/nikit-cpp/CalcInterpreter.git
 * */

import interpreter.Interpreter;

import java.io.*;
import java.util.HashMap;

import options.Options;
import parser.Parser;
import types.TypedValue;
import lexer.Lexer;
import lexer.Tag;

public class Executor {
	public static void main(String[] args) throws Exception {
		System.out.println("Добро пожаловать в интерпретатор.\n");

		BufferedReader stdin = new BufferedReader(new InputStreamReader(
				System.in));
		OutputSystem output = new OutputSystem();
		Options options = new Options(output);
		Lexer lexer = new Lexer();
		Buffer buffer = new Buffer(lexer, args, stdin, options, output);
		HashMap<String, TypedValue> table = new HashMap<String, TypedValue>(); 
		Interpreter inter = new Interpreter(options, table, output);
		Parser p = new Parser(buffer, inter);
		
		while (true) {
			try {
				p.program();
				if (p.getCurrTok().name == Tag.EXIT)
					break;
			} catch (MyException m) {
				System.err.println("Ошибка на " + buffer.getLineNum()
						+ " на токене №" + buffer.getTokNum() + " " + p.getCurrTok().toStringWithName()
						+ ":");
				System.err.println(m.getMessage());
				continue;
			} catch (Exception e) {
				System.err.println("Критическая ошибка на " + buffer.getLineNum()
						+ " на токене №" + buffer.getTokNum() + " " + p.getCurrTok().toStringWithName()
						+ ", продолжение работы невозможно.");
				System.err.println(e.getMessage() + "\n");
				e.printStackTrace();
				break;// while
			}
		}// while
		System.out.println("Выход...");
	}
}