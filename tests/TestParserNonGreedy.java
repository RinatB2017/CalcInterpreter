import org.junit.*;
import junit.framework.Assert;


public class TestParserNonGreedy extends Assert{
	static Lexer l;
	static Buffer b;
	static Parser p;
	
	@Before
	public void setUp() {
		l = new Lexer();
		b = new Buffer(l, null, null, true, false);
		p = new Parser(b, true, false);
		p.reset(Parser.what.ALL);
	}
	
	@After
	public void tearDown() throws Exception {
		if (p.getCurrTok().name==Names.RF) Parser.error("Неправильный выход из expr_list, возможно лишняя RF }");
		if(Parser.getErrors()>0) System.err.println("Ошибка на строке "+b.getLineNum());
		assertTrue(Parser.getErrors()==0);
	}
	
	
	@Test
	public void testPrint2As3() throws Exception {
		// http://automated-testing.info/forum/kak-poluchit-imya-metoda-vo-vremya-vypolneniya-testa#comment-961
		System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
		b.setArgs(new String[] {"sin(-pi/2)"});
		p.exprList();
		assertEquals(-1.0, p.lastResult);
	}
	
	@Test
	public void test3FactorialFactorial() throws Exception {
		b.setArgs(new String[] {"3!!"});
		p.exprList();
		assertEquals(720.0, p.lastResult);
	}
	
	@Test
	public void test3FactorialAdd4Factorial() throws Exception {
		b.setArgs(new String[] {"3!+4!"});
		p.exprList();
		assertEquals(30.0, p.lastResult);
	}
	
	@Test
	public void test1minus3FactoriAladd4Factorial() throws Exception {
		b.setArgs(new String[] {"1-3!+4!"});
		p.exprList();
		assertEquals(19.0, p.lastResult);
	}
	
	@Test
	public void test1plus3FactoriAladd4Factorial() throws Exception {
		b.setArgs(new String[] {"1+3!+4!"});
		p.exprList();
		assertEquals(31.0, p.lastResult);
	}
	
	@Test
	public void test3FactorialMul4Factorial() throws Exception {
		b.setArgs(new String[] {"3!*4!"});
		p.exprList();
		assertEquals(144.0, p.lastResult);
	}
	
	@Test
	public void test2pow3Factorial() throws Exception {
		b.setArgs(new String[] {"2^3!"}); // 2^(3!)
		p.exprList();
		assertEquals(64.0, p.lastResult);
	}
	
	@Test
	public void test2pow3FactorialPlus1() throws Exception {
		b.setArgs(new String[] {"2^3!+1"}); // 2^(3!)+1
		p.exprList();
		assertEquals(65.0, p.lastResult);
	}
}