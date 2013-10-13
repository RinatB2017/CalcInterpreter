import static org.junit.Assert.assertTrue;
import inter.Interpreter;
import inter.Parser;

import java.util.HashMap;

import lexer.Lexer;
import main.Buffer;
import main.OutputSystem;
import options.OptId;
import options.Options;

import org.junit.After;
import org.junit.Before;

import exceptions.MyException;
import types.TypedValue;
import types.func.def.Dimension;


public abstract class EnvForTests {
	static Lexer l;
	static Buffer b;
	static Parser p;
	static Interpreter i;
	static Options o;
	
	@Before
	public void setUp() throws Exception {
		OutputSystem out = new OutputSystem();
		l = new Lexer();
		o = new Options(out);
		// Сначала интерпретатор сбрасывает всё в значания по умолчанию из Options
		i = new Interpreter(o, new HashMap<String, TypedValue>(), out);
		// А затем выставляем нужные нам значения 
		o.set(OptId.AUTO_END, true);
		o.set(OptId.GREEDY_FUNC, false);
		o.set(OptId.DIM, Dimension.RAD);
		o.set(OptId.STRICTED, false);
		MyException.staticInit(o);
		b = new Buffer(l, null, null, o, out);
		p = new Parser(b, i);
	}
	
	@After
	public void tearDown() {
		if (MyException.getErrors() > 0)
			System.err.println("Ошибка на " + b.getLineNum());
		assertTrue(MyException.getErrors() == 0);
	}
}
