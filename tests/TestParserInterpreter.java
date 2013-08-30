import org.junit.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import interpreter.Interpreter;
import options.OptId;
import options.Options;

import parser.Parser;
import types.TypedValue;

import lexer.Lexer;
import main.Buffer;
import main.MyException;
import main.OutputSystem;

public class TestParserInterpreter {
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
		i = new Interpreter(o, new HashMap<String, TypedValue>(), out);
		p = new Parser(b, i);
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
	
	@Test
	public void testDelAll() throws Exception {
		b.setArgs(new String[] { "del *;" });
		p.program();
		assertEquals(0, i.table.size());
	}
	
	@Test
	public void testDelAllAddOneDelOne() throws Exception {
		b.setArgs(new String[] { "del *; var uut=9; var t; del uut;" });
		p.program();
		assertEquals(1, i.table.size());
	}
	
	@Test(expected = MyException.class)
	public void checkIntDivZero() throws Exception {
		b.setArgs(new String[] { "1/0" });
		p.program();
	}
	
	@Test(expected = MyException.class)
	public void checkDoubleDivZero() throws Exception {
		b.setArgs(new String[] { "1.8/0" });
		p.program();
	}
}