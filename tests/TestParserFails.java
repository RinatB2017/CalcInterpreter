import org.junit.*;
import junit.framework.Assert;


public class TestParserFails extends Assert{
	static Lexer l;
	static 	Parser p;
		
	@Before
	public void setUp() {
		l = new Lexer(null, true, false);
		
		p = new Parser(l, true, true); // greedy
		p.reset(Parser.what.ALL);
	}
	
	@After
	public void tearDown() throws Exception {
		assertTrue(Parser.getErrors()!=0);
	}

	
	@Test (expected=MyException.class)
	public void checkDivZero() throws Exception {
		l.scan("1/sin(-pi)}"); // Работает округление до 0 в Parser.func()
		p.exprList();
	}
		
	@Test (expected=MyException.class)
	public void checkFactorial() throws Exception {
		l.scan("-3!");
		p.exprList();
	}
	
	@Test (expected=MyException.class, timeout=2000)
	public void checkFactorialCos() throws Exception {
		l.scan("(cos pi)!"); // Greedy!
		p.exprList();
	}
	
	@Test (expected=MyException.class)
	public void checkExtraRP() throws Exception {
		l.scan("sin(-pi/2))");
		p.exprList();
	}
	
	@Test (expected=MyException.class)
	public void checkExtraRF() throws Exception {
		l.scan("sin(-pi/2)}");
		p.exprList();
	}
	
	@Test (expected=MyException.class)
	public void checkExtraRFIf() throws Exception {
		l.scan("if(1+-9){sin(-pi/4);}}");
		p.exprList();
		
		if (p.getCurrTok()==Names.RF) Parser.error("Неправильный выход из expr_list, возможно лишняя RF }");
	}
}