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

public class TestParserGreedyMost {
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
		o.set(OptId.GREEDY_FUNC, true);
		MyException.staticInit(o, out);
		b = new Buffer(l, null, null, o, out);
		i = new Interpreter(o, new HashMap<String, TypedValue>(), out);
		p = new Parser(b, i);
	}

	@After
	public void tearDown() throws Exception {
		if (MyException.getErrors() > 0)
			System.err.println("Ошибка на " + b.getLineNum());
		assertTrue(MyException.getErrors() == 0);
	}

	@Test
	public void testPrint2As3() throws Exception {
		// http://automated-testing.info/forum/kak-poluchit-imya-metoda-vo-vremya-vypolneniya-testa#comment-961
		System.out.println(new Object() {
		}.getClass().getEnclosingMethod().getName());
		b.setArgs(new String[] { "print print2=as3=-321.694;\nas3=-as3" }); // не
																			// смотря
																			// на
																			// '\n',
																			// лексер
																			// считает
																			// это
																			// как
																			// одну
																			// строку
		p.program();
		assertEquals(321.694, i.lastResult.getDouble(), 0.001); // работает
	}

	@Test
	public void testPrint_() throws Exception {
		b.setArgs(new String[] { "print print_ = as3=321.694", "", "" });
		p.program();
		assertEquals(321.694, i.lastResult.getDouble(), 0.001); // работает
	}

	@Ignore
	@Test
	public void testPrintCosPiDiv2() throws Exception {
		b.setArgs(new String[] { "print cos pi/2" });
		p.program();
		assertEquals(0.0, i.lastResult); // работает
	}

	@Test
	public void testPrint_1() throws Exception {
		b.setArgs(new String[] { "print -1.0+0.0" });
		p.program();
		assertEquals(-1.0, i.lastResult.getDouble(), 0.01); // работает
	}

	@Ignore
	@Test
	public void testPrintSinPiDiv2() throws Exception {
		b.setArgs(new String[] { "print sin pi/2" });
		p.program();
		assertEquals(1.0, i.lastResult); // работает
	}

	@Ignore
	@Test
	public void testPrintCosSinPiDiv2() throws Exception {
		b.setArgs(new String[] { "print cos sin pi/2" });
		p.program();
		assertEquals(Math.cos(Math.sin(Math.PI / 2.0)), i.lastResult); // работает
	}

	@Test
	public void testPrintZero() throws Exception {
		b.setArgs(new String[] { "print 0.0-0.0" });
		p.program();
		assertEquals(0.0, i.lastResult.getDouble(), 0.01); // работает
	}
	
	@Ignore
	@Test
	public void testIf_false_firstAfterIf() throws Exception {
		b.setArgs(new String[] { "if(sin pi) {print 2+ 2*2;}  print e" });
		p.program();
		assertEquals(Math.E, i.lastResult.getDouble(), 0.001);
	}

	@Ignore
	@Test
	public void testIf_true_() throws Exception {
		b.setArgs(new String[] { "if(sin pi+3) {print 2 + 2*2;}" });
		p.program();
		assertEquals(6.0, i.lastResult); // работает
	}

	@Ignore
	@Test
	public void testIf_false_El() throws Exception {
		b.setArgs(new String[] { "if(sin pi){print 2 + 2*2;} else {print printMe;}" });
		p.program();
		assertEquals(0.0, i.lastResult); // работает
	}

	@Ignore
	@Test
	public void testInsertedIfEl1() throws Exception {
		b.setArgs(new String[] { "if(1){ if(2){print 2+2*2;}else{print err2;} }else{print err1;}" });
		p.program();
		assertEquals(6.0, i.lastResult); // работает
	}

	@Ignore
	@Test
	public void testInsertedIfEl2() throws Exception {
		b.setArgs(new String[] { "if(1){ if(2){print 2+2*20;}}else{print err1;}" });
		p.program();
		assertEquals(42.0, i.lastResult); // работает
	}

	@Ignore
	@Test
	public void testInsertedIfEl3() throws Exception {
		b.setArgs(new String[] { "if(1){ if(2){print -10+2*2;}else{print err2;} }" });
		p.program();
		assertEquals(-6.0, i.lastResult); // работает
	}

	@Test
	public void testaab() throws Exception {
		b.setArgs(new String[] { "a = 1; b = a+2; print b;" });
		p.program();
		assertEquals(3, i.lastResult.getInt()); // работает
	}
	
	@Ignore
	@Test
	public void testPow1() throws Exception {
		b.setArgs(new String[] { "aaa=2^3^4; bb=( 2 ^ 3 ) ^ 4; if(a-b){}else{print 2; } print 3" });
		p.program();
		assertEquals(3.0, i.lastResult); // работает
	}

	@Ignore
	@Test
	public void testIf1() throws Exception {
		b.setArgs(new String[] { "if(-e){print 2 + 3;} print" });
		p.program();
		assertEquals(5.0, i.lastResult); // работает
	}

	@Ignore
	@Test
	public void testIf2() throws Exception {
		b.setArgs(new String[] { "if(-e){print 2 + 3;}; print" });
		p.program();
		assertEquals(5.0, i.lastResult); // работает
	}

	@Ignore
	@Test
	public void testIf3() throws Exception {
		b.setArgs(new String[] { "if(-e+e){print 2 + 3; ;}else{ print 14; ;} print;" });
		p.program();
		assertEquals(14.0, i.lastResult); // работает
	}

	@Test
	public void testAns() throws Exception {
		b.setArgs(new String[] { "2; (5+3)+ans" });
		p.program();
		assertEquals(16, i.lastResult.getInt()); // работает
	}

	@Test
	public void testTemplateForFutureVector() throws Exception {
		b.setArgs(new String[] { "p=4; (3*p^2 + 6*p - 4)/2" }); // TODO Убрать
																// p=4 и
																// использовать
																// для проверки
																// деления
																// функции на
																// ЧИСЛО
		p.program();
		assertEquals(34, i.lastResult.getInt()); // работает
	}
}
