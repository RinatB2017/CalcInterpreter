package exceptions;

import main.Executor;
import options.OptId;
import options.Options;

/**
 * Собственный(мой) тип исключений. Эти исключения - обозначают некритичные
 * ожидаемые ошибки, перехватив которые можно продолжать работу ( см. цикл в
 * Executor.main() ). Остальные ошибки, которым соответствуют встроенные в Java
 * типы исключений - неожиданные, поэтому цикл в Executor.main() прерывается и
 * программа аварийно завершает работу, выдав соответствующее сообщение
 * По замыслу генерируются из parser.program().
 * @see Executor#main(String[])
 * */

public class MyException extends Exception {
	private static final long serialVersionUID = 1L;
	String message;
	private static Options options;
	private static boolean completeInitialized=false;
	private static int errors=0;
	
	public static void staticInit(Options options1){
		options=options1;
		completeInitialized=true;
	}
	
	// Конструктор
	public MyException(String message) {
		this.message = message;
		errors++;
		if(completeInitialized){
			errors = options.getInt(OptId.ERRORS);
			errors++; // вынужденное дублирование
			try{
				options.set(OptId.ERRORS, errors);
			}catch (Exception e){
				System.err.println("fail on increment errors counter.");
			}
		}else{
			System.err.println("WARNING: error occured when MyException is not complete initialized.");
		}
	}

	@Override
	public String getMessage() {
		return message;
	}
	
	// Используется только в тестах
	public static int getErrors() {
		if(completeInitialized)
			return options.getInt(OptId.ERRORS);
		else
			return errors;
	}
}
