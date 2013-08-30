import org.junit.*;
import static org.junit.Assert.*;

import java.util.HashMap;

import interpreter.Interpreter;
import options.OptId;
import options.Options;

import parser.Parser;
import types.TypedValue;
import lexer.Lexer;
import lexer.Tag;
import main.Buffer;
import main.MyException;
import main.OutputSystem;

public class TestParserFails {
	static Lexer l;
	static Buffer b;
	static Parser p;
	static Interpreter i;

	@Before
	public void setUp() throws MyException {
		OutputSystem out = new OutputSystem();
		l = new Lexer();
		Options o = new Options(out);
		o.set(OptId.AUTO_END, true);
		o.set(OptId.GREEDY_FUNC, true);
		
		b = new Buffer(l, null, null, o, out);
		i = new Interpreter(o, new HashMap<String, TypedValue>(), out);
		p = new Parser(b, i);
	}

	@After
	public void tearDown() throws Exception {
		assertTrue(p.getErrors() != 0);
	}

	@Test(expected = MyException.class)
	public void checkDivZero() throws Exception {
		b.setArgs(new String[] { "1/sin(-pi)" }); // Работает округление до 0 в
													// Parser.func()
		p.program();
	}

	@Test(expected = MyException.class)
	public void checkFactorial() throws Exception {
		b.setArgs(new String[] { "-3!" }); // Факториал отрицательного
		p.program();
	}

	@Test(expected = MyException.class, timeout = 2000)
	public void checkFactorialCos() throws Exception {
		b.setArgs(new String[] { "(cos pi)!" }); // Greedy! // Факториал
													// отрицательного
		p.program();
	}

	@Test(expected = MyException.class)
	public void checkExtraRP() throws Exception {
		b.setArgs(new String[] { "sin(-pi/2))" });
		p.program();
	}

	@Test(expected = MyException.class)
	public void checkExtraRF() throws Exception {
		b.setArgs(new String[] { "sin(-pi/2)}" });
		p.program();
	}

	@Ignore
	// Ложный тест верного выражения, которое не бросит исключения
	@Test(expected = MyException.class)
	public void checkExtraRFIgrore() throws Exception {
		b.setArgs(new String[] { "sin(-pi/2)" });
		p.program();
	}

	@Test(expected = MyException.class)
	public void checkExtraRFIf() throws Exception {
		b.setArgs(new String[] { "if(1+-9){sin(-pi/4);}}" });
		p.program();

		if (p.getCurrTok().name == Tag.RF)
			p.error("Неправильный выход из expr_list, возможно лишняя RF }");
	}

	@Test(expected = MyException.class)
	public void checkEarlyExit() throws Exception {
		b.setArgs(new String[] { "aaa=2^3^4; bb=( 2 ^ 3 ) ^ 4; if(a-b){}else{print 2; exit;} print 3" });
		p.program();
	}
}
