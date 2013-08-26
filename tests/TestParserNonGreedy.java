
import org.junit.*;

import junit.framework.Assert;
import lexer.Lexer;

public class TestParserNonGreedy extends Assert {
	static Lexer l;
	static Buffer b;
	static Parser p;

	@Before
	public void setUp() throws MyException {
		OutputSystem out = new OutputSystem();
		l = new Lexer();
		Options o = new Options(out);
		o.set(Id.AUTO_END, true);
		o.set(Id.GREEDY_FUNC, false);
		// Старый конструктор Buffer: опции lexerAutoEnd, lexerPrintTokens :
		// true, false
		b = new Buffer(l, null, null, o, out);
		// Старый конструктор Parser: опции autoPrint, greedyFunc : true, false
		p = new Parser(b, o, out);
	}

	@After
	public void tearDown() throws Exception {
		// if (p.getCurrTok().name==Terminal.RF)
		// p.error("Неправильный выход из expr_list, возможно лишняя RF }");
		if (p.getErrors() > 0)
			System.err.println("Ошибка на " + b.getLineNum());
		assertTrue(p.getErrors() == 0);
	}

	@Test
	public void testPrint2As3() throws Exception {
		// http://automated-testing.info/forum/kak-poluchit-imya-metoda-vo-vremya-vypolneniya-testa#comment-961
		System.out.println(new Object() {
		}.getClass().getEnclosingMethod().getName());
		b.setArgs(new String[] { "sin(-pi/2)" });
		p.program();
		assertEquals(-1.0, p.lastResult);
	}

	@Test
	public void test3FactorialFactorial() throws Exception {
		b.setArgs(new String[] { "3!!" });
		p.program();
		assertEquals(720.0, p.lastResult);
	}

	@Test
	public void test3FactorialAdd4Factorial() throws Exception {
		b.setArgs(new String[] { "3!+4!" });
		p.program();
		assertEquals(30.0, p.lastResult);
	}

	@Test
	public void test1minus3FactoriAladd4Factorial() throws Exception {
		b.setArgs(new String[] { "1-3!+4!" });
		p.program();
		assertEquals(19.0, p.lastResult);
	}

	@Test
	public void test1plus3FactoriAladd4Factorial() throws Exception {
		b.setArgs(new String[] { "1+3!+4!" });
		p.program();
		assertEquals(31.0, p.lastResult);
	}

	@Test
	public void test3FactorialMul4Factorial() throws Exception {
		b.setArgs(new String[] { "3!*4!" });
		p.program();
		assertEquals(144.0, p.lastResult);
	}

	@Test
	public void test2pow3Factorial() throws Exception {
		b.setArgs(new String[] { "2^3!" }); // 2^(3!)
		p.program();
		assertEquals(64.0, p.lastResult);
	}

	@Test
	public void test2pow3FactorialPlus1() throws Exception {
		b.setArgs(new String[] { "2^3!+1" }); // 2^(3!)+1
		p.program();
		assertEquals(65.0, p.lastResult);
	}

	@Test
	public void testRightAssociatePower() throws Exception {
		b.setArgs(new String[] { "2^3^4" });
		p.program();
		assertEquals(Math.pow(2, Math.pow(3, 4)), p.lastResult);
	}
}