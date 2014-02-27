import org.junit.*;
import static org.junit.Assert.*;

public class TestIf extends EnvForTests {
	
	@Test
	public void testIfFalse() throws Exception {
		b.setArgs(new String[] { "if(false){2-3;}4+0;" });
		p.program();
		assertEquals(4, i.lastResult.getInt());
	}

	@Test
	public void testIfTrue() throws Exception {
		b.setArgs(new String[] { "if(true){2-3;};" });
		p.program();
		assertEquals(-1, i.lastResult.getInt());
	}
	
	@Test
	public void testIfFalseIfTrueExpr() throws Exception {
		b.setArgs(new String[] { "if(false){ if(true){2-3;} 2-3;};" });
		p.program();
		assertEquals(0, i.lastResult.getInt());
	}
	
	@Test
	public void testIfFalseElse() throws Exception {
		b.setArgs(new String[] { "if(false){ if(true){2-3;} 2-3;} else {-9-0;};" });
		p.program();
		assertEquals(-9, i.lastResult.getInt());
	}
	
	@Test
	public void testIfTrueElse() throws Exception {
		b.setArgs(new String[] { "if(true){ if(true){2-3;} 10+0;} else {-9;};" });
		p.program();
		assertEquals(10, i.lastResult.getInt());
	}
	
	
	@Test
	public void testIfWTrueExpr() throws Exception {
		b.setArgs(new String[] { "if(true) 9+10; else 2+3;" });
		p.program();
		assertEquals(19, i.lastResult.getInt());
	}
	
	@Test
	public void testIfWTrueExpr2() throws Exception {
		b.setArgs(new String[] { "if(true) 9+10; else print 2+3;" });
		p.program();
		assertEquals(19, i.lastResult.getInt());
	}
	
	@Test
	public void testIfWFalseExpr() throws Exception {
		b.setArgs(new String[] { "if(false) print 9+10; else 2+3;" });
		p.program();
		assertEquals(5, i.lastResult.getInt());
	}
		
	
	@Test
	public void testIfWTrueElse() throws Exception {
		b.setArgs(new String[] { "if(true) print 9+10; else print 2+3;" });
		p.program();
		assertEquals(19, i.lastResult.getInt());
	}
	
	@Test
	public void testIfWFalseElse() throws Exception {
		b.setArgs(new String[] { "if(false) print 9+10; else print 2+3;" });
		p.program();
		assertEquals(5, i.lastResult.getInt());
	}
	
	@Test
	public void testIfWTrue() throws Exception {
		b.setArgs(new String[] { "if(true) print 9+10" });
		p.program();
		assertEquals(19, i.lastResult.getInt());
	}
	
	@Test
	public void testIfWFalse() throws Exception {
		b.setArgs(new String[] { "if(false) print 9+10" });
		p.program();
		assertEquals(0, i.lastResult.getInt());
	}
	
	// Тестирование значения после if-else
	@Test
	public void testIfWTrueExprGuaranted() throws Exception {
		b.setArgs(new String[] { "if(true) 9+10; else 2+3; guaranted=1337" });
		p.program();
		assertEquals(1337, i.lastResult.getInt());
	}
	
	@Test
	public void testIfWTrueExpr2Guaranted() throws Exception {
		b.setArgs(new String[] { "if(true) 9+10; else print 2+3; guaranted=1337" });
		p.program();
		assertEquals(1337, i.lastResult.getInt());
	}
	
	@Test
	public void testIfWFalseExprGuaranted() throws Exception {
		b.setArgs(new String[] { "if(false) print 9+10; else 2+3; guaranted=1337" });
		p.program();
		assertEquals(1337, i.lastResult.getInt());
	}
		
	
	@Test
	public void testIfWTrueElseGuaranted() throws Exception {
		b.setArgs(new String[] { "if(true) print 9+10; else print 2+3; guaranted=1337" });
		p.program();
		assertEquals(1337, i.lastResult.getInt());
	}
	
	@Test
	public void testIfWFalseElseGuaranted() throws Exception {
		b.setArgs(new String[] { "if(false) print 9+10; else print 2+3; guaranted=1337" });
		p.program();
		assertEquals(1337, i.lastResult.getInt());
	}
	
	@Test
	public void testIfWTrueGuaranted() throws Exception {
		b.setArgs(new String[] { "if(true) print 9+10; guaranted=1337" });
		p.program();
		assertEquals(1337, i.lastResult.getInt());
	}
	
	@Test
	public void testIfWFalseGuaranted() throws Exception {
		b.setArgs(new String[] { "if(false) print 9+10; guaranted=1337" });
		p.program();
		assertEquals(1337, i.lastResult.getInt());
	}
	
	// Тестирование функции в блоке
	@Test
	public void testIfsin() throws Exception {
		b.setArgs(new String[] { "-666; if(false) sin 30;" });
		p.program();
		assertEquals(false, i.table.get("ans").getBoolean());
	}
}