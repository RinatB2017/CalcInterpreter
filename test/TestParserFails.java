import org.junit.*;
import junit.framework.Assert;


public class TestParserFails extends Assert{
	static Lexer l;
	static 	Parser p;
		
	@Before
	public void setUp() {
		l = new Lexer(true, true);
		
		p = new Parser(true, true);
		p.reset(Parser.what.ALL);
	}
	
	@After
	public void tearDown() throws Exception {
		assertTrue(Parser.getErrors()!=0);
	}

	@Test (expected=MyException.class)
	public void checkExtraRP() throws Exception {
		l.scan("sin(-pi/2))");
		p.addTokens(l.getTokens());
		p.exprList();
	}
	
	@Test (expected=MyException.class)
	public void checkExtraRF() throws Exception {
		l.scan("sin(-pi/2)}");
		p.addTokens(l.getTokens());
		p.exprList();
	}
	
	@Test (expected=MyException.class)
	public void checkExtraRFIf() throws Exception {
		l.scan("if(1+-9){sin(-pi/4);}}");
		p.addTokens(l.getTokens());
		p.exprList();
		
		if (p.getCurrTok()==Names.RF) Parser.error("Неправильный выход из expr_list, возможно лишняя RF }");
	}
}
