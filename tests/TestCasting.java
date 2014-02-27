import static org.junit.Assert.*;
import org.junit.*;


public class TestCasting extends EnvForTests{

	/*@Test
	public void testInsertedIfEl1() throws Exception {
		b.setArgs(new String[] { "if(1){ if(2){print 2+2*2;}else{print err2;} }else{print err1;}" });
		p.program();
		assertEquals(6, i.lastResult.getInt());
	}

	@Test
	public void testInsertedIfEl2() throws Exception {
		b.setArgs(new String[] { "if(1){ if(2){print 2+2*20;}}else{print err1;}" });
		p.program();
		assertEquals(42, i.lastResult.getInt());
	}

	@Test
	public void testInsertedIfEl3() throws Exception {
		b.setArgs(new String[] { "if(1){ if(2){print -10+2*2;}else{print err2;} }" });
		p.program();
		assertEquals(-6, i.lastResult.getInt());
	}

	@Test
	public void testPow1() throws Exception {
		b.setArgs(new String[] { "aaa=2^3^4; bb=( 2 ^ 3 ) ^ 4; if(a-b){}else{print 2; } print 3" });
		p.program();
		assertEquals(3, i.lastResult.getInt());
	}

	@Test
	public void testIf1() throws Exception {
		b.setArgs(new String[] { "if(-e){print 2 + 3;} print" });
		p.program();
		assertEquals(5, i.lastResult.getInt());
	}

	@Test
	public void testIf2() throws Exception {
		b.setArgs(new String[] { "if(-e){print 2 + 3;}; print" });
		p.program();
		assertEquals(5, i.lastResult.getInt());
	}

	@Test
	public void testIf3() throws Exception {
		b.setArgs(new String[] { "if(-e+e){print 2 + 3; ;}else{ print 14+0; ;} print;" });
		p.program();
		assertEquals(14, i.lastResult.getInt());
	}*/
	
	@Test
	public void checkCastingMaxDoubleAndInt() throws Exception {
		b.setArgs(new String[] { "-pi/4" });
		p.program();
		assertEquals(-Math.PI / 4.0, i.lastResult.getDouble(), 0.001);
	}
	
	@Test
	public void testFuncSinPiDiv2MinusCosPi() throws Exception {
		b.setArgs(new String[] { "sin(pi/2)-cos(pi)" });
		p.program();
		assertEquals(2.0, i.lastResult.getDouble(), 0.01);
	}
	
	@Test
	public void testX() throws Exception {
		b.setArgs(new String[] { "2/2.0" });
		p.program();
		assertEquals(1.0, i.lastResult.getDouble(), 0.01);
	}
}
