import static org.junit.Assert.assertTrue;
import inter.Interpreter;

import java.util.HashMap;

import lexer.Lexer;
import main.Buffer;
import main.MyException;
import main.OutputSystem;
import options.OptId;
import options.Options;

import org.junit.After;
import org.junit.Before;

import parser.Parser;
import types.TypedValue;


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
		o.set(OptId.AUTO_END, true);
		o.set(OptId.GREEDY_FUNC, false);
		MyException.staticInit(o, out);
		b = new Buffer(l, null, null, o, out);
		i = new Interpreter(o, new HashMap<String, TypedValue>(), out);
		p = new Parser(b, i);
	}
	
	@After
	public void tearDown() {
		if (MyException.getErrors() > 0)
			System.err.println("Ошибка на " + b.getLineNum());
		assertTrue(MyException.getErrors() == 0);
	}
}
