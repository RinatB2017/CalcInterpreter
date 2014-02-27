import static org.junit.Assert.*;

import org.junit.*;


public class TestCastingAndFunctions extends EnvForTests{
	/*@Test
	public void testIf_false_firstAfterIf() throws Exception {
		b.setArgs(new String[] { "if(sin pi) {print 2+ 2*2;}  print e" });
		p.program();
		assertEquals(Math.E, i.lastResult.getDouble(), 0.001);
	}

	@Test
	public void testIf_true_() throws Exception {
		b.setArgs(new String[] { "if(sin pi+3) {print 2 + 2*2;}" });
		p.program();
		assertEquals(6, i.lastResult.getInt());
	}

	@Test
	public void testIf_false_El() throws Exception {
		b.setArgs(new String[] { "if(sin pi){print 2 + 2*2;} else {print printMe;}" });
		p.program();
		assertEquals(0, i.lastResult.getInt());
	}*/
	
	@Test
	public void testPrint2As3() throws Exception {
		// http://automated-testing.info/forum/kak-poluchit-imya-metoda-vo-vremya-vypolneniya-testa#comment-961
		System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
		b.setArgs(new String[] { "sin(-pi/2)" });
		p.program();
		assertEquals(-1.0, i.lastResult.getDouble(), 0.01);
	}
	
	@Test
	public void testPrintCosPiDiv2() throws Exception {
		b.setArgs(new String[] { "print cos (pi/2)" });
		p.program();
		assertEquals(0.0, i.lastResult.getDouble(), 0.01);
	}

	@Test
	public void testPrintSinPiDiv2() throws Exception {
		b.setArgs(new String[] { "print sin (pi/2)" });
		p.program();
		assertEquals(1.0, i.lastResult.getDouble(), 0.01);
	}

	@Test
	public void testPrintCosSinPiDiv2() throws Exception {
		b.setArgs(new String[] { "print cos( sin( pi/2) )" });
		p.program();
		assertEquals(Math.cos(Math.sin(Math.PI / 2.0)), i.lastResult.getDouble(), 0.01);
	}
}
