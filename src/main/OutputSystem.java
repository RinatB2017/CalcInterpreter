package main;
import inter.Parser;

import java.util.*;

/**
 * Буфер для умной задержки вывода при интерактивном вводе. Изначально
 * разрабатывался для взаимодействия с эхопечатью Parser.echoPrint и системой
 * auto_print для умной печати: чтобы сообщения эхопечати не забивали консоль
 * при многострочном вводе когда все токены ещё не получены.
 * 
 * Вывод происходит при вызове flush() из Parser.program()
 * 
 * @author Ник
 * @see OutputSystem#flush()
 * @see Parser#program()
 */

/*
 * Примерный вид того, с чем борется этот буфер: 1+ // пользовательский ввод
 * "1+" 1 +4; // преждевременная эхопечать "1 +" смешивается с пользовательским
 * вводом "4;" = 5 // вывод результата " = 5"
 * 
 * Также устраняет следующую проблему: 1+tt=5; // пользовательский ввод "1+tt=5"
 * 1 +Создана переменная tt // преждевременная эхопечать "1 +" смешивается с
 * сообщением о создании переменной Переменная tt изменена на 5 // //
 * преждевременное сообщение об изменении переменной tt = 6 // эхопечать и вывод
 * результата
 */

public final class OutputSystem {
	private ArrayList<String> buffer; // буфер для всех сообщений
	private StringBuilder builder; // буфер для сообщений bprint или expr

	public OutputSystem() {
		this.buffer = new ArrayList<String>();
		this.builder = new StringBuilder();
	}

	public void append(String s) {
		builder.append(s);
	}

	public void finishAppend(String s) {
		builder.append(s);
		builder.append('\n');
		buffer.add(builder.toString());
		builder.setLength(0);
	}

	public void add(String s) {
		buffer.add(s);
	}

	public void addln(String s) {
		buffer.add(s + '\n');
	}

	/**
	 * Вывод и очистка буфера
	 */
	public void flush() {
		for (String s : buffer) {
			System.out.print(s);
		}

		clear();
	}

	public void clear() {
		buffer.clear();
		builder.setLength(0);
	}
}
