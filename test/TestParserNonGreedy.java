import org.junit.*;
import junit.framework.Assert;


public class TestParserNonGreedy extends Assert{
	static Lexer l;
	static 	Parser p;
	
	@Before
	public void setUp() {
		l = new Lexer(true, true);
		
		p = new Parser(true, false);
		p.reset(Parser.what.ALL);
	}
	
	@After
	public void tearDown() throws Exception {
		if (p.getCurrTok()==Names.RF) Parser.error("Неправильный выход из expr_list, возможно лишняя RF }");
		assertTrue(Parser.getErrors()==0);
	}
	
	
	@Test
	public void testPrint2As3() throws Exception {
		// http://automated-testing.info/forum/kak-poluchit-imya-metoda-vo-vremya-vypolneniya-testa#comment-961
		System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
		l.scan("sin(-pi/2)");
		p.addTokens(l.getTokens());
		p.exprList();
		assertEquals(-1.0, p.lastResult);
	}
}