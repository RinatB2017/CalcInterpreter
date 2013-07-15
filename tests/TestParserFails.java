import org.junit.*;
import junit.framework.Assert;


public class TestParserFails extends Assert{
	static Lexer l;
	static Buffer b;
	static Parser p;
		
	@Before
	public void setUp() {
		l = new Lexer();
		b = new Buffer(l, null, null, true, false);
		p = new Parser(b, true, true); // greedy
		p.reset(Parser.what.ALL);
	}
	
	@After
	public void tearDown() throws Exception {
		assertTrue(Parser.getErrors()!=0);
	}

	
	@Test (expected=MyException.class)
	public void checkDivZero() throws Exception {
		b.setArgs(new String[] {"1/sin(-pi)}"}); // Работает округление до 0 в Parser.func()
		p.exprList();
	}
		
	@Test (expected=MyException.class)
	public void checkFactorial() throws Exception {
		b.setArgs(new String[] {"-3!"});
		p.exprList();
	}
	
	@Test (expected=MyException.class, timeout=2000)
	public void checkFactorialCos() throws Exception {
		b.setArgs(new String[] {"(cos pi)!"}); // Greedy!
		p.exprList();
	}
	
	@Test (expected=MyException.class)
	public void checkExtraRP() throws Exception {
		b.setArgs(new String[] {"sin(-pi/2))"});
		p.exprList();
	}
	
	@Test (expected=MyException.class)
	public void checkExtraRF() throws Exception {
		b.setArgs(new String[] {"sin(-pi/2)}"});
		p.exprList();
	}
	
	@Test (expected=MyException.class)
	public void checkExtraRFIf() throws Exception {
		b.setArgs(new String[] {"if(1+-9){sin(-pi/4);}}"});
		p.exprList();
		
		if (p.getCurrTok().name==Names.RF) Parser.error("Неправильный выход из expr_list, возможно лишняя RF }");
	}
}
