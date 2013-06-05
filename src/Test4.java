import org.junit.*;
import org.junit.Test;

import junit.framework.Assert;


public class Test4 extends Assert{
	static Lexer l;
	static 	Parser p;
	
	@Before
	public void setUp() {
		l = new Lexer(true, true);
		
		p = new Parser();
		p.reset(Parser.what.ALL);
	}

	@After
	public void tearDown() throws Exception {
		//l.scan("state");
		//p.addTokens(l.getTokens());
		//p.exprList();
		
		if (p.getCurrTok()==Names.RF) Parser.error("Неправильный выход из expr_list, возможно лишняя RF }");
		assertTrue(Parser.getErrors()==0);
	}
	
	
	@Test
	public void testPrintCosPiDiv2() throws Exception {
		l.scan("print cos pi/2");
		p.addTokens(l.getTokens());
		p.exprList();
		assertEquals(0.0, p.lastResult); // работает
	}
	
	@Test
	public void testPrint_1() throws Exception {
		l.scan("print -1");
		p.addTokens(l.getTokens());
		p.exprList();
		assertEquals(-1.0, p.lastResult); // работает
	}
	
	@Test
	public void testPrintSinPiDiv2() throws Exception{
		l.scan("print sin pi/2"); 
		p.addTokens(l.getTokens());
		p.exprList();
		assertEquals(1.0, p.lastResult); // работает
	}
	
	@Test
	public void testPrintCosSinPiDiv2() throws Exception{
		l.scan("print cos sin pi/2"); 
		p.addTokens(l.getTokens());
		p.exprList();
		assertEquals(Math.cos(Math.sin(Math.PI/2.0)), p.lastResult); // работает
	}
	
	@Test
	public void testPrintZero() throws Exception{
		l.scan("print 0.0"); 
		p.addTokens(l.getTokens());
		p.exprList();
		assertEquals(0.0, p.lastResult); // работает
	}
	
	@Test
	public void testIf_false_firstAfterIf() throws Exception{
		l.scan("if(sin pi) {print 2+ 2*2;}  print e"); 
		p.addTokens(l.getTokens());
		p.exprList();
		assertEquals(Math.E, p.lastResult);
	}
	
	@Test
	public void testIf_true_() throws Exception{
		l.scan("if(sin pi+3) {print 2 + 2*2;}"); 
		p.addTokens(l.getTokens());
		p.exprList();
		assertEquals(6.0, p.lastResult); // работает
	}
	
	@Test
	public void testIf_false_El() throws Exception{
		l.scan("if(sin pi){print 2 + 2*2;} else {print printMe;}"); 
		p.addTokens(l.getTokens());
		p.exprList();
		assertEquals(0.0, p.lastResult); // работает
	}
	
	@Test
	public void testInsertedIfEl1() throws Exception{
		l.scan("if(1){ if(2){print 2+2*2;}else{print err2;} }else{print err1;}"); 
		p.addTokens(l.getTokens());
		p.exprList();
		assertEquals(6.0, p.lastResult); // работает
	}
	
	@Test
	public void testInsertedIfEl2() throws Exception{
		l.scan("if(1){ if(2){print 2+2*20;}}else{print err1;}"); 
		p.addTokens(l.getTokens());
		p.exprList();
		assertEquals(42.0, p.lastResult); // работает
	}
	
	@Test
	public void testInsertedIfEl3() throws Exception{
		l.scan("if(1){ if(2){print -10+2*2;}else{print err2;} }"); 
		p.addTokens(l.getTokens());
		p.exprList();
		assertEquals(-6.0, p.lastResult); // работает
	}
	
	@Test
	public void testaab() throws Exception{
		l.scan("a = 1; b = a+2; print b;"); 
		p.addTokens(l.getTokens());
		p.exprList();
		assertEquals(3.0, p.lastResult); // работает
	}
	
	@Test
	public void testPow1() throws Exception {
		l.scan("aaa=2^3^4; bb=( 2 ^ 3 ) ^ 4; if(a-b){}else{print 2; exit;} print 3");
		p.addTokens(l.getTokens());
		p.exprList();
		assertEquals(2.0, p.lastResult); // работает
	}
	
	@Test
	public void testIfExit() throws Exception {
		l.scan("if(-e){print 2 + 3; exit;} print");
		p.addTokens(l.getTokens());
		p.exprList();
		assertEquals(5.0, p.lastResult); // работает
	}
	
	@Test
	public void testIfElExit() throws Exception {
		l.scan("if(-e+e){print 2 + 3; exit;}else{ print 14; exit;} print;");
		p.addTokens(l.getTokens());
		p.exprList();
		assertEquals(14.0, p.lastResult); // работает
	}
}
