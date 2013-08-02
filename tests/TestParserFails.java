import org.junit.*;
import junit.framework.Assert;


public class TestParserFails extends Assert{
	static Lexer l;
	static Buffer b;
	static Parser p;
		
	@Before
	public void setUp() throws MyException {
		OutputSystem out=new OutputSystem();
		l = new Lexer();
		Options o = new Options(out);
		o.set(Terminal.AUTO_END, true);
		o.set(Terminal.GREEDY_FUNC, true);
		// Старый конструктор Buffer: опции lexerAutoEnd, lexerPrintTokens : true, false
		b = new Buffer(l,  null, null,  o, out);
		// Старый конструктор Parser: опции autoPrint, greedyFunc : true, true
		p = new Parser(b, o, out);
	}
	
	@After
	public void tearDown() throws Exception {
		assertTrue(p.getErrors()!=0);
	}

	
	@Test (expected=MyException.class)
	public void checkDivZero() throws Exception {
		b.setArgs(new String[] {"1/sin(-pi)"}); // Работает округление до 0 в Parser.func()
		p.program();
	}
		
	@Test (expected=MyException.class)
	public void checkFactorial() throws Exception {
		b.setArgs(new String[] {"-3!"}); // Факториал отрицательного
		p.program();
	}
	
	@Test (expected=MyException.class, timeout=2000)
	public void checkFactorialCos() throws Exception {
		b.setArgs(new String[] {"(cos pi)!"}); // Greedy! // Факториал отрицательного
		p.program();
	}
	
	@Test (expected=MyException.class)
	public void checkExtraRP() throws Exception {
		b.setArgs(new String[] {"sin(-pi/2))"});
		p.program();
	}
	
	@Test (expected=MyException.class)
	public void checkExtraRF() throws Exception {
		b.setArgs(new String[] {"sin(-pi/2)}"});
		p.program();
	}
	
	@Ignore // Ложный тест врного выражения, которое не бросит исключения
	@Test (expected=MyException.class)
	public void checkExtraRFIgrore() throws Exception {
		b.setArgs(new String[] {"sin(-pi/2)"});
		p.program();
	}
	
	@Test (expected=MyException.class)
	public void checkExtraRFIf() throws Exception {
		b.setArgs(new String[] {"if(1+-9){sin(-pi/4);}}"});
		p.program();
		
		if (p.getCurrTok().name==Terminal.RF) p.error("Неправильный выход из expr_list, возможно лишняя RF }");
	}
}
