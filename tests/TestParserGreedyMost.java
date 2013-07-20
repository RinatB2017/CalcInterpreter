import org.junit.*;
import junit.framework.Assert;


public class TestParserGreedyMost extends Assert{
	static Lexer l;
	static Buffer b;
	static Parser p;
	String inputString=null;
	
	@Before
	public void setUp() throws MyException {
		//StringReader in2 = new StringReader(inputString);
		//BufferedReader stdio = new BufferedReader(in2);
		
		l = new Lexer();
		Options o = new Options();
		o.set(Terminal.AUTO_END, true);
		o.set(Terminal.GREEDY_FUNC, true);
		// Старый конструктор Buffer: опции lexerAutoEnd, lexerPrintTokens : true, false
		b = new Buffer(l,  null, null,  o);
		// Старый конструктор Parser: опции autoPrint, greedyFunc : true, true
		p = new Parser(b, o);
	}
	
	@After
	public void tearDown() throws Exception {
		if (p.getCurrTok().name==Terminal.RF) p.error("Неправильный выход из expr_list, возможно лишняя RF }");
		if(p.getErrors()>0) System.err.println("Ошибка на "+b.getLineNum());
		assertTrue(p.getErrors()==0);
	}
	
	
	@Test
	public void testPrint2As3() throws Exception {
		// http://automated-testing.info/forum/kak-poluchit-imya-metoda-vo-vremya-vypolneniya-testa#comment-961
		System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
		b.setArgs(new String[] {"print print2=as3=-321.694;\nas3=-as3"}); // не смотря на '\n', лексер считает это как одну строку
		p.exprList();
		assertEquals(321.694, p.lastResult); // работает
	}
	
	@Test
	public void testPrint_() throws Exception {
		b.setArgs(new String[] {"print print_ = as3=321.694", "", ""});
		p.exprList();
		assertEquals(321.694, p.lastResult); // работает
	}
	
	@Test
	public void testPrintCosPiDiv2() throws Exception {
		b.setArgs(new String[] {"print cos pi/2"});
		p.exprList();
		assertEquals(0.0, p.lastResult); // работает
	}
	
	@Test
	public void testPrint_1() throws Exception {
		b.setArgs(new String[] {"print -1"});
		p.exprList();
		assertEquals(-1.0, p.lastResult); // работает
	}
	
	@Test
	public void testPrintSinPiDiv2() throws Exception{
		b.setArgs(new String[] {"print sin pi/2"}); 
		p.exprList();
		assertEquals(1.0, p.lastResult); // работает
	}
	
	@Test
	public void testPrintCosSinPiDiv2() throws Exception{
		b.setArgs(new String[] {"print cos sin pi/2"}); 
		p.exprList();
		assertEquals(Math.cos(Math.sin(Math.PI/2.0)), p.lastResult); // работает
	}
	
	@Test
	public void testPrintZero() throws Exception{
		b.setArgs(new String[] {"print 0.0"}); 
		p.exprList();
		assertEquals(0.0, p.lastResult); // работает
	}
	
	@Test
	public void testIf_false_firstAfterIf() throws Exception{
		b.setArgs(new String[] {"if(sin pi) {print 2+ 2*2;}  print e"}); 
		p.exprList();
		assertEquals(Math.E, p.lastResult);
	}
	
	@Test
	public void testIf_true_() throws Exception{
		b.setArgs(new String[] {"if(sin pi+3) {print 2 + 2*2;}"}); 
		p.exprList();
		assertEquals(6.0, p.lastResult); // работает
	}
	
	@Test
	public void testIf_false_El() throws Exception{
		b.setArgs(new String[] {"if(sin pi){print 2 + 2*2;} else {print printMe;}"}); 
		p.exprList();
		assertEquals(0.0, p.lastResult); // работает
	}
	
	@Test
	public void testInsertedIfEl1() throws Exception{
		b.setArgs(new String[] {"if(1){ if(2){print 2+2*2;}else{print err2;} }else{print err1;}"}); 
		p.exprList();
		assertEquals(6.0, p.lastResult); // работает
	}
	
	@Test
	public void testInsertedIfEl2() throws Exception{
		b.setArgs(new String[] {"if(1){ if(2){print 2+2*20;}}else{print err1;}"}); 
		p.exprList();
		assertEquals(42.0, p.lastResult); // работает
	}
	
	@Test
	public void testInsertedIfEl3() throws Exception{
		b.setArgs(new String[] {"if(1){ if(2){print -10+2*2;}else{print err2;} }"}); 
		p.exprList();
		assertEquals(-6.0, p.lastResult); // работает
	}
	
	@Test
	public void testaab() throws Exception{
		b.setArgs(new String[] {"a = 1; b = a+2; print b;"}); 
		p.exprList();
		assertEquals(3.0, p.lastResult); // работает
	}
	
	@Test
	public void testPow1() throws Exception {
		b.setArgs(new String[] {"aaa=2^3^4; bb=( 2 ^ 3 ) ^ 4; if(a-b){}else{print 2; exit;} print 3"});
		p.exprList();
		assertEquals(2.0, p.lastResult); // работает
	}
	
	@Test
	public void testIfExit() throws Exception {
		b.setArgs(new String[] {"if(-e){print 2 + 3; exit;} print"});
		p.exprList();
		assertEquals(5.0, p.lastResult); // работает
	}
	
	@Test
	public void testIfElExit() throws Exception {
		b.setArgs(new String[] {"if(-e+e){print 2 + 3; exit;}else{ print 14; exit;} print;"});
		p.exprList();
		assertEquals(14.0, p.lastResult); // работает
	}
	
	@Test
	public void testAns() throws Exception {
		b.setArgs(new String[] {"2; (5+3)+ans"});
		p.exprList();
		assertEquals(10.0, p.lastResult); // работает
	}
	
	@Test
	public void testTemplateForFutureVector() throws Exception {
		b.setArgs(new String[] {"p=4; (3*p^2 + 6*p - 4)/2"}); // TODO Убрать p=8 и использовать для проверки деления вектора на ЧИСЛО
		p.exprList();
		assertEquals(34.0, p.lastResult); // работает
	}
}
