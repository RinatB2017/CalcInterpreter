
import interpretator.Interpreter;
import options.OptId;
import options.Options;

import org.junit.*;

import junit.framework.Assert;
import lexer.Lexer;
import main.Buffer;
import main.MyException;
import main.OutputSystem;
import main.Parser;

public class TestParserInterpreter extends Assert {
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
		o.set(OptId.GREEDY_FUNC, false);

		b = new Buffer(l, null, null, o, out);
		i = new Interpreter(o, out);
		p = new Parser(b, o, out, i);
	}

	@After
	public void tearDown() throws Exception {
		if (p.getErrors() > 0)
			System.err.println("Ошибка на " + b.getLineNum());
		assertTrue(p.getErrors() == 0);
	}

	
	
	@Test
	public void test2plus2mul2() throws Exception {
		b.setArgs(new String[] { "2+2*2;" });
		p.program();
		assertEquals(6, i.lastResult.getInt());
	}
	
	@Test
	public void testIfFalse() throws Exception {
		b.setArgs(new String[] { "if(false){2-3;}4+0;" });
		p.program();
		assertEquals(4, i.lastResult.getInt());
	}

	@Test
	public void testIfTrue() throws Exception {
		b.setArgs(new String[] { "if(true){2-3;};" });
		p.program();
		assertEquals(-1, i.lastResult.getInt());
	}
	
	@Test
	public void testIfFalseIfTrueExpr() throws Exception {
		b.setArgs(new String[] { "if(false){ if(true){2-3;} 2-3;};" });
		p.program();
		assertEquals(0, i.lastResult.getInt());
	}
	
	@Test
	public void testIfFalseElse() throws Exception {
		b.setArgs(new String[] { "if(false){ if(true){2-3;} 2-3;} else {-9-0;};" });
		p.program();
		assertEquals(-9, i.lastResult.getInt());
	}
	
	@Test
	public void testIfTrueElse() throws Exception {
		b.setArgs(new String[] { "if(true){ if(true){2-3;} 10+0;} else {-9;};" });
		p.program();
		assertEquals(10, i.lastResult.getInt());
	}
}