/**
 * Собственный(мой) тип исключений. Эти исключения - обозначают некритичные
 * ожидаемые ошибки, перехватив которые можно продолжать работу ( см. цикл в
 * Executor.main() ). Остальные ошибки, которым соответствуют встроенные в Java
 * типы исключений - неожиданные, поэтому цикл в Executor.main() прерывается и
 * программа аварийно завершает работу, выдав соответствующее сообщение
 * 
 * @see Executor#main(String[])
 * */

public class MyException extends Exception {
	private static final long serialVersionUID = 1L;
	String message;

	public MyException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
